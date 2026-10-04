locals {
  name            = "${var.project}-${var.environment}"
  task_subnets    = var.enable_nat_gateway ? module.networking.private_subnet_ids : module.networking.public_subnet_ids
  database_url    = "r2dbc:postgresql://${module.database.endpoint}:${module.database.port}/${module.database.database_name}?sslMode=require"
  container_image = "${module.ecr.repository_url}:${var.image_tag}"
}

module "networking" {
  source             = "./modules/networking"
  name               = local.name
  vpc_cidr           = var.vpc_cidr
  enable_nat_gateway = var.enable_nat_gateway
}

module "security" {
  source   = "./modules/security"
  name     = local.name
  vpc_id   = module.networking.vpc_id
  app_port = var.app_port
}

module "ecr" {
  source = "./modules/ecr"
  name   = local.name
}

module "database" {
  source                = "./modules/database"
  name                  = local.name
  private_subnet_ids    = module.networking.private_subnet_ids
  security_group_id     = module.security.database_security_group_id
  instance_class        = var.database_instance_class
  multi_az              = var.database_multi_az
  backup_retention_days = var.database_backup_retention_days
  deletion_protection   = var.database_deletion_protection
  skip_final_snapshot   = !var.database_deletion_protection
}

module "alb" {
  source            = "./modules/alb"
  name              = local.name
  vpc_id            = module.networking.vpc_id
  public_subnet_ids = module.networking.public_subnet_ids
  security_group_id = module.security.alb_security_group_id
  app_port          = var.app_port
}

module "iam" {
  source              = "./modules/iam"
  name                = local.name
  ecr_repository_arn  = module.ecr.repository_arn
  log_group_arn       = module.ecs.log_group_arn
  database_secret_arn = module.database.master_user_secret_arn
}

module "ecs" {
  source              = "./modules/ecs"
  name                = local.name
  aws_region          = var.aws_region
  container_image     = local.container_image
  app_port            = var.app_port
  task_cpu            = var.task_cpu
  task_memory         = var.task_memory
  desired_count       = var.min_tasks
  execution_role_arn  = module.iam.execution_role_arn
  database_url        = local.database_url
  database_secret_arn = module.database.master_user_secret_arn
  subnet_ids          = local.task_subnets
  assign_public_ip    = !var.enable_nat_gateway
  security_group_id   = module.security.ecs_security_group_id
  target_group_arn    = module.alb.target_group_arn

  depends_on = [module.alb]
}

module "autoscaling" {
  source       = "./modules/autoscaling"
  cluster_name = module.ecs.cluster_name
  service_name = module.ecs.service_name
  min_capacity = var.min_tasks
  max_capacity = var.max_tasks
}

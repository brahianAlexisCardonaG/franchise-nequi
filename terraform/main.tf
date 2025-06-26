provider "aws" {
  region = var.aws_region
}

resource "aws_cloudwatch_log_group" "franchise" {
  name              = "/ecs/franchise"
  retention_in_days = 7
}

module "iam" {
  source = "./modules/iam"
}

module "rds" {
  source       = "./modules/rds"
  vpc_id       = var.vpc_id
  subnet_ids   = var.subnet_ids
  db_username  = var.db_username
  db_password  = var.db_password
}

module "security" {
  source = "./modules/security"
  vpc_id = var.vpc_id
}

module "ssm" {
  source       = "./modules/ssm"
  db_username  = var.db_username
  db_password  = var.db_password
  rds_endpoint = module.rds.db_endpoint
}

module "ecs" {
  source             = "./modules/ecs"
  subnet_ids         = var.subnet_ids
  aws_region         = var.aws_region
  iam_role_arn       = module.iam.ecs_task_execution_role_arn
  log_group_name     = aws_cloudwatch_log_group.franchise.name
  ssm_r2dbc_url_arn  = module.ssm.r2dbc_url_arn
  ssm_username_arn   = module.ssm.db_username_arn
  ssm_password_arn   = module.ssm.db_password_arn
  vpc_id             = var.vpc_id
  security_group_id  = module.security.security_group_id
}
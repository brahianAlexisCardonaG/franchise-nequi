resource "aws_ecs_cluster" "franchise" {
  name = "franchise-cluster"
}

resource "aws_ecs_task_definition" "franchise_task" {
  family                   = "franchise-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = var.iam_role_arn

  container_definitions = jsonencode([{
    name  = "franchise-app",
    image = "087000170463.dkr.ecr.${var.aws_region}.amazonaws.com/franchise/franchise-app:latest",
    portMappings = [{ containerPort = 8085, protocol = "tcp" }],
    secrets = [
      { name = "SPRING_R2DBC_URL", valueFrom = var.ssm_r2dbc_url_arn },
      { name = "SPRING_R2DBC_USERNAME", valueFrom = var.ssm_username_arn },
      { name = "SPRING_R2DBC_PASSWORD", valueFrom = var.ssm_password_arn }
    ],
    logConfiguration = {
      logDriver = "awslogs",
      options = {
        awslogs-group         = var.log_group_name,
        awslogs-region        = var.aws_region,
        awslogs-stream-prefix = "franchise"
      }
    }
  }])
}

resource "aws_ecs_service" "franchise_service" {
  name            = "franchise-service"
  cluster         = aws_ecs_cluster.franchise.id
  task_definition = aws_ecs_task_definition.franchise_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [var.security_group_id]
    assign_public_ip = true
  }
}

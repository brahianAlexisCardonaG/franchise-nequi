//5. Crear Task Definition
resource "aws_ecs_task_definition" "franchise_task" {
  family                   = "franchise-task"
  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs_task_execution_role.arn

  container_definitions = jsonencode([
    {
      name  = "franchise-app"
      image = "087000170463.dkr.ecr.${var.aws_region}.amazonaws.com/franchise/franchise-app:latest"

      portMappings = [
        {
          containerPort = 8085
          protocol      = "tcp"
        }
      ]

      secrets = [
        {
          name      = "SPRING_R2DBC_URL"
          valueFrom = aws_ssm_parameter.r2dbc_url.arn
        },
        {
          name      = "SPRING_R2DBC_USERNAME"
          valueFrom = aws_ssm_parameter.db_username.arn
        },
        {
          name      = "SPRING_R2DBC_PASSWORD"
          valueFrom = aws_ssm_parameter.db_password.arn
        }
      ]

      logConfiguration = {
        logDriver = "awslogs"
        options = {
          awslogs-group         = "/ecs/franchise"
          awslogs-region        = var.aws_region
          awslogs-stream-prefix = "franchise"
        }
      }
    }
  ])
}
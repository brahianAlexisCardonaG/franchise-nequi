//6. Crear ECS Service
resource "aws_ecs_service" "franchise_service" {
  name            = "franchise-service"
  cluster         = aws_ecs_cluster.franchise.id
  task_definition = aws_ecs_task_definition.franchise_task.arn
  launch_type     = "FARGATE"
  desired_count   = 1

  network_configuration {
    subnets         = var.subnet_ids
    security_groups = [aws_security_group.franchise_sg.id]
    assign_public_ip = true
  }
}
resource "aws_cloudwatch_log_group" "franchise" {
  name              = "/ecs/franchise"
  retention_in_days = 7
}
output "cluster_name" {
  description = "Name of the ECS cluster"
  value       = aws_ecs_cluster.this.name
}

output "service_name" {
  description = "Name of the ECS service"
  value       = aws_ecs_service.api.name
}

output "log_group_arn" {
  description = "ARN of the API log group"
  value       = aws_cloudwatch_log_group.api.arn
}

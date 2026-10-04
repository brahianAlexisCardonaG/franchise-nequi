output "execution_role_arn" {
  description = "Role used by ECS to pull the image, write logs and inject the database secret"
  value       = aws_iam_role.execution.arn
}

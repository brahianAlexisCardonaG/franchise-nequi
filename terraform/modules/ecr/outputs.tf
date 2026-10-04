output "repository_url" {
  description = "URL used to tag and push the API image"
  value       = aws_ecr_repository.this.repository_url
}

output "repository_arn" {
  description = "ARN of the repository, used to scope the pull permissions"
  value       = aws_ecr_repository.this.arn
}

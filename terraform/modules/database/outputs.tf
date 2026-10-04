output "endpoint" {
  description = "Hostname of the database"
  value       = aws_db_instance.this.address
}

output "port" {
  description = "Port of the database"
  value       = aws_db_instance.this.port
}

output "database_name" {
  description = "Name of the application database"
  value       = aws_db_instance.this.db_name
}

output "master_user_secret_arn" {
  description = "ARN of the Secrets Manager secret with the database username and password"
  value       = aws_db_instance.this.master_user_secret[0].secret_arn
}

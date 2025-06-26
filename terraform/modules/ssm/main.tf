resource "aws_ssm_parameter" "r2dbc_url" {
  name  = "/franchise-app/r2dbc-url"
  type  = "String"
  value = "r2dbc:postgresql://${var.rds_endpoint}:5432/franchise?sslmode=require"
}

resource "aws_ssm_parameter" "db_username" {
  name  = "/franchise-app/db-username"
  type  = "String"
  value = var.db_username
}

resource "aws_ssm_parameter" "db_password" {
  name  = "/franchise-app/db-password"
  type  = "String"
  value = var.db_password
}

output "r2dbc_url_arn" {
  value = aws_ssm_parameter.r2dbc_url.arn
}

output "db_username_arn" {
  value = aws_ssm_parameter.db_username.arn
}

output "db_password_arn" {
  value = aws_ssm_parameter.db_password.arn
}

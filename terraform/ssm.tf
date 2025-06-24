//2. Crear parámetros en Parameter Store
resource "aws_ssm_parameter" "r2dbc_url" {
  name  = "/franchise-app/r2dbc-url"
  type  = "String"
  value = "r2dbc:postgresql://${aws_db_instance.franchise_db.address}:5432/franchise?sslmode=require"
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
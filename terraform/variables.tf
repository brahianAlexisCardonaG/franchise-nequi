//1. Variables necesarias
variable "aws_region" {
  default = "us-east-1"
}

variable "vpc_id" {}
variable "subnet_ids" {
  type = list(string)
}

variable "db_username" {
  description = "Nombre de usuario para la base de datos"
  type        = string
  sensitive   = true
}

// Contraseña de la base de datos
variable "db_password" {
  description = "Contraseña para la base de datos"
  type        = string
  sensitive   = true
}
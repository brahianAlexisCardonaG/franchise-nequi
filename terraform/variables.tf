variable "aws_region" {
  default = "us-east-1"
}

variable "vpc_id" {}

variable "subnet_ids" {
  type = list(string)
}

variable "db_username" {
  type        = string
  sensitive   = true
}

variable "db_password" {
  type        = string
  sensitive   = true
}
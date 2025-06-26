variable "aws_region" {}

variable "subnet_ids" {
  type = list(string)
}

variable "iam_role_arn" {}

variable "log_group_name" {}

variable "ssm_r2dbc_url_arn" {}

variable "ssm_username_arn" {}

variable "ssm_password_arn" {}

variable "security_group_id" {}

variable "vpc_id" {}
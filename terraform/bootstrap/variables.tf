variable "aws_region" {
  type        = string
  description = "AWS region where the state bucket is created"
  default     = "us-east-1"
}

variable "project" {
  type        = string
  description = "Project name used as prefix for resource names"
  default     = "franchise"
}

variable "force_destroy_state_bucket" {
  type        = bool
  description = "Allow terraform destroy to delete the state bucket even if it still contains state files"
  default     = true
}

variable "monthly_budget_usd" {
  type        = string
  description = "Monthly cost limit in USD that triggers the budget alerts"
  default     = "5"
}

variable "budget_alert_email" {
  type        = string
  description = "Email address that receives the budget alerts"
}

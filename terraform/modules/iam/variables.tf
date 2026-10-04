variable "name" {
  type        = string
  description = "Prefix for resource names, usually <project>-<environment>"
}

variable "ecr_repository_arn" {
  type        = string
  description = "Repository the tasks are allowed to pull images from"
}

variable "log_group_arn" {
  type        = string
  description = "Log group the tasks are allowed to write to"
}

variable "database_secret_arn" {
  type        = string
  description = "Secret with the database credentials the tasks are allowed to read"
}

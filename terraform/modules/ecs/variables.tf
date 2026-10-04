variable "name" {
  type        = string
  description = "Prefix for resource names, usually <project>-<environment>"
}

variable "aws_region" {
  type        = string
  description = "Region of the CloudWatch log group"
}

variable "container_image" {
  type        = string
  description = "Full image reference to run, <repository_url>:<tag>"
}

variable "app_port" {
  type        = number
  description = "Port where the API container listens"
  default     = 8085
}

variable "task_cpu" {
  type        = number
  description = "Fargate CPU units for the task (256 = 0.25 vCPU)"
  default     = 256
}

variable "task_memory" {
  type        = number
  description = "Fargate memory for the task in MiB"
  default     = 1024
}

variable "desired_count" {
  type        = number
  description = "Initial number of tasks, later managed by auto scaling"
  default     = 1
}

variable "execution_role_arn" {
  type        = string
  description = "Role ECS uses to pull the image, write logs and read the database secret"
}

variable "database_url" {
  type        = string
  description = "R2DBC connection URL of the database"
}

variable "database_secret_arn" {
  type        = string
  description = "Secrets Manager secret with the database username and password"
}

variable "subnet_ids" {
  type        = list(string)
  description = "Subnets where the tasks run"
}

variable "assign_public_ip" {
  type        = bool
  description = "Give tasks a public IP, required when they run in public subnets without a NAT gateway"
}

variable "security_group_id" {
  type        = string
  description = "Security group of the tasks"
}

variable "target_group_arn" {
  type        = string
  description = "Load balancer target group where the tasks register"
}

variable "log_retention_days" {
  type        = number
  description = "Days the API logs are kept"
  default     = 1
}

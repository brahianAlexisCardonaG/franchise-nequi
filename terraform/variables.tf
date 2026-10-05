variable "aws_region" {
  type        = string
  description = "AWS region where the environment is deployed"
  default     = "us-east-1"
}

variable "project" {
  type        = string
  description = "Project name used as prefix for resource names"
  default     = "franchise"
}

variable "environment" {
  type        = string
  description = "Environment name, part of every resource name"

  validation {
    condition     = contains(["dev", "prod"], var.environment)
    error_message = "The environment must be dev or prod."
  }
}

variable "vpc_cidr" {
  type        = string
  description = "CIDR block of the environment VPC"
  default     = "10.0.0.0/16"
}

variable "enable_nat_gateway" {
  type        = bool
  description = "Run the tasks in private subnets behind a NAT gateway; when false they run in public subnets reachable only from the ALB"
  default     = false
}

variable "app_port" {
  type        = number
  description = "Port where the API container listens"
  default     = 8085
}

variable "image_tag" {
  type        = string
  description = "Tag of the API image in ECR, usually the git commit hash"

  validation {
    condition     = var.image_tag != "latest"
    error_message = "Use an immutable tag such as the git commit hash instead of latest."
  }
}

variable "task_cpu" {
  type        = number
  description = "Fargate CPU units per task (256 = 0.25 vCPU)"
  default     = 256
}

variable "task_memory" {
  type        = number
  description = "Fargate memory per task in MiB"
  default     = 1024
}

variable "min_tasks" {
  type        = number
  description = "Minimum number of tasks kept by auto scaling"
  default     = 1
}

variable "max_tasks" {
  type        = number
  description = "Maximum number of tasks auto scaling can reach"
  default     = 2
}

variable "database_instance_class" {
  type        = string
  description = "RDS instance class"
  default     = "db.t3.micro"
}

variable "database_multi_az" {
  type        = bool
  description = "Create a standby database in another availability zone"
  default     = false
}

variable "database_backup_retention_days" {
  type        = number
  description = "Days of automated database backups, 0 disables them"
  default     = 0
}

variable "database_deletion_protection" {
  type        = bool
  description = "Protect the database from terraform destroy and keep a final snapshot"
  default     = false
}

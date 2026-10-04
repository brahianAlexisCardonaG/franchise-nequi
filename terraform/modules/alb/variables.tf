variable "name" {
  type        = string
  description = "Prefix for resource names, usually <project>-<environment>"
}

variable "vpc_id" {
  type        = string
  description = "Identifier of the VPC"
}

variable "public_subnet_ids" {
  type        = list(string)
  description = "Public subnets in at least two availability zones"
}

variable "security_group_id" {
  type        = string
  description = "Security group of the load balancer"
}

variable "app_port" {
  type        = number
  description = "Port where the API container listens"
  default     = 8085
}

variable "health_check_path" {
  type        = string
  description = "Path the load balancer calls to decide if a task is healthy"
  default     = "/actuator/health"
}

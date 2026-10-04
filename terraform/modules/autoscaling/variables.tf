variable "cluster_name" {
  type        = string
  description = "Name of the ECS cluster"
}

variable "service_name" {
  type        = string
  description = "Name of the ECS service to scale"
}

variable "min_capacity" {
  type        = number
  description = "Minimum number of running tasks"
  default     = 1
}

variable "max_capacity" {
  type        = number
  description = "Maximum number of running tasks"
  default     = 2
}

variable "cpu_target_percent" {
  type        = number
  description = "Average CPU utilization the policy keeps the service at"
  default     = 70
}

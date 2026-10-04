variable "name" {
  type        = string
  description = "Prefix for resource names, usually <project>-<environment>"
}

variable "vpc_id" {
  type        = string
  description = "Identifier of the VPC that holds the security groups"
}

variable "app_port" {
  type        = number
  description = "Port where the API container listens"
  default     = 8085
}

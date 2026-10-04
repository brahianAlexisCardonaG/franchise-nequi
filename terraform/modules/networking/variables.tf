variable "name" {
  type        = string
  description = "Prefix for resource names, usually <project>-<environment>"
}

variable "vpc_cidr" {
  type        = string
  description = "CIDR block of the VPC"
  default     = "10.0.0.0/16"
}

variable "az_count" {
  type        = number
  description = "Number of availability zones to spread the subnets across (the ALB and RDS need at least 2)"
  default     = 2

  validation {
    condition     = var.az_count >= 2
    error_message = "At least two availability zones are required by the ALB and the RDS subnet group."
  }
}

variable "enable_nat_gateway" {
  type        = bool
  description = "Create a NAT gateway so tasks in private subnets can reach the internet; disabled in dev to save cost"
  default     = false
}

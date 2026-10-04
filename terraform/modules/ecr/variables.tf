variable "name" {
  type        = string
  description = "Name of the ECR repository"
}

variable "images_to_keep" {
  type        = number
  description = "Number of images kept by the lifecycle policy"
  default     = 5
}

variable "force_delete" {
  type        = bool
  description = "Allow terraform destroy to delete the repository even if it still contains images"
  default     = true
}

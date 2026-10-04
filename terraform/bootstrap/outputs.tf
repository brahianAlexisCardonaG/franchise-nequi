output "state_bucket_name" {
  description = "Name of the S3 bucket that stores the Terraform state of every environment"
  value       = aws_s3_bucket.state.bucket
}

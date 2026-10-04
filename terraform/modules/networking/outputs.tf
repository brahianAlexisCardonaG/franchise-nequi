output "vpc_id" {
  description = "Identifier of the VPC"
  value       = aws_vpc.this.id
}

output "public_subnet_ids" {
  description = "Identifiers of the public subnets (ALB and, without NAT, ECS tasks)"
  value       = aws_subnet.public[*].id
}

output "private_subnet_ids" {
  description = "Identifiers of the private subnets (database and, with NAT, ECS tasks)"
  value       = aws_subnet.private[*].id
}

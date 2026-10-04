output "dns_name" {
  description = "Public DNS name of the load balancer"
  value       = aws_lb.this.dns_name
}

output "target_group_arn" {
  description = "Target group where the API tasks register"
  value       = aws_lb_target_group.api.arn
}

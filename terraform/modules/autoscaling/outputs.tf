output "policy_name" {
  description = "Name of the CPU target tracking policy"
  value       = aws_appautoscaling_policy.cpu.name
}

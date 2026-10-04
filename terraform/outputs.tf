output "api_url" {
  description = "Public URL of the API"
  value       = "http://${module.alb.dns_name}"
}

output "swagger_url" {
  description = "Swagger UI of the deployed API"
  value       = "http://${module.alb.dns_name}/webjars/swagger-ui/index.html"
}

output "ecr_repository_url" {
  description = "Repository where the API image must be pushed"
  value       = module.ecr.repository_url
}

output "ecs_cluster_name" {
  description = "Name of the ECS cluster"
  value       = module.ecs.cluster_name
}

output "ecs_service_name" {
  description = "Name of the ECS service"
  value       = module.ecs.service_name
}

output "database_endpoint" {
  description = "Private hostname of the database"
  value       = module.database.endpoint
}

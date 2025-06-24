//3. Crear ECS Cluster
resource "aws_ecs_cluster" "franchise" {
  name = "franchise-cluster"
}
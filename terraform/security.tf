//7. Security Group
resource "aws_security_group" "franchise_sg" {
  name        = "franchise-sg"
  description = "Allow traffic for franchise app"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 8085
    to_port     = 8085
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
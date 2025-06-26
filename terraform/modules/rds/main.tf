resource "aws_db_subnet_group" "this" {
  name       = "franchise-db-subnet-group"
  subnet_ids = var.subnet_ids
}

resource "aws_security_group" "rds" {
  name        = "franchise-db-sg"
  description = "Allow PostgreSQL access"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
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

resource "aws_db_instance" "franchise" {
  identifier              = "franchise-db"
  engine                  = "postgres"
  engine_version          = "15.13"
  instance_class          = "db.t4g.micro"
  allocated_storage       = 20
  db_name                 = "franchise"
  username                = var.db_username
  password                = var.db_password
  publicly_accessible     = true
  skip_final_snapshot     = true
  db_subnet_group_name    = aws_db_subnet_group.this.name
  vpc_security_group_ids  = [aws_security_group.rds.id]
}

output "db_endpoint" {
  value = aws_db_instance.franchise.address
}

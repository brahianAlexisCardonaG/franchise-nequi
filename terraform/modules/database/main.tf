resource "aws_db_subnet_group" "this" {
  name       = "${var.name}-database"
  subnet_ids = var.private_subnet_ids
}

resource "aws_db_instance" "this" {
  identifier                  = "${var.name}-database"
  engine                      = "postgres"
  engine_version              = var.engine_version
  instance_class              = var.instance_class
  allocated_storage           = var.allocated_storage
  storage_type                = "gp3"
  storage_encrypted           = true
  db_name                     = var.database_name
  username                    = var.master_username
  manage_master_user_password = true
  db_subnet_group_name        = aws_db_subnet_group.this.name
  vpc_security_group_ids      = [var.security_group_id]
  publicly_accessible         = false
  multi_az                    = var.multi_az
  backup_retention_period     = var.backup_retention_days
  skip_final_snapshot         = var.skip_final_snapshot
  deletion_protection         = var.deletion_protection
  apply_immediately           = true
}

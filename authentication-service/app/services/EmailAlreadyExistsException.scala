package services

case class EmailAlreadyExistsException(email: String) extends RuntimeException


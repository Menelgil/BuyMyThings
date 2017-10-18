package services

case class InvalidEmailException(email: String) extends RuntimeException

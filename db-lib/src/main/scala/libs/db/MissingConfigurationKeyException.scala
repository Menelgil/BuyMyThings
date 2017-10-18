package libs.db

case class MissingConfigurationKeyException(path: String) extends RuntimeException(s"Missing configuration key $path")

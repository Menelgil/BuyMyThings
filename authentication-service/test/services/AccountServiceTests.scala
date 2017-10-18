package services

import libs.tests.FutureMatchers._
import libs.tests._
import org.junit.Test
import org.scalatest.Matchers._
import org.scalatest.junit.JUnitSuite


class AccountServiceTests extends JUnitSuite with TestDB {
  private def service: AccountService = {
    injector.getInstance(classOf[AccountService])
  }

  private val EMAIL = "email@domain.tld"
  private val PASSWORD = "password"
  private val PASSWORD2 = "passwd"

  @Test
  def CreateAccount_shouldSucceed(): Unit = {
    val result = service.createAccount(EMAIL, PASSWORD)
    result should eventuallySucceed
  }

  @Test
  def CreateAccount_shouldFail_whenEmailIsEmpty(): Unit = {
    val result = service.createAccount("", PASSWORD)
    result should eventuallyFailWith[InvalidEmailException]
  }

  @Test
  def CreateAccount_shouldFail_whenPasswordIsEmpty(): Unit = {
    val result = service.createAccount(EMAIL, "")
    result should eventuallyFailWith[InvalidPasswordException]

  }

  @Test
  def CreateAccount_shouldFail_whenEmailIsAlreadyUsed(): Unit = {
    val result = service.createAccount(EMAIL, PASSWORD).flatMap { _ =>
      service.createAccount(EMAIL, PASSWORD2)
    }

    result should eventuallyFailWith[EmailAlreadyExistsException]
  }


  @Test
  def AccountExists_shouldSucceed(): Unit = {
    val result = service.createAccount(EMAIL, PASSWORD).flatMap { _ =>
      service.authenticate(EMAIL, PASSWORD)
    }

    result should eventuallyBe(true)
  }

  @Test
  def AccountExists_shouldFail_whenAccountDoesNotExists(): Unit = {
    service.authenticate(EMAIL, PASSWORD) should eventuallyBe(false)
  }

  @Test
  def AccountExists_shouldFail_whenPasswordIsIncorrect(): Unit = {
    val result = service.createAccount(EMAIL, PASSWORD).flatMap { _ =>
      service.authenticate(EMAIL, PASSWORD2)
    }

    result should eventuallyBe(false)
  }
}
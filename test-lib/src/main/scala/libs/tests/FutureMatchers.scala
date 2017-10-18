package libs.tests

import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.time.{Milliseconds, Span}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}


object FutureMatchers {
  private implicit val timeout: FiniteDuration = 500.milliseconds

  def eventuallyBe[T](data: T)(implicit timeout: FiniteDuration = timeout) = new Matcher[Future[T]] {
    def apply(left: Future[T]): MatchResult = {
      val leftValue: Try[T] = Try(Await.ready(left, timeout)).flatMap(_.value.get)

      leftValue match {
        case Success(d) if d.equals(data) => MatchResult(matches = true, "", s"$left succeed and had the right value")
        case Success(d) => MatchResult(matches = false, s"$d did not equal $data", "")
        case Failure(e) => MatchResult(matches = false, s"did not complete in less than ${Span(timeout.toMillis, Milliseconds).prettyString}: $e", "")
      }
    }
  }

  def eventuallySucceed()(implicit timeout: FiniteDuration = timeout) = new Matcher[Future[_]] {
    def apply(left: Future[_]): MatchResult = {
      val leftValue: Try[_] = Try(Await.ready(left, timeout)).flatMap(_.value.get)

      leftValue match {
        case Success(_) => MatchResult(matches = true, "", s"did complete in less than ${Span(timeout.toMillis, Milliseconds).prettyString}")
        case Failure(e) => MatchResult(matches = false, s"did not complete in less than ${Span(timeout.toMillis, Milliseconds).prettyString}: $e", "")
      }
    }
  }

  def eventuallyFailWith[T: Manifest](implicit timeout: FiniteDuration = timeout) = new org.scalatest.matchers.Matcher[Future[_]] {
    def apply(left: Future[_]) = {
      val leftValue: Try[_] = Try(Await.ready(left, timeout)).flatMap(_.value.get)

      val clazz = manifest.runtimeClass.asInstanceOf[Class[T]]

      leftValue match {
        case Failure(e) if clazz.isAssignableFrom(e.getClass) => MatchResult(matches = true, "", s"$left was an expected failure")
        case Failure(e) => MatchResult(matches = false, s"$left raised a ${e.getClass.getName} but a ${clazz.getName} was expected", "")
        case _ => MatchResult(matches = false, s"$left did not raise an exception but a ${clazz.getName} was expected", "")
      }
    }
  }
}

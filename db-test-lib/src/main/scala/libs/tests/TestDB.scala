package libs.tests

import com.google.inject.{AbstractModule, Guice, Injector}
import libs.db.DBConnection
import org.junit.After

import scala.concurrent.ExecutionContext

trait TestDB {
  private case class DBTestModule() extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[ExecutionContext]).toInstance(scala.concurrent.ExecutionContext.global)
      bind(classOf[DBConnection]).to(classOf[TestMongoDBConnection])
    }
  }

  protected var injector: Injector = Guice.createInjector(TestModules():_*)
  protected implicit val ec: ExecutionContext = scala.concurrent.ExecutionContext.global
  private val dbConnection: TestMongoDBConnection = injector.getInstance(classOf[DBConnection]).asInstanceOf[TestMongoDBConnection]

  def TestModules(): Seq[AbstractModule] = {
    Seq(DBTestModule())
  }

  @After
  def test_teardown(): Unit = {
    dbConnection.teardown()
  }
}

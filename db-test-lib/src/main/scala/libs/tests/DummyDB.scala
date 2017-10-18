package libs.tests

import com.google.inject.AbstractModule
import libs.db.DBConnection

trait DummyDB {
  private case class DummyDBModule() extends AbstractModule {
    override def configure(): Unit = {
      bind(classOf[DBConnection]).to(classOf[DummyDBConnection])
    }
  }
}

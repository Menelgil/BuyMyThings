package libs.tests

import libs.db.DBConnection

class DummyDBConnection extends DBConnection {
  override def apply(collectionName: String) = throw new UnsupportedOperationException("A DummyDBCollection is not supposed to be used")
}

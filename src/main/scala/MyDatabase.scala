import java.sql._

class MyDatabase(dbFilePath: String) {
  private var connection: Connection = _

  // Method to establish the database connection
  def connect(): Unit = {
    Class.forName("org.sqlite.JDBC")
    connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath)
  }

  // Method to close the database connection
  def close(): Unit = {
    if (connection != null) {
      connection.close()
    }
  }

  // Method to execute a query and return the result set
  def executeQuery(query: String): ResultSet = {
    val statement = connection.createStatement()
    statement.executeQuery(query)
  }

  // Method to execute an update or insert query
  def executeUpdate(query: String): Int = {
    val statement = connection.createStatement()
    statement.executeUpdate(query)
  }

  // Add other methods for additional database actions as needed
}
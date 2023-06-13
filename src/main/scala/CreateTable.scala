import java.sql._

object CreateTable extends App {

    println("Creating database...")
    val dbFilePath = "src/main/resources/database.db"

    // Load the SQLite JDBC driver
    Class.forName("org.sqlite.JDBC")

    // Create a connection to the database
    val connection = DriverManager.getConnection("jdbc:sqlite:" + dbFilePath)

    // Create a statement object
    val statement = connection.createStatement()

    try {
      // Execute the CREATE TABLE statement to create a table in the database
      val createTableQuery = "CREATE TABLE games (id INTEGER PRIMARY KEY, status TEXT, white TEXT, black TEXT, CurrMover TEXT, LastMove TEXT, win TEXT, loss TEXT, draw BOOLEAN)"
      statement.executeUpdate(createTableQuery)

      // Add more CREATE TABLE statements or perform other initial database setup if needed

      println("Database created successfully.")
    } catch {
      case ex: SQLException =>
        ex.printStackTrace()
    } finally {
      // Close the statement and connection
      statement.close()
      connection.close()
    }
}
import java.sql.*;

public class DatabaseDriver
{
    Connection connection = null;
    Statement statement;

    public DatabaseDriver(String dbName, String dbType) throws ClassNotFoundException
    {
	Class.forName(dbType);
	
	try
	    {
		connection = DriverManager.getConnection(dbName);
		statement = connection.createStatement();

		ResultSet table = statement.executeQuery("select name from sqlite_master where type='table' and name='movie'");

		if (!table.next()) createDB(statement);
	    }
	catch (SQLException exc)
	    {
		exc.printStackTrace();
	    }
    }

    private static void createDB(Statement stmnt) throws SQLException
    {
	stmnt.executeUpdate("create table movie (name string, category string, path string)");
    }

    public ResultSet getAllMovies() throws SQLException
    {
	ResultSet movies = statement.executeQuery("select name from movie");
	return(movies);
    }

    public ResultSet getCategory(String category) throws SQLException
    {
	ResultSet movies = statement.executeQuery(String.format("select name from movie where category='%s'", category));
	return(movies);
    }

    public String getMoviePath(String movie) throws SQLException
    {
	ResultSet path = statement.executeQuery(String.format("select path from movie where name='%s'", movie));
	return(path.getString(1));
    }

    public void setNewMovie(String movie, String category, String path) throws SQLException
    {
	statement.executeUpdate(String.format("insert into movie values('%s','%s','%s')", movie, category, path));
	// ResultSet movies = statement.executeQuery("select name from movie");
	// return(movies);
    }

    public boolean notExists(String movie) throws SQLException
    {
	ResultSet path = statement.executeQuery(String.format("select path from movie where name='%s'", movie));
	System.out.println(path.first());
	return(path.isClosed());
    }

    public void closeConnection()
    {
	try	    {
		if (connection != null) connection.close();
	    }
	catch (SQLException exc)
	    {
		exc.printStackTrace();
	    }
    }

    public static void main(String[] args)
    {}
}

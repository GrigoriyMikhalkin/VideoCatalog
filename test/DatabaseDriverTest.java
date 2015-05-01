import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.io.File;
import java.sql.SQLException;

public class DatabaseDriverTest
{
    DatabaseDriver testDD;
    
    @Test
    public void testExistDB() throws ClassNotFoundException
    {
	testDD = new DatabaseDriver("jdbc:sqlite:../resources/testdb.db","org.sqlite.JDBC");
	File testdb = new File("../resources/testdb.db");
	assertTrue(testdb.exists());
    }

     @Test
    public void testSetNewMovie() throws SQLException
    {}

    @Test
    public void testGetAllMovies() throws SQLException
    {
	
    }

    @Test
    public void testGetMoviePath() throws SQLException
    {}
}



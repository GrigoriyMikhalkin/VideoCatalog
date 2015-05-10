import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.io.File;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseDriverTest
{
    DatabaseDriver testDD;
    
    
    @Test
    public void testExistDB() throws ClassNotFoundException
    {
	testDD = new DatabaseDriver("jdbc:sqlite:../resources/testdb.db","org.sqlite.JDBC");
	testDD.closeConnection();
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
    public void testGetMovies() throws Exception
    {
	testDD = new DatabaseDriver("jdbc:sqlite:../resources/testdb.db","org.sqlite.JDBC");

	testDD.setNewMovie("tstMovie", "test", "test");
	ResultSet t = testDD.getMovies("tst");

	t.next();
	String name = t.getString(1);
	
	testDD.closeConnection();
	
	assertEquals("tstMovie", name);
    }

    @Test
    public void testGetMoviePath() throws SQLException
    {}

    @Test
    public void testSetGetPaths() throws Exception
    {
	testDD = new DatabaseDriver("jdbc:sqlite:../resources/testdb.db","org.sqlite.JDBC");
	testDD.setNewPath("test","test");
	testDD.setNewPath("test2","test2");
	testDD.setNewPath("testdaf","testz");
	testDD.setNewPath("testdaaf","tesatz");
	ResultSet testrs = testDD.getPaths();

	testrs.next();
	String t1 = testrs.getString(1);
	testrs.next();
	String t2 = testrs.getString(1);
	testrs.next();
	String t3 = testrs.getString(1);
	testrs.next();
	String t4 = testrs.getString(1);

	testDD.closeConnection();
	
	String[] exp = {"test","test2","testdaf","testdaaf"};
	String[] res = {t1,t2,t3,t4};
	
	assertArrayEquals(exp,res);
	
    }

    @Test
    public void testGetPathCategory() throws Exception
    {
	testDD = new DatabaseDriver("jdbc:sqlite:../resources/testdb.db","org.sqlite.JDBC");
	testDD.setNewPath("test3","testCategory");
	ResultSet t = testDD.getPathCategory("test3");

	t.next();
	String category = t.getString(1);
	testDD.closeConnection();
	
	assertEquals("testCategory", category);
    }
}



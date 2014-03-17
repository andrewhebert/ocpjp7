package jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class JdbcTest {
	private static final String url = "jdbc:derby:memory:myDB;create=true";
	private Connection connection;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
         
		
	}

	@Before
	public void setUp() throws Exception {
		connection = DriverManager.getConnection(url); 
		
		Statement statement = connection.createStatement();
		String sql = "DECLARE GLOBAL TEMPORARY TABLE memtable "+
                "(id int, name varchar(10)) not logged";
		statement.execute(sql);
		
		
		
		
	}
	
	@Test
	public void testConnection(){
		boolean valid = false;
		try {
			valid = connection.isValid(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Assert.assertTrue(valid);
	}
	
	@Test
	public void testStatement(){
		Statement createStatement = null;
		try {
			createStatement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			resultSet = createStatement.executeQuery("select 1");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int resultInt = 0;
		try {
			resultInt = resultSet.getInt(0);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Assert.assertEquals(resultInt, 1);
	}

	@After
	public void tearDown() throws Exception {
		connection.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		
	}



}

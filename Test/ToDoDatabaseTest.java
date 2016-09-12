import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import sample.ToDoDatabase;
import sample.ToDoItem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by bearden-tellez on 9/11/16.
 */

public class ToDoDatabaseTest {
    static ToDoDatabase todoDatabase = null;

    public ToDoDatabaseTest() {
        super();
        System.out.println("Building a new ToDoDatabase instance ********");
    }

    @Before
    public void setUp() throws Exception {
//        System.out.println("setup is running");
        if (todoDatabase == null) {
//            System.out.println("init database - should only see this once");
            todoDatabase = new ToDoDatabase();
            todoDatabase.init();
        }
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testInit() throws Exception {
        // test to make sure we can access the new database
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        PreparedStatement todoQuery = conn.prepareStatement("SELECT * FROM todos");
        ResultSet results = todoQuery.executeQuery();
        assertNotNull(results);

    }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String todoText = "UnitTest-ToDo";

        todoDatabase.insertToDo(conn, todoText);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos WHERE text = ?");
        stmt.setString(1, todoText);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteToDo(conn, todoText);

        // make sure there are no more records for our test todo
        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);

    }

    @Test
    public void testToggle() throws Exception{
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);

        System.out.println("Create a new item");
        String toggleToDoText = "UnitTest-ToDolkhtdead";
        Boolean testToggle = true;

        todoDatabase.insertToDo(conn, toggleToDoText);

        PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM todos WHERE text = ?" );
        stmt2.setString(1, toggleToDoText);
//        stmt2.setBoolean(1, testToggle);
        ResultSet results2 = stmt2.executeQuery();
        results2.next();
        int toggleTestId = results2.getInt("id");

        boolean beforeToggletest = results2.getBoolean("is_done");

//        ArrayList<ToDoItem> todosThis = todoDatabase.selectToDos(conn);

        System.out.println("1 " + beforeToggletest);


        todoDatabase.toggleToDo(conn, toggleTestId);


//        stmt2 = conn.prepareStatement("SELECT * FROM todos WHERE text = ?" );
//        stmt2.setString(1, toggleToDoText);
//        ResultSet results3 = stmt2.executeQuery();
        results2 = stmt2.executeQuery();
        results2.next();
        boolean afterToggle = results2.getBoolean("is_done");



        System.out.println(afterToggle);

       assertTrue(beforeToggletest != afterToggle);

        todoDatabase.deleteToDo(conn, toggleToDoText);

    }

}

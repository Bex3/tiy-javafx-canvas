package sample;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by bearden-tellez on 9/8/16.
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
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos");
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
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
        int todosBefore = todos.size();


        todoDatabase.insertToDo(conn, firstToDoText);
        todoDatabase.insertToDo(conn, secondToDoText);

        todos = todoDatabase.selectToDos(conn);

//        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
       System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() >= todosBefore + 2);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);
    }

    @Test
    public void testToggle() throws Exception{
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);

        System.out.println("Create a new item");
        String toggleToDoText = "UnitTest-ToDoToggle";


        ArrayList<ToDoItem> todos = todoDatabase.selectToDos(conn);
        todoDatabase.insertToDo(conn, toggleToDoText);


        System.out.println("Found " + todos.size() + " todos in the database");

        System.out.println("Retrieve it - save id");

        System.out.println("Change it/call toggle method");

        System.out.println("retrieve it by id & ensure it's been toggled");

        System.out.println("delete it... by id");


    }

}
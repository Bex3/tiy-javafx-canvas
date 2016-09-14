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

//    @Test
//    public void testInsertToDo() throws Exception {
//        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
//        String todoText = "UnitTest-ToDo";
//
//        todoDatabase.insertToDo(conn, todoText);
//
//        // make sure we can retrieve the todo we just created
//        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos WHERE text = ?");
//        stmt.setString(1, todoText);
//        ResultSet results = stmt.executeQuery();
//        assertNotNull(results);
//        // count the records in results to make sure we get what we expected
//        int numResults = 0;
//        while (results.next()) {
//            numResults++;
//        }
//
//        assertEquals(1, numResults);
//
//        todoDatabase.deleteToDo(conn, todoText);
//
//        // make sure there are no more records for our test todo
//        results = stmt.executeQuery();
//        numResults = 0;
//        while (results.next()) {
//            numResults++;
//        }
//        assertEquals(0, numResults);
//    }

    @Test
    public void testInsertToDo() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-Hello2xyzzzzyyy";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        todoDatabase.insertToDo(conn, todoText, userID);

        // make sure we can retrieve the todo we just created
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM todos where text = ?");
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
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);

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
            String toggleUserName = "dismyemail@gmail.com";
            String toggleFullName = "Testy McTesterson";

            String toggleToDoText = "UnitTest-ToDolkhtdead";
            int userID = todoDatabase.insertUser(conn, toggleUserName, toggleFullName);

            todoDatabase.insertToDo(conn, toggleToDoText, userID);


            PreparedStatement stmt2 = conn.prepareStatement("SELECT * FROM todos WHERE text = ?" );
            stmt2.setString(1, toggleToDoText);
            ResultSet results2 = stmt2.executeQuery();
            results2.next();
            int toggleTestId = results2.getInt("id");

            boolean beforeToggletest = results2.getBoolean("is_done");

            System.out.println( beforeToggletest);

            todoDatabase.toggleToDo(conn, toggleTestId);

            results2 = stmt2.executeQuery();
            results2.next();
            boolean afterToggle = results2.getBoolean("is_done");

            System.out.println(afterToggle);

            assertTrue(beforeToggletest != afterToggle);

            todoDatabase.deleteToDo(conn, toggleToDoText);
            todoDatabase.deleteUser(conn, toggleUserName);

    }


    @Test
    public void testInsertUser() throws Exception {
        Connection conn = DriverManager.getConnection(ToDoDatabase.DB_URL);
        String testUser = "UnitTest-User";
        String testFName = "Rebecca";

        todoDatabase.insertUser(conn, testUser, testFName);

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
        stmt.setString(1, testUser);
        //stmt.setString (2, testFName);
        ResultSet results = stmt.executeQuery();
        assertNotNull(results);
        // count the records in results to make sure we get what we expected
        int numResults = 0;
        while (results.next()) {
            numResults++;
        }

        assertEquals(1, numResults);

        todoDatabase.deleteUser(conn, testUser);

        results = stmt.executeQuery();
        numResults = 0;
        while (results.next()) {
            numResults++;
        }
        assertEquals(0, numResults);
    }

    @Test
    public void testSelectAllToDos() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String firstToDoText = "UnitTest-ToDo1";
        String secondToDoText = "UnitTest-ToDo2";

        ArrayList <ToDoItem> todos = todoDatabase.selectToDos(conn);
        int todosBefore = todos.size();

        String username = "testusername";
        String fullname = "Tabby the rat";
        todoDatabase.insertUser(conn, username, fullname);

        int userIddd = todoDatabase.insertUser(conn, username, fullname);

        todoDatabase.insertToDo(conn, firstToDoText, userIddd);
        todoDatabase.insertToDo(conn, secondToDoText, userIddd);

       todos = todoDatabase.selectToDos(conn);
        System.out.println("Found " + todos.size() + " todos in the database");

        assertTrue("There should be at least 2 todos in the database (there are " +
                todos.size() + ")", todos.size() > 1);

        todoDatabase.deleteToDo(conn, firstToDoText);
        todoDatabase.deleteToDo(conn, secondToDoText);

        todoDatabase.deleteUser(conn, username);
    }

    @Test
    public void testInsertToDoForUser() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        String todoText = "UnitTest-ToDo";
        String todoText2 = "UnitTest-ToDo2";

        // adding a call to insertUser, so we have a user to add todos for
        String username = "unittester@tiy.com";
        String fullName = "Unit Tester";
        int userID = todoDatabase.insertUser(conn, username, fullName);

        String username2 = "unitester2@tiy.com";
        String fullName2 = "Unit Tester 2";
        int userID2 = todoDatabase.insertUser(conn, username2, fullName2);

        todoDatabase.insertToDo(conn, todoText, userID);
        todoDatabase.insertToDo(conn, todoText2, userID2);

        // make sure each user only has one todo item
        ArrayList<ToDoItem> todosUser1 = todoDatabase.selectToDosForUser(conn, userID);
        ArrayList<ToDoItem> todosUser2 = todoDatabase.selectToDosForUser(conn, userID2);

        assertEquals(1, todosUser1.size());
        assertEquals(1, todosUser2.size());

        // make sure each todo item matches
        ToDoItem todoUser1 = todosUser1.get(0);
        assertEquals(todoText, todoUser1.text);
        ToDoItem todoUser2 = todosUser2.get(0);
        assertEquals(todoText2, todoUser2.text);

        todoDatabase.deleteToDo(conn, todoText);
        todoDatabase.deleteToDo(conn, todoText2);
        // make sure we remove the test user we added earlier
        todoDatabase.deleteUser(conn, username);
        todoDatabase.deleteUser(conn, username2);

    }

}
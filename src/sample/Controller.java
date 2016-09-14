package sample;



import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Scanner;

public class Controller implements Initializable {
    @FXML
    ListView todoList;

    @FXML
    TextField todoText;

    ObservableList<ToDoItem> todoItems = FXCollections.observableArrayList();
    ArrayList<ToDoItem> savableList = new ArrayList<ToDoItem>();
    String fileName = "todos.json";

    Connection conn;
    ToDoDatabase db;
    User myUser;
    int userid;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new ToDoDatabase();
        try {
         conn = db.init();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        System.out.println("Starting");

        System.out.println("If you have an existing account please type e, and/or if you would like to create a new account please type n.......");
        Scanner inputScanner = new Scanner(System.in);
        String userInput = inputScanner.nextLine();
        try {
            if (userInput.equalsIgnoreCase("e")) {
                System.out.println("Existing account it is. ");
                System.out.println("Please enter your email");
//                myUser.setUsername(inputScanner.nextLine());
                String userName = inputScanner.nextLine();
                myUser = db.selectUser(conn, userName);
                myUser.getUserId();
                System.out.println(myUser.getUserId());

                if (myUser!= null) {
                   userid = myUser.getUserId();
//                   userid = db.insertUser(conn, myUser.getUsername(), myUser.getFullname());
                    savableList = db.selectToDosForUser(conn, userid);
                    if (savableList != null) {
                        for (ToDoItem item : savableList) {
                            todoItems.add(item);
                            System.out.println("Checking existing list ...");
                        }
                    }
                }
            }else if (userInput.equalsIgnoreCase("n")){
                System.out.println("New account for you");
                System.out.println("Please enter your email address");
                String newEmail = inputScanner.nextLine();
                System.out.println(newEmail);
                myUser = new User();
                myUser.setUsername(newEmail);
                System.out.println("Please enter your full name");
                String newFullName = inputScanner.nextLine();
                myUser.setFullname(newFullName);
//                myUser.setFullname(inputScanner.nextLine());
                userid = db.insertUser(conn, myUser.getUsername(), myUser.getFullname());
            }

            System.out.println("Setting up two-way data binding between the UI and the obs list");
            todoList.setItems(todoItems);

        } catch (SQLException ex) {
            System.out.println("We had an account exception");

//            todoList.setItems(todoItems);
        }
    }

    public void saveToDoList() {
        if (todoItems != null && todoItems.size() > 0) {
            System.out.println("Saving " + todoItems.size() + " items in the list");
            savableList = new ArrayList<ToDoItem>(todoItems);
            System.out.println("There are " + savableList.size() + " items in my savable list");
            saveList();
        } else {
            System.out.println("No items in the ToDo List");
        }
    }

    public void addItem() {
        try {
            System.out.println("Adding item ...");
            System.out.println(todoText.getText());


            int todoId = db.insertToDo(conn, todoText.getText(), userid);
//            System.out.println(todoText.getText() + "2");


            todoItems.add(new ToDoItem(todoId, todoText.getText(), false));
//            System.out.println(todoText.getText() + "3");

            todoText.setText("");

        } catch (Exception exception){
            System.out.println("Add item exception");
        }
    }

    public void removeItem() {
        ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
        System.out.println("Removing " + todoItem.text + " ...");
        todoItems.remove(todoItem);
    }

    public void toggleItem() {
        System.out.println("Toggling item ...");
        ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
        if (todoItem != null) {
            todoItem.isDone = !todoItem.isDone;
            todoList.setItems(null);
            todoList.setItems(todoItems);


//            db.toggleToDo(conn, ); ***


        }
    }

    public void saveList() {
        try {

            // write JSON
            JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
            String jsonString = jsonSerializer.serialize(new ToDoItemList(todoItems));

            System.out.println("JSON = ");
            System.out.println(jsonString);

            File sampleFile = new File(fileName);
            FileWriter jsonWriter = new FileWriter(sampleFile);
            jsonWriter.write(jsonString);
            jsonWriter.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public ToDoItemList retrieveList() {
        try {

            Scanner fileScanner = new Scanner(new File(fileName));
            fileScanner.useDelimiter("\\Z"); // read the input until the "end of the input" delimiter
            String fileContents = fileScanner.next();
            JsonParser ToDoItemParser = new JsonParser();

            ToDoItemList theListContainer = ToDoItemParser.parse(fileContents, ToDoItemList.class);
            System.out.println("==============================================");
            System.out.println("        Restored previous ToDoItem");
            System.out.println("==============================================");
            return theListContainer;
        } catch (IOException ioException) {
            // if we can't find the file or run into an issue restoring the object
            // from the file, just return null, so the caller knows to create an object from scratch
            return null;
        }
    }
}
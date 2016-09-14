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
//    @FXML
//    ListView todoList;
//
//    @FXML
//    TextField todoText;
//
//    ObservableList<ToDoItem> todoItems = FXCollections.observableArrayList();
//    ArrayList<ToDoItem> savableList = new ArrayList<ToDoItem>();
//    String fileName = "todos.json";
//
//    public String username;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//
//        System.out.print("Please enter your name: ");
//        Scanner inputScanner = new Scanner(System.in);
//        username = inputScanner.nextLine();
//
//        if (username != null && !username.isEmpty()) {
//            fileName = username + ".json";
//        }
//
//        System.out.println("Checking existing list ...");
//        ToDoItemList retrievedList = retrieveList();
//        if (retrievedList != null) {
//            for (ToDoItem item : retrievedList.todoItems) {
//                todoItems.add(item);
//            }
//        }
//
//        todoList.setItems(todoItems);
//    }
//
//    public void saveToDoList() {
//        if (todoItems != null && todoItems.size() > 0) {
//            System.out.println("Saving " + todoItems.size() + " items in the list");
//            savableList = new ArrayList<ToDoItem>(todoItems);
//            System.out.println("There are " + savableList.size() + " items in my savable list");
//            saveList();
//        } else {
//            System.out.println("No items in the ToDo List");
//        }
//    }
//
//    public void addItem() {
//        System.out.println("Adding item ...");
//        todoItems.add(new ToDoItem(todoText.getText()));
//        todoText.setText("");
//    }
//
//    public void removeItem() {
//        ToDoItem todoItem = (ToDoItem)todoList.getSelectionModel().getSelectedItem();
//        System.out.println("Removing " + todoItem.text + " ...");
//        todoItems.remove(todoItem);
//    }
//
//    public void toggleItem() {
//        System.out.println("Toggling item ...");
//        ToDoItem todoItem = (ToDoItem)todoList.getSelectionModel().getSelectedItem();
//        if (todoItem != null) {
//            todoItem.isDone = !todoItem.isDone;
//            todoList.setItems(null);
//            todoList.setItems(todoItems);
//        }
//    }
//
//    public void saveList() {
//        try {
//
//            // write JSON
//            JsonSerializer jsonSerializer = new JsonSerializer().deep(true);
//            String jsonString = jsonSerializer.serialize(new ToDoItemList(todoItems));
//
//            System.out.println("JSON = ");
//            System.out.println(jsonString);
//
//            File sampleFile = new File(fileName);
//            FileWriter jsonWriter = new FileWriter(sampleFile);
//            jsonWriter.write(jsonString);
//            jsonWriter.close();
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
//    }
//
//    public ToDoItemList retrieveList() {
//        try {
//
//            Scanner fileScanner = new Scanner(new File(fileName));
//            fileScanner.useDelimiter("\\Z"); // read the input until the "end of the input" delimiter
//            String fileContents = fileScanner.next();
//            JsonParser ToDoItemParser = new JsonParser();
//
//            ToDoItemList theListContainer = ToDoItemParser.parse(fileContents, ToDoItemList.class);
//            System.out.println("==============================================");
//            System.out.println("        Restored previous ToDoItem");
//            System.out.println("==============================================");
//            return theListContainer;
//        } catch (IOException ioException) {
//            // if we can't find the file or run into an issue restoring the object
//            // from the file, just return null, so the caller knows to create an object from scratch
//            return null;
//        }
//    }


    //public class Controller implements Initializable {
    @FXML
    ListView todoList;

    @FXML
    TextField todoText;

    ObservableList<ToDoItem> todoItems = FXCollections.observableArrayList();
    ArrayList<ToDoItem> savableList = new ArrayList<ToDoItem>();
    String fileName = "todos.json";

    Connection conn;
    ToDoDatabase db;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new ToDoDatabase();
        try {
         conn = db.init();
        } catch (Exception exception) {

        }
        System.out.println("Starting");
        //myToDoDatabase.selectToDos(conn);

        System.out.print("Please enter your email address aka your username: ");
        Scanner inputScanner = new Scanner(System.in);
        User myUser = new User();
        myUser.setUsername(inputScanner.nextLine());


//        if (username != null && !username.isEmpty()) {
//            fileName = username + ".json";
//        }

        System.out.println("Checking existing list ...");
//        ToDoItemList retrievedList = retrieveList();
//        int thisUserid = myUser.getUserId();

        try {
            User thisUser = db.selectUser(conn, myUser.getUsername());
            if (thisUser != null) {
                int thisUserId = myUser.getUserId();
                savableList = db.selectToDosForUser(conn, thisUserId);
                if (savableList != null) {
                    for (ToDoItem item : savableList) {
                        todoItems.add(item);
                    }
                }
            } else if (thisUser == null){
                System.out.println("Existing todo list not found, would you like to create one? y/n");
                String createAccountQuestion = inputScanner.nextLine();
                if (createAccountQuestion.equalsIgnoreCase("y")) {
                    System.out.println("Please enter your full name");
                    myUser.setFullname(inputScanner.nextLine());
                    db.insertUser(conn, myUser.getUsername(), myUser.getFullname());
                } else {
                    System.out.println("Then why are you here?");
                }
            }
        } catch (SQLException ex) {
            System.out.println("We had a new account exception");

            todoList.setItems(todoItems);
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
        System.out.println("Adding item ...");
        todoItems.add(new ToDoItem(todoText.getText()));
        todoText.setText("");
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
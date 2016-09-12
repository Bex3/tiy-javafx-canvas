package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.engine.Database;
import org.h2.tools.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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
    public String username;
    Connection conn;
    ToDoDatabase db;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        db = new ToDoDatabase();
        try {
            db.init();
            conn = DriverManager.getConnection("jdbc:h2:./main");
            savableList = db.selectToDos(conn);

            }catch (Exception exception){
            exception.printStackTrace();
            }
            todoList.setItems(todoItems);
    }


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

//        todoList.setItems(todoItems);
//    }

    public void saveToDoList() {
        if (todoItems != null && todoItems.size() > 0) {
            System.out.println("Saving " + todoItems.size() + " items in the list");
            savableList = new ArrayList<ToDoItem>(todoItems);
            System.out.println("There are " + savableList.size() + " items in my savable list");
//            saveList();

        } else {
            System.out.println("No items in the ToDo List");
        }
    }

    public void addItem() {
        try {
            System.out.println("Adding item ...");
            db.insertToDo(conn, todoText.getText());
            todoItems.add(new ToDoItem(todoText.getText()));
            todoText.setText("");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void removeItem() {
        try {
            ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
            System.out.println("Removing " + todoItem.text + " ...");
            db.deleteToDo(conn, todoText.getText());
            todoItems.remove(todoItem);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void toggleItem() {
        try {
            System.out.println("Toggling item ...");
            ToDoItem todoItem = (ToDoItem) todoList.getSelectionModel().getSelectedItem();
            if (todoItem != null) {
                todoItem.isDone = !todoItem.isDone;
                todoList.setItems(null);
                todoList.setItems(todoItems);
                db.toggleToDo(conn, todoItem.id);
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
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

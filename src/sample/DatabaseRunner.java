package sample;

/**
 * Created by bearden-tellez on 9/9/16.
 */
public class DatabaseRunner {
    public static void main(String[] args) throws Exception {
        ToDoDatabase db = new ToDoDatabase();
        db.init();
    }
}
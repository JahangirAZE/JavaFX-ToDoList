package az.jahangir.todolist;

import az.jahangir.todolist.model.ToDoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main-window.fxml")));
        Scene scene = new Scene(root, 900, 500);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm());
        primaryStage.setTitle("ToDo List");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        try {
            ToDoData.getInstance().storeToDoItems();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() {
        try {
            ToDoData.getInstance().loadToDoItems();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
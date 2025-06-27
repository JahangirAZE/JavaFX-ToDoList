module az.jahangir.todolist {
    requires javafx.controls;
    requires javafx.fxml;


    opens az.jahangir.todolist to javafx.fxml;
    exports az.jahangir.todolist;
}
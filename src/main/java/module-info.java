module az.jahangir.todolist {
    requires javafx.controls;
    requires javafx.fxml;
    requires static lombok;


    opens az.jahangir.todolist to javafx.fxml;
    exports az.jahangir.todolist;
    exports az.jahangir.todolist.model;
}
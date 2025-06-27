package az.jahangir.todolist.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ToDoData {

    private static final ToDoData instance = new ToDoData();
    private static final String fileName = "ToDoListItems.txt";

    private ObservableList<ToDoItem> toDoItemList;
    private final DateTimeFormatter formatter;

    public static ToDoData getInstance() {
        return instance;
    }

    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ToDoItem> getToDoItemList() {
        return toDoItemList;
    }

    public void addToDoItem(ToDoItem item) {
        toDoItemList.add(item);
    }

    public void loadToDoItems() throws IOException {
        toDoItemList = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String input;
            while ((input = reader.readLine()) != null) {
                String[] parts = input.split("\t");

                String shortDescription = parts[0];
                String details = parts[1];
                String deadline = parts[2];

                LocalDate date = LocalDate.parse(deadline, formatter);

                ToDoItem item = new ToDoItem(shortDescription, details, date);
                toDoItemList.add(item);
            }
        }
    }

    public void storeToDoItems() throws IOException {
        Path path = Paths.get(fileName);
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (ToDoItem item : toDoItemList) {
                writer.write("%s\t%s\t%s".formatted(item.getShortDescription(), item.getDetails(), item.getDeadline().format(formatter)));
                writer.newLine();
            }
        }
    }

    public void deleteToDoItem(ToDoItem item) {
        toDoItemList.remove(item);
    }
}
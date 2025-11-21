package az.jahangir.todolist.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ToDoData {

    @Getter
    private static final ToDoData instance = new ToDoData();
    private static final String fileName = "ToDoListItems.txt";

    @Getter
    private ObservableList<ToDoItem> toDoItemList;
    private final DateTimeFormatter formatter;

    private ToDoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public void addToDoItem(ToDoItem item) {
        toDoItemList.add(item);
    }

    public void loadToDoItems() throws IOException {
        toDoItemList = FXCollections.observableArrayList();

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(fileName))) {
            String input;
            while (Objects.nonNull(input = reader.readLine())) {
                String[] parts = input.split("\t");

                String shortDescription = parts[0];
                String details = parts[1];
                String deadline = parts[2];

                LocalDate date = LocalDate.parse(deadline, formatter);

                toDoItemList.add(new ToDoItem(shortDescription, details, date));
            }
        }
    }

    public void storeToDoItems() throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName))) {
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
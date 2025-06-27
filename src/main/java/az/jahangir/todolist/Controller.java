package az.jahangir.todolist;

import az.jahangir.todolist.model.ToDoData;
import az.jahangir.todolist.model.ToDoItem;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Controller {

    @FXML
    private ListView<ToDoItem> toDoListView;
    @FXML
    private TextArea itemDetailsTextArea;
    @FXML
    private Label deadlineLabel;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;
    @FXML
    private ComboBox<String> filterComboBox;

    public void initialize() {

        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(event -> {
            ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
            deleteItem(item);
        });

        MenuItem updateMenuItem = new MenuItem("Update");
        updateMenuItem.setOnAction(event -> {
            ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                showEditItemDialog(selectedItem);
            }
        });

        listContextMenu.getItems().addAll(updateMenuItem, deleteMenuItem);

        toDoListView.getSelectionModel().selectedItemProperty().addListener((observableValue, toDoItem, t1) -> {
            if (t1 != null) {
                ToDoItem item = toDoListView.getSelectionModel().getSelectedItem();
                itemDetailsTextArea.setText(item.getDetails());
                DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                deadlineLabel.setText(df.format(item.getDeadline()));
            }
        });

        toDoListView.setItems(ToDoData.getInstance().getToDoItemList());
        toDoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        toDoListView.getSelectionModel().selectFirst();

        toDoListView.setCellFactory(new Callback<>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
                return new ListCell<>() {
                    @Override
                    protected void updateItem(ToDoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                            setContextMenu(null);
                        } else {
                            setText(item.getShortDescription());

                            if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.RED);
                            } else if (item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.ORANGE);
                            } else {
                                setTextFill(Color.BLACK);
                            }
                            setContextMenu(listContextMenu);
                        }
                    }
                };
            }
        });
    }

    @FXML
    public void showNewItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New ToDo Item");
        dialog.setHeaderText("Use this dialog to create a new ToDo item");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("todo-item-dialog.fxml"));

        try {
            dialog.getDialogPane().setContent(loader.load());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = loader.getController();
            ToDoItem newItem = controller.processResults();
            toDoListView.getSelectionModel().select(newItem);
        }
    }

    public void deleteItem(ToDoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete ToDo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or CANCEL to back out.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ToDoItem selectedItem = toDoListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && keyEvent.getCode() == KeyCode.DELETE) {
            deleteItem(selectedItem);
        }
    }

    private void showEditItemDialog(ToDoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit ToDo Item");
        dialog.setHeaderText("Update the selected item");
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("todo-item-dialog.fxml"));

        try {
            dialog.getDialogPane().setContent(loader.load());
            DialogController controller = loader.getController();
            controller.populateFields(item);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return;
        }

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = loader.getController();
            controller.updateItem(item);
            toDoListView.refresh();
            toDoListView.getSelectionModel().select(item);
        }
    }

    @FXML
    public void filterList() {
        String selected = filterComboBox.getValue();
        ObservableList<ToDoItem> allItems = ToDoData.getInstance().getToDoItemList();

        switch (selected) {
            case "All" -> toDoListView.setItems(allItems);
            case "Today" ->
                    toDoListView.setItems(allItems.filtered(item -> item.getDeadline().isEqual(LocalDate.now())));
            case "Upcoming" ->
                    toDoListView.setItems(allItems.filtered(item -> item.getDeadline().isAfter(LocalDate.now())));
        }

        toDoListView.getSelectionModel().selectFirst();
    }
}
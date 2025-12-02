package com.teamlostandfound;

import java.sql.SQLException;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.control.TableRow;
import javafx.beans.value.ChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminPanelController {

    private ItemDao itemDao = new ItemDao();
    private ObservableList<Item> items = FXCollections.observableArrayList();

    @FXML
    private TableColumn<Item, String> actionsCol;

    @FXML
    private TableView<Item> adminTable;

    @FXML
    private TableColumn<Item, String> categoryCol;

    @FXML
    private TableColumn<Item, String> dateCol;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button editBtn;

    @FXML
    private Button homeBtn;

    @FXML
    private TableColumn<Item, String> locationCol;

    @FXML
    private TableColumn<Item, String> nameCol;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchBtn;

    @FXML
    private TableColumn<Item, String> statusCol;

    @FXML
    private TableColumn<Item, String> contactNameCol;

    @FXML
    private TableColumn<Item, String> contactPhoneCol;

    @FXML
    private ComboBox<String> categoryFilter;

    @FXML
    void goHome(ActionEvent event) {
        App.loadScene("LandingPage.fxml", "Landing Page");
    }

    @FXML
    private void initialize() {
        setupTableColumns();
        setupRowFactory();
        setupCategoryFilter();
        setupButtons();
        setupTableSelectionListener();
        loadItemsFromDatabase();
    }
    
    private void setupTableColumns() {
        adminTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        contactNameCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        contactPhoneCol.setCellValueFactory(new PropertyValueFactory<>("contactPhone"));
        
        dateCol.setCellValueFactory(cellData -> {
            Item item = cellData.getValue();
            if (item.getDate() != null) {
                String formattedDate = item.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                return new javafx.beans.property.SimpleStringProperty(formattedDate);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        actionsCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(""));
    }

    private void setupRowFactory() {
        adminTable.setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();

            ChangeListener<Boolean> highlightListener = (obs, oldVal, newVal) -> {
                if (newVal != null && newVal) {
                    row.setStyle("-fx-background-color: lightgoldenrodyellow;");
                } else {
                    row.setStyle("");
                }
            };

            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (oldItem != null) {
                    try {
                        oldItem.highlightedProperty().removeListener(highlightListener);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                if (newItem != null) {
                    if (newItem.isHighlighted()) {
                        row.setStyle("-fx-background-color: lightgoldenrodyellow;");
                    } else {
                        row.setStyle("");
                    }
                    newItem.highlightedProperty().addListener(highlightListener);
                } else {
                    row.setStyle("");
                }
            });

            return row;
        });
    }
    
    private void setupTableSelectionListener() {
        // Disable buttons initially
        editBtn.setDisable(true);
        deleteBtn.setDisable(true);
        
        // Enable/disable buttons based on selection
        adminTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // remove highlight from previously selected item
            if (oldValue != null) {
                try {
                    oldValue.setHighlighted(false);
                } catch (Exception e) {
                    // ignore
                }
            }

            // highlight the newly selected item
            if (newValue != null) {
                newValue.setHighlighted(true);
            }

            boolean hasSelection = newValue != null;
            editBtn.setDisable(!hasSelection);
            deleteBtn.setDisable(!hasSelection);
        });
    }
    
    private void setupCategoryFilter() {
        categoryFilter.getItems().addAll(
            "All Categories",
            "Electronics",
            "Clothing",
            "Books or Documents",
            "Accessories",
            "Others"
        );
        categoryFilter.setValue("All Categories");
        categoryFilter.setOnAction(event -> onCategoryFilterChanged());
    }
    
    private void setupButtons() {
        // Buttons will be enabled/disabled by selection listener
    }
    
    private void loadItemsFromDatabase() {
        try {
            items.clear();
            List<Item> allItems = itemDao.getAllItems();
            items.addAll(allItems);
            adminTable.setItems(items);
        } catch (SQLException e) {
            App.showAlert("Error loading items: " + e.getMessage());
        }
    }

    @FXML
    void onEdit(ActionEvent event) {
        Item selected = adminTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setHighlighted(true);
            adminTable.refresh();
            showEditDialog(selected);
            selected.setHighlighted(false);
            adminTable.refresh();
        }
    }

    @FXML
    void onDelete(ActionEvent event) {
        Item selected = adminTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setHighlighted(true);
            adminTable.refresh();
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Delete");
            confirm.setHeaderText("Delete item");
            confirm.setContentText("Are you sure you want to delete '" + selected.getName() + "'?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    itemDao.deleteItem(selected.getId());
                    items.remove(selected);
                    adminTable.refresh();
                } catch (SQLException e) {
                    App.showAlert("Error deleting item: " + e.getMessage());
                }
            }
            // if still present (user cancelled), remove highlight
            if (items.contains(selected)) {
                selected.setHighlighted(false);
                adminTable.refresh();
            }
        }
    }

    private void showEditDialog(Item selected) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nameField = new TextField(selected.getName());
        TextField categoryField = new TextField(selected.getCategory());
        TextField locationField = new TextField(selected.getLocation());
        DatePicker datePicker = new DatePicker(selected.getDate());
        TextArea descriptionArea = new TextArea(selected.getDescription());
        descriptionArea.setPrefRowCount(3);
        TextField statusField = new TextField(selected.getStatus());
        TextField contactNameField = new TextField(selected.getContactName());
        TextField contactPhoneField = new TextField(selected.getContactPhone());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Category:"), 0, 1);
        grid.add(categoryField, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(locationField, 1, 2);
        grid.add(new Label("Date:"), 0, 3);
        grid.add(datePicker, 1, 3);
        grid.add(new Label("Description:"), 0, 4);
        grid.add(descriptionArea, 1, 4);
        grid.add(new Label("Status:"), 0, 5);
        grid.add(statusField, 1, 5);
        grid.add(new Label("Contact Name:"), 0, 6);
        grid.add(contactNameField, 1, 6);
        grid.add(new Label("Contact Phone:"), 0, 7);
        grid.add(contactPhoneField, 1, 7);

        dialog.getDialogPane().setContent(grid);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == saveButtonType) {
            String dateStr = datePicker.getValue() != null ? datePicker.getValue().toString() : null;
            try {
                itemDao.updateItem(selected.getId(), nameField.getText(), categoryField.getText(), locationField.getText(), dateStr, descriptionArea.getText(), statusField.getText(), contactNameField.getText(), contactPhoneField.getText());

                selected.setName(nameField.getText());
                selected.setCategory(categoryField.getText());
                selected.setLocation(locationField.getText());
                selected.setDate(datePicker.getValue());
                selected.setDescription(descriptionArea.getText());
                selected.setStatus(statusField.getText());
                selected.setContactName(contactNameField.getText());
                selected.setContactPhone(contactPhoneField.getText());

                adminTable.refresh();
            } catch (SQLException e) {
                App.showAlert("Error updating item: " + e.getMessage());
            }
        }
    }

    @FXML
    void onSearch(ActionEvent event) {
        String searchQuery = searchField.getText().trim().toLowerCase();
        if (searchQuery.isEmpty()) {
            loadItemsFromDatabase();
            return;
        }

        String[] keywords = searchQuery.split("\\s+");

        try {
            items.clear();
            List<Item> allItems = itemDao.getAllItems();

            ObservableList<Item> filteredItems = FXCollections.observableArrayList();

            for (Item item : allItems) {
                if (matchesSearchCriteria(item, keywords, searchQuery)) {
                    filteredItems.add(item);
                }
            }

            items.addAll(filteredItems);
            adminTable.setItems(items);

            if (filteredItems.isEmpty()) {
                App.showAlert("No items found matching: " + searchQuery);
            }
        } catch (SQLException e) {
            App.showAlert("Error searching items: " + e.getMessage());
        }
    }

    private void onCategoryFilterChanged() {
        String selectedCategory = categoryFilter.getValue();

        if (selectedCategory.equals("All Categories")) {
            loadItemsFromDatabase();
            return;
        }

        try {
            items.clear();
            List<Item> allItems = itemDao.getAllItems();

            ObservableList<Item> filteredItems = FXCollections.observableArrayList();

            for (Item item : allItems) {
                if (item.getCategory() != null &&
                    item.getCategory().equalsIgnoreCase(selectedCategory)) {
                    filteredItems.add(item);
                }
            }

            items.addAll(filteredItems);
            adminTable.setItems(items);

        } catch (SQLException e) {
            App.showAlert("Error filtering items: " + e.getMessage());
        }
    }

    private boolean matchesSearchCriteria(Item item, String[] keywords, String fullQuery) {
        String name = item.getName() != null ? item.getName().toLowerCase() : "";
        String category = item.getCategory() != null ? item.getCategory().toLowerCase() : "";
        String location = item.getLocation() != null ? item.getLocation().toLowerCase() : "";
        String status = item.getStatus() != null ? item.getStatus().toLowerCase() : "";

        if (name.contains(fullQuery) || category.contains(fullQuery) ||
            location.contains(fullQuery) || status.contains(fullQuery)) {
            return true;
        }

        for (String keyword : keywords) {
            boolean keywordFound = name.contains(keyword) ||
                                  category.contains(keyword) ||
                                  location.contains(keyword) ||
                                  status.contains(keyword);
            if (!keywordFound) {
                return false;
            }
        }

        return true;
}

}
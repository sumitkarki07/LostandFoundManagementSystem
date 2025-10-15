package com.teamlostandfound;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class LandingPageController {

    @FXML
    private Button addFoundBtn;

    @FXML
    private Button addLostBtn;

    @FXML
    private Button adminLoginBtn;

    @FXML
    private TableColumn<?, ?> categoryCol;

    @FXML
    private ComboBox<?> categoryFilter;

    @FXML
    private Button dashboardBtn;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableView<?> itemTable;

    @FXML
    private TableColumn<?, ?> locationCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<?, ?> statusCol;

}

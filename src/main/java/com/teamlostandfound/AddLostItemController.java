package com.teamlostandfound;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class AddLostItemController {

    @FXML
    private ComboBox<?> categoryBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField itemNameField;

    @FXML
    private TextField locationField;

    @FXML
    private Button submitBtn;

}

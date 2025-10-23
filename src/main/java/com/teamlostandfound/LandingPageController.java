package com.teamlostandfound;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LandingPageController {

    private Stage stage;
    private Scene scene;
    private Parent root;
    
    

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

    public void goToLoginPanel(ActionEvent event){
        App.loadScene("LoginPanel.fxml", "Admin Login");
    }

    public void goToAddLostItem(ActionEvent event){
        App.loadScene("AddLostItem.fxml", "Add Lost Item");
    }

    public void goToAddFoundItem(ActionEvent event){
        App.loadScene("AddFoundItem.fxml", "Add Found Item");
    }

    public void goToAdminPanel(ActionEvent event){
        App.loadScene("AdminPanel.fxml", "Admin Panel");
    }

    public void goToLandingPage(ActionEvent event){
        App.loadScene("LandingPage.fxml", "Landing Page");
    }
}

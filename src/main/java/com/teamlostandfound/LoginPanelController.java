package com.teamlostandfound;

import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginPanelController {

    @FXML
    private Button homeBtn;

    @FXML
    private Button loginBtn;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;

    @FXML
    void goHome(ActionEvent event) {
        App.loadScene("LandingPage.fxml", "Lost and Found System");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void handleLogin(ActionEvent event) throws SQLException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        UserDao userDao = new UserDao();
        boolean isValid = userDao.authenticateUser(username, password);

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Input Error", "Please enter both username and password.");
        }
        else if (isValid){
            App.loadScene("AdminPanel.fxml", "Admin Panel");
        }
        
        else{
            showAlert("Invalid Credentials", "Username or password is incorrect.");
            passwordField.clear();
            usernameField.clear();
        }
    }

}

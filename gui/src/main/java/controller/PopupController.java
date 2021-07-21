package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.ResourceBundle;

public class PopupController implements Initializable {

    public PasswordField password;
    public TextField login;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void register(ActionEvent event) {
        if (login.getText().length() >= 5 && password.getText().length() >= 5) {
            Controller.network.sendMsg("/registration " + login.getText() + " " + password.getText());
        } else {
            showAlert();
        }
    }

    public void login(ActionEvent event) {
        if (login.getText().length() >= 5 && password.getText().length() >= 5) {
            Controller.network.sendMsg("/login " + login.getText() + " " + password.getText());
        } else {
            showAlert();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText("Password or login error");
        alert.setContentText("Password and login could not be less then 5 characters!");
        alert.showAndWait();
    }
}

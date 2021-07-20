package controller;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
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
        if (!login.getText().equals("") && !password.getText().equals("")) {
            Controller.network.sendMsg("/registration " + login.getText() + " "+password.getText());
        }
    }

    public void login(ActionEvent event) {
    }
}

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import network.Network;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;


@Slf4j
public class Controller implements Initializable {

    @FXML
    public TextArea Client_text_area;
    public ListView serverView;
    public ListView clientView;

    private String root = "gui/clientFiles";
    private String HOST = "localhost";
    private int PORT = 8181;

    private Network network;

    private FileChooser fileChooser;
    private FileMessage fm;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();
        network = new Network();
    }

    public void send(ActionEvent actionEvent) throws IOException {
        if (fm.getFile() != null)
        network.send(fm);
    }

    public void chose(ActionEvent event) {
        fm = new FileMessage();
        fileChooser.setTitle("Chose file");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All files", "*.html", "*.jpg",
                        "*.jfif", "*.png", "*.txt", "*.mpeg4", "*.mp3", "*.wav", "*.docx", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);

        fm.setFile(
                new File(String.valueOf(
                        fileChooser.showOpenDialog(Client_text_area.getScene().getWindow()))));
        fm.setName(fm.getFile().getName());
        fm.setSize(fm.getFile().length());
    }

    public void refresh(ActionEvent event) {
        network.sendMsg("/refresh");
    }

    public void download(ActionEvent event) {
        network.sendMsg("filename");
    }

    public void refreshServerView(String[] list) {
        System.out.println(Arrays.toString(list));
        serverView.getItems().clear();
        serverView.getItems().addAll(list);
    }


}

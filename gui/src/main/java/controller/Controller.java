package controller;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import model.DeleteFileMessage;
import model.FileMessage;
import network.Network;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;


@Slf4j
@Getter
public class Controller implements Initializable {

    @FXML
    public ListView<String> serverView;
    public ListView<String> clientView;
    public TextArea serverText;
    public TextArea clientText;

    private String clientFiles = "gui/clientFiles/";
    private String HOST = "localhost";
    private int PORT = 8181;

    private Network network;

    private FileChooser fileChooser;
    private FileMessage fm;

    private File dir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();
        network = new Network();
        dir = new File(clientFiles);
        if (!dir.exists()) dir.mkdir();

        addViewListener(serverView,serverText);
        addViewListener(clientView,clientText);
    }

    private void addViewListener(ListView<String> lv, TextArea ta) {
        lv.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    try {
                        ta.clear();
                        ta.appendText(lv.getSelectionModel().getSelectedItem());
                    } catch (NullPointerException ignored) {}
                });
    }

    public void download(ActionEvent event) {
        if (!serverText.getText().equals("")) {
            network.sendMsg(serverText.getText());
        }
        refreshAll();
    }

    public void send(ActionEvent actionEvent) {
        if (fm != null && fm.getFile() != null) {
            network.send(fm);
        } else if (fm == null && !clientText.getText().equals("")) {
            fm = new FileMessage();
            File toSend = new File(clientFiles + clientText.getText());
            fm.setFile(toSend);
            fm.setName(toSend.getName());
            fm.setSize(toSend.length());
            network.send(fm);
            fm = null;
        }
        refreshAll();
    }

    public void deleteFileFromClient(ActionEvent actionEvent) {
        if (!clientText.getText().equals("")) {
            File delete = new File(clientFiles + clientText.getText());
            delete.delete();
        }
        refreshAll();
    }

    public void deleteFileFromServer(ActionEvent actionEvent) {
        if (!serverText.getText().equals("")) {
            network.delete(new DeleteFileMessage(serverText.getText()));
        }
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
                        fileChooser.showOpenDialog(clientText.getScene().getWindow()))));
        fm.setName(fm.getFile().getName());
        fm.setSize(fm.getFile().length());
        setClientText(fm.getName());
    }

    private void setClientText(String text) {
        clientText.clear();
        clientText.setText(text);
    }

    public void refreshAll() {
        refreshClient();
        refresh();
    }

    public void refresh() {
        network.sendMsg("/refresh");
        refreshClient();
    }

    private void refreshClient() {
        clientView.getItems().clear();
        clientView.getItems().addAll(dir.list());
    }

}

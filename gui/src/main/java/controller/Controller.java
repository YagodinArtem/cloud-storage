package controller;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import model.DeleteFileMessage;
import model.FileMessage;
import network.Network;
import start.App;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;
import java.util.ResourceBundle;


@Slf4j
@Getter
public class Controller implements Initializable {

    @FXML
    public ListView<String> serverView;
    public ListView<String> clientView;
    public TextArea serverText;
    public TextArea clientText;
    public TextArea clientCurrentFolder;
    public TextArea serverCurrentFolder;
    public MenuItem reg;
    public MenuItem exit;

    private String clientFiles = "clientFiles";
    private String HOST = "localhost";
    private int PORT = 8181;

    public static Network network;
    public String userName;
    public String password;
    public String userId;

    private FileChooser fileChooser;
    private FileMessage fm;

    private File dir;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();

        network = new Network(
                (FileMessage fm) -> {
                    Files.copy(fm.getFile().toPath(), new File(clientFiles + "\\" + fm.getName()).toPath());
                    Platform.runLater(this::refresh);
                },

                (String[] list) -> Platform.runLater(() -> {
                    if (list[0].equals("/registration") || list[0].equals("/login")) {
                        userName = list[1];
                        password = list[2];
                        userId = list[3];
                        refresh();
                    } else if (list[0].equals("/refresh")) {
                        getServerView().getItems().clear();
                        getServerView().getItems().addAll(Arrays.copyOfRange(list, 1, list.length));
                    }
                }));

        File temp = new File(clientFiles);
        dir = new File(temp.getAbsolutePath());
        if (!dir.exists()) dir.mkdir();

        addViewListener(serverView, serverText);
        addViewListener(clientView, clientText);
        addDialogActionListener();

    }

    private void addViewListener(ListView<String> lv, TextArea ta) {
        lv.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    try {
                        ta.clear();
                        ta.appendText(lv.getSelectionModel().getSelectedItem());
                    } catch (NullPointerException ignored) {
                    }
                });
    }

    public void download(ActionEvent event) {
        if (!serverText.getText().equals("")) {
            network.sendMsg("/download " + serverText.getText() + " " + userId);
        }
        refresh();
    }

    public void send(ActionEvent actionEvent) {
        if (fm != null && fm.getFile() != null) {
            network.send(fm);
        } else if (fm == null && !clientText.getText().equals("")) {
            fm = new FileMessage();
            File toSend = new File(dir.getAbsolutePath() + "\\" + clientText.getText());
            fm.setFile(toSend);
            fm.setName(toSend.getName());
            fm.setSize(toSend.length());
            fm.setFileOwner(userId);
            network.send(fm);
            fm = null;
        }
        refresh();
    }

    public void deleteFileFromClient(ActionEvent actionEvent) {
        if (!clientText.getText().equals("")) {
            File delete = new File(clientCurrentFolder.getText() + "\\" + clientText.getText());
            delete.delete();
        }
        refresh();
    }

    public void deleteFileFromServer(ActionEvent actionEvent) {
        if (!serverText.getText().equals("")) {
            network.delete(new DeleteFileMessage(serverText.getText()));
        }
        refresh();
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
        fm.setFileOwner(userId);
        setClientText(fm.getName());
    }

    private void setClientText(String text) {
        clientText.clear();
        clientText.setText(text);
    }

    public void refresh() {
        network.sendMsg("/refresh " + userId);
        refreshClient();
    }

    private void refreshClient() {
        clientView.getItems().clear();
        clientView.getItems().addAll(dir.list());
        clientCurrentFolder.clear();
        clientCurrentFolder.appendText(dir.getAbsolutePath());
    }

    public void dirRight(ActionEvent event) {
        if (Paths.get(dir.getAbsolutePath() +
                "\\" + clientText.getText()).toFile().isDirectory()) {
            dir = Paths.get(dir + "\\" + clientText.getText()).toFile();
            System.out.println(dir.getAbsolutePath());
            refreshClient();
        }
    }

    public void dirLeft(ActionEvent event) {
        if (dir.getParent() != null) {
            dir = new File(dir.getParent());
        } else {
            dir = new File(System.getProperty("user.home"));
        }
        refreshClient();
    }

    public void enterPath(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            try {
                dir = new File(clientCurrentFolder.getText());
                refreshClient();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addDialogActionListener() {
        reg.setOnAction(
                new EventHandler<ActionEvent>() {
                    @SneakyThrows
                    @Override
                    public void handle(ActionEvent event) {
                        final Stage dialog = new Stage();
                        dialog.initModality(Modality.APPLICATION_MODAL);
                        dialog.initOwner(App.ps);
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("popup.fxml"));
                        Parent parent = loader.load();
                        dialog.setScene(new Scene(parent));
                        dialog.show();
                    }
                });
    }

    public void registration(ActionEvent event) {
        addDialogActionListener();
    }

    public void exit(ActionEvent event) {
        String propsPath = System.getProperty("user.home") + "/cloud-storage/prop.properties";
        File props = new File(propsPath);

        Properties prop = new Properties();

        prop.setProperty("login", "");
        prop.setProperty("password", "");
        try {
            FileOutputStream fos = new FileOutputStream(props);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        userName = "";
        password = "";
        userId = "0";

        refresh();
    }

}

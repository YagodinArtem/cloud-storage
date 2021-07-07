import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ResourceBundle;

public class UiController implements Initializable {

    private FileChooser fileChooser;

    private DataInputStream dis;
    private DataOutputStream dos;
    private FileInputStream fis;
    private byte[] buffer;

    private Socket socket;

    private File toSend;
    private File dir;
    private static final String fileStorage = "./client/files/";

    private static final int PORT = 8181;
    private static final String SERVER_IP = "localhost";

    public ListView<String> listView;

    private static final Logger LOG = LogManager.getLogger(UiApp.class.getName());

    @FXML
    private TextField inputLine;

    @FXML
    private TextArea status;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fileChooser = new FileChooser();
        buffer = new byte[1024];
        connect();
        reloadListView();
        listView.getSelectionModel()
                .selectedItemProperty()
                .addListener((ObservableValue<? extends String> ov, String old_val, String new_val) -> {
                    try {
                        inputLine.clear();
                        inputLine.appendText(listView.getSelectionModel().getSelectedItem());
                    } catch (NullPointerException ignored) {}
                });
    }

    private void connect() {
        try {
            dir = new File(fileStorage);
            if (!dir.exists()) dir.mkdir();
            socket = new Socket(SERVER_IP, PORT);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            LOG.trace("Client started");
            statusEdit("Connected to server");
        } catch (IOException e) {
            LOG.error("Unable to start client, possible server is down");
            statusEdit("Server is down");
        } catch (NullPointerException ignored) {

        }
    }

    private void sendFile() {
        try {
            fis = new FileInputStream(toSend);

            dos.writeUTF("/^transfer");
            dos.flush();

            if (dis.readUTF().contains("/^ready")) {

                dos.writeUTF(toSend.getName());
                dos.flush();

                dos.writeLong(toSend.length());
                dos.flush();

                String prop = toSend.getName() + " size: " + toSend.length();
                LOG.trace("Transfer started - " + prop);
                statusEdit("Transfer started - " + prop);
                while ((fis.read(buffer)) >= 0) {
                    dos.write(buffer);
                }
                dos.flush();
                LOG.trace("Transfer finished successfully " + prop);
                statusEdit("Transfer finished successfully " + prop);
                reloadListView();
            }
        }  catch (IOException e) {
            LOG.error("Exception I/O when sending file");
            statusEdit("Unable to transfer file");
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                LOG.error("Unable to close FIS");
            }
        }
    }

    public void sendFile(MouseEvent mouseEvent) {
        sendOperation();
    }

    public void send(ActionEvent actionEvent) {
        sendOperation();
    }

    private void sendOperation() {
        if (toSend != null) {
            saveFileInLocalStorage();
            sendFile();
        }
        toSend = null;

    }

    public void choseFile(MouseEvent mouseEvent) {
        fileChooser.setTitle("Chose file");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All files", "*.html", "*.jpg",
                        "*.jfif", "*.png", "*.txt", "*.mpeg4", "*.mp3", "*.wav", "*.docx", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        toSend = new File(String.valueOf(fileChooser.showOpenDialog(inputLine.getScene().getWindow())));
        inputLine.clear();
        inputLine.appendText(toSend.getPath());
    }

    private void saveFileInLocalStorage() {
        try {
            Path toCopy = Paths.get(fileStorage + toSend.getName());
            Files.copy(toSend.toPath(), toCopy, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("Unable to copy file in local storage");
        }
    }

    private void reloadListView() {
        listView.getItems().clear();
        listView.getItems().addAll(dir.list());
    }

    private void statusEdit(String msg) {
        status.appendText(msg);
        status.appendText("\n");
    }

    public void reconnect(MouseEvent mouseEvent) {
        connect();
        inputLine.clear();
    }
}
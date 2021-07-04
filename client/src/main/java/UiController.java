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
import java.util.ResourceBundle;

public class UiController implements Initializable {

    private FileChooser fileChooser;

    private DataInputStream dis;
    private DataOutputStream dos;
    private FileInputStream fis;
    private byte[] buffer;

    private File toSend;

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
        try {
            File dir = new File("./");
            listView.getItems().clear();
            listView.getItems().addAll(dir.list());
            Socket socket = new Socket(SERVER_IP, PORT);

            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            LOG.trace("Client started");
            statusEdit("Connected to server");
        } catch (IOException e) {
            LOG.error("Unable to start client, possible server is down");
            statusEdit("Server is down");
        }
    }


    private void sendFile() {
        try {
            fis = new FileInputStream(toSend);
            int count;

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
            }
        } catch (IOException e) {
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


    public void choseFile(MouseEvent mouseEvent) {
        fileChooser.setTitle("Chose file");
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All files", "*.html", "*.jpg",
                        "*.jfif", "*.png", "*.txt", "*.mpeg4", "*.mp3", "*.wav", "*.docx", "*.xlsx", "*.xls");
        fileChooser.getExtensionFilters().add(extFilter);
        toSend = new File(String.valueOf(fileChooser.showOpenDialog(inputLine.getScene().getWindow())));
    }

    private void statusEdit(String msg) {
        status.appendText(msg);
        status.appendText("\n");
    }

    public void sendFile(MouseEvent mouseEvent) {
        if (toSend != null) sendFile();
    }

    public void send(ActionEvent actionEvent) {
        if (toSend != null) sendFile();
    }

    public void reconnect(MouseEvent mouseEvent) {
        initialize(null, null);
    }
}
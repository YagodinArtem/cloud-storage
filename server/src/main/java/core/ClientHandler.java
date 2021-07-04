package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.JsonUtils;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientHandler implements Runnable {

    private Socket socket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private FileOutputStream fos;

    private byte[] buffer;

    private static final Logger LOG = LogManager.getLogger(ClientHandler.class.getName());
    private static final String fileStorage = "./server/files/";

    public ClientHandler(Socket socket) {
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            LOG.error("Unable to open DIS / DOS");
        }
        buffer = new byte[1024];
        File file = new File(fileStorage);
        if (!file.exists()) file.mkdir();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String fromClient = dis.readUTF();
                if (fromClient.equals("/^transfer")) {
                    receive();
                } else {
                    System.out.println("ждем");
                }
                System.out.println("я вышел");
            }
        } catch (StringIndexOutOfBoundsException e) {
            LOG.trace("Client disconnected " + this);
        } catch (IOException e) {
           LOG.trace("IOE");
        } finally {
            try {
                dis.close();
                dos.close();
            } catch (IOException e) {
                LOG.error("Unable to close streams <ClientHandler> " + this);
            }
        }
    }

    private void receive() {
        System.out.println("я тут ресиве");
        try {
            dos.writeUTF("/^ready");
            dos.flush();

            String fileName = dis.readUTF();

            File file = new File(fileStorage + fileName);
            fos = new FileOutputStream(file);

            long fileLength = dis.readLong();
            int count;
            long size = 0;

            if(fileLength != 0) {
                while ((count = dis.read(buffer)) >= 0) {
                    fos.write(buffer, 0, count);
                    if ((size += count) >= fileLength) break;
                }
                fos.flush();
            }
        } catch (SocketException e) {
            LOG.trace("Connection interrupted " + this);
        } catch (IOException e) {
            LOG.trace("IO exception when receive file");
        }
    }
}

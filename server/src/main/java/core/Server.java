package core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static final Logger LOG = LogManager.getLogger(Server.class.getName());

    public Server() {
        try (ServerSocket socket = new ServerSocket(8181)) {
            LOG.trace("Server started");
            while (true) {
                LOG.info("Awaiting client connection");
                Socket clientSocket = socket.accept();
                LOG.info("Client connected");
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            LOG.error("Unable to open socket");
        } finally {
            // some future code
        }
    }
}

package ru.core;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class Terminal {

    private static int cnt = 1;
    private ServerSocketChannel sc;
    private Selector selector;
    private String name = "user";

    private Path currentFolder;

    public Terminal() throws IOException {
        sc = ServerSocketChannel.open();
        selector = Selector.open();
        sc.bind(new InetSocketAddress(8189));
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_ACCEPT);
        currentFolder = Paths.get("./");

        while (sc.isOpen()) {
            selector.select();
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectedKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = sc.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ, name + cnt);
        cnt++;
    }

    private void handleRead(SelectionKey key) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        SocketChannel channel = (SocketChannel) key.channel();
        String name = (String) key.attachment();
        int read;
        StringBuilder sb = new StringBuilder();
        while (true) {
            read = channel.read(buffer);
            buffer.flip();
            if (read == -1) {
                channel.close();
                break;
            }
            if (read > 0) {
                while (buffer.hasRemaining()) {
                    sb.append((char) buffer.get());
                }
                buffer.clear();
            } else {
                break;
            }
        }
        System.out.println("received: " + sb);
        for (SelectionKey selectionKey : selector.keys()) {
            if (selectionKey.isValid() && selectionKey.channel() instanceof SocketChannel) {
                SocketChannel ch = (SocketChannel) selectionKey.channel();

                if (sb.toString().trim().equals("ls")) {
                    ls(ch);
                }

                if (sb.toString().trim().contains("cat")) {
                    cat(sb, ch);
                }

                if (sb.toString().trim().contains("cd ")) {
                    String[] temp = sb.toString().trim().split(" ");
                    String[] path = currentFolder.toAbsolutePath().toString().split("\\\\");
                    if (temp[1].equals("..")) {
                        StringBuilder b = new StringBuilder();
                        for (int i = 0; i < path.length-1; i++) {
                            b.append(path[i]).append("\\\\");
                        }
                        currentFolder = Paths.get(b.toString());
                    }

                    if (temp[1].contains("\\")) {
                        currentFolder = Paths.get(currentFolder.toString() + temp[1]);
                    }
                    sendMsg(ch,currentFolder.toString());
                }

            }
        }
    }

    private void cat(StringBuilder sb, SocketChannel ch) throws IOException {
        try {
            String[] temp = sb.toString().trim().split(" ");
            ch.write(ByteBuffer.wrap((Files.readAllBytes(Paths.get(temp[1])))));
        } catch (NoSuchFileException e) {
            sendMsg(ch, ("@NoSuchFile").trim());
            sendMsg(ch, "\n\r");
        } catch (AccessDeniedException e) {
            sendMsg(ch, ("@AccessDenied").trim());
            sendMsg(ch, "\n\r");
        }
    }

    private void ls(SocketChannel ch) throws IOException {
        List<Path> list = Files.walk(Paths.get(currentFolder.toUri()))
                .collect(Collectors.toList());
        for (Path path : list) {
            sendMsg(ch, (path.getFileName().toString()).trim());
            sendMsg(ch, "\n\r");
        }
    }

    private void sendMsg(SocketChannel ch, String s) throws IOException {
        ch.write(ByteBuffer.wrap((s).getBytes(StandardCharsets.UTF_8)));
    }

}

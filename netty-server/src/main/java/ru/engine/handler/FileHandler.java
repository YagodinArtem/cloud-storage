package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<FileMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileMessage fileMessage)  {

        log.debug(fileMessage.getName() + fileMessage.getSize());

        try {
            copyFile(fileMessage.getFile(), new File("netty-server/filesServer/" + fileMessage.getName()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }
}

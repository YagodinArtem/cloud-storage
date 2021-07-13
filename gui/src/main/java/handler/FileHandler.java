package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import start.App;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<FileMessage> {

    private String clientFiles= "gui/clientFiles/";
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileMessage fm) throws Exception {
        copyFile(fm.getFile(),new File(clientFiles + fm.getName()));
        Platform.runLater(() -> App.controller.refreshAll());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("ClientFileHandler " + cause);
    }

    private static void copyFile(File source, File dest) throws IOException {
        Files.copy(source.toPath(), dest.toPath());
    }
}

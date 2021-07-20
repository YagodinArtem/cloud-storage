package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import ru.engine.database.SaveFileCallback;

@Slf4j
public class FileHandler extends SimpleChannelInboundHandler<FileMessage> {

    private SaveFileCallback saveFileCallback;

    public FileHandler(SaveFileCallback saveFileCallback) {
        this.saveFileCallback = saveFileCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileMessage fileMessage)  {

        log.debug(fileMessage.getName() + fileMessage.getSize());
        saveFileCallback.call(fileMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
    }
}

package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import model.DeleteFileMessage;
import ru.engine.database.callback.DeleteFileCallback;
import ru.engine.server.Server;

import java.io.File;
import java.util.Objects;


@Log4j
public class DeleteFileHandler extends SimpleChannelInboundHandler<DeleteFileMessage> {

    private DeleteFileCallback deleteFileCallback;

    public DeleteFileHandler(DeleteFileCallback deleteFileCallback) {
        this.deleteFileCallback = deleteFileCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeleteFileMessage dfm) {
        log.debug(dfm.getName() + " - to delete");
        deleteFileCallback.call(dfm);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("DeleteFileHandler " + cause);
    }
}

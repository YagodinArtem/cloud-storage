package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j;
import model.DeleteFileMessage;
import ru.engine.server.Server;

import java.io.File;
import java.util.Objects;


@Log4j
public class DeleteFileHandler extends SimpleChannelInboundHandler<DeleteFileMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DeleteFileMessage dfm){
        for (String fileName : Objects.requireNonNull
                (new File(Server.filesServer).list())) {
            if (fileName.contains(dfm.getName())) {
                File delete = new File(Server.filesServer + fileName);
                delete.delete();
                ctx.writeAndFlush(new File(Server.filesServer).list());
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.debug("DeleteFileHandler " + cause);
    }
}

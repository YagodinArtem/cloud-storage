package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import ru.engine.server.Server;

import java.io.File;
import java.util.Objects;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("Received = " + s);

        if (s.equals("/refresh")) {
            ctx.writeAndFlush(new File(Server.filesServer).list());
        } else {
            for (String fileName : Objects.requireNonNull
                    (new File(Server.filesServer).list())) {
                if (fileName.contains(s)) {
                    File f = new File(Server.filesServer + fileName);
                    ctx.writeAndFlush(new FileMessage(f, f.getName(), f.length()));
                }
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("ServerMessageHandler " + cause);
    }
}

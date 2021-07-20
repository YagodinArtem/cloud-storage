package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;
import ru.engine.database.CommandCallback;
import ru.engine.server.Server;

import java.io.File;
import java.util.Objects;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<String> {

    private CommandCallback commandCallback;

    public MessageHandler(CommandCallback commandCallback) {
        this.commandCallback = commandCallback;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("Received = " + s);
        commandCallback.call(ctx, s);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("ServerMessageHandler " + cause);
    }
}

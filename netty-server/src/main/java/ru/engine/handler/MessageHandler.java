package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import model.FileMessage;

import java.io.File;

@Slf4j
public class MessageHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("Received = " + s);
        if (s.equals("1.txt"))
            ctx.writeAndFlush(new FileMessage(new File("netty-server/filesServer/1.txt"), "1.txt", 5));
        if (s.equals("/refresh"))
            ctx.writeAndFlush(new File("netty-server/filesServer").list());

    }
}

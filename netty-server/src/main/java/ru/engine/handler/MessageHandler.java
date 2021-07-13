package ru.engine.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.FileMessage;

import java.io.File;

public class MessageHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        if (s.equals("1.txt"))
        ctx.writeAndFlush(new FileMessage(new File("netty-server/filesServer/1.txt"), "1.txt", 5));
    }
}

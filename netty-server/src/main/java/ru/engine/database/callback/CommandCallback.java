package ru.engine.database.callback;

import io.netty.channel.ChannelHandlerContext;

public interface CommandCallback {

    void call(ChannelHandlerContext ctx, String s);
}

package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import model.FileMessage;

public class FileHandler extends SimpleChannelInboundHandler<FileMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FileMessage fileMessage) throws Exception {
        System.out.println(fileMessage.getName() + "Мы получили файл с сервера");
    }
}

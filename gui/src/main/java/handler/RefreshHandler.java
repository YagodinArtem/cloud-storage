package handler;

import controller.Controller;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.util.Arrays;


public class RefreshHandler extends SimpleChannelInboundHandler<String[]> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String[] list) throws Exception {
        //TODO somehow refresh serverView
    }
}

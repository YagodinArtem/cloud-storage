package handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import lombok.extern.slf4j.Slf4j;
import start.App;

@Slf4j
public class RefreshHandler extends SimpleChannelInboundHandler<String[]> {

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String[] list) {
        Platform.runLater(() -> {
            App.controller.getServerView().getItems().clear();
            App.controller.getServerView().getItems().addAll(list);
        });
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.debug("ClientRefreshHandler " + cause);
    }
}

package ru.engine.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import ru.engine.database.CommandCallback;
import ru.engine.database.SaveFileCallback;
import ru.engine.handler.DeleteFileHandler;
import ru.engine.handler.FileHandler;
import ru.engine.handler.MessageHandler;

import java.util.Date;

@Slf4j
public class Server {

    private int PORT = 8181;
    public static String filesServer = "netty-server/filesServer/";

    public Server(SaveFileCallback saveFileCallback,
                  CommandCallback commandCallback) {
        EventLoopGroup auth = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(auth, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ObjectEncoder(),
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new FileHandler(saveFileCallback),
                                    new MessageHandler(commandCallback),
                                    new DeleteFileHandler()
                            );
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            log.debug("Server started " + new Date());
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            auth.shutdownGracefully();
            worker.shutdownGracefully();
        }
    }
}

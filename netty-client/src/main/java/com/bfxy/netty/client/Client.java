package com.bfxy.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class Client {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    private EventLoopGroup group = new NioEventLoopGroup(2);
    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8765;
    private Channel channel;
    public Client(){
        this.connect(HOST,PORT);
    }

    private void connect(String host, int port) {

        //配置客户端NIO线程组
        try {

            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {

                        }
                    });
            // 发起异步连接操作
            ChannelFuture future = b.connect(host, port).sync();
            Channel channel = future.channel();
            System.out.println("Client Start.. ");
            this.channel.closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            // 所有资源释放完成之后,清空资源,再次发起重连接操作
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        // 发起重连接操作
                        connect(host,port);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }
}

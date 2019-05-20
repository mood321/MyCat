package com.mood.framework.minicat.service;

import com.mood.framework.minicat.service.serviceInterface.Service;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

public class NettyService implements Service {
    private int port;
    private Integer timeOut;

    @Override
    public void openProt(Integer prot, Integer timeOut) throws IOException {
        this.port = prot;
        this.timeOut = timeOut;
    }

    @Override
    public void doService() throws IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try {
            //创建启动
            ServerBootstrap boot=new ServerBootstrap();
            /** 添加*/
            boot.group(group,work)
                    /** 声明nio 模式*/
                    .channel(NioServerSocketChannel.class)
                    /** 打开端口*/
                    .localAddress(new InetSocketAddress(port))

                    /*接收到连接请求，新启一个socket通信，也就是channel，每个channel
                     * 有自己的事件的handler*/
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new NettyHandler())
                            //把多个消息转换为一个单一的FullHttpRequest或是FullHttpResponse
                                    .addLast(new HttpObjectAggregator(65536))
                                    //压缩Http消息
//						    .addLast(new HttpChunkContentCompressor())
                                    //大文件支持
                            //        .addLast(new ChunkedWriteHandler());
                            //http响应编码
                            .addLast("encode",new HttpResponseEncoder())
                            //http请求编码
                            .addLast("decode",new HttpRequestDecoder());
                            ;
                            ch.pipeline().addLast("idleStateHandler",new IdleStateHandler(timeOut,timeOut,timeOut, TimeUnit.MILLISECONDS));
                        }
                    });
            boot.bind().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

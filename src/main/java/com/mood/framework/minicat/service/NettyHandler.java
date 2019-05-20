package com.mood.framework.minicat.service;

import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.init.NettyBuilder;
import com.mood.framework.minicat.init.abs.HttpBulider;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import java.nio.charset.Charset;

public class NettyHandler extends ChannelInboundHandlerAdapter {
    private static Log log = LogFactory.getLog(NettyHandler.class);

    HttpBulider builder;
    StringBuilder res = new StringBuilder();


    /*** 服务端读取到网络数据后的处理*/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* ByteBuf buf = (ByteBuf)msg;
         byte[] req = new byte[buf.readableBytes()]；
         buf.readBytes(req);
         String body = new String(req,"UTF-8");*/
        if ( msg != null) {
            ByteBuf byteBuf = (ByteBuf) msg;
            res.append(byteBuf.toString(CharsetUtil.UTF_8));
        }

    }


    /*** 服务端读取完成网络数据后的处理*/
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx)
            throws Exception {
     builder = new NettyBuilder(res, ctx);
        builder.builder();
        builder.flushAndClose();

    
      //  res = new StringBuilder();
      ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)//*flush掉所有的数据*//*
           .addListener(ChannelFutureListener.CLOSE);//*当flush完成后，关闭连接*//*
    }

    /*** 发生异常后的处理*/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        ctx.close();
    }


}

package com.mood.framework.minicat.service;

import com.mood.framework.init.NIOBulider;
import com.mood.framework.init.abs.HttpBulider;
import com.mood.framework.minicat.service.serviceInterface.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class NIOService implements Service {
    private Selector service;

    @Override
    public void openProt(Integer prot, Integer timeOut) throws IOException {
        // 打开选择器
        service = Selector.open();
        //打开通道 ServerSocketChannel 是一个通道管理类 类似serviceSocket
        ServerSocketChannel server = ServerSocketChannel.open();
        //设置端口
        server.socket().bind(new InetSocketAddress(prot));
        //设置是否阻塞
        server.configureBlocking(false);
        //像通道注册一个 连接事件
        server.register(service, SelectionKey.OP_ACCEPT);
        //一个socket 的超时时间
        server.socket().setSoTimeout(timeOut);

    }

    @Override
    public void doService() throws IOException {
        while (true) {
            service.select();
            Iterator<SelectionKey> selectionKeys = service.selectedKeys().iterator();
            while (selectionKeys.hasNext()) {
                final SelectionKey skey = selectionKeys.next();
                selectionKeys.remove();
                process(skey);
            }
        }

    }

    private void process(SelectionKey skey) throws IOException {
        if (skey.isAcceptable()) { // 接收请求
            acceptable(skey);
            return;
        }
        if (skey.isReadable()) { // 读信息
            readable(skey);
            return;
        }
        if (skey.isWritable()) { // 写事件
            writable(skey);
        }
    }

    private void writable(SelectionKey skey) {
        SocketChannel channel = (SocketChannel)skey.channel();
        HttpBulider builder=(HttpBulider)skey.attachment();
        builder.flushAndClose();
        try {
            channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readable(SelectionKey skey) throws  ClosedChannelException{
        SocketChannel channel = (SocketChannel)skey.channel();
        HttpBulider builder=new NIOBulider(channel);
        builder.builder();
        SelectionKey sKey = channel.register(service, SelectionKey.OP_WRITE);
        sKey.attach(builder);
    }

    private void acceptable(SelectionKey skey) throws IOException {
        //获得一个channel 通道管理
        final ServerSocketChannel server = (ServerSocketChannel) skey.channel();
        //侦听连接请求
        SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        channel.register(service, SelectionKey.OP_READ);
    }
}

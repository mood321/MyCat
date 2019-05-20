package com.mood.framework.minicat.init;

import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.exception.BadRequestException;
import com.mood.framework.minicat.init.abs.HttpBulider;
import com.mood.framework.minicat.util.ByteUtils;
import com.mood.framework.minicat.util.StringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;

import javax.naming.Context;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class NettyBuilder extends HttpBulider {
    StringBuilder res;
    ChannelHandlerContext ctx;
    /**
     * 接受 byteBuf 传参
     */
    public NettyBuilder(StringBuilder res, ChannelHandlerContext ctx) {
this.ctx=ctx;
        this.res = res;
    }

    @Override
    public void buildRequestHeader() throws IOException {
        if (res == null) {
            throw new BadRequestException("错误的请求报文");
        }
        try {
            String headContext = "";//请求头
            String bodyContext = null;//请求体
            String context = this.res.toString();
            if (context.contains(splitFlag)) {
                headContext = context.substring(0, context.indexOf(splitFlag) + splitFlag.length());
                bodyContext = context.substring(context.indexOf(splitFlag) + splitFlag.length());
            }

            String[] headers = headContext.split("\r\n");
            String line = headers[0];
            while (line.contains("  ")) {
                line.replace("  ", " ");
            }
            String[] lineVang = line.trim().split(" ");

            if (lineVang.length != 3) {
                throw new BadRequestException("错误的请求报文");
            }
            request.setMethod(lineVang[0]);
            String requestURI = lineVang[1];
            if (requestURI.contains("?")) {
                int index = requestURI.indexOf("?");
                if (index < requestURI.length() - 1) {
                    request.setQueryString(requestURI.substring(index + 1));
                }
                requestURI = requestURI.substring(0, index);
                if("/".equals(requestURI)){
                    requestURI=MoodCatConfig.WELCOME_PATH;
                }
            }
            request.setRequestURI(requestURI);
            request.setProtocol(lineVang[2]);
            //处理特殊的请求头
            for (int i = 1; i < headers.length; i++) {
                String header = headers[i];
                int index = header.indexOf(":");
                if (index < 1) {
                    throw new BadRequestException("错误的请求头部:" + line);
                }
                String name = header.substring(0, index).trim();
                String value = header.substring(index + 1).trim();
                if (StringUtil.hasNull(name, value)) {
                    continue;
                }
                request.setHeader(name, value);
                if (name.equals("Content-Encoding")) {
                    if (value.contains("gzip")) {
                        request.setGzip(true);
                    }
                }
                if (name.equals("Host")) {
                    String basePath = request.getScheme() + "://" + value;
                    if (requestURI.startsWith(basePath)) {
                        requestURI = requestURI.substring(basePath.length());
                        request.setRequestURI(requestURI);
                    }
                }
                if (name.equals("Content-Length")) {
                    request.setContextLength(Integer.valueOf(value));
                }
            }

            try {

                if (!StringUtil.isNullOrEmpty(bodyContext)) {
                    request.setInputStream(new ByteArrayInputStream(bodyContext.getBytes(MoodCatConfig.ENCODE)));
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] bodyData = bodyContext.getBytes(MoodCatConfig.ENCODE);
                    byteArrayOutputStream.write(bodyData);


                    request.setInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                } finally {
                    byteArrayOutputStream.close();
                }
            } catch (BadRequestException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void buildRequest() {
        this.request = new HttpServletRequest();
    }

    @Override
    protected void flush() throws IOException {
        byte[] bytes = response.getOutputStream().toByteArray();
        if (StringUtil.isNullOrEmpty(bytes)) {
            return;
        }
        ctx.write(Unpooled.copiedBuffer(bytes));

    }
}

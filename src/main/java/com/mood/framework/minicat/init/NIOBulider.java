package com.mood.framework.minicat.init;

import com.mood.framework.minicat.exception.BadRequestException;
import com.mood.framework.minicat.exception.RequestNotInitException;
import com.mood.framework.minicat.init.abs.HttpBulider;
import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.util.ByteUtils;
import com.mood.framework.minicat.util.StringUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NIOBulider extends HttpBulider {
    private SocketChannel channel;

    public NIOBulider(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public void buildRequestHeader() throws IOException {
        if (request == null) {
            throw new RequestNotInitException("Request尚未初始化");
        }
        try {
            /**
             * 接受header
             */
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(MoodCatConfig.MAX_HEADER_LENGTH);
            int count = channel.read(byteBuffer);
            if (count < 1) {
                throw new BadRequestException("错误的请求报文");
            }
            boolean isReadEnd = count < MoodCatConfig.MAX_HEADER_LENGTH;
            byteBuffer.flip();
            byte[] headerData = new byte[byteBuffer.remaining()];
            byteBuffer.get(headerData, 0, headerData.length);
            String headerContext = new String(headerData, "iso-8859-1");
            String bodyContext = null;
            if (headerContext.contains(splitFlag)) {
                bodyContext = headerContext.substring(headerContext.indexOf(splitFlag) + splitFlag.length());
                headerContext = headerContext.substring(0, headerContext.indexOf(splitFlag));
            }
            headerContext += splitFlag;
            String[] headers = headerContext.split("\r\n");
            if (headers.length < 2) {
                throw new BadRequestException("错误的请求报文");
            }
            String line = headers[0];
            while (line.contains("  ")) {
                line = line.replace("  ", " ");
            }
            String[] vanguards = line.trim().split(" ");
            if (vanguards.length != 3) {
                throw new BadRequestException("错误的请求报文");
            }
            request.setMethod(vanguards[0]);
            String requestURI = vanguards[1];
            if (requestURI.contains("?")) {
                int index = requestURI.indexOf("?");
                if (index < requestURI.length() - 1) {
                    request.setQueryString(requestURI.substring(index + 1));
                }
                requestURI = requestURI.substring(0, index);
            }
            request.setRequestURI(requestURI);
            request.setProtocol(vanguards[2]);
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
                if (isReadEnd || request.getContextLength() < 1) {
                    if (!StringUtil.isNullOrEmpty(bodyContext)) {
                        request.setInputStream(new ByteArrayInputStream(bodyContext.getBytes("iso-8859-1")));
                    }
                    return;
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] bodyData = bodyContext.getBytes("iso-8859-1");
                    byteArrayOutputStream.write(bodyData);
                    int remainLength = request.getContextLength() - bodyData.length;
                    byte[] remainData = ByteUtils.getBytes(channel, remainLength);
                    byteArrayOutputStream.write(remainData);
                    request.setInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
                } finally {
                    byteArrayOutputStream.close();
                }
            } catch (Exception e) {
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
        byte[] bytes = this.response.getOutputStream().toByteArray();
        // 如果没有数据
        channel.write(ByteBuffer.wrap(bytes));
    }
}

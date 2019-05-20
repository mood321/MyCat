package com.mood.framework.minicat.init.abs;

import com.mood.framework.minicat.exception.ResponseNotInitException;
import com.mood.framework.minicat.config.MoodCatConfig;
import com.mood.framework.minicat.entity.HttpServletRequest;
import com.mood.framework.minicat.entity.HttpServletResponse;
import com.mood.framework.minicat.util.GZIPUtils;
import com.mood.framework.minicat.util.StringUtil;
import com.mood.framework.minicat.press.MoodcatProcess;

import java.io.IOException;
import java.text.MessageFormat;

public abstract class HttpBulider {
    //private  Http
    protected HttpServletRequest request;

    protected HttpServletResponse response;

    protected static final String splitFlag = "\r\n\r\n";

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
    public void buildResponse() throws IOException {
        if (response == null) {
            response = new HttpServletResponse();
        }
        buildResponse(response.getHttpCode(), response.getOutputStream().toByteArray());
    }

    public void buildResponse(byte[] data) throws IOException {
        if (response == null) {
            response = new HttpServletResponse();
        }
        buildResponse(response.getHttpCode(), data);
    }

    public void buildResponse(int httpCode, String msg) throws IOException {
        if (response == null) {
            response = new HttpServletResponse();
        }
        buildResponse(httpCode, msg.getBytes(MoodCatConfig.ENCODE));
    }

    public void buildResponse(int httpCode, byte[] data) throws IOException {
        if (response == null) {
            response = new HttpServletResponse();
        }
        buildResponseHeader();
        if (MoodCatConfig.OPENGZIP) {
            response.setHeader("Content-Encoding", "gzip");
            // 压缩数据
            data = GZIPUtils.compress(data);
        }
        Integer contextLength = 0;
        if (data != null) {
            contextLength = data.length;
        }
        response.setHeader("Content-Length", contextLength.toString());
        StringBuilder responseHeader = new StringBuilder("HTTP/1.1 ").append(httpCode).append(" ").append("\r\n");
        for (String key : response.getHeaders().keySet()) {
            for (String header : response.getHeader(key)) {
                responseHeader.append(key).append(": ").append(header).append("\r\n");
            }
        }
        responseHeader.append("\r\n");
        response.getOutputStream().reset();
        response.getOutputStream().write(responseHeader.toString().getBytes(MoodCatConfig.ENCODE));
        if (!StringUtil.isNullOrEmpty(data)) {
            response.getOutputStream().write(data);
        }
    }

    public void buildResponseHeader() throws IOException {
        if (response == null) {
            throw new ResponseNotInitException("Response尚未初始化");
        }
        response.setHeader("Connection", "close");
        response.setHeader("Server", "MoodCat/1.0 By Mood");
        if (!response.containsHeader("Content-Type")) {
            response.setHeader("Content-Type", "text/html");
        }
        if (MoodCatConfig.OPENGZIP) {
            response.setHeader("Content-Encoding", "gzip");
        }
        if (request != null && request.isSessionCread()) {
            String cookie = MessageFormat.format("{0}={1}; HttpOnly", MoodCatConfig.SESSION_ID_FIELD_NAME,
                    request.getSessionId());
            response.setHeader("Set-Cookie", cookie);
        }
    }

    //
    public abstract void buildRequestHeader() throws IOException;

    protected abstract void buildRequest() ;

    protected abstract void flush() throws IOException;

    public void builder() {
        try {
            buildRequest();
            buildRequestHeader();
            this.response = new HttpServletResponse();
            MoodcatProcess.doService(this);
            buildResponse();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void flushAndClose() {
        try {
            flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            destroy();
        }
    }
    private void destroy() {
        if (response != null && response.getOutputStream() != null) {
            try {
                response.getOutputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (request != null && request.getInputStream() != null) {
            try {
                request.getInputStream().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

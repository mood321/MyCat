package com.mood.framework.minicat.entity;

import com.mood.framework.minicat.config.MoodCatConfig;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServletResponse {
    private Integer httpCode = 200;
    private Map<String, List<String>> header;
    private MiniCatOutputStream outputStream = new MiniCatOutputStream();


    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    public boolean containsHeader(String name) {
        if(header==null){
            return false;
        }
        return header.containsKey(name);
    }
    public Map<String, List<String>> getHeaders() {
        if(header==null){
            return null;
        }
        return header;
    }
    public List<String> getHeader(String name) {
        if(header==null){
            return null;
        }
        return header.get(name);
    }
    public void setHeader(String name, String headerLine) {
        if(header==null){
            header=new ConcurrentHashMap<String, List<String>>();
        }
        if(!header.containsKey(name)){
            List<String> headers=new ArrayList<String>();
            header.put(name, headers);
        }
        header.get(name).clear();
        header.get(name).add(headerLine);
        return;
    }
    public void addHeader(String name, String headerLine) {
        if(header==null){
            header=new ConcurrentHashMap<String, List<String>>();
        }
        if(!header.containsKey(name)){
            List<String> headers=new ArrayList<String>();
            header.put(name, headers);
        }
        header.get(name).add(headerLine);
        return;
    }
    public void setHeaders(Map<String, List<String>> header) {
        this.header = header;
    }

    public MiniCatOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(MiniCatOutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public static   class MiniCatOutputStream extends ByteArrayOutputStream{
        public void print(String s) throws IOException {
            write(s.getBytes(MoodCatConfig.ENCODE));
        }

        public void write(String s) throws IOException {
            print(s);
        }

        public void print(boolean b) throws IOException {
            String msg;
            if (b)
                msg = "true";
            else {
                msg = "false";
            }
            print(msg);
        }

        public void print(char c) throws IOException {
            print(String.valueOf(c));
        }

        public void print(int i) throws IOException {
            print(String.valueOf(i));
        }

        public void print(long l) throws IOException {
            print(String.valueOf(l));
        }

        public void print(float f) throws IOException {
            print(String.valueOf(f));
        }

        public void print(double d) throws IOException {
            print(String.valueOf(d));
        }

        public void println() throws IOException {
            print("\r\n");
        }

        public void println(String s) throws IOException {
            print(s);
            println();
        }

        public void println(boolean b) throws IOException {
            print(b);
            println();
        }

        public void println(char c) throws IOException {
            print(c);
            println();
        }

        public void println(int i) throws IOException {
            print(i);
            println();
        }

        public void println(long l) throws IOException {
            print(l);
            println();
        }

        public void println(float f) throws IOException {
            print(f);
            println();
        }

        public void println(double d) throws IOException {
            print(d);
            println();
        }
    }
}

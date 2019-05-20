package com.mood.framework.minicat.service;

import com.mood.framework.minicat.init.BioBulider;
import com.mood.framework.minicat.init.abs.HttpBulider;
import com.mood.framework.minicat.catpool.CatThreadPool;
import com.mood.framework.minicat.service.serviceInterface.Service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BIOService implements Service {
    private ServerSocket serverSocket;

    @Override
    public void openProt(Integer prot, Integer timeOut) throws IOException {
        serverSocket = new ServerSocket(prot);
        serverSocket.setSoTimeout(timeOut);
    }

    @Override
    public void doService() throws IOException {
        while (true) {
            Socket socket = serverSocket.accept();
            doSocket(socket);
        }

    }

    private void doSocket(final Socket socket) {
        CatThreadPool.HTTP_POOL.execute(() -> {
            try {
                HttpBulider bulider = new BioBulider(socket);
                bulider.builder();
              //  bulider.buildRequestHeader();
                bulider.flushAndClose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

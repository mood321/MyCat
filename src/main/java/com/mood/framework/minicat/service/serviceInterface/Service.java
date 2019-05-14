package com.mood.framework.minicat.service.serviceInterface;

import java.io.IOException;

public interface Service {
    void openProt(Integer prot,Integer timeOut) throws IOException;
    void doService() throws IOException;
}

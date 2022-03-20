package com.dzytsiuk.ioc.service;


import java.util.Objects;

public class MailService {
    private int port;
    private String protocol;

    public MailService() {
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MailService that = (MailService) o;
        return port == that.port &&
                Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {

        return Objects.hash(port, protocol);
    }

}

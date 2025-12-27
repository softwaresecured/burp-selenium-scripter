package com.softwaresecured.burp.selenium;

public class ProxyConfig {

    private int port = 0;
    private String host;

    public ProxyConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }


}

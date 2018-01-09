
package com.danenergy.common;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Http implements Serializable
{

    @SerializedName("Ip")
    @Expose
    private String ip;
    @SerializedName("Port")
    @Expose
    private int port;
    private final static long serialVersionUID = -2246823627204491825L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Http() {
    }

    /**
     * 
     * @param port
     * @param ip
     */
    public Http(String ip, int port) {
        super();
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("ip", ip).append("port", port).toString();
    }

}

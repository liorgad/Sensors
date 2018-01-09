
package com.danenergy.common;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Serial implements Serializable
{

    @SerializedName("PortName")
    @Expose
    private String portName;
    @SerializedName("BaudRate")
    @Expose
    private int baudRate;
    @SerializedName("ParityType")
    @Expose
    private int parityType;
    @SerializedName("DataBits")
    @Expose
    private int dataBits;
    @SerializedName("StopBitsType")
    @Expose
    private int stopBitsType;
    private final static long serialVersionUID = -2942653201992908869L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Serial() {
    }

    /**
     * 
     * @param parityType
     * @param stopBitsType
     * @param dataBits
     * @param baudRate
     * @param portName
     */
    public Serial(String portName, int baudRate, int parityType, int dataBits, int stopBitsType) {
        super();
        this.portName = portName;
        this.baudRate = baudRate;
        this.parityType = parityType;
        this.dataBits = dataBits;
        this.stopBitsType = stopBitsType;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(int baudRate) {
        this.baudRate = baudRate;
    }

    public int getParityType() {
        return parityType;
    }

    public void setParityType(int parityType) {
        this.parityType = parityType;
    }

    public int getDataBits() {
        return dataBits;
    }

    public void setDataBits(int dataBits) {
        this.dataBits = dataBits;
    }

    public int getStopBitsType() {
        return stopBitsType;
    }

    public void setStopBitsType(int stopBitsType) {
        this.stopBitsType = stopBitsType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("portName", portName).append("baudRate", baudRate).append("parityType", parityType).append("dataBits", dataBits).append("stopBitsType", stopBitsType).toString();
    }

}

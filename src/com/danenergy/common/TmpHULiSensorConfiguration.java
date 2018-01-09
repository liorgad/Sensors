
package com.danenergy.common;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class TmpHULiSensorConfiguration implements Serializable
{

    @SerializedName("address")
    @Expose
    private int address;
    @SerializedName("functionCode")
    @Expose
    private int functionCode;
    @SerializedName("registerAddress")
    @Expose
    private int registerAddress;
    @SerializedName("numberOfRegister")
    @Expose
    private int numberOfRegister;
    @SerializedName("pollingIntervalMillisec")
    @Expose
    private int pollingIntervalMillisec;
    private final static long serialVersionUID = -8089553784495041216L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TmpHULiSensorConfiguration() {
    }

    /**
     * 
     * @param numberOfRegister
     * @param functionCode
     * @param address
     * @param pollingIntervalMillisec
     * @param registerAddress
     */
    public TmpHULiSensorConfiguration(int address, int functionCode, int registerAddress, int numberOfRegister, int pollingIntervalMillisec) {
        super();
        this.address = address;
        this.functionCode = functionCode;
        this.registerAddress = registerAddress;
        this.numberOfRegister = numberOfRegister;
        this.pollingIntervalMillisec = pollingIntervalMillisec;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getFunctionCode() {
        return functionCode;
    }

    public void setFunctionCode(int functionCode) {
        this.functionCode = functionCode;
    }

    public int getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(int registerAddress) {
        this.registerAddress = registerAddress;
    }

    public int getNumberOfRegister() {
        return numberOfRegister;
    }

    public void setNumberOfRegister(int numberOfRegister) {
        this.numberOfRegister = numberOfRegister;
    }

    public int getPollingIntervalMillisec() {
        return pollingIntervalMillisec;
    }

    public void setPollingIntervalMillisec(int pollingIntervalMillisec) {
        this.pollingIntervalMillisec = pollingIntervalMillisec;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("address", address).append("functionCode", functionCode).append("registerAddress", registerAddress).append("numberOfRegister", numberOfRegister).append("pollingIntervalMillisec", pollingIntervalMillisec).toString();
    }

}

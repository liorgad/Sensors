
package com.danenergy.common;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class SensorsConf implements Serializable
{

    @SerializedName("Sensors")
    @Expose
    private Sensors sensors;
    @SerializedName("Http")
    @Expose
    private Http http;
    @SerializedName("Serial")
    @Expose
    private Serial serial;
    private final static long serialVersionUID = -5300107549517372313L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public SensorsConf() {
    }

    /**
     * 
     * @param sensors
     * @param http
     * @param serial
     */
    public SensorsConf(Sensors sensors, Http http, Serial serial) {
        super();
        this.sensors = sensors;
        this.http = http;
        this.serial = serial;
    }

    public Sensors getSensors() {
        return sensors;
    }

    public void setSensors(Sensors sensors) {
        this.sensors = sensors;
    }

    public Http getHttp() {
        return http;
    }

    public void setHttp(Http http) {
        this.http = http;
    }

    public Serial getSerial() {
        return serial;
    }

    public void setSerial(Serial serial) {
        this.serial = serial;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("sensors", sensors).append("http", http).append("serial", serial).toString();
    }

}

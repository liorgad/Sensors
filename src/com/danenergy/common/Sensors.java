
package com.danenergy.common;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Sensors implements Serializable
{

    @SerializedName("WindSensorConfiguration")
    @Expose
    private WindSensorConfiguration windSensorConfiguration;
    @SerializedName("TmpHULiSensorConfiguration")
    @Expose
    private TmpHULiSensorConfiguration tmpHULiSensorConfiguration;
    private final static long serialVersionUID = -8415724675835111519L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Sensors() {
    }

    /**
     * 
     * @param tmpHULiSensorConfiguration
     * @param windSensorConfiguration
     */
    public Sensors(WindSensorConfiguration windSensorConfiguration, TmpHULiSensorConfiguration tmpHULiSensorConfiguration) {
        super();
        this.windSensorConfiguration = windSensorConfiguration;
        this.tmpHULiSensorConfiguration = tmpHULiSensorConfiguration;
    }

    public WindSensorConfiguration getWindSensorConfiguration() {
        return windSensorConfiguration;
    }

    public void setWindSensorConfiguration(WindSensorConfiguration windSensorConfiguration) {
        this.windSensorConfiguration = windSensorConfiguration;
    }

    public TmpHULiSensorConfiguration getTmpHULiSensorConfiguration() {
        return tmpHULiSensorConfiguration;
    }

    public void setTmpHULiSensorConfiguration(TmpHULiSensorConfiguration tmpHULiSensorConfiguration) {
        this.tmpHULiSensorConfiguration = tmpHULiSensorConfiguration;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("windSensorConfiguration", windSensorConfiguration).append("tmpHULiSensorConfiguration", tmpHULiSensorConfiguration).toString();
    }

}

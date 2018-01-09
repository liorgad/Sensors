package com.danenergy.common;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Messages {


    public static class WindSpeed
    {
        @Expose
        @SerializedName("Speed")
        public float speedM_S=0;
    }

    public static class LightTempHumidity
    {
        @Expose
        @SerializedName("Luminance")
        public int luminance = 0;
        @Expose
        @SerializedName("Temperature")
        public double temperature = 0.0;
        @Expose
        @SerializedName("Humidity")
        public double humidity = 0.0;
    }
}

package com.danenergy.temp_hum_light_sensor;

import com.danenergy.common.ParserDefinition;
import com.danenergy.common.parser.GenericParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class THLSensorData
{
    @Expose
    @ParserDefinition(Index=1,BytesLength = 2)
    public short luminance;

    @Expose
    @ParserDefinition(Index=2,BytesLength = 2)
    public short temperature ;

    @Expose
    @ParserDefinition(Index=3,BytesLength = 2)
    public short humidity;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);    }

    public String Serialize()
    {
        String result = GenericParser.Build(this, THLSensorData.class);

        if (StringUtils.isEmpty(result))
        {
            return null;
        }
        return result;
    }

    public String getAsJson()
    {
        Gson gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .setPrettyPrinting()
                .create();

// 2. Java object to JSON, and assign to a String
        String jsonInString = gson.toJson(this);

        return jsonInString;
    }
}

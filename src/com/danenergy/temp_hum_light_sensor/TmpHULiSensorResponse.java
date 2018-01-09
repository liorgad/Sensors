package com.danenergy.temp_hum_light_sensor;

import com.danenergy.common.ParserDefinition;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.windsensor.WindSensorCommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class TmpHULiSensorResponse {
    @Expose
    @ParserDefinition(Index=1,BytesLength = 1)
    public byte address;

    @Expose
    @ParserDefinition(Index=2,BytesLength = 1)
    public byte functionCode;

    @Expose
    @ParserDefinition(Index=3,BytesLength = 1)
    public byte dataLengthInBytes;

    @Expose
    @ParserDefinition(Index=4,BytesLength = 0,RelatedFieldLength = "dataLengthInBytes")
    public byte[] data;

    @Expose
    @ParserDefinition(Index=5,BytesLength = 2)
    public short crc;

    @Override
    public String toString()
    {
        return ToStringBuilder.reflectionToString(this);    }

    public String Serialize()
    {
        String result = GenericParser.Build(this, WindSensorCommand.class);

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

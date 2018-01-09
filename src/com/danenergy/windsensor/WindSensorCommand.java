package com.danenergy.windsensor;

import com.danenergy.common.ParserDefinition;
import com.danenergy.common.parser.GenericParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class WindSensorCommand {

    @Expose
    @ParserDefinition(Index=1,BytesLength = 1)
    public byte address;

    @Expose
    @ParserDefinition(Index=2,BytesLength = 1)
    public byte functionCode;

    @Expose
    @ParserDefinition(Index=3,BytesLength = 2)
    public short startingRegAddr;

    @Expose
    @ParserDefinition(Index=4,BytesLength = 2)
    public short dataLength;

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

    public byte[] getBytes()
    {
        return new byte[]{(byte)address,(byte)functionCode,(byte) (byte) ((startingRegAddr & 0x0000ff00) >>> 8),
                (byte) ((startingRegAddr & 0x000000ff)), (byte) ((dataLength & 0x0000ff00) >>> 8),
                (byte) ((dataLength & 0x000000ff))};
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

    public void calculateCRC()
    {
        byte[] cmdBytes = getBytes();

        int crcInt = com.invertor.modbus.utils.CRC16.calc(cmdBytes);

        byte[] byteStr = new byte[2];
        byteStr[0] = (byte) ((crcInt & 0x000000ff));
        byteStr[1] = (byte) ((crcInt & 0x0000ff00) >>> 8);

        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.BIG_ENDIAN);
        bb.put(byteStr[0]);
        bb.put(byteStr[1]);
        short shortVal = bb.getShort(0);

        crc = shortVal;
    }


}

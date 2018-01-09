package com.danenergy.main;

import com.danenergy.SerialComm;

import com.danenergy.common.Utilities;
import com.danenergy.common.parser.GenericParser;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.invertor.modbus.exception.ModbusIOException;
import com.invertor.modbus.msg.base.mei.ReadDeviceIdentificationCode;
import com.invertor.modbus.serial.SerialParameters;
import com.invertor.modbus.serial.SerialPortFactoryRXTX;
import com.invertor.modbus.serial.SerialUtils;
import gnu.io.SerialPort;
import com.invertor.modbus.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;

public class SensorsMain {

    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    public static void main(String[] args)
    {

        try
        {
            logger.info("Application staring");

            if(args.length == 0)
            {
                System.out.println("Usage: java -jar Sensors [path for configuration.json file]");
                return;
            }

            if(args.length ==1)
            {
                String confPath = args[0];

                Injector guice = Guice.createInjector(new MainLogicGuiceModule(confPath));
                SensorsManager logic = guice.getInstance(SensorsManager.class);

                logic.start();
            }
            else
            {
                System.out.println("Usage: java -jar Sensors [path for configuration.json file]");
            }


            //logger.info("Application running, press key to stop");
            //while(true){}
        }
        catch (Exception e)
        {
            logger.error("Error in main loop",e);
        }
    }
}


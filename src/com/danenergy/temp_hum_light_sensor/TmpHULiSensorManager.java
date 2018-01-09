package com.danenergy.temp_hum_light_sensor;

import com.danenergy.common.*;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.windsensor.WindSensorCommand;
import com.danenergy.windsensor.WindSensorResponse;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class TmpHULiSensorManager  extends PluginBase implements IPlugin {

    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();
    final int numOfRetry = 5;
    ICommPort sensorComm;
    Timer sensorPollingTimer;
    TmpHULiSensorConfiguration conf;
    //final byte generalPurposeAddr = (byte)0xFA;
    byte getFuncCode = (byte)0x03;
    short registerAddr = 0x0000;
    short numberOfRegister = 0x0001;
    byte currentAddr = 0x02;

    byte sensorAddress;

    @Inject
    public TmpHULiSensorManager(EventBus eventBus,ICommPort commPort,TmpHULiSensorConfiguration conf)
    {
        super(eventBus);
        this.conf = conf;
        this.sensorComm = commPort;
        sensorPollingTimer = new Timer("WindSensor Polling Timer");
        this.currentAddr = (byte)conf.getAddress();
        this.getFuncCode = (byte)conf.getFunctionCode();
        this.registerAddr = (short)conf.getRegisterAddress();
        this.numberOfRegister = (short)conf.getNumberOfRegister();
    }

    private byte[] sendCommand(TmpHULiSensorCommand cmd) {
        try {
            if (null == sensorComm) {
                throw new NullPointerException("temp humid light communication is null");
            }

            if (!sensorComm.isOpen()) {
                sensorComm.open();
            }

            byte[] request = GenericParser.BuildToBytes(cmd, TmpHULiSensorCommand.class, null);


            if (null == request) {
                throw new NullPointerException("could not extract command bytes");
            }

            logger.info("Sending: " + com.danenergy.common.Utilities.getHexString(request));

            byte[] result = sensorComm.sendReceive(request,500);
            return result;
        }
        catch (Exception e)
        {
            logger.error("Error in sendCommand",e);
        }
        return null;
    }

    private TmpHULiSensorCommand getSensorCommand(byte addr,byte code,short startRegAddr,short numOfRegs) {
        TmpHULiSensorCommand cmd = new TmpHULiSensorCommand();
        cmd.address = addr;
        cmd.functionCode = code;
        cmd.startingRegAddr = startRegAddr;
        cmd.dataLength = numOfRegs;
        cmd.calculateCRC();
        return cmd;
    }

    public void startTimer()
    {
        sensorPollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
                logger.info(s+"polling timer started");
                logger.info("Timer task started at:"+new Date());
                if(!sensorComm.isOpen())
                {
                    //sensorComm.initializePort(null);
                    //commPort.initializePort("",9600,8,1,0);
                    sensorComm.open();
                }
                completeTask();
                logger.info("Timer task finished at:"+new Date());
            }
            private void completeTask() {
                try {
                    TmpHULiSensorCommand cmd = getSensorCommand(currentAddr, getFuncCode, registerAddr, numberOfRegister);

                    byte[] responseBytes = sendCommand(cmd);

                    if(null == responseBytes || responseBytes.length == 0)
                    {
                        logger.warn("tempLightHum sensor response is empty");
                        return;
                    }

                    TmpHULiSensorResponse wr = GenericParser.ParseFromBytes(responseBytes, TmpHULiSensorResponse.class);

                    logger.info("SensorReponse:\n" + wr.getAsJson());

                    THLSensorData sd = GenericParser.ParseFromBytes(wr.data,THLSensorData.class);

                    logger.info("Sensor Data:\n" + sd.getAsJson());

                    Messages.LightTempHumidity lmh = new Messages.LightTempHumidity();
                    lmh.luminance = sd.luminance & 0xffff;
                    lmh.temperature = sd.temperature / 100.0;
                    lmh.humidity = sd.humidity /100.0;

                    logger.info("Sensor data=" + lmh.luminance + " lumen ," + lmh.temperature + "(c) ," + lmh.humidity + "% RH");

                    publishWindSpeed(lmh);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0,conf.getPollingIntervalMillisec());
        logger.info("TimerTask started");
    }

    private void runOnce()
    {
        String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
        logger.info(s+"polling timer started");
        logger.info("Timer task started at:"+new Date());
        if(!sensorComm.isOpen())
        {
            sensorComm.open();
        }
        try {
            TmpHULiSensorCommand cmd = getSensorCommand(currentAddr, getFuncCode, registerAddr, numberOfRegister);

            byte[] responseBytes = sendCommand(cmd);

            if(null == responseBytes || responseBytes.length == 0)
            {
                logger.warn("tempLightHum sensor response is empty");
                return;
            }

            logger.info("Received: " + Utilities.getHexString(responseBytes));

            TmpHULiSensorResponse wr = GenericParser.ParseFromBytes(responseBytes, TmpHULiSensorResponse.class);

            logger.info("SensorReponse:\n" + wr.getAsJson());

            THLSensorData sd = GenericParser.ParseFromBytes(wr.data,THLSensorData.class);

            logger.info("Sensor Data:\n" + sd.getAsJson());

            Messages.LightTempHumidity lmh = new Messages.LightTempHumidity();
            lmh.luminance = sd.luminance;
            lmh.temperature = sd.temperature / 100;
            lmh.humidity = sd.humidity /100;

            logger.info("Sensor data=" + lmh.luminance + "lumen ," + lmh.temperature + "(c) ," + lmh.humidity + "% RH");

            publishWindSpeed(lmh);

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Timer task finished at:"+new Date());
    }

    @Override
    public void Start() {
        logger.info("TmpHULiSensorManager started");
        this.start();

        try {

            //runOnce();
            //runOnce();
            startTimer();
        }
        catch (Exception e)
        {
            logger.error("TmpHULiSensorManager Start Error",e);
        }
    }

    @Override
    public void Stop() {

    }

    @Override
    public void Dispose() {

    }

    public void publishWindSpeed(Messages.LightTempHumidity lth) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            eventBus.post(lth);
        });
    }
}

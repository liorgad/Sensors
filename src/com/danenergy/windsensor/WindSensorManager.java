package com.danenergy.windsensor;

import com.danenergy.common.*;
import com.danenergy.common.parser.GenericParser;
import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import org.apache.commons.lang3.*;
import org.apache.logging.log4j.Logger;

import javax.swing.text.Utilities;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WindSensorManager extends PluginBase implements IPlugin {

    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();
    final int numOfRetry = 5;
    ICommPort windSensorComm;
    Timer windSensorPollingTimer;
    WindSensorConfiguration conf;
    final byte generalPurposeAddr = (byte)0xFA;
    byte getWindSpeedFuncCode = (byte)0x03;
    short registerAddr = 0x0000;
    short numberOfRegister = 0x0001;
    byte currentAddr;

    byte windsensorAddress;

    @Inject
    public WindSensorManager(EventBus eventBus,ICommPort commPort,WindSensorConfiguration conf)
    {
        super(eventBus);
        this.conf = conf;
        this.windSensorComm = commPort;
        windSensorPollingTimer = new Timer("WindSensor Polling Timer");
        this.currentAddr = (byte)conf.getAddress();
        this.getWindSpeedFuncCode = (byte)conf.getFunctionCode();
        this.registerAddr = (short)conf.getRegisterAddress();
        this.numberOfRegister = (short)conf.getNumberOfRegister();
    }

    public byte getWindSensorAddress() throws NullPointerException
    {
        WindSensorCommand cmd = getWindSensorCommand(generalPurposeAddr,getWindSpeedFuncCode,registerAddr,numberOfRegister);

        byte[] responseBytes=null;
        for(int i=0 ;i < numOfRetry ; i++) {
            responseBytes = sendCommand(cmd);

            if (null == responseBytes || responseBytes.length == 0) {
                continue;
            }
            else {
                break;
            }
        }
        if(null == responseBytes || responseBytes.length == 0)
        {
            throw new NullPointerException("could not get response");
        }
        currentAddr = responseBytes[0];

        return currentAddr;
    }

    private byte[] sendCommand(WindSensorCommand cmd) {
        try {
            if (null == windSensorComm) {
                throw new NullPointerException("WindSensor communication is null");
            }

            if (!windSensorComm.isOpen()) {
                windSensorComm.open();
            }

            byte[] request = GenericParser.BuildToBytes(cmd, WindSensorCommand.class, null);

            //byte[] request = com.danenergy.common.Utilities.getByteArrayFromHexString(result);

            if (null == request) {
                throw new NullPointerException("could not extract command bytes");
            }

            logger.info("Sending: " + com.danenergy.common.Utilities.getHexString(request));

            byte[] result = windSensorComm.sendReceive(request,500);
            return result;
        }
        catch (Exception e)
        {
            logger.error("Error in sendCommand",e);
        }
        return null;
    }

    private WindSensorCommand getWindSensorCommand(byte addr,byte code,short startRegAddr,short numOfRegs) {
        WindSensorCommand cmd = new WindSensorCommand();
        cmd.address = addr;
        cmd.functionCode = code;
        cmd.startingRegAddr = startRegAddr;
        cmd.dataLength = numOfRegs;
        cmd.calculateCRC();
        return cmd;
    }

    public void startTimer()
    {
        windSensorPollingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
                logger.info(s+"polling timer started");
                logger.info("Timer task started at:"+new Date());
                if(!windSensorComm.isOpen())
                {
                    //windSensorComm.initializePort(null);
                    //commPort.initializePort("",9600,8,1,0);
                    windSensorComm.open();
                }
                completeTask();
                logger.info("Timer task finished at:"+new Date());
            }
            private void completeTask() {
                try {
                    WindSensorCommand cmd = getWindSensorCommand(currentAddr,getWindSpeedFuncCode,registerAddr,numberOfRegister);

                    byte[] responseBytes = sendCommand(cmd);

                    if(null == responseBytes || responseBytes.length == 0)
                    {
                        logger.warn("Windsensor response is empty");
                        return;
                    }

                    WindSensorResponse response = GenericParser.ParseFromBytes(responseBytes, WindSensorResponse.class);

                    logger.info("WindSensorReponse:\n"+ response.getAsJson());

                    Messages.WindSpeed ws = new Messages.WindSpeed();
                    ws.speedM_S = (float)response.data / 100L;

                    logger.info("WindSpeed=" + ws.speedM_S + "m/s");

                    publishWindSpeed(ws);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, 0,5000);
        logger.info("TimerTask started");
    }
    @Override
    public void Start() {
        logger.info("WindSensorManager started");
        this.start();

        try {
            //windsensorAddress = getWindSensorAddress();

            //runOnce();
            //runOnce();
            startTimer();
        }
        catch (Exception e)
        {
            logger.error("WindSensor Start Error",e);
        }
    }

    private void runOnce()
    {
        String s = String.format("[Thread=%s]",String.valueOf(Thread.currentThread().getId()));
        logger.info(s+"polling timer started");
        logger.info("Timer task started at:"+new Date());
        if(!windSensorComm.isOpen())
        {
            windSensorComm.open();
        }
        try {
            WindSensorCommand cmd = getWindSensorCommand(currentAddr,getWindSpeedFuncCode,registerAddr,numberOfRegister);

            byte[] responseBytes = sendCommand(cmd);

            if(null == responseBytes || responseBytes.length == 0)
            {
                logger.warn("Windsensor response is empty");
                return;
            }

            WindSensorResponse response = GenericParser.ParseFromBytes(responseBytes, WindSensorResponse.class);

            logger.info("WindSensorReponse:\n"+ response.getAsJson());

            Messages.WindSpeed ws = new Messages.WindSpeed();
            ws.speedM_S = (float)response.data / 100L;

            logger.info("WindSpeed=" + ws.speedM_S + "m/s");

        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Timer task finished at:"+new Date());
    }

    @Override
    public void Stop() {
        windSensorPollingTimer.cancel();
        windSensorComm.close();
    }

    @Override
    public void Dispose() {
        windSensorPollingTimer.purge();
        windSensorComm.dispose();
    }

    public void publishWindSpeed(Messages.WindSpeed ws) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            eventBus.post(ws);
        });
    }
}

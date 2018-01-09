package com.danenergy.main;

import com.danenergy.common.*;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import gnu.io.SerialPort;
import org.apache.logging.log4j.Logger;

import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SensorsManager {
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();
    final int SECOND_IN_MILLI = 1000;
    private final Serial serialConf;

    EventBus eventBus;

    Set<IPlugin> plugins;

    ExecutorService executorService;

    ICommPort comm;


    @Inject
    public SensorsManager(EventBus eventBus,Set<IPlugin> plugins,ICommPort comm,Serial serialConf)
    {
        this.eventBus = eventBus;
        this.plugins = plugins;
        this.comm = comm;
        this.serialConf = serialConf;
    }

    public void start()
    {
        try {
            executorService = Executors.newFixedThreadPool(10);

            eventBus.register(this);

            logger.info("com.danenergy.main.SensorsManager Started");

            logger.info("port name from conf " + serialConf.getPortName());
            comm.initializePort(serialConf.getPortName(),serialConf.getBaudRate(), SerialPort.DATABITS_8,SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            comm.open();

            for (IPlugin plgn : plugins) {
                plgn.Start();
            }
        }
        catch (Exception e)
        {
            logger.error("Error main",e);
            stop();
        }

    }

    public void stop()
    {
        logger.info("com.danenergy.main.SensorsManager: stop");

        try {
            if (null != this.eventBus) {
                this.eventBus.unregister(this);
            }

            if (null != this.plugins) {
                plugins.stream().forEach(new Consumer<IPlugin>() {
                    @Override
                    public void accept(IPlugin iPlugin) {
                        iPlugin.Stop();
                        iPlugin.Dispose();
                    }
                });
            }

            logger.info("Shutting down executorservice");
            executorService.shutdown();

            logger.info("com.danenergy.main.SensorsManager stopped");

            if(null != comm)
            {
                comm.close();
            }
        }
        catch(Exception e)
        {
            logger.error("Error stopping com.danenergy.main.SensorsManager: ",e);
        }
    }
}

package com.danenergy.main;

import com.danenergy.HttpServer;
import com.danenergy.SerialComm;
import com.danenergy.common.*;
import com.danenergy.temp_hum_light_sensor.TmpHULiSensorManager;
import com.danenergy.windsensor.WindSensorManager;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import java.io.FileReader;


public class MainLogicGuiceModule extends com.google.inject.AbstractModule{

    private final EventBus eventBus = new EventBus("Default EventBus");
    private SensorsConf conf;

    public MainLogicGuiceModule(String congfigurationPath)
    {
        super();
        try
        {
            Gson gson = new Gson();
            FileReader reader = new FileReader(congfigurationPath);
            this.conf = gson.fromJson(reader, SensorsConf.class);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    @Override
    protected void configure() {
        bind(EventBus.class).toInstance(eventBus);
        bindListener(Matchers.any(), new TypeListener() {
            public <I> void hear(TypeLiteral<I> typeLiteral, TypeEncounter<I> typeEncounter) {
                typeEncounter.register(new InjectionListener<I>() {
                    public void afterInjection(I i) {
                        eventBus.register(i);
                    }
                });
            }
        });

        Multibinder<IPlugin> binder = Multibinder.newSetBinder(binder(), IPlugin.class);
        binder.addBinding().to(WindSensorManager.class);
        binder.addBinding().to(TmpHULiSensorManager.class);
        binder.addBinding().to(com.danenergy.http.HttpServer.class);

        bind(SensorsConf.class).toInstance(conf);
        bind(Sensors.class).toInstance(conf.getSensors());
        bind(Http.class).toInstance(conf.getHttp());
        bind(Serial.class).toInstance(conf.getSerial());
        bind(WindSensorConfiguration.class).toInstance(conf.getSensors().getWindSensorConfiguration());
        bind(TmpHULiSensorConfiguration.class).toInstance(conf.getSensors().getTmpHULiSensorConfiguration());
        bind(ICommPort.class).to(SerialComm.class).asEagerSingleton();


    }
}

package com.danenergy.common;

import com.google.common.eventbus.EventBus;
import org.apache.logging.log4j.Logger;

public class PluginBase {
    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    protected String name = this.getClass().getSimpleName();

    protected EventBus eventBus;

    public PluginBase()
    {
        eventBus = new EventBus();
    }

    public PluginBase(EventBus eventBus)
    {
        this.eventBus = eventBus;
    }

    protected void start() {
        this.eventBus.register(this);
    }

    public void stop()
    {
        this.eventBus.unregister(this);
    }

    public String getName()
    {
        return name;
    }
}

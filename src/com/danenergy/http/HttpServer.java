package com.danenergy.http;

import com.danenergy.common.Http;
import com.danenergy.common.IPlugin;
import com.danenergy.common.Messages;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.util.ServerRunner;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HttpServer extends NanoHTTPD implements IPlugin {

    final static Logger logger = org.apache.logging.log4j.LogManager.getLogger();

    protected String name = this.getClass().getSimpleName();

    private Http conf;
    private EventBus eventBus;

    private Map<String, Object> httpResponse = new ConcurrentHashMap<>();

    @Inject
    public HttpServer(EventBus eventBus,Http conf)
    {
        super(conf.getIp(),conf.getPort());
        this.conf = conf;
        this.eventBus = eventBus;
    }

    public HttpServer(String hostname,int port)
    {
        super(hostname,port);
    }

    public HttpServer(int port)
    {
        super(port);
    }

    @Override
    public void Start() {
        try
        {
            //ServerRunner.run(this.getClass());
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            String listenIp  = conf.getIp();
            int listenPort = conf.getPort();
            logger.info("Running! Point your browsers to http://"+listenIp+":"+listenPort +"/ \n");
        }
        catch (Exception e)
        {
            System.err.println("\nError running ! IOException : " + e.getMessage() + "\n" + e.getStackTrace());
        }
    }

    @Override
    public void Stop() {
        stop();
    }

    @Override
    public void Dispose() {
        closeAllConnections();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Response serve(IHTTPSession session) {

        Map<String, String> files = new HashMap<String, String>();
        Method method = session.getMethod();

        if(Method.GET.equals(method))
        {
            String queryString = "Empty";
            try
            {
                queryString = session.getQueryParameterString();
                Map map = session.getParameters();
                Gson gson = new GsonBuilder()
                        .excludeFieldsWithoutExposeAnnotation()
                        .setPrettyPrinting()
                        .create();

                String jsonInString = gson.toJson(httpResponse);

                return newFixedLengthResponse(Response.Status.OK,"application/json",jsonInString);
            }
            catch(Exception e)
            {
                logger.error(e);
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT,"Parameter sent: " + queryString + " " + e);
            }
        }

        return newFixedLengthResponse(Response.Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT,"Received method :" + method);
    }

    @Subscribe
    public void handleWindSpeed(Messages.WindSpeed windSpeed)
    {
        logger.info("WindSpeed = " + windSpeed.speedM_S);

        if(httpResponse.containsKey("WindSensor"))
        {
            httpResponse.replace("WindSensor",windSpeed);
        }
        else
        {
            httpResponse.put("WindSendor", windSpeed);
        }
        logger.info("WindSpeed updated !");
    }

    @Subscribe
    public void handleTemperatureLightHumidity(Messages.LightTempHumidity lth)
    {
        logger.info("temperature  = " + lth.temperature + " light = " + lth.luminance + " lumen, humidity = " + lth.humidity + "%");

        if(httpResponse.containsKey("LightTempHumiditySensor"))
        {
            httpResponse.replace("LightTempHumiditySensor",lth);
        }
        else
        {
            httpResponse.put("LightTempHumiditySensor", lth);
        }

        logger.info("Light temperature humidity updated !");
    }
}

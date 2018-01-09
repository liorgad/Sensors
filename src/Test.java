import com.danenergy.SerialComm;
import com.danenergy.common.Messages;
import com.danenergy.common.SensorsConf;
import com.danenergy.common.parser.GenericParser;
import com.danenergy.main.SensorsMain;
import com.danenergy.temp_hum_light_sensor.THLSensorData;
import com.danenergy.temp_hum_light_sensor.TmpHULiSensorCommand;
import com.danenergy.temp_hum_light_sensor.TmpHULiSensorManager;
import com.danenergy.temp_hum_light_sensor.TmpHULiSensorResponse;
import com.danenergy.windsensor.WindSensorCommand;
import com.danenergy.windsensor.WindSensorManager;
import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import gnu.io.SerialPort;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by dev on 31/10/2017.
 */
public class Test {
    public static void main(String[] args)
    {
        testResponse();
        //testInject();
        //testConfiguration();
        //testComm();



//        try {
//            EventBus eb = new EventBus("SensorEventBus");
//            SerialComm comm = new SerialComm();
//
//            comm.initializePort("COM3", 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
//
//            TmpHULiSensorManager m = new TmpHULiSensorManager(eb, comm);
//
//            m.Start();
//
//            System.out.println("Press any key to exit...");
//            System.in.read();
//        }
//        catch (Exception e)
//        {
//            System.out.println(e);
//        }
    }


    private static void testResponse()
    {
        try {
            TmpHULiSensorResponse tr = new TmpHULiSensorResponse();

            tr.address = 2;
            tr.functionCode = 3;
            tr.dataLengthInBytes = 6;
            tr.data = new byte[]{-52, -15, 12, -35, 12, -116};
            tr.crc = -12717;

            THLSensorData data = GenericParser.ParseFromBytes(tr.data, THLSensorData.class);

            Messages.LightTempHumidity lmh = new Messages.LightTempHumidity();
            lmh.luminance = data.luminance & 0xffff;
            lmh.temperature = data.temperature / 100.0;
            lmh.humidity = data.humidity /100.0;

            System.out.println("Sensor data=" + lmh.luminance + " lumen ," + lmh.temperature + "(c) ," + lmh.humidity + "% RH");

            System.in.read();
        }
        catch (Exception e) {

        }
    }

    private static void testInject() {
        try {
            SensorsMain.main(null);

            System.in.read();
        }
        catch (Exception e)
        {

        }
    }

    public static void testConfiguration()
    {
        try
        {
            Gson gson = new Gson();
            FileReader reader = new FileReader("resources/configuration.json");
            SensorsConf conf = gson.fromJson(reader, SensorsConf.class);

            System.in.read();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }

    }

    public static void testComm()
    {
        try {
            //com.danenergy.windsensor.WindSensorCommand cmd = new com.danenergy.windsensor.WindSensorCommand();
//            //cmd.address = 0x02;
//            cmd.address = 0xFA;
//            cmd.functionCode = 0x03;
//            cmd.startingRegAddr = 0x002A;
//            cmd.dataLength = 0x0001;

            TmpHULiSensorCommand cmd = new TmpHULiSensorCommand();


            //cmd.address = 0x02;
            cmd.address = (byte)0x02;
            cmd.functionCode = 0x03;
            cmd.startingRegAddr = 0x0000;
            cmd.dataLength = 0x0003;

            cmd.calculateCRC();

            byte[] strBytes = cmd.getBytesWithCRC();

            //String result = GenericParser.Build(cmd, TmpHULiSensorCommand.class);



            SerialComm comm = new SerialComm();

            comm.initializePort("COM3", 9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
            comm.open();

            byte[] response = comm.sendReceive(strBytes);

            Thread.sleep(1000);

            response = comm.sendReceive(strBytes);

            System.out.println(response);
            comm.close();
        }
        catch (Exception e)
        {}
    }
}

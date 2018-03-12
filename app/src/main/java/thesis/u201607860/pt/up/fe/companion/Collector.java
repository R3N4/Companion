package thesis.u201607860.pt.up.fe.companion;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;

import java.io.FileOutputStream;
import java.io.PrintWriter;

public class Collector extends Service implements SensorEventListener {

    private SensorManager _sensorManager;
    private Sensor _accelerometer;
    private Sensor _gyroscope;
    private Sensor _orientation;
    private Sensor _magnetic;
    private String _fileAccelerometer = "accelData";
    private String _fileGyroscope = "gyroData";
    private String _fileOrientation = "orienData";
    private String _fileMagRot = "magneticData";
    private FileOutputStream _fileStreamAccel;
    private FileOutputStream _fileStreamGyro;
    private PrintWriter accelWriter;
    private PrintWriter gyroWriter;
    private PrintWriter orienWriter;
    private PrintWriter magneticWriter;

    private PrintWriter InitiateWriter(String fileName, Long timestamp){
        try {
            return new PrintWriter(getApplicationContext().getExternalFilesDir(null)+"/"+fileName+".session"+timestamp+".csv", "UTF-8");
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private void WriteHeader(PrintWriter writer, String[] data){
        writer.println(data[0]+";"+data[1]+";"+data[2]+";"+data[3]+";"); //Timestamp;X;Y;Z;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){


        System.out.println("Running");

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _gyroscope = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        _orientation = _sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        _magnetic = _sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);

        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        Long time = System.currentTimeMillis();
        System.out.println(getApplicationContext().getExternalFilesDir(null)+"/"+".session"+time);

        accelWriter = InitiateWriter(_fileAccelerometer, time);
        WriteHeader(accelWriter, new String[]{time.toString(), "x Acceleration", "y Acceleration", "z Acceleration"});
        gyroWriter = InitiateWriter(_fileGyroscope, time);
        WriteHeader(gyroWriter, new String[]{time.toString(), "Rotation Around x", "Rotation Around y", "Rotation Around z"});
        orienWriter = InitiateWriter(_fileOrientation, time);
        WriteHeader(orienWriter, new String[]{time.toString(), "Azimuth", "Pitch", "Roll"});
        magneticWriter = InitiateWriter(_fileMagRot, time);
        WriteHeader(magneticWriter, new String[]{time.toString(), "Rotation Along x", "Rotation Along y", "Rotation Along z"});

        MainActivity.ChangeState();
        //MainActivity.Rename();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        _sensorManager.unregisterListener(this);

        accelWriter.close();
        gyroWriter.close();
        orienWriter.close();
        magneticWriter.close();

        MainActivity.ChangeState();
        //MainActivity.Rename();
    }


    /* *** *** *** *** */
    /*  Sensor Events  */
    /* *** *** *** *** */

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        switch (sensorEvent.sensor.getType()){

            case Sensor.TYPE_ACCELEROMETER:
                //System.out.println(sensorEvent.timestamp + " :: " + sensorEvent.values[0] + "|" + sensorEvent.values[1] + "|" + sensorEvent.values[2]);
                try{
                    String data = sensorEvent.timestamp + ";" + sensorEvent.values[0] + ";" + sensorEvent.values[1] + ";" + sensorEvent.values[2] + ";";
                    //System.out.println("Error : " + accelWriter.checkError());
                    accelWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                try{
                    String data = sensorEvent.timestamp + ";" + sensorEvent.values[0] + ";" + sensorEvent.values[1] + ";" + sensorEvent.values[2] + ";";
                    System.out.println(sensorEvent.timestamp + " :: " + sensorEvent.values[0] + "|" + sensorEvent.values[1] + "|" + sensorEvent.values[2]);
                    //System.out.println("Error : " + gyroWriter.checkError());
                    gyroWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_ORIENTATION:
                try{
                    String data = sensorEvent.timestamp + ";" + sensorEvent.values[0] + ";" + sensorEvent.values[1] + ";" + sensorEvent.values[2] + ";";
                    //System.out.println("Error : " + orienWriter.checkError());
                    orienWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR:
                try{
                    String data = sensorEvent.timestamp + ";" + sensorEvent.values[0] + ";" + sensorEvent.values[1] + ";" + sensorEvent.values[2] + ";";
                    //System.out.println("Error : " + magneticWriter.checkError());
                    magneticWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            default:
                //Nothing to do
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do something here if sensor accuracy changes.
    }
}

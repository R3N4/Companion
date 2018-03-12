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
    private String _fileAccelerometer = "accelData";
    private String _fileGyroscope = "gyroData";
    private String _fileOrientation = "orienData";
    private FileOutputStream _fileStreamAccel;
    private FileOutputStream _fileStreamGyro;
    private PrintWriter accelWriter;
    private PrintWriter gyroWriter;

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
        //_orientation = _sensorManager.getDefaultSensor(Sensor.TYPE_);

        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        try{
            Long time = System.currentTimeMillis();
            System.out.println();
            System.out.println(getApplicationContext().getExternalFilesDir(null)+"/"+_fileGyroscope+".session"+time);
            gyroWriter = new PrintWriter(getApplicationContext().getExternalFilesDir(null)+"/"+_fileGyroscope+".session"+time, "UTF-8");

            accelWriter = new PrintWriter(getApplicationContext().getExternalFilesDir(null)+"/"+ _fileAccelerometer+".session"+time, "UTF-8");
            //_fileStreamAccel = new FileOutputStream(getApplicationContext().getExternalFilesDir(null)+"/"+_fileAccelerometer+".session"+time, true);
            //_fileStreamGyro = new FileOutputStream(getApplicationContext().getExternalFilesDir(null)+"/"+_fileGyroscope+".session"+time, true);
        }catch (Exception e){
            System.out.println(e.toString());
        }

        MainActivity.ChangeState();
        //MainActivity.Rename();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        _sensorManager.unregisterListener(this);
        accelWriter.close();
        gyroWriter.close();
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

                System.out.println(sensorEvent.timestamp + " :: " + sensorEvent.values[0] + "|" + sensorEvent.values[1] + "|" + sensorEvent.values[2]);

                try{
                    //_fileStreamAccel = openFileOutput(_fileAccelerometer, Context.MODE_APPEND);
                    String data = sensorEvent.timestamp + " :: " + sensorEvent.values[0] + "|" + sensorEvent.values[1] + "|" + sensorEvent.values[2] + "\n";
                    //_fileStreamAccel.write(data.getBytes());
                    //_fileStreamAccel.flush();
                    //_fileStreamAccel.close();
                    System.out.println("Error : " + accelWriter.checkError());
                    accelWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }

                break;

            case Sensor.TYPE_GYROSCOPE:
                //System.out.println("Sensor Activated");
                try{
                    //_fileStreamGyro = openFileOutput(_fileGyroscope, Context.MODE_APPEND);
                    String data = sensorEvent.timestamp + " :: " + sensorEvent.values[0] + "|" + sensorEvent.values[1] + "|" + sensorEvent.values[2] + "\n";
                    //_fileStreamGyro.write(data.getBytes());
                    //_fileStreamGyro.close();
                    System.out.println("Error : " + gyroWriter.checkError());
                    gyroWriter.println(data);
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

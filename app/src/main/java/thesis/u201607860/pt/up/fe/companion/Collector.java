package thesis.u201607860.pt.up.fe.companion;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.security.Permission;
import java.util.List;

public class Collector extends Service implements SensorEventListener {

    private static final int TIME_BETWEEN_REQUESTS = 1000 * 15;

    private SensorManager _sensorManager;
    private LocationManager _locationManager;
    LocationListener _locationListener;
    private Sensor _accelerometer;
    private Sensor _gyroscope;
    private Sensor _orientation;
    private Sensor _magnetic;
    private String _fileAccelerometer = "accelData";
    private String _fileGyroscope = "gyroData";
    private String _fileOrientation = "orienData";
    private String _fileMagRot = "magneticData";
    private String _fileLocation = "locationData";
    private PrintWriter accelWriter;
    private PrintWriter gyroWriter;
    private PrintWriter orienWriter;
    private PrintWriter magneticWriter;
    private PrintWriter locationWriter;

    //Compass
    float[] mGravity;
    float[] mGeomagnetic;

    private Location _currentBestLocation;



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){


        System.out.println("Running");

        _sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        _locationManager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        _accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        _gyroscope = _sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        _orientation = _sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        _magnetic = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        for (Sensor sensor : _sensorManager.getSensorList(Sensor.TYPE_ALL)) {
            System.out.println(sensor);
        }

        _locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //Do Stuff
                try{
                    String data = System.currentTimeMillis() + "," + location.getLatitude() + "," + location.getLongitude() + ",";
                    locationWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.toString());
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        };
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, TIME_BETWEEN_REQUESTS, 0, _locationListener);
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, TIME_BETWEEN_REQUESTS, 0, _locationListener);
        }

        RegisterListeners();
        InitiateAllWriters();

        MainActivity.ChangeState();
        //MainActivity.Rename();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        _sensorManager.unregisterListener(this);
        _locationManager.removeUpdates(_locationListener);

        accelWriter.close();
        gyroWriter.close();
        orienWriter.close();
        magneticWriter.close();
        locationWriter.close();

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
                try{
                    mGravity = sensorEvent.values;
                    String data = System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2] + ",";
                    MainActivity.ShowAccelData(""+sensorEvent.values[0], ""+sensorEvent.values[1], ""+sensorEvent.values[2]);
                    accelWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_GYROSCOPE:
                try{
                    String data = System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2] + ",";
                    //System.out.println("Error : " + gyroWriter.checkError());
                    MainActivity.ShowGyroData(""+sensorEvent.values[0], ""+sensorEvent.values[1], ""+sensorEvent.values[2]);
                    gyroWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_ORIENTATION:
                try{
                    String data = System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2] + ",";
                    //System.out.println("Error : " + orienWriter.checkError());
                    orienWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }
                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                mGeomagnetic = sensorEvent.values;
                /*try{
                    String data = System.currentTimeMillis() + "," + sensorEvent.values[0] + "," + sensorEvent.values[1] + "," + sensorEvent.values[2] + ",";
                    //System.out.println("Error : " + magneticWriter.checkError());
                    magneticWriter.println(data);
                } catch (Exception e){
                    e.printStackTrace();

                    System.out.println(e.toString());
                }*/
                break;

            default:
                //Nothing to do
                break;
        }
        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                String data = System.currentTimeMillis() + "," + orientation[0]*360/(2*3.14159f) + "," + orientation[1] + "," + orientation[2] + ",";
                MainActivity.ShowOrientationData(""+orientation[0]*360/(2*3.14159f));
                magneticWriter.println(data);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do something here if sensor accuracy changes.
    }


    private void RegisterListeners(){
        _sensorManager.registerListener(this, _accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        _sensorManager.registerListener(this, _gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        _sensorManager.registerListener(this, _orientation, SensorManager.SENSOR_DELAY_NORMAL);
        _sensorManager.registerListener(this, _magnetic, SensorManager.SENSOR_DELAY_NORMAL);


    }

    private void InitiateAllWriters(){
        Long time = System.currentTimeMillis();
        System.out.println(getApplicationContext().getExternalFilesDir(null)+"/"+".session"+time);

        accelWriter = InitiateWriter(_fileAccelerometer, time);
        WriteHeader(accelWriter, new String[]{"Timestamp", "x Acceleration", "y Acceleration", "z Acceleration"});
        gyroWriter = InitiateWriter(_fileGyroscope, time);
        WriteHeader(gyroWriter, new String[]{"Timestamp", "Rotation Around x", "Rotation Around y", "Rotation Around z"});
        orienWriter = InitiateWriter(_fileOrientation, time);
        WriteHeader(orienWriter, new String[]{"Timestamp", "Azimuth", "Pitch", "Roll"});
        magneticWriter = InitiateWriter(_fileMagRot, time);
        WriteHeader(magneticWriter, new String[]{"Timestamp", "Rotation Along x", "Rotation Along y", "Rotation Along z"});

        locationWriter = InitiateWriter(_fileLocation, time);
        WriteHeader(locationWriter, new String[]{"Timestamp", "Latitude", "Longitude", "N/A"});
    }

    private PrintWriter InitiateWriter(String fileName, Long timestamp){
        try {
            return new PrintWriter(getApplicationContext().getExternalFilesDir(null)+"/"+fileName+".session"+timestamp+".csv", "UTF-8");
        }catch (Exception e){
            System.out.println(e.toString());
        }
        return null;
    }

    private void WriteHeader(PrintWriter writer, String[] data){
        writer.println(data[0]+","+data[1]+","+data[2]+","+data[3]+","); //Timestamp;X;Y;Z;
    }



    //https://developer.android.com/guide/topics/location/strategies.html
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TIME_BETWEEN_REQUESTS;
        boolean isSignificantlyOlder = timeDelta < -TIME_BETWEEN_REQUESTS;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}

package thesis.u201607860.pt.up.fe.companion;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Context _context;
    private Intent service;
    private static boolean _isRecording;

    private static TextView accelX;
    private static TextView accelY;
    private static TextView accelZ;
    private static TextView gyroX;
    private static TextView gyroY;
    private static TextView gyroZ;
    private static TextView azimuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _context = getApplicationContext();
        service = new Intent(_context, Collector.class);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        accelX = findViewById(R.id.accelX);
        accelY = findViewById(R.id.accelY);
        accelZ = findViewById(R.id.accelZ);
        gyroX = findViewById(R.id.gyroX);
        gyroY = findViewById(R.id.gyroY);
        gyroZ = findViewById(R.id.gyroZ);
        azimuth = findViewById(R.id.azimuth);

        final Button toService = findViewById(R.id.toService);
        toService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!_isRecording)
                    startService(service);
                else
                    stopService(service);
            }
        });
        Rename();

        /*
        SensorManager s = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        for (Sensor sensor : s.getSensorList(Sensor.TYPE_ALL)) {
            System.out.println(sensor);
        }
        */
    }

    public void Rename(){
        Button button = findViewById(R.id.toService);
        button.setText(_isRecording ? R.string.main_recordOff : R.string.main_recordOn);
    }

    public static void ShowAccelData(String x, String y, String z){
        accelX.setText(x);
        accelY.setText(y);
        accelZ.setText(z);
    }
    public static void ShowGyroData(String x, String y, String z){
        gyroX.setText(x);
        gyroY.setText(y);
        gyroZ.setText(z);
    }
    public static void ShowOrientationData(String x){
        azimuth.setText(x);
    }

    public static void ChangeState(){
        _isRecording = !_isRecording;
    }
    public static boolean CheckState(){
        return _isRecording;
    }
}

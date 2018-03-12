package thesis.u201607860.pt.up.fe.companion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Context _context;
    private Intent service;
    private static boolean _isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _context = getApplicationContext();
        service = new Intent(_context, Collector.class);

        setContentView(R.layout.activity_main);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

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
    }

    public void Rename(){
        Button button = findViewById(R.id.toService);
        button.setText(_isRecording ? R.string.main_recordOff : R.string.main_recordOn);
    }

    public static void ChangeState(){
        _isRecording = !_isRecording;
    }
    public static boolean CheckState(){
        return _isRecording;
    }
}

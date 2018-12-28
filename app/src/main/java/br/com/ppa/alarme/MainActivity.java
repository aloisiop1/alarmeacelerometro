package br.com.ppa.alarme;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    ConstraintLayout viewTela;

    ImageButton arme;
    ImageButton desarme;
    ImageView status;
    TextView coordenadas;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    MediaPlayer mp;

    float sensorX;
    float sensorY;
    float sensorZ;

    boolean armado = false;
    boolean disparo = false;

    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewTela = (ConstraintLayout) findViewById(R.id.tela);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        arme = findViewById(R.id.botaoArme);
        desarme = findViewById(R.id.botaoDesarme);
        status = findViewById(R.id.status);

        coordenadas = findViewById(R.id.coordenadas);

        //mp = MediaPlayer.create(this, R.raw.siren);


        arme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                armado = true;
                viewTela.setBackgroundColor(Color.rgb(255,109,0) );
                mudarStatus("@drawable/status_arme");

            }
        });

        desarme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                armado = false;
                disparo = false;

                viewTela.setBackgroundColor(Color.rgb(245,245,245) );
                mudarStatus("@drawable/status_desarme");

                try {

                    if( mp != null ){
                        mp.stop();
                        mp.release();
                    }
                }catch (Exception e){

                }


            }
        });

        lastUpdate = System.currentTimeMillis();

    }

    private void mudarStatus(String imagem){
        Resources resources =  MainActivity.this.getResources();
        int idFoto = resources.getIdentifier(imagem, "drawable",MainActivity.this.getPackageName() );
        Drawable drawable = resources.getDrawable(idFoto);
        status.setImageDrawable(drawable);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

//        getAccelerometer(event);

        coordenadas.setText( "X  = " + sensorX + ", Y = " + sensorY + ", Z = " + sensorZ);

        if( armado ){
            if( Math.abs(sensorX - event.values[0]) > 1 ){
                viewTela.setBackgroundColor(Color.rgb(255,0,0) );
                mudarStatus("@drawable/status_sirene_tocando");

                if( ! disparo ){

                    try {

                        if( mp != null ){
                            mp.stop();
                            mp.release();
                        }

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }


                    mp = MediaPlayer.create(this, R.raw.siren);
                    mp.start();

                    disparo = true;
                }


            }
        }

        sensorX = event.values[0];
        sensorY = event.values[1];
        sensorZ = event.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void getAccelerometer(SensorEvent event) {

        float[] values = event.values;

        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];

        float accelationSquareRoot = (x * x + y * y + z * z)
                / (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
        long actualTime = event.timestamp;
        if (accelationSquareRoot >= 2) //
        {
            if (actualTime - lastUpdate < 200) {
                return;
            }
            lastUpdate = actualTime;

            Toast.makeText(this, "Device was shuffed", Toast.LENGTH_SHORT)
                    .show();

        }
    }
}

package com.example.griselda.crono;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;

import static android.app.AlarmManager.RTC_WAKEUP;

public class MainActivity extends AppCompatActivity {

    Button contar, pausa;
    CountDownTimer countDownTimer;
    CountDownTimer countRest;
    EditText minutes,seconds;
    EditText restMinutes,restSeconds,editSets;
    SeekBar seekBar;
    AudioManager audioManager;
    Application myapp;

    //Alarma
    PendingIntent pendingIntent;
    AlarmManager alarmManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //elimina el nombre del proyecto en el layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initialize();
        validateEditTime();
        //startHandler();


        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        contar.setEnabled(true); // activado
        pausa.setEnabled(false); // desactivado

        //Alarma

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Crear un intent para la clase de alarm receiver
        final Intent my_intent = new Intent(this, AlarmReceiver.class);

        ////////Fin alarma///////////////////////////
        contar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Alarma
                //crear un pending. Delay del intent
                pendingIntent = PendingIntent.getBroadcast(MainActivity.this,0,my_intent,PendingIntent.FLAG_UPDATE_CURRENT);

                //Set alarmManager
                // Realizar la repetición cada 5 segundos.
                // Con el primer parámetro estamos indicando que se continue ejecutando
                // aunque el dsipositivo este con la pantalla apagada.

                alarmManager.set(RTC_WAKEUP, System.currentTimeMillis()+5*1000,pendingIntent);

               // LanzarTemporizador();
                startHandler();
            }
        });



        pausa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contar.setEnabled(true);
                pausa.setEnabled(false);
                //cancel();
                //LanzarTemporizador();
            }
        });

    }

    private void initialize() {
        contar = (Button) findViewById(R.id.botonContar);
        pausa = (Button) findViewById(R.id.botonPausa);
        minutes = (EditText) findViewById(R.id.textMinutes);
        seconds = (EditText) findViewById(R.id.textSeconds);
        restMinutes = (EditText) findViewById(R.id.editRestMinutes);
        restSeconds = (EditText) findViewById(R.id.editRestSeconds);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        editSets = (EditText) findViewById(R.id.editSets);
    }

    public void LanzarTemporizador(){
        Intent i = new Intent(this,Temporizador.class);
        i.putExtra("minutes",minutes.getText()+"");
        i.putExtra("seconds",seconds.getText()+"");
        i.putExtra("sets",editSets.getText()+"");
        i.putExtra("restMinutes",restMinutes.getText()+"");
        i.putExtra("restSeconds",restSeconds.getText()+"");
        startActivity(i);
    }

    public void startHandler(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LanzarTemporizador();
            }
        });
    }

    /*
    private void playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.campana_1);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        seekBar.setMax(maxVolume);
        seekBar.setProgress(curVolume);

        mediaPlayer.start();
    }

*/
    ///**********************METODOS AXULIARES*****************************


    private boolean isEmptyEditText(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    private int convertStringToInt(String number){
       return  Integer.parseInt(number);
    }

    //valida que segundos y minutos no sean mayores a 60
    private void validateEditTime(){
        validateTime(minutes);
        validateTime(seconds);
        validateTime(restMinutes);
        validateTime(restSeconds);
    }


    private void validateCantSets(){
        if(isEmptyEditText(editSets)){
            editSets.setText("1");
        }
    }

    private void validateTime(final EditText edit){
        edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!isEmptyEditText(edit)){
                    int num = convertStringToInt(edit.getText().toString());
                    if(num >= 60){
                        edit.setText("59");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }
}

package com.example.griselda.crono;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import static android.app.AlarmManager.RTC_WAKEUP;

/**
 * Created by Griselda on 15/01/2017.
 */

public class Temporizador extends Activity {
    //public static permite acceder a las variables desde otras clases
    EditText minutes, seconds;
    TextView nroSets,textView;
    String restMinutes, restSeconds;
    Button pausar, cancelar;
    FormatClass format;
    CountDownTimer countDownTimer,countDownTimerRest;
    String sets;
    MediaPlayer mediaPlayer;
    // State 1 sets, state 2 rest
    int state = 1;

    //Alarma
    PendingIntent pendingIntent;
    AlarmManager alarmManager;
    Intent my_intent;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //elimina el nombre del proyecto en el layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.temporizador);
        initialize();/**********************************************************/
        setEditText();
        //impide editar mientras corre
        setEnableFalseEditText();
        format = new FormatClass();
        mediaPlayer = MediaPlayer.create(this, R.raw.campana_1);
        /**
         * el metodo setVolumeControlStream(), el cual le dice a android que cuando el usuario presione el boton de subir/bajar
         * el volumen, mientras esta aplicacion esta activa,
         * debera ajustar el volumen multimedia en vez del volumen de llamada.
         */
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //Alarma
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //Crear un intent para la clase de alarm receiver
         my_intent = new Intent(this, AlarmReceiver.class);

        startSet();
        //startHandler();
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countDownTimer.cancel();
                lanzarMainActivity();
            }
        });

        pausar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pausar.getText().equals("Pausar")) {
                    pausar.setText("Reanudar");
                    //countDownTimer.cancel();
                    cancelCount();
                } else {
                     restartCount();
                    //startHandler();
                    pausar.setText("Pausar");
                }


            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void cancelCount(){
        if(state == 1){
            countDownTimer.cancel();
        }else{
            countDownTimerRest.cancel();
        }
    }

    public void restartCount(){
        if(state==1){
            startSet();
        }else{
            startRest();
        }
    }

    public void startHandler(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                startSet();
            }
        });
    }


    public void setEditText() {
        this.minutes.setText(getIntent().getStringExtra("minutes"));
        this.seconds.setText(getIntent().getStringExtra("seconds"));
        sets = getIntent().getStringExtra("sets");
        if (isEmptySet()) {
            sets = "1";
        }
        restMinutes = getIntent().getStringExtra("restMinutes");
        restSeconds = getIntent().getStringExtra("restSeconds");
        nroSets.setText(sets);
    }

    public void initialize() {
        minutes = (EditText) findViewById(R.id.textMinutesTemp);
        seconds = (EditText) findViewById(R.id.textSecondsTemp);
        pausar = (Button) findViewById(R.id.botonPausaTemp);
        cancelar = (Button) findViewById(R.id.botonCancelarTemp);
        nroSets = (TextView) findViewById(R.id.nroSets);
        textView = (TextView) findViewById(R.id.textView);

    }

    private void setEnableFalseEditText() {
        minutes.setEnabled(false);
        seconds.setEnabled(false);
    }

    private void startAlarmManager(){
        //Alarma
        //crear un pending. Delay del intent
        pendingIntent = PendingIntent.getBroadcast(Temporizador.this,0,my_intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //Set alarmManager
        // Realizar la repetición cada 5 segundos.
        // Con el primer parámetro estamos indicando que se continue ejecutando
        // aunque el dsipositivo este con la pantalla apagada.

        //alarmManager.set(RTC_WAKEUP, System.currentTimeMillis()+5*1000,pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis(),5*1000, pendingIntent);

    }

    private void startSet() {
        Log.e("start set minutes", String.valueOf(minutes.getText()));
        int duration = format.formatTimeToMilliseconds(minutes, seconds);
        Log.e("start set", "despues format");
        startAlarmManager();
        //setEnableFalseEditText(hour,minutes,seconds);
        //Creo el contador
        countDownTimer = new CountDownTimer(duration, 1000) { //15000,1000
            @Override
            public void onTick(long l) {
                minutes.setText(String.format(format.formatMinutes(l)));
                seconds.setText(String.format(format.formatSeconds(l)));
            }


            @Override
            public void onFinish() {
                if (finishRoutine()) {
                    lanzarMainActivity();
                    displayNotification();
                    alarmManager.cancel(pendingIntent);
                } else {
                    restSet();
                    //lanzarRest();
                    startRest();
                }
                state=2;
                playSound();
            }
        };
        countDownTimer.start();
    }

    public void startRest(){
        textView.setText("Descanso");
        nroSets.setText("");
        int duration = format.formatTimeToMilliseconds(restMinutes,restSeconds);
        countDownTimerRest = new CountDownTimer(duration,1000) {
            @Override
            public void onTick(long l) {
                minutes.setText(String.format(format.formatMinutes(l)));
                seconds.setText(String.format(format.formatSeconds(l)));
            }

            @Override
            public void onFinish() {
                playSound();
                minutes.setText(getIntent().getStringExtra("minutes"));
                seconds.setText(getIntent().getStringExtra("seconds"));
                state = 1;
                textView.setText("A trabajar!!");
                nroSets.setText(sets);
                startSet();
            }
        };
        countDownTimerRest.start();
    }

    public boolean finishRoutine() {
        return Integer.parseInt(sets) == 1;
    }

    public void lanzarMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void restSet() {
        int set = Integer.parseInt(sets) - 1;
        sets = set + "";
        nroSets.setText(sets);
    }

    private boolean isEmptySet() {
        return sets.trim().length() == 0;
    }

    public void playSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.campana_1);
        mediaPlayer.start();
    }


    public void displayNotification() {

        Notification.Builder builder = new Notification.Builder(Temporizador.this);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        // Tienes que usar un PendingIntent para especificar qué hacer cuando el usuario pulsa sobre la notificación o
        // las acciones dentro de ella como es el caso de Gmail.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setSmallIcon(R.drawable.timeclock)
                .setContentTitle("Music player8888888888")
                // - Establece el PendingIntent que se llamará cuando se pulse sobre la notificación
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = builder.getNotification();
        notificationManager.notify(R.drawable.notification_template_icon_bg, notification);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Temporizador Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

}

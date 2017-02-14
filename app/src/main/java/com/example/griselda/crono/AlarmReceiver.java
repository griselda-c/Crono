package com.example.griselda.crono;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Griselda on 10/01/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("estamos en el reciver","yea");

    }

}

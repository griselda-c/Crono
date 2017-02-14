package com.example.griselda.crono;

import android.widget.EditText;

/**
 * Created by Griselda on 15/01/2017.
 */

public class FormatClass {


    public int formatTimeToMilliseconds(EditText editMinutes, EditText editSeconds) {
        return  convertMinutesToMilliseconds(editMinutes) + convertSecondsToMilliseconds(editSeconds);
    }


    public int formatTimeToMilliseconds(String editMinutes, String editSeconds) {
        return  convertMinutesToMilliseconds(editMinutes) + convertSecondsToMilliseconds(editSeconds);
    }

    /*
    private int convertHourToMilliseconds(EditText editHour) {
        if (isEmptyEditText(editHour)) {
            return 0;
        } else {
            int hour2 = Integer.parseInt(editHour.getText().toString());
            return hour2 * 60 * 60 * 1000;
        }
    }
*/
    private int convertMinutesToMilliseconds(EditText editMinutes) {
        if (isEmptyEditText(editMinutes)) {
            return 0;
        } else {
            int minutes2 = Integer.parseInt(editMinutes.getText().toString());
            return minutes2 * 60 * 1000;
        }
    }

    private int convertMinutesToMilliseconds(String minutes) {
            if(minutes.length()==0){
                return 0;
            }else{
                return Integer.parseInt(minutes) * 60 * 1000;
            }


    }

    private int convertSecondsToMilliseconds(String seconds) {
            return Integer.parseInt(seconds) * 1000;
    }

    private int convertSecondsToMilliseconds(EditText editSecond) {
        if (isEmptyEditText(editSecond)) {
            return 0;
        } else {
            int seconds2 = Integer.parseInt(editSecond.getText().toString());
            return seconds2 * 1000;
        }
    }

    // Metodos que convierte los milisegundos en horas minutos segundos
    //para imprimir en pantalla

    public String formatSeconds(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        return twoDigitString(seconds);
    }

    public String formatMinutes(long milliseconds) {
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        return twoDigitString(minutes);
    }


    /*public String formatHours(long milliseconds) {
        int hour = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return twoDigitString(hour);
    }

*/
    private String twoDigitString(long number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }

    private boolean isEmptyEditText(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

}

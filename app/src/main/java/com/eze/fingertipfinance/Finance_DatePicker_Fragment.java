package com.eze.fingertipfinance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.format.Time;
import android.widget.DatePicker;

import java.util.Calendar;


public class Finance_DatePicker_Fragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    public interface SaveDateListener {
        void didFinishDatePickerDialog(Time selectedTime);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        Time selectedTime = new Time();
        selectedTime.set(0,0,0,day, month, year);

        SaveDateListener activity = (SaveDateListener) getActivity();
        activity.didFinishDatePickerDialog(selectedTime);
        getDialog().dismiss();
    }
}

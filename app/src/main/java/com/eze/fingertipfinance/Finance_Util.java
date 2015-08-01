package com.eze.fingertipfinance;

import android.text.format.Time;

import java.util.Calendar;

/**
 * Created by Erik on 1/26/2015.
 */
public class Finance_Util {

    public static Time adjustDate(String occurstype, Time date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date.toMillis(false));
        if(occurstype.equals(Finance_Constants.OCCURTYPE_WEEKLY)){
            c.add(Calendar.DAY_OF_MONTH, 7);
        }
        else if(occurstype.equals(Finance_Constants.OCCURTYPE_BIWEEKLY)){
            c.add(Calendar.DAY_OF_MONTH, 14);
        }
        else if(occurstype.equals(Finance_Constants.OCCURTYPE_MONTHLY)){
            c.add(Calendar.MONTH, 1);
        }
        date.set(c.getTimeInMillis());
        return date;
    }
}

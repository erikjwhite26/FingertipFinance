package com.eze.fingertipfinance;

import java.util.Calendar;

/**
 * Created by Erik on 3/5/2015.
 */
public class Finance_Cell_Day {

    private Calendar day = Calendar.getInstance();
    private float balance = 0;

    public Finance_Cell_Day(){}

    public Finance_Cell_Day(Calendar day, float balance){
        this.day = day;
        this.balance = balance;
    }

    public Calendar getDay() {
        return day;
    }

    public void setDay(Calendar day) {
        this.day = day;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }
}

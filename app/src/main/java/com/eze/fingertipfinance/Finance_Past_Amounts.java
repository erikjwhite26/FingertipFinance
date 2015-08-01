package com.eze.fingertipfinance;

import android.text.format.Time;

/**
 * Created by Erik on 2/8/2015.
 */
public class Finance_Past_Amounts {

    private int pastAmountId;
    private int financeId;
    private float pastAmount;
    private Time pastAmountDate;

    public Finance_Past_Amounts(){
        pastAmountId = -1;
        financeId = -1;
        pastAmount = 0;
        pastAmountDate = null;
    }

    public int getPastAmountId() {
        return pastAmountId;
    }

    public void setPastAmountId(int pastAmountId) {
        this.pastAmountId = pastAmountId;
    }

    public int getFinanceId() {
        return financeId;
    }

    public void setFinanceId(int financeId) {
        this.financeId = financeId;
    }

    public float getPastAmount() {
        return pastAmount;
    }

    public void setPastAmount(float pastAmount) {
        this.pastAmount = pastAmount;
    }

    public Time getPastAmountDate() {
        return pastAmountDate;
    }

    public void setPastAmountDate(Time pastAmountDate) {
        this.pastAmountDate = pastAmountDate;
    }
}

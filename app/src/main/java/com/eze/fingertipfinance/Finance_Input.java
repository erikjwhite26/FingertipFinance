package com.eze.fingertipfinance;

import android.text.format.Time;

/**
 * Created by Erik on 8/17/2014.
 */
public class Finance_Input {

    private int id;
    private String type;
    private String name;
    private float amount;
    private Time occurs;
    private String occurstype;

    public Finance_Input(){
        id = -1;
        name = "";
        amount = 0;
        type = "";
        occurs = null;
        occurstype = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public Time getOccurs() {
        return occurs;
    }

    public void setOccurs(Time occurs) {
        this.occurs = occurs;
    }

    public String getOccurstype() {
        return occurstype;
    }

    public void setOccurstype(String occurstype) {
        this.occurstype = occurstype;
    }

}

package com.eze.fingertipfinance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Erik on 8/17/2014.
 */
public class Finance_DB_Helper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "fingertip_finance.db";
    private static final int DATABASE_VERSION = 6;

    //Database creation sql statement
    //type: income/bill, occurstype: weekly/bi-weekly/monthly
    private static final String CREATE_TABLE_FINANCE = "create table finance (_id integer primary key autoincrement, "
            + "type text, name text not null, amount float, occurs text, occurstype text)";
    private static final String CREATE_TABLE_PAST_AMOUNTS = "create table past_amounts (_id integer primary key autoincrement, "
            + "finance_id integer, past_amount float, past_amount_date text)";


    public Finance_DB_Helper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database){
        database.execSQL(CREATE_TABLE_FINANCE);
        database.execSQL(CREATE_TABLE_PAST_AMOUNTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        Log.w(Finance_DB_Helper.class.getName(), "Upgrading database from version " + oldVersion + "to "
                + newVersion + ", which will destroy all old data");
//        db.execSQL("DROP TABLE IF EXISTS finance");
//        onCreate(db);
        try{
//            db.execSQL("ALTER TABLE finance ADD COLUMN column text");
        }
        catch(Exception e){
            //do nothing
        }
    }
}

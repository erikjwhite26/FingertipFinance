package com.eze.fingertipfinance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.format.Time;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Erik on 8/17/2014.
 */
public class Finance_DataSource {

    private SQLiteDatabase database;
    private Finance_DB_Helper dbHelper;

    public Finance_DataSource(Context context){
        dbHelper = new Finance_DB_Helper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public boolean insertFinance(Finance_Input input){
        boolean didSucceed = false;
        try{
            ContentValues initialValues = new ContentValues();

            initialValues.put("type", input.getType());
            initialValues.put("name", input.getName());
            initialValues.put("amount", input.getAmount());
            initialValues.put("occurs", String.valueOf(input.getOccurs().toMillis(false)));
            initialValues.put("occurstype", input.getOccurstype());

            didSucceed = database.insert("finance", null, initialValues) > 0;
        }
        catch (Exception e){
            //Do nothing...cause I'm lazy
        }
        return didSucceed;
    }

    public boolean updateFinance(Finance_Input input){
        boolean didSucceed = false;
        try{
            Long rowId = (long)input.getId();
            ContentValues updateValues = new ContentValues();

            updateValues.put("type", input.getType());
            updateValues.put("name", input.getName());
            updateValues.put("amount", input.getAmount());
            updateValues.put("occurs", String.valueOf(input.getOccurs().toMillis(false)));
            updateValues.put("occurstype", input.getOccurstype());

            didSucceed = database.update("finance", updateValues, "_id=" + rowId, null) > 0;
        }
        catch (Exception e){
            //Do nothing...cause I'm lazy
        }
        return didSucceed;
    }

    public int getLastFinanceId(){
        int lastId = -1;
        Cursor cursor = null;
        try{
            String query = "Select MAX(_id) from finance";
            cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
        }
        catch(Exception e){
            lastId = -1;
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return lastId;
    }

    public ArrayList<Finance_Input> getFinanceInputs(String sortStr){
        ArrayList<Finance_Input> inputs = new ArrayList<Finance_Input>();
        Cursor cursor = null;
        try{
            if(sortStr.equals(Finance_Constants.SORT_AMOUNT)){
                sortStr += " desc";
            }
            String query = "SELECT * FROM finance order by type desc, " + sortStr;
            cursor = database.rawQuery(query, null);

            Finance_Input input;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                input = new Finance_Input();
                input.setId(cursor.getInt(0));
                input.setType(cursor.getString(1));
                input.setName(cursor.getString(2));
                input.setAmount(cursor.getFloat(3));
                Time t = new Time();
                t.set(Long.valueOf(cursor.getString(4)));
                input.setOccurs(t);
                input.setOccurstype(cursor.getString(5));

                inputs.add(input);
                cursor.moveToNext();
            }
        }
        catch(Exception e){
            inputs = new ArrayList<Finance_Input>();
        }
        finally{
            if(cursor != null) {
                cursor.close();
            }
        }
        return inputs;
    }

    public Finance_Input getFinanceInput(int id){
        Finance_Input input = new Finance_Input();
        Cursor cursor = null;
        try{
            String query = "SELECT * FROM finance where _id=" + id;
            cursor = database.rawQuery(query, null);

            if(cursor.moveToFirst()){
                input.setId(cursor.getInt(0));
                input.setType(cursor.getString(1));
                input.setName(cursor.getString(2));
                input.setAmount(cursor.getFloat(3));
                Time t = new Time();
                t.set(Long.valueOf(cursor.getString(4)));
                input.setOccurs(t);
                input.setOccurstype(cursor.getString(5));
            }
        }
        catch(Exception e){
            input = new Finance_Input();
        }
        finally{
            if(cursor != null) {
                cursor.close();
            }
        }
        return input;
    }

    public boolean deleteInput(int inputId){
        boolean didDelete = false;
        try{
            didDelete = database.delete("finance", "_id=" + inputId, null) > 0;
        }
        catch(Exception e){
            //be lazy
        }
        return didDelete;
    }

    public boolean insertPastAmounts(Finance_Past_Amounts input){
        boolean didSucceed = false;
        try{
            ContentValues initialValues = new ContentValues();

            initialValues.put("finance_id", input.getFinanceId());
            initialValues.put("past_amount", input.getPastAmount());
            initialValues.put("past_amount_date", String.valueOf(input.getPastAmountDate().toMillis(false)));

            didSucceed = database.insert("past_amounts", null, initialValues) > 0;
        }
        catch (Exception e){
            //Do nothing...cause I'm lazy
        }
        return didSucceed;
    }

    public int getLastPastAmountsId(){
        int lastId = -1;
        Cursor cursor = null;
        try{
            String query = "Select MAX(_id) from finance";
            cursor = database.rawQuery(query, null);

            cursor.moveToFirst();
            lastId = cursor.getInt(0);
        }
        catch(Exception e){
            lastId = -1;
        }
        finally {
            if(cursor != null) {
                cursor.close();
            }
        }
        return lastId;
    }

    public ArrayList<Finance_Past_Amounts> getFinancePastAmounts(int id){
        ArrayList<Finance_Past_Amounts> inputs = new ArrayList<Finance_Past_Amounts>();
        Cursor cursor = null;
        try{
            String query = "SELECT * FROM past_amounts where finance_id = " + id + " order by past_amount_date desc";
            cursor = database.rawQuery(query, null);

            Finance_Past_Amounts input;
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                input = new Finance_Past_Amounts();
                input.setPastAmountId(cursor.getInt(0));
                input.setFinanceId(cursor.getInt(1));
                input.setPastAmount(cursor.getFloat(2));
                Time t = new Time();
                t.set(Long.valueOf(cursor.getString(3)));
                input.setPastAmountDate(t);

                inputs.add(input);
                cursor.moveToNext();
            }
        }
        catch(Exception e){
            inputs = new ArrayList<Finance_Past_Amounts>();
        }
        finally{
            if(cursor != null) {
                cursor.close();
            }
        }
        return inputs;
    }

    public boolean deletePastAmounts(int id){
        boolean didDelete = false;
        try{
            didDelete = database.delete("past_amounts", "finance_id=" + id, null) > 0;
        }
        catch(Exception e){
            return didDelete;
        }
        return didDelete;
    }
}

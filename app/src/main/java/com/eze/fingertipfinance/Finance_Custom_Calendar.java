package com.eze.fingertipfinance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Erik on 2/16/2015.
 */
public class Finance_Custom_Calendar extends FragmentActivity {

    public GridView calendar;
    private Map<Integer, Finance_Cell_Day> daysMap = new HashMap<Integer, Finance_Cell_Day>();
    private Calendar currentMonth = Calendar.getInstance();

    private Finance_DataSource ds = new Finance_DataSource(Finance_Custom_Calendar.this);
    private ArrayList<Finance_Input> inputs = new ArrayList<Finance_Input>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_calendar);

        setDaysMap();
        setInputs();
        initCalendar();

        calendar = (GridView) findViewById(R.id.calendar);
        calendar.setAdapter(new GridCellAdapter(this));

        initPreviousMonth();
        initNextMonth();
    }

    private void setDaysMap() {
        for(int i=0; i<42; i++){
            daysMap.put(i,new Finance_Cell_Day());
        }
    }

    private void setInputs() {
        try {
            ds.open();
            inputs = ds.getFinanceInputs(Finance_Constants.SORT_DATE);
        }
        catch(Exception e){
            inputs = new ArrayList<Finance_Input>();
        }
        finally {
            ds.close();
        }
    }

    private void initPreviousMonth(){
        ImageView prevMonth = (ImageView) findViewById(R.id.prevMonth);
        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMonth.add(Calendar.MONTH, -1);
                calendar = (GridView) findViewById(R.id.calendar);
                calendar.setAdapter(new GridCellAdapter(getBaseContext()));
                initCalendar();
            }
        });
    }

    private void initNextMonth() {
        ImageView nextMonth = (ImageView) findViewById(R.id.nextMonth);
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentMonth.add(Calendar.MONTH, 1);
                calendar = (GridView) findViewById(R.id.calendar);
                calendar.setAdapter(new GridCellAdapter(getBaseContext()));
                initCalendar();
            }
        });
    }

    //initialize the gridview calendar
    private void initCalendar() {
        setCurrentMonth();

        Calendar firstDayOfMonth = Calendar.getInstance();
        firstDayOfMonth.set(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH), 1,0,0,0);
        int dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);

        //get previous days if needed
        while (dayOfWeek > 1) {
            firstDayOfMonth.add(Calendar.DAY_OF_MONTH, -1);
            dayOfWeek = firstDayOfMonth.get(Calendar.DAY_OF_WEEK);
        }
        //add days to list
        int index = 0;
        Calendar compareMonth = Calendar.getInstance();
        compareMonth.set(currentMonth.get(Calendar.YEAR), currentMonth.get(Calendar.MONTH)+1, currentMonth.get(Calendar.DAY_OF_MONTH),0,0,0);
        while ((firstDayOfMonth.get(Calendar.MONTH) != compareMonth.get(Calendar.MONTH))) {
            daysMap.get(index).getDay().set(firstDayOfMonth.get(Calendar.YEAR), firstDayOfMonth.get(Calendar.MONTH), firstDayOfMonth.get(Calendar.DAY_OF_MONTH),0,0,0);
            daysMap.get(index).setBalance(getBalance(daysMap.get(index).getDay()));
            firstDayOfMonth.add(Calendar.DAY_OF_MONTH, 1);
            index++;
        }
        //add extra days if needed
        while (index < 42) {
            daysMap.get(index).getDay().set(firstDayOfMonth.get(Calendar.YEAR), firstDayOfMonth.get(Calendar.MONTH), firstDayOfMonth.get(Calendar.DAY_OF_MONTH),0,0,0);
            daysMap.get(index).setBalance(getBalance(daysMap.get(index).getDay()));
            firstDayOfMonth.add(Calendar.DAY_OF_MONTH, 1);
            index++;
        }
    }

    //set the month name
    private void setCurrentMonth() {
        TextView currentMonthText = (TextView) findViewById(R.id.currentMonth);
        currentMonthText.setText(currentMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()) + " " + currentMonth.get(Calendar.YEAR));
    }

    private float getBalance(Calendar cellDate) {
        float balance = 0;

        Time selectedDate = new Time();
        selectedDate.set(cellDate.getTimeInMillis());

        Time inputDate = new Time();

        for (Finance_Input input : inputs) {
            inputDate.set(input.getOccurs().toMillis(false));
            boolean onetime = false;
            while ((inputDate.before(selectedDate) || inputDate.equals(selectedDate)) && !onetime && input.getAmount() > 0) {
                if (input.getType().equals(Finance_Constants.INCOME)) {
                    balance += input.getAmount();
                } else {
                    balance -= input.getAmount();
                }
                inputDate = Finance_Util.adjustDate(input.getOccurstype(), inputDate);
                if (input.getOccurstype().equals(Finance_Constants.OCCURTYPE_ONETIME)) {
                    onetime = true;
                }
            }
        }

        return balance;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.finance__calendar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_summary){
            Intent intent = new Intent(Finance_Custom_Calendar.this, Finance_Summary.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public class GridCellAdapter extends BaseAdapter {
        private final Context context;

        public GridCellAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return daysMap.size();
        }

        @Override
        public Object getItem(int i) {
            return daysMap.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView button;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.grid_cell, viewGroup, false);
                button = (TextView) view.findViewById(R.id.calendar_cell);
            }
            else{
                button = (TextView) view.findViewById(R.id.calendar_cell);
            }

            initGridCell(button,i);

            return view;
        }

        private void initGridCell(TextView gridcell, int i) {
            // Get a reference to the Day gridcell
            final Calendar cellDate = daysMap.get(i).getDay();
            float balance = daysMap.get(i).getBalance();
            if(balance < 0){
                gridcell.setTextColor(Color.parseColor("#B40431"));
            }
            else if(balance < 100){
                gridcell.setTextColor(Color.parseColor("#868A08"));
            }
            else{
                gridcell.setTextColor(Color.parseColor("#088A08"));
            }

            if(cellDate != null) {
                if (cellDate.get(Calendar.MONTH) != currentMonth.get(Calendar.MONTH)) {
                    gridcell.setTextColor(Color.parseColor("#A4A4A4"));
                }
                String cellString = cellDate.get(Calendar.DAY_OF_MONTH) + "\n\n" + String.format("%.0f", balance);
                gridcell.setText(cellString);
            }
            gridcell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(cellDate != null) {
                        Bundle b = new Bundle();
                        b.putLong("date", cellDate.getTimeInMillis());
                        FragmentManager fm = getSupportFragmentManager();
                        Finance_Calendar_Dialog dialog = new Finance_Calendar_Dialog();
                        dialog.setArguments(b);
                        dialog.show(fm, "ShowInputs");
                    }
                }
            });
        }
    }
}

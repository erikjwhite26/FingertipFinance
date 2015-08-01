package com.eze.fingertipfinance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CalendarView;

public class Finance_Calendar extends FragmentActivity {

    private Time date = new Time();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finance__calendar);

        CalendarView calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Time selectedDate = new Time();
                selectedDate.set(dayOfMonth, month, year,0,0,0);
                if(date.toMillis(false) != selectedDate.toMillis(false)) {
                    date.set(dayOfMonth, month, year,0,0,0);
                    Bundle b = new Bundle();
                    b.putLong("date", date.toMillis(false));
                    FragmentManager fm = getSupportFragmentManager();
                    Finance_Calendar_Dialog dialog = new Finance_Calendar_Dialog();
                    dialog.setArguments(b);
                    dialog.show(fm, "ShowInputs");
                }
            }
        });
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
            Intent intent = new Intent(Finance_Calendar.this, Finance_Summary.class);
            this.startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}

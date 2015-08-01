package com.eze.fingertipfinance;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by Erik on 9/6/2014.
 */
public class Finance_Calendar_Dialog extends DialogFragment {

    Logger logger = Logger.getLogger(Finance_Calendar_Dialog.class.getName());
    private float total = 0;
    private Map<Integer, Integer> inputOccurances = new HashMap<Integer, Integer>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.dialog_finance_calendar, container);
        Bundle b = getArguments();
        Time selectedDate = new Time();
        selectedDate.set(b.getLong("date"));

        getDialog().setTitle(DateFormat.format("MM/dd/yyyy", selectedDate.toMillis(false)).toString());

        Finance_DataSource ds = new Finance_DataSource(this.getActivity());
        try {
            ds.open();
            final ArrayList<Finance_Input> inputs = ds.getFinanceInputs(Finance_Constants.SORT_DATE);
            Finance_Adapter_Calendar inputAdapter;

            final ArrayList<Finance_Input> applicableInputs = setApplicableInputs(inputs, selectedDate);
            if(applicableInputs.size() > 0){
                inputAdapter = new Finance_Adapter_Calendar(this.getActivity(), applicableInputs, inputOccurances);
                ListView listView = (ListView) view.findViewById(R.id.calendar_list);
                listView.setAdapter(inputAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                        Finance_Input selectedInput = applicableInputs.get(position);
                        Intent intent = new Intent(Finance_Calendar_Dialog.this.getActivity(), Finance_Add_Input.class);
                        intent.putExtra("inputid", selectedInput.getId());
                        intent.putExtra("type", selectedInput.getType());
                        startActivity(intent);
                    }
                });
            }
            TextView totalAmount = (TextView) view.findViewById(R.id.calendar_total_amount);
            totalAmount.setText(String.format("%.2f", total));

        }
        catch(Exception e){
            logger.info("Error calling database to populate list in Finance_Calendar_Dialog: \n" + e);
        }
        finally {
            ds.close();
        }
        return view;
    }

    private ArrayList<Finance_Input> setApplicableInputs(ArrayList<Finance_Input> inputs, Time selectedDate) {
        ArrayList<Finance_Input> applicableInputs = new ArrayList<Finance_Input>();

        for(Finance_Input input : inputs){
            Time inputDate = input.getOccurs();
            boolean onetime = false;
            if((inputDate.before(selectedDate) || inputDate.equals(selectedDate)) && input.getAmount() > 0){
                Finance_Input temp = new Finance_Input();
                temp.setId(input.getId());
                temp.setType(input.getType());
                temp.setName(input.getName());
                temp.setAmount(input.getAmount());
                temp.setOccurstype(input.getOccurstype());
                Time t = new Time();
                t.set(inputDate);
                temp.setOccurs(t);
                applicableInputs.add(temp);
                int integerOccurance = 0;
                while((inputDate.before(selectedDate) || inputDate.equals(selectedDate)) && !onetime && input.getAmount() > 0){
                    inputDate = Finance_Util.adjustDate(temp.getOccurstype(), inputDate);
                    if(temp.getOccurstype().equals(Finance_Constants.OCCURTYPE_ONETIME)){
                        onetime = true;
                    }
                    integerOccurance++;
                }
                if(temp.getType().equals(Finance_Constants.INCOME)){
                    total += (temp.getAmount() * integerOccurance);
                }
                else{
                    total -= (temp.getAmount() * integerOccurance);
                }
                inputOccurances.put(temp.getId(), integerOccurance);
            }

        }

        return applicableInputs;
    }
}

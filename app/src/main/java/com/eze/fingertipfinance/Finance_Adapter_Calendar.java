package com.eze.fingertipfinance;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Erik on 8/17/2014.
 */
public class Finance_Adapter_Calendar extends ArrayAdapter<Finance_Input> {

    private ArrayList<Finance_Input> inputs = new ArrayList<Finance_Input>();
    private Map<Integer, Integer> inputOccurances = new HashMap<Integer, Integer>();
    private Context adapterContext;

    public Finance_Adapter_Calendar(Context context, ArrayList<Finance_Input> inputs, Map<Integer, Integer> inputOccurances){
        super(context, R.layout.list_item_calendar, inputs);
        adapterContext = context;
        this.inputs = inputs;
        this.inputOccurances = inputOccurances;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            Finance_Input input = inputs.get(position);

            if(v == null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item_calendar, null);
            }
            TextView name = (TextView) v.findViewById(R.id.list_itemc_name);
            TextView amount = (TextView) v.findViewById(R.id.list_itemc_amount);
            TextView type = (TextView) v.findViewById(R.id.list_itemc_type);
            TextView occurances = (TextView) v.findViewById(R.id.list_itemc_occurances);
            name.setText(input.getName());
            amount.setText(String.format("%.2f", input.getAmount()));
            occurances.setText("x" + inputOccurances.get(input.getId()) + ":  " + String.format("%.2f", input.getAmount() * inputOccurances.get(input.getId())));
            type.setText(input.getType());

            //TODO update date to current month
            if(type.getText().equals(Finance_Constants.INCOME)){
                type.setTextColor(Color.rgb(0,100,0));
                type.setText(type.getText().toString().toUpperCase() + "   "
                        + DateFormat.format("MM/dd/yyyy", input.getOccurs().toMillis(false)).toString()
                        + "  " + input.getOccurstype().toLowerCase());
            }
            else{
                type.setTextColor(Color.RED);
                type.setText(type.getText().toString().toUpperCase() + "          "
                        + DateFormat.format("MM/dd/yyyy", input.getOccurs().toMillis(false)).toString()
                        + "  " + input.getOccurstype().toLowerCase());
            }
        }
        catch(Exception e){
            e.printStackTrace();
            e.getCause();
        }
        return v;
    }
}

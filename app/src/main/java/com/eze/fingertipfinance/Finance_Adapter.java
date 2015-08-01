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

/**
 * Created by Erik on 8/17/2014.
 */
public class Finance_Adapter extends ArrayAdapter<Finance_Input> {

    private ArrayList<Finance_Input> inputs;
    private Context adapterContext;

    public Finance_Adapter(Context context, ArrayList<Finance_Input> inputs){
        super(context, R.layout.list_item, inputs);
        adapterContext = context;
        this.inputs = inputs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        try{
            Finance_Input input = inputs.get(position);

            if(v == null){
                LayoutInflater vi = (LayoutInflater) adapterContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.list_item, null);
            }
            TextView name = (TextView) v.findViewById(R.id.list_item_name);
            TextView amount = (TextView) v.findViewById(R.id.list_item_amount);
            TextView type = (TextView) v.findViewById(R.id.list_item_type);
            name.setText(input.getName());
            amount.setText(String.format("%.2f", input.getAmount()));
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

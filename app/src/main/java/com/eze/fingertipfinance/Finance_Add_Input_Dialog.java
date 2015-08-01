package com.eze.fingertipfinance;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by Erik on 2/8/2015.
 */
public class Finance_Add_Input_Dialog extends DialogFragment{

    private float amountToAdd = 0;
    private EditText amountText = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dialog_finance_add_amount, container);
        getDialog().setTitle("Amount to Add");

        amountText = (EditText) view.findViewById(R.id.dialog_add_amount);
        amountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(amountText.getText().toString() != null && !amountText.getText().toString().equals("")) {
                    float amt = Float.valueOf(amountText.getText().toString());
                    setAmountToAdd(amt);
                }else{
                    setAmountToAdd(0);
                }
            }
        });

        initAddButton(view);
        initCancelButton(view);

        return view;
    }

    private void initAddButton(View view) {
        Button save = (Button) view.findViewById(R.id.dialog_add_add);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Finance_Add_Input activity = (Finance_Add_Input) getActivity();
                activity.setAddAmountValue(getAmountToAdd());

                Finance_Past_Amounts pastAmount = new Finance_Past_Amounts();
                pastAmount.setFinanceId(activity.getCurrentInput().getId());
                pastAmount.setPastAmount(getAmountToAdd());
                Time t = new Time();
                Calendar c = Calendar.getInstance();
                t.set(0,0,0,c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.MONTH),c.get(Calendar.YEAR));
                pastAmount.setPastAmountDate(t);

                Finance_DataSource ds = new Finance_DataSource(getActivity().getBaseContext());
                try {
                    ds.open();
                    int id = ds.getLastPastAmountsId();
                    pastAmount.setPastAmountId(id);
                    ds.insertPastAmounts(pastAmount);
                }
                catch(Exception e){
                    //be lazy
                }
                finally{
                    ds.close();
                }
                dismiss();
            }
        });
    }

    private void initCancelButton(View view) {
        Button cancel = (Button) view.findViewById(R.id.dialog_add_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAmountToAdd(0);
                Finance_Add_Input activity = (Finance_Add_Input) getActivity();
                activity.setAddAmountValue(getAmountToAdd());
                dismiss();
            }
        });
    }

    public float getAmountToAdd() {
        return amountToAdd;
    }

    public void setAmountToAdd(float amountToAdd) {
        this.amountToAdd = amountToAdd;
    }
}

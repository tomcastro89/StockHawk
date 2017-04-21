package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.udacity.stockhawk.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetails extends AppCompatActivity {

    @BindView(R.id.textview_symbol)
    TextView mTextViewSymbol;

    @BindView(R.id.textview_value)
    TextView mTextViewValue;

    Intent mIntentThatStartedThisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        mIntentThatStartedThisActivity = getIntent();

        if (mIntentThatStartedThisActivity != null) {
            if (mIntentThatStartedThisActivity.hasExtra("Symbol")) {
                String symbol = mIntentThatStartedThisActivity.getStringExtra("Symbol");
                mTextViewSymbol.setText(symbol);
            }
        }
    }
}


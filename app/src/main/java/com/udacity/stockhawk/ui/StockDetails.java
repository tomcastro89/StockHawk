package com.udacity.stockhawk.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockDetails extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    @BindView(R.id.textview_price)
    TextView mTextViewPrice;

    @BindView(R.id.textview_abs_change)
    TextView mTextViewAbsChange;

    @BindView(R.id.textview_perc_change)
    TextView mTextViewPerChange;

    @BindView(R.id.chart)
    LineChart mChart;

    Intent mIntentThatStartedThisActivity;
    private static final int CURSOR_LOADER_ID = 0;
    private static String symbol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_details);
        ButterKnife.bind(this);
        mIntentThatStartedThisActivity = getIntent();
        String symbol = "";
        if (mIntentThatStartedThisActivity != null) {
            if (mIntentThatStartedThisActivity.hasExtra("Symbol")) {
                symbol = mIntentThatStartedThisActivity.getStringExtra("Symbol");
                this.symbol = symbol;
                setTitle(symbol);
                getLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = new String[]{
                Contract._ID,
                Contract.Quote.COLUMN_SYMBOL,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
                Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
                Contract.Quote.COLUMN_HISTORY,
                Contract.Quote.COLUMN_PRICE};

        String selection = Contract._ID + " = ?";

        String[] selectionArgs = new String[]{symbol};

        String sortOrder = Contract.Quote.COLUMN_HISTORY + " DESC";

        return new CursorLoader(this,
                Contract.Quote.makeUriForStock(symbol),
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean dataAvailable = data.moveToFirst();
        if(dataAvailable){

            ArrayList<Entry> prices = new ArrayList<>();

            String currentPrice = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PRICE));
            String absChange = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_ABSOLUTE_CHANGE));
            String percChange = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_PERCENTAGE_CHANGE));
            String historyString = data.getString(data.getColumnIndex(Contract.Quote.COLUMN_HISTORY));

            ArrayList<String> dates = new ArrayList<>();

            String[] historyElement = historyString.split("\n");
            float x = 0;
            for(int counter=historyElement.length - 1; counter >= 0;counter--){
                String element = historyElement[counter];
                String timestampInMills = element.split(",")[0];
                String stringPriceWithLeadingSpace = element.split(",")[1];
                String stringPrice = stringPriceWithLeadingSpace.substring(1);
                Float price = Float.valueOf(stringPrice);

                Date date = new Date();
                date.setTime(Long.valueOf(timestampInMills));
                String dateString = new SimpleDateFormat("yyyy/MM").format(date);

                dates.add(dateString);
                prices.add(new Entry(x,price));

                x++;
            }

            createDiagram(symbol,prices,dates);

            if(Float.parseFloat(absChange) >=0){
                mTextViewAbsChange.setBackgroundColor(Color.GREEN);
                mTextViewPerChange.setBackgroundColor(Color.GREEN);
            }else{
                mTextViewAbsChange.setBackgroundColor(Color.RED);
                mTextViewPerChange.setBackgroundColor(Color.RED);
            }
            mTextViewPerChange.setText(percChange+"%");
            mTextViewAbsChange.setText(absChange);
            mTextViewPrice.setText(currentPrice);

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void createDiagram(String symbol, ArrayList<Entry> stockPrices, final ArrayList<String> dates) {

        LineDataSet pricesDataSet = new LineDataSet(stockPrices,symbol);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.WHITE);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return dates.get((int) value);
            }
        };

        xAxis.setValueFormatter(formatter);

        YAxis left = mChart.getAxisLeft();
        left.setEnabled(true);
        left.setLabelCount(10, true);
        left.setTextColor(Color.WHITE);

        mChart.getAxisRight().setEnabled(false);
        mChart.getLegend().setEnabled(false);
        mChart.setDrawGridBackground(true);
        mChart.setGridBackgroundColor(Color.BLACK);
        Description desc = new Description();
        desc.setText("");
        mChart.setDescription(desc);
        mChart.animateX(2000, Easing.EasingOption.Linear);

        pricesDataSet.setDrawCircles(false);
        pricesDataSet.setColor(Color.WHITE);
        LineData lineData = new LineData(pricesDataSet);
        mChart.setData(lineData);

    }
}


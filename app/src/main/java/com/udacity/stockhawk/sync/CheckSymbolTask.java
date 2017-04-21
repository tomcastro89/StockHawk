package com.udacity.stockhawk.sync;

import android.os.AsyncTask;

import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Created by Tcastrovillari on 21.04.2017.
 */

public class CheckSymbolTask extends AsyncTask<String,Void,Boolean> {

    @Override
    protected Boolean doInBackground(String... symbolParams) {
        String[] symbolArray = {symbolParams[0]};
        Map<String, Stock> quotes;
        try{
            quotes = YahooFinance.get(symbolArray);
        }catch(Exception e){
            System.err.print("Could not reach Yahoo API");
            return false;
        }
        if(quotes.get(symbolArray[0]).getName()!=null){
            return true;
        }else{
            return false;
        }
    }

    public AsyncResponse delegate = null;

    public CheckSymbolTask(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected void onPostExecute(Boolean isValid) {
        delegate.processFinish(isValid);
    }
}
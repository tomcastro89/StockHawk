package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

/**
 * Created by Tcastrovillari on 09.05.2017.
 * Adjusted Code from:
 * https://github.com/udacity/Advanced_Android_Development/blob/7.02_Get_Real_Data/app/src/main/java/com/example/android/sunshine/app/widget/TodayWidgetIntentService.java
 */

public class StockHawkIntendService extends IntentService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };
    // these indices must match the projection
    private static final int INDEX_STOCK_SYMBOL = 0;
    private static final int INDEX_STOCK_PRICE = 1;
    private static final int INDEX_STOCK_PERC = 2;

    public StockHawkIntendService() {
        super("StockHawkIntendService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockHawkWidgetProvider.class));

        final Uri stockHawkUri = Contract.Quote.URI;
        Cursor data = getContentResolver().query(stockHawkUri, STOCK_COLUMNS, null,
                null, Contract.Quote.COLUMN_SYMBOL);
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        data.moveToNext();

        String symbol = data.getString(INDEX_STOCK_SYMBOL);
        String price = data.getString(INDEX_STOCK_PRICE);
        String perc = data.getString(INDEX_STOCK_PERC);

        data.close();

        for (int appWidgetId : appWidgetIds) {

            int layoutId = R.layout.stock_hawk_widget;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.TextView_Item_Symbol, symbol);
            views.setTextViewText(R.id.TextView_Item_Price, price);
            views.setTextViewText(R.id.TextView_Item_Perc, perc + "%");

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.StockHawkWidget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}

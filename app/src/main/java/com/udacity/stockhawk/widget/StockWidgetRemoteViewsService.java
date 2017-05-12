package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.StockDetails;

/**
 * Created by Tcastrovillari on 12.05.2017.
 */

public class StockWidgetRemoteViewsService extends RemoteViewsService {

    private static final String[] STOCK_COLUMNS = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE
    };

    private static final int INDEX_STOCK_SYMBOL = 0;
    private static final int INDEX_STOCK_PRICE = 1;
    private static final int INDEX_STOCK_PERC = 2;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                final Uri stockHawkUri = Contract.Quote.URI;
                data = getContentResolver().query(stockHawkUri, STOCK_COLUMNS, null,
                        null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.list_item_quote);

                String symbol = data.getString(INDEX_STOCK_SYMBOL);
                String price = data.getString(INDEX_STOCK_PRICE);
                String perc = data.getString(INDEX_STOCK_PERC);

                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.change, perc + "%");

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}

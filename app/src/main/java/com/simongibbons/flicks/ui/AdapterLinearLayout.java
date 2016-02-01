package com.simongibbons.flicks.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;
import android.widget.LinearLayout;

/**
 * A Linear Layout that populates all of it's items from
 * an adapter so that it can be used within a ScrollView.
 */
public class AdapterLinearLayout extends LinearLayout {

    private static final String LOG_TAG = AdapterLinearLayout.class.getSimpleName();

    public AdapterLinearLayout(Context context) {
        super(context);
    }

    public AdapterLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(11)
    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public AdapterLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private Adapter adapter;
    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            reloadChildViews();
        }
    };

    public void setAdapter(Adapter adapter) {
        if(this.adapter == adapter) return;

        this.adapter = adapter;

        if(adapter != null) {
            adapter.registerDataSetObserver(dataSetObserver);
        }
        reloadChildViews();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(adapter != null) {
            adapter.unregisterDataSetObserver(dataSetObserver);
        }
    }

    private void reloadChildViews() {
        removeAllViews();

        if(adapter == null) return;

        int count = adapter.getCount();
        for(int position = 0 ; position < count ; ++position) {
            View view = adapter.getView(position, null, this);
            if(view != null) {
                addView(view);
            }
        }
    }
}

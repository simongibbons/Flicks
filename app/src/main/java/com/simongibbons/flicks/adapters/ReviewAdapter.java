package com.simongibbons.flicks.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.simongibbons.flicks.R;
import com.simongibbons.flicks.fragments.DetailFragment;


public class ReviewAdapter extends CursorAdapter {

    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    public ReviewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String reviewText = cursor.getString(DetailFragment.COL_REVIEW_TEXT);
        viewHolder.reviewTextView.setText(reviewText);
    }

    public static class ViewHolder {
        TextView reviewTextView;

        public ViewHolder(View view) {
            reviewTextView = (TextView) view.findViewById(R.id.review_item_textview);
        }
    }

}

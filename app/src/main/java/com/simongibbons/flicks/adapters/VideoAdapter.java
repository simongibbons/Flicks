package com.simongibbons.flicks.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simongibbons.flicks.R;
import com.simongibbons.flicks.fragments.DetailFragment;
import com.squareup.picasso.Picasso;


public class VideoAdapter extends CursorAdapter {

    public Context context;

    public VideoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String title = cursor.getString(DetailFragment.COL_VIDEO_NAME);
        viewHolder.videoTitleView.setText(title);

        final String youtubeKey = cursor.getString(DetailFragment.COL_YOUTUBE_KEY);
        Uri thumbnailUri = Uri.parse("http://img.youtube.com/vi/").buildUpon()
                .appendPath(youtubeKey)
                .appendPath("default.jpg")
                .build();

        Picasso.with(context).load(thumbnailUri).into(viewHolder.videoThumbnailView);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri youtubeUri = Uri.parse("http://www.youtube.com/watch").buildUpon()
                        .appendQueryParameter("v", youtubeKey)
                        .build();
                Intent intent = new Intent(Intent.ACTION_VIEW, youtubeUri);
                context.startActivity(intent);
            }
        });
    }

    public static class ViewHolder {
        ImageView videoThumbnailView;
        TextView videoTitleView;

        public ViewHolder(View view) {
            videoThumbnailView = (ImageView) view.findViewById(R.id.video_thumbnail);
            videoTitleView = (TextView) view.findViewById(R.id.video_title);
        }
    }
}

package com.simongibbons.flicks.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public class MovieProvider {
    public static final String AUTHORITY = "com.simongibbons.flicks.database.MovieProvider";

    interface Path {
        String MOVIES = "movie";
    }

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {
        @ContentUri(path = Path.MOVIES, type = "vnd.android.cursor.dir/list")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/movie");
    }
}

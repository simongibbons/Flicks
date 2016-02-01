package com.simongibbons.flicks.database;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public class MovieProvider {
    public static final String AUTHORITY = "com.simongibbons.flicks.database.MovieProvider";

    interface Path {
        String MOVIES = "movie";
    }

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movies {
        @ContentUri(path = Path.MOVIES, type = "vnd.android.cursor.dir/movie")
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/movie");
    }

    @TableEndpoint(table = MovieDatabase.REVIEWS)
    public static class Reviews {
        @InexactContentUri(
                path = Path.MOVIES + "/#" + "/reviews",
                type = "vnd.android.cursor.dir/review",
                name = "MOVIE_ID",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(int id) {
            return Uri.parse("content://" + AUTHORITY + "/movie/" + id + "/reviews");
        }
    }

    @TableEndpoint(table = MovieDatabase.VIDEOS)
    public static class Videos {
        @InexactContentUri(
                path = Path.MOVIES + "/#" + "/videos",
                type = "vnd.android.cursor.dir/video",
                name = "MOVIE_ID",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(int id) {
            return Uri.parse("content://" + AUTHORITY + "/movie/" + id + "/videos");
        }
    }

    @TableEndpoint(table = MovieDatabase.MOVIES)
    public static class Movie {
        @InexactContentUri(
                path = Path.MOVIES + "/#",
                type = "vn.android.cursor.item/movie",
                name = "MOVIE_ID",
                whereColumn = MovieColumns.MOVIE_ID,
                pathSegment = 1
        )
        public static Uri withId(int id) {
            return Uri.parse("content://" + AUTHORITY + "/movie/" + id);
        }
    }
}
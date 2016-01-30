package com.simongibbons.flicks.database;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

@Database(version = MovieDatabase.VERSION)
public class MovieDatabase {
    public static final int VERSION = 1;

    @Table(MovieColumns.class) public static final String MOVIES = "movies";
    @Table(ReviewColumns.class) public static final String REVIEWS = "reviews";
}
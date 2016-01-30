package com.simongibbons.flicks.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;


public class VideoColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.MOVIES, column = MovieColumns.MOVIEID)
    public static final String MOVIEID = "movieid";

    @DataType(DataType.Type.TEXT)
    public static final String YOUTUBE_KEY = "youtube_key";
}
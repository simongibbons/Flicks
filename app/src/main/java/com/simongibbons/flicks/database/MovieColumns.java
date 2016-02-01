package com.simongibbons.flicks.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;

public class MovieColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)
    public static final String OVERVIEW = "overview";

    @DataType(DataType.Type.REAL)
    public static final String RATING = "rating";

    @DataType(DataType.Type.TEXT)
    public static final String POSTER_PATH = "poster_path";

    @DataType(DataType.Type.INTEGER)
    public static final String RELEASE_DATE = "release_date";
}

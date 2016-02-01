package com.simongibbons.flicks.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;

public class ReviewColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.MOVIES, column = MovieColumns.MOVIE_ID)
    @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @NotNull
    public static final String REVIEW_ID = "review_id";

    @DataType(DataType.Type.TEXT)
    public static final String AUTHOR = "author";

    @DataType(DataType.Type.TEXT)
    public static final String REVIEW = "review";
}

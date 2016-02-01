package com.simongibbons.flicks.database;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.ConflictResolutionType;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;
import net.simonvt.schematic.annotation.Unique;


public class VideoColumns {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID = "_id";

    @DataType(DataType.Type.TEXT)
    @Unique(onConflict = ConflictResolutionType.REPLACE)
    @NotNull
    public static final String VIDEO_ID = "video_id";

    @DataType(DataType.Type.INTEGER)
    @References(table = MovieDatabase.MOVIES, column = MovieColumns.MOVIE_ID)
    @NotNull
    public static final String MOVIE_ID = "movie_id";

    @DataType(DataType.Type.TEXT)
    public static final String NAME = "name";

    @DataType(DataType.Type.TEXT)
    public static final String TYPE = "type";

    @DataType(DataType.Type.TEXT)
    public static final String YOUTUBE_KEY = "youtube_key";
}

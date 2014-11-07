package cis642.aphidcounter.storage.model;

import android.provider.BaseColumns;

/**
 * Created by japshvincent on 11/6/2014.
 */
public final class FieldModel {

    public FieldModel(){}

    public static abstract class FieldEntry implements BaseColumns{
        public static final String TABLE_NAME = "fields";
        public static final String COLUMN_NAME_ENTRY_ID = "field_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_SUBTITLE = "subtitle";
    }

}

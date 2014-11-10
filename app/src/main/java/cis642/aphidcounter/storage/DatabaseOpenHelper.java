package cis642.aphidcounter.storage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by japshvincent on 11/6/2014.
 */
public class DatabaseOpenHelper extends SQLiteOpenHelper{
    final static String TABLE_NAME = "ac_fields";
    final static String FIELD_NAME = "field_name";
    final static String FIELD_CROP = "field_crop";
    final static String _ID = "_id";
    final static String[] columns = { _ID,FIELD_NAME, FIELD_CROP };

    final private static String CREATE_CMD =
            "CREATE TABLE ac_fields (" + _ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + FIELD_NAME + " TEXT NOT NULL , "
                    + FIELD_CROP + " TEXT NOT NULL)" ;

    final private static String NAME = "ac_db";
    final private static Integer VERSION = 1;
    final private Context mContext;

    public DatabaseOpenHelper(Context context) {
        super(context, NAME, null, VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

    }
    void deleteDatabase() {
        mContext.deleteDatabase(NAME);
    }
}

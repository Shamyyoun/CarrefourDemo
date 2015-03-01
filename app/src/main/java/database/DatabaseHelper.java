package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_INSTANT_OFFERS = "instant_offers";
    public static final String INSTANT_OFFERS_ID = "_id";
    public static final String INSTANT_OFFERS_NAME = "_name";
    public static final String INSTANT_OFFERS_DESC = "_desc";
    public static final String INSTANT_OFFERS_CREATE_TIME = "create_time";
    public static final String INSTANT_OFFERS_EXPIRE_TIME = "expire_time";

    private static final String DATABASE_NAME = "carrefour_demo.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_INSTANT_OFFERS
                    + "("
                    + INSTANT_OFFERS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + INSTANT_OFFERS_NAME + " TEXT NOT NULL, "
                    + INSTANT_OFFERS_DESC + " TEXT NOT NULL, "
                    + INSTANT_OFFERS_CREATE_TIME + " TEXT NOT NULL, "
                    + INSTANT_OFFERS_EXPIRE_TIME + " TEXT NOT NULL"
                    + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTANT_OFFERS);
        onCreate(db);
    }

} 

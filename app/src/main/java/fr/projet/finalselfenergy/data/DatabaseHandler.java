package fr.projet.finalselfenergy.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import fr.projet.finalselfenergy.data.DailyStat;


/**
 * Created by caroline on 03/07/17.
 */

public class DatabaseHandler extends SQLiteOpenHelper {
    private static String TAG = "DatabaseHandler";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "statsManager";

    // Table name
    private static final String DAILY_STATS_TABLE = "dailyStats";

    // Contacts Table Columns names
    private static final String KEY_DAY = "day";
    private static final String KEY_STEPS = "steps";

    private static DatabaseHandler databaseHandler = null;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
        if (databaseHandler == null) {
            databaseHandler = new DatabaseHandler(context);
        }
        return databaseHandler;
     }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "::onCreate");
        String CREATE_STAT_TABLE = "CREATE TABLE " + DAILY_STATS_TABLE + "("
                + KEY_DAY + " TEXT PRIMARY KEY," + KEY_STEPS + " INTEGER" + ")";
        db.execSQL(CREATE_STAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "::onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + DAILY_STATS_TABLE);
        onCreate(db);
    }

    public void addDailyStat(DailyStat dailyStat) {
        Log.d(TAG, "::addDailyStat");
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, dailyStat.getFormattedDate());
        Log.d(TAG, "trying to insert date : " + dailyStat.getFormattedDate());
        values.put(KEY_STEPS, dailyStat.get_steps());
        Log.d(TAG, "trying to insert steps : " + dailyStat.get_steps());

        // Inserting Row
        db.insert(DAILY_STATS_TABLE, null, values);
        db.close(); // Closing database connection
    }

    public DailyStat getDailyStat(String day) throws ParseException, NullPointerException {
        Log.d(TAG, "::getDailyStat");
        Log.i(TAG, "looking for : " + String.valueOf(day));
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DAILY_STATS_TABLE,
                new String[] {KEY_DAY, KEY_STEPS},
                KEY_DAY + "=?",
                new String[] {day},
                null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        DailyStat result = null;
        try {
            Log.d(TAG, "got : " + cursor.getString(0) + " and " + cursor.getInt(1));
            result = new DailyStat(cursor.getString(0), cursor.getInt(1));
        } catch (CursorIndexOutOfBoundsException e) {
            Log.i(TAG, "no stats for this day.");
        } finally {
            cursor.close();
        }
        return result;
    }

    public List<DailyStat> getAllDailyStats() throws ParseException {
        Log.d(TAG, "::getAllDailyStats");
        List<DailyStat> dailyStats = new ArrayList<DailyStat>();
        String selectQuery = "SELECT  * FROM " + DAILY_STATS_TABLE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DailyStat dailyStat = new DailyStat(cursor.getString(0), cursor.getInt(1));
                dailyStats.add(dailyStat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return dailyStats;
    }

    public int updateDailyStat(DailyStat dailyStat) {
        Log.d(TAG, "::updateDailyStat");
        SQLiteDatabase db = this.getWritableDatabase();
        String dateToInsert = dailyStat.getFormattedDate();

        ContentValues values = new ContentValues();
        values.put(KEY_DAY, dateToInsert);
        values.put(KEY_STEPS, dailyStat.get_steps());
        Log.d(TAG, "Trying to update : " + dateToInsert + ";" + dailyStat.get_steps());

        return db.update(DAILY_STATS_TABLE,
                values,
                KEY_DAY + " = ?",
                new String[] { dateToInsert });
    }

    public void deleteDailyStat(DailyStat dailyStat) {
        Log.d(TAG, "::deleteDailyStat");
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(DAILY_STATS_TABLE,
                KEY_DAY + " = ?",
                new String[] { dailyStat.getFormattedDate() });
        db.close();
    }
}
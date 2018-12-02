package com.google.developer.colorvalue.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.developer.colorvalue.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Helper class to manage database
 */
public class CardSQLite extends SQLiteOpenHelper {

    private static final String TAG = CardSQLite.class.getName();
    private static final String DB_NAME = "colorvalue.db";
    private static final int DB_VERSION = 1;

    private Resources mResources;

    private static CardSQLite sInstance;

    public static synchronized CardSQLite getsInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CardSQLite(context.getApplicationContext());
        }

        return sInstance;
    }

    // Tab le create statements
    private static final String CREATE_TABLE = "CREATE TABLE "
            + CardProvider.Contract.TABLE_NAME + "(" + CardProvider.Contract.Columns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + CardProvider.Contract.Columns.COLOR_HEX + " TEXT," + CardProvider.Contract.Columns.COLOR_NAME + " TEXT)";

    public CardSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mResources = context.getResources();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Create and fill the database
        db.execSQL(CREATE_TABLE);
        addDemoCards(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Handle database version upgrades
        db.execSQL("DROP TABLE IF EXISTS " + DB_NAME);
        onCreate(db);

    }

    public static String getColumnString(Cursor cursor, String name) {
        return cursor.getString(cursor.getColumnIndex(name));
    }

    public static int getColumnInt(Cursor cursor, String name) {
        return cursor.getInt(cursor.getColumnIndex(name));
    }

    /**
     * save demo cards into database
     */
    private void addDemoCards(SQLiteDatabase db) {
        try {
            db.beginTransaction();
            try {
                readCardsFromResources(db);
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        } catch (IOException | JSONException e) {
            Log.e(TAG, "Unable to pre-fill database", e);
        }
    }

    /**
     * load demo color cards from {@link raw/colorcards.json}
     */
    private void readCardsFromResources(SQLiteDatabase db) throws IOException, JSONException {
        StringBuilder builder = new StringBuilder();
        InputStream in = mResources.openRawResource(R.raw.colorcards);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }

        //Parse resource into key/values
        JSONObject root = new JSONObject(builder.toString());
        // TODO Parse JSON data and insert into the provided database instance
        JSONArray cards = root.getJSONArray("cards");
        for (int i = 0; i < cards.length(); i++) {
            Log.d(TAG, "JSON for loop called!");

            JSONObject currentCards = cards.getJSONObject(i);
            ContentValues contentValues = new ContentValues();

            contentValues.put("question", currentCards.getString("hex"));
            contentValues.put("answer", currentCards.getString("name"));

            db.insert(CardProvider.Contract.TABLE_NAME, null, contentValues);
        }
    }

}

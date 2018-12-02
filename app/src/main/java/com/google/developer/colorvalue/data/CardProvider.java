package com.google.developer.colorvalue.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import static com.google.developer.colorvalue.data.CardProvider.Contract.CONTENT_URI;
import static com.google.developer.colorvalue.data.CardProvider.Contract.TABLE_NAME;

public class CardProvider extends ContentProvider {

    /** Matcher identifier for all cards */
    private static final int CARD = 100;
    /** Matcher identifier for one card */
    private static final int CARD_WITH_ID = 102;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        // content://com.google.developer.colorvalue/cards
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME, CARD);
        // content://com.google.developer.colorvalue/cards/#
        sUriMatcher.addURI(CardProvider.Contract.CONTENT_AUTHORITY,
                TABLE_NAME + "/#", CARD_WITH_ID);
    }

    private CardSQLite mCardSQLite;

    @Override
    public boolean onCreate() {
        mCardSQLite = new CardSQLite(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                return  Contract.CONTEN_LIST_TYPE;
            case CARD_WITH_ID:
                return Contract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {
        // TODO Implement query function by Uri all cards or single card by id
        // Get access to database in read-only mode
        final SQLiteDatabase database = mCardSQLite.getReadableDatabase();

        // Write URI match code and return a Cursor
        int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            // Query for the cards directory
            case CARD:
                retCursor = database.query(Contract.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            case CARD_WITH_ID:
                String id = uri.getPathSegments().get(1);
                retCursor = database.query(Contract.TABLE_NAME,
                        projection,
                        " _id=?",
                        new String[]{id},
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw  new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set the notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(), uri);

        // return the cursor
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // TODO Implement insert new color and return Uri with ID
        final SQLiteDatabase database = mCardSQLite.getReadableDatabase();

        // Write URI matcher
        int match = sUriMatcher.match(uri);
        Uri retUri; // Uri to return
        switch (match) {
            case CARD:
                // Insert new values into the databse
                long id = database.insert(Contract.TABLE_NAME, null , values);
                if (id > 0) {
                    retUri = ContentUris.withAppendedId(Contract.CONTENT_URI, id);
                } else {
                    throw new SQLException("Failed ti insert row into " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Notify the resolver if the uri has been changed, and return the newly inserted URI
        Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);

        // Return constructed uri (this points to the newly inserted row of data)
        return  retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        // TODO delete card by Uri
        // Get access to the database and write URI matching code to recognize a single item
        final SQLiteDatabase database = mCardSQLite.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        // Keep track of the number of deleted cards
        int cardDeleted; // starts as 0

        switch (match) {
            // Handle the single item case, recognized by the ID included in the URI path
            case CARD_WITH_ID:
                // Get the card id from the uri path
                String id = uri.getPathSegments().get(1);
                // Use selection/selectionsArgs to filter for this ID
                cardDeleted = database.delete(Contract.TABLE_NAME,
                        " _id=?",
                        new String[]{id});
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the resolver of a change and return the number of items deleted
        if (cardDeleted != 0) {
            // A plant (or more) was deleted, set notification
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri, null);
        }
        // Return the number of plant deleted
        return cardDeleted;
    }



    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values,
                      @Nullable String selection, @Nullable String[] selectionArgs) {
        throw new UnsupportedOperationException("This provider does not support updates");
    }

    /**
     * Database contract
     */
    public static class Contract {
        public static final String TABLE_NAME = "cards";
        public static final String CONTENT_AUTHORITY = "com.google.developer.colorvalue";
        public static final Uri CONTENT_URI = new Uri.Builder().scheme("content")
                .authority(CONTENT_AUTHORITY)
                .appendPath(TABLE_NAME)
                .build();

        // MIME Types
        public static final String CONTEN_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final class Columns implements BaseColumns {
            public static final String _ID = BaseColumns._ID;
            public static final String COLOR_HEX = "question";
            public static final String COLOR_NAME = "answer";
        }
    }

}

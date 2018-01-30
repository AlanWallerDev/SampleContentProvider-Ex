package com.example.musfiqrahman.samplecontentprovider.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.musfiqrahman.samplecontentprovider.data.DictionaryContract;
import com.example.musfiqrahman.samplecontentprovider.data.DictionaryDao;
import com.example.musfiqrahman.samplecontentprovider.data.DictionaryDatabase;
import com.example.musfiqrahman.samplecontentprovider.data.DictionaryEntry;

import java.util.ArrayList;

/**
 * Created by musfiqrahman on 2018-01-21.
 */

/**
 * A {@link ContentProvider} based on a Room database.
 *
 * <p>Note that you don't need to implement a ContentProvider unless you want to expose the data
 * outside your process or your application already uses a ContentProvider.</p>
 */

public class SampleDictionaryProvider extends ContentProvider {

    private DictionaryDao dictionaryDao;




    /** The match code for some items in the Dictionary table. */
    private static final int CODE_DICTIONARY_DIR = 1;

    /** The match code for an item in the Dictionary table. */
    private static final int CODE_DICTIONARY_ITEM = 2;

    /** The URI matcher. */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static UriMatcher getMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(DictionaryContract.AUTHORITY, DictionaryContract.DictionaryEntity.TABLE_NAME, CODE_DICTIONARY_DIR);
        matcher.addURI(DictionaryContract.AUTHORITY, DictionaryContract.DictionaryEntity.TABLE_NAME + "/*", CODE_DICTIONARY_ITEM);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        if(context == null)
            return false;
        dictionaryDao = DictionaryDatabase.getInstance(context).dictionaryDao();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {

        Cursor cursor = null;

        switch (MATCHER.match(uri)){
            case CODE_DICTIONARY_DIR:
                //return all data
                cursor = dictionaryDao.selectAll();
                break;
            case CODE_DICTIONARY_ITEM:
                //return specific data
                cursor = dictionaryDao.selectById(ContentUris.parseId(uri));
                break;
                default:
                    //throw exception
                    throw new IllegalArgumentException("Unknown URI");
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (MATCHER.match(uri)){
            case CODE_DICTIONARY_DIR:
                return new String("vnd.android.cursor.dir/" + DictionaryContract.AUTHORITY + DictionaryContract.DictionaryEntity.TABLE_NAME);

            case CODE_DICTIONARY_ITEM:
                return new String("vnd.android.cursor.item/");
            default:
                throw new IllegalArgumentException("Unknown Uri");

        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (MATCHER.match(uri)){
            case CODE_DICTIONARY_DIR:
                final long id = dictionaryDao.insert(DictionaryEntry.fromContentValues(contentValues));
            return ContentUris.withAppendedId(uri, id);
            case CODE_DICTIONARY_ITEM:
                throw new IllegalArgumentException("Invalid Uri: Cannot Insert with ID");
            default:
                throw new IllegalArgumentException("Unknown Uri");

        }

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

        switch (MATCHER.match(uri)){
            case CODE_DICTIONARY_DIR:
                throw new IllegalArgumentException("Need Id");
            case CODE_DICTIONARY_ITEM:
                final int count = dictionaryDao.deleteById(ContentUris.parseId(uri));
                return count;
        }
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        switch (MATCHER.match(uri)){
            case CODE_DICTIONARY_DIR:
                throw new IllegalArgumentException("Cannot Update Without ID");
            case CODE_DICTIONARY_ITEM:
                final int id = dictionaryDao.update(DictionaryEntry.fromContentValues(contentValues));
                return id;
            default:
                throw new IllegalArgumentException("Unknown Uri");

        }

    }


    // Need to override six methods.
}

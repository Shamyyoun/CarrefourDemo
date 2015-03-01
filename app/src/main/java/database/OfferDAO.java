package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import datamodels.Offer;

public class OfferDAO {
    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private String[] allColumns = {
            DatabaseHelper.INSTANT_OFFERS_ID,
            DatabaseHelper.INSTANT_OFFERS_NAME,
            DatabaseHelper.INSTANT_OFFERS_DESC,
            DatabaseHelper.INSTANT_OFFERS_CREATE_TIME,
            DatabaseHelper.INSTANT_OFFERS_EXPIRE_TIME
    };

    public OfferDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long save(Offer offer) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.INSTANT_OFFERS_NAME, offer.getName());
        values.put(DatabaseHelper.INSTANT_OFFERS_DESC, offer.getDescription());
        values.put(DatabaseHelper.INSTANT_OFFERS_CREATE_TIME, offer.getStrCreateDate());
        values.put(DatabaseHelper.INSTANT_OFFERS_EXPIRE_TIME, offer.getStrExpireDate());

        long offerId = database.insert(DatabaseHelper.TABLE_INSTANT_OFFERS, null, values);
        return offerId;
    }

    public void delete(String id) {
        database.delete(DatabaseHelper.TABLE_INSTANT_OFFERS, DatabaseHelper.INSTANT_OFFERS_ID + " = " + id, null);
    }

    public List<Offer> getAll() {
        List<Offer> offers = new ArrayList<>();

        Cursor cursor = database.query(DatabaseHelper.TABLE_INSTANT_OFFERS, allColumns, null, null, null, null,
                DatabaseHelper.INSTANT_OFFERS_ID + " DESC");
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Offer offer = cursorToOffer(cursor);
            offers.add(offer);
            cursor.moveToNext();
        }
        cursor.close();
        return offers;
    }

    private Offer cursorToOffer(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.INSTANT_OFFERS_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.INSTANT_OFFERS_NAME));
        String desc = cursor.getString(cursor.getColumnIndex(DatabaseHelper.INSTANT_OFFERS_DESC));
        String createDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.INSTANT_OFFERS_CREATE_TIME));
        String expireDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.INSTANT_OFFERS_EXPIRE_TIME));

        Offer offer = new Offer("" + id);
        offer.setName(name);
        offer.setDescription(desc);
        offer.setCreateDate(createDate);
        offer.setExpireDate(expireDate);

        return offer;
    }
}

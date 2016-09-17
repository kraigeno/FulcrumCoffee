package com.qmatica.arsen.aglistview;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBManager extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;

    private static final String DATABASE_NAME = "fulcrum.db";

    private static final String TABLE_ORDER      = "orders";
    private static final String COLUMN_ID        = "id";
    private static final String COLUMN_PRODUCT   = "product";
    private static final String COLUMN_CATEGORY  = "category";
    private static final String COLUMN_UNIT      = "unit";
    private static final String COLUMN_QUANTITY1 = "quantity1";
    private static final String COLUMN_QUANTITY2 = "quantity2";
    private static final String COLUMN_QUANTITY3 = "quantity3";
    private static final String COLUMN_QUANTITY4 = "quantity4";

    private static final String TABLE_PROFILE    = "profile";
    private static final String COLUMN_NAME      = "name";
    private static final String COLUMN_COMPANY   = "company";
    private static final String COLUMN_SENDTO    = "sendTo";
    private static final String COLUMN_COPYTO    = "copyTo";
    private static final String COLUMN_USERNAME  = "username";
    private static final String COLUMN_PASSWORD  = "password";
    private static final String COLUMN_MODE      = "mode";         // 0 or 1 to switch between mail clients

    private static final String TABLE_HISTORY    = "history";
//  private static final String COLUMN_ID        = "id";
    private static final String COLUMN_DATETIME  = "datetime";
    private static final String COLUMN_COMMENTS  = "comments";

    private static final String TABLE_DETAIL     = "detail";
//  private static final String COLUMN_ID        = "id";
    private static final String COLUMN_ORDER_ID  = "order_id";
//  private static final String COLUMN_PRODUCT   = "product";
//  private static final String COLUMN_CATEGORY  = "category";
//  private static final String COLUMN_UNIT      = "unit";
    private static final String COLUMN_QUANTITY  = "quantity";


    private static final String CREATE_TABLE_PROFILE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + "("
            + COLUMN_ID         + " INTEGER PRIMARY KEY,"
            + COLUMN_NAME       + " TEXT,"
            + COLUMN_COMPANY    + " TEXT,"
            + COLUMN_SENDTO     + " TEXT,"
            + COLUMN_COPYTO     + " TEXT,"
            + COLUMN_USERNAME   + " TEXT,"
            + COLUMN_PASSWORD   + " TEXT,"
            + COLUMN_MODE       + " INTEGER" + ")";


    private static final String CREATE_TABLE_ORDER = "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER + "("
            + COLUMN_ID         + " INTEGER PRIMARY KEY,"
            + COLUMN_PRODUCT    + " TEXT,"
            + COLUMN_CATEGORY   + " TEXT,"
            + COLUMN_UNIT       + " TEXT,"
            + COLUMN_QUANTITY1  + " INTEGER,"
            + COLUMN_QUANTITY2  + " INTEGER,"
            + COLUMN_QUANTITY3  + " INTEGER,"
            + COLUMN_QUANTITY4  + " INTEGER" + ")";

    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE IF NOT EXISTS " + TABLE_HISTORY + "("
            + COLUMN_ID         + " INTEGER PRIMARY KEY,"
            + COLUMN_DATETIME   + " TEXT,"
            + COLUMN_COMMENTS   + " TEXT" + ")";

    private static final String CREATE_TABLE_DETAIL = "CREATE TABLE IF NOT EXISTS " + TABLE_DETAIL + "("
            + COLUMN_ID         + " INTEGER PRIMARY KEY,"
            + COLUMN_ORDER_ID   + " INTEGER, "
            + COLUMN_PRODUCT    + " TEXT,"
            + COLUMN_CATEGORY   + " TEXT,"
            + COLUMN_UNIT       + " TEXT,"
            + COLUMN_QUANTITY   + " INTEGER" + ")";


    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_PROFILE);
        db.execSQL(CREATE_TABLE_ORDER);
        db.execSQL(CREATE_TABLE_HISTORY);
        db.execSQL(CREATE_TABLE_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAIL);
        onCreate(db);
    }


    public boolean existsProfile() {
        return (readProfile().getID() != 0);
    }

    public UserProfile readProfile() {
        UserProfile result = null;
        String query = "Select * FROM " + TABLE_PROFILE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            result = new UserProfile(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), Integer.parseInt(cursor.getString(7)));
        }
        else {
            result = new UserProfile(); // TODO empty profile
        }
        cursor.close();
        db.close();
        return result;
    }

    public long saveProfile(UserProfile profile) {
        long result;
        ContentValues values = initProfileValues(profile);
        UserProfile dbProfile = readProfile();

        if(dbProfile.getID() == 0) {
            SQLiteDatabase db = this.getWritableDatabase();
            result = db.insert(TABLE_PROFILE, null, values);
            db.close();
        }
        else {
            SQLiteDatabase db = this.getWritableDatabase();
            String whereClause = COLUMN_ID + " = \"" + String.valueOf(profile.getID()) + "\"";
            result = db.update(TABLE_PROFILE, values, whereClause, null);
            db.close();
        }
        return result;
    }

    private ContentValues initProfileValues(UserProfile profile) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME,     profile.getName());
        values.put(COLUMN_COMPANY,  profile.getCompany());
        values.put(COLUMN_SENDTO,   profile.getSendTo());
        values.put(COLUMN_COPYTO,   profile.getCopyTo());
        values.put(COLUMN_USERNAME, profile.getUsername());
        values.put(COLUMN_PASSWORD, profile.getPassword());
        values.put(COLUMN_MODE,     profile.getMode());
        return values;
    }

    public int deleteProfile() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_PROFILE, "1", null);
        db.close();
//        Log.w(" Profile deleted", String.valueOf(count));
        return count;
    }


    //  TODO implement sorting by CATEGORIES (Currently all sorting done in java)
    public long insertOrder(Order order) {
        long result = A.DB_RECORD_EXISTS;
        if(!existsOrder(order)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = initOrderValues(order);
            result = db.insert(TABLE_ORDER, null, values);
            db.close();
        }
        return result;
    }


//  getOrder() and existsOrder() will work with both DB records with real ids and Order objects with id=0
    public Order getOrder(Order order) {
        Order result = null;
        String query = selectOrderQuery(order);
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            result = readOrderFrom(cursor);
        }
        cursor.close();
        db.close();
        return result;
    }

    public boolean existsOrder(Order order) {
        return (getOrder(order) != null);
    }

//  returns 1 if deleted and 0 if not
    public int deleteOrder(Order order) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();
        result = db.delete(TABLE_ORDER, whereOrderClause(order), null);
        db.close();
        return result;
    }

    public int updateOrder(Order order) {
        int result;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = initOrderValues(order);
        result = db.update(TABLE_ORDER, values, whereOrderClause(order), null);
        db.close();
        return result;
    }

    public ArrayList<Order> getAllOrders() {
        ArrayList<Order> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_ORDER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Order order = readOrderFrom(cursor);
                list.add(order);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    public long getOrderCount() {
        long count;
        String query = "SELECT * FROM " + TABLE_ORDER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public long insertAllOrders(ArrayList<Order> list) {
        long result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        for(Order order : list) {
            ContentValues values = initOrderValues(order);
            result = db.insert(TABLE_ORDER, null, values);
        }
        db.close();
        return result;
    }

    public int deleteAllOrders() {
        SQLiteDatabase db = this.getWritableDatabase();
        int count = db.delete(TABLE_ORDER, "1", null);
        db.close();
        return count;
    }

//  private helpers
    private Order readOrderFrom(Cursor cursor) {
        Order order = new Order();
        order.setID(Integer.parseInt(cursor.getString(0)));
        order.setProductName(cursor.getString(1));
        order.setCategory(cursor.getString(2));
        order.setUnit(cursor.getString(3));
        order.setQuantity(1, Integer.parseInt(cursor.getString(4)));
        order.setQuantity(2, Integer.parseInt(cursor.getString(5)));
        order.setQuantity(3, Integer.parseInt(cursor.getString(6)));
        order.setQuantity(4, Integer.parseInt(cursor.getString(7)));
        return order;
    }

    private ContentValues initOrderValues(Order order) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT, order.getProductName());
        values.put(COLUMN_UNIT, order.getUnit());
        values.put(COLUMN_CATEGORY, order.getCategory());
        values.put(COLUMN_QUANTITY1, order.getQuantity(1));
        values.put(COLUMN_QUANTITY2, order.getQuantity(2));
        values.put(COLUMN_QUANTITY3, order.getQuantity(3));
        values.put(COLUMN_QUANTITY4, order.getQuantity(4));
        return values;
    }

    private String whereOrderClause(Order order) {
        String withID =     COLUMN_ID       + " = \"" + String.valueOf(order.getID()) + "\"" +
                " AND "   + COLUMN_PRODUCT     + " = \"" + order.getProductName()               + "\"" +
                " AND "   + COLUMN_CATEGORY + " = \"" + order.getCategory()           + "\"" +
                " AND "   + COLUMN_UNIT     + " = \"" + order.getUnit()               + "\"";
        String withoutID =  COLUMN_PRODUCT     + " = \"" + order.getProductName()               + "\"" +
                " AND "   + COLUMN_CATEGORY + " = \"" + order.getCategory()           + "\"" +
                " AND "   + COLUMN_UNIT     + " = \"" + order.getUnit()               + "\"";
        return (order.getID() == 0) ? withoutID : withID;
    }
    private String selectOrderQuery(Order order) {
        return "Select * FROM " + TABLE_ORDER + " WHERE " + whereOrderClause(order);
    }


//  History methods

    public ArrayList<History> getAllHistory() {
        ArrayList<History> historyList = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                History history = new History(Integer.parseInt(cursor.getString(0)), cursor.getString(1), cursor.getString(2));
                historyList.add(history);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return historyList;
    }

    public int getHistoryCount() {
        int count;
        String query = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public long saveHistoryAndDetail(ArrayList<Order> orderList) {
        long orderID;
        History history = new History();        // gets now() timestamp in constructor
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues historyValues = new ContentValues();
        historyValues.put(COLUMN_DATETIME, history.getDateTime());
        historyValues.put(COLUMN_COMMENTS, history.getComments());

        orderID = db.insert(TABLE_HISTORY, null, historyValues);

        for(Order order : orderList) {
            ContentValues values = initDetailValues(order, orderID);
            db.insert(TABLE_DETAIL, null, values);
        }
        db.close();
        return orderID;
    }

//  returns 2 or 1 if deleted and 0 if not
    public int deleteHistoryAndDetail(long orderID) {
        int result = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        String historyWhereClause = COLUMN_ID       + " = \"" + String.valueOf(orderID) + "\"";
        String detailWhereClause =  COLUMN_ORDER_ID + " = \"" + String.valueOf(orderID) + "\"";

        result += db.delete(TABLE_HISTORY, historyWhereClause, null);
        result += db.delete(TABLE_DETAIL,  detailWhereClause,  null);

        db.close();
        return result;
    }

    public void deleteAllHistoryAndDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, "1", null );
        db.delete(TABLE_DETAIL, "1", null);
        db.close();
    }


//  Detail methods
    public ArrayList<Detail> getDetail(long orderID) {
        ArrayList<Detail> list = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_DETAIL + " WHERE " + COLUMN_ORDER_ID + " = \"" + String.valueOf(orderID) + "\"";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Detail detail = readDetailFrom(cursor);
                list.add(detail);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }


    private ContentValues initDetailValues(Order order, long orderID) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderID);
        values.put(COLUMN_PRODUCT,  order.getProductName());
        values.put(COLUMN_UNIT,     order.getUnit());
        values.put(COLUMN_CATEGORY, order.getCategory());
        values.put(COLUMN_QUANTITY, order.getQuantity());
        return values;
    }

    private Detail readDetailFrom(Cursor cursor) {
        return new Detail(Integer.parseInt(cursor.getString(0)), Integer.parseInt(cursor.getString(1)), cursor.getString(2), cursor.getString(3), cursor.getString(4), Integer.parseInt(cursor.getString(5)));
    }


}



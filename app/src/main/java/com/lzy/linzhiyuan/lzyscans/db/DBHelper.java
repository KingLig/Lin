package com.lzy.linzhiyuan.lzyscans.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2016/4/29.
 */
public class DBHelper extends SQLiteOpenHelper {

    // 数据库名
    private static final String DB_NAME = "sndn.db";
    // 数据库表
    private static final String TBL_NAME = "goods";

    // 创建表字节
    private static final String CREATE_TBL = " create table "
            + " goods(_id integer primary key autoincrement,barcode text,number text,code text,user text,time text)";
    // SQLiteDatabase
    private SQLiteDatabase db;
    // 获取总数据
    public ArrayList<HashMap<String, Object>> listData;


//    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL(CREATE_TBL);
    }

    public DBHelper(Context c) {
        super(c, DB_NAME, null, 2);
    };

    public void colse() {
        if (db != null) {
            db.close();
        }
    }
    /*
 * 插入数据
 */
    public void insert(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TBL_NAME, null, values);
        db.close();
    }

    /*
 * 查询所有数据
 */
    public Cursor query() {

        SQLiteDatabase db = getWritableDatabase();

        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);
        //
        return c;

    }

    /*
    *
    * 针对单据数据查询
    */
    public synchronized ArrayList<HashMap<String, Object>> querybarcode(String barcode){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<HashMap<String, Object>>();
        Cursor cursor = db.query("goods", new String[] { "_id", "barcode","number", "code","user","time" }, "barcode=?" ,
                new String[] {barcode }, null, null, null);
        int is = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < is; i++) {
                map.put("_id", cursor.getString(0));
                map.put("barcode", cursor.getString(1));
                map.put("number", cursor.getString(2));
                map.put("code", cursor.getString(3));
                map.put("user", cursor.getString(4));
                map.put("time", cursor.getString(5));
            }
            listDatas.add(map);
        }

        cursor.close();
        return listDatas;

    }


    /*
    *
    * 针对商品条码数据查询
    */
    public synchronized ArrayList<HashMap<String, Object>> querycode(String code){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<HashMap<String, Object>>();
        Cursor cursor = db.query("goods", new String[] { "_id", "barcode","number", "code","user","time" }, "code=?" ,
                new String[] {code }, null, null, null);
        int is = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < is; i++) {
                map.put("_id", cursor.getString(0));
                map.put("barcode", cursor.getString(1));
                map.put("number", cursor.getString(2));
                map.put("code", cursor.getString(3));
                map.put("user", cursor.getString(4));
                map.put("time", cursor.getString(5));
            }
            listDatas.add(map);
        }

        cursor.close();
        return listDatas;

    }


    /*
*
* 针对商品条码数据查询
*/
    public synchronized ArrayList<HashMap<String, Object>> querycodeall( String barode){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<HashMap<String, Object>>();
        Cursor cursor = db.query("goods", new String[] { "_id", "barcode","number", "code","user","time" }, " barcode=?",
                new String[] {barode }, null, null, null);
        int is = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < is; i++) {
                map.put("_id", cursor.getString(0));
                map.put("barcode", cursor.getString(1));
                map.put("number", cursor.getString(2));
                map.put("code", cursor.getString(3));
                map.put("user", cursor.getString(4));
                map.put("time", cursor.getString(5));
            }
            listDatas.add(map);
        }

        cursor.close();
        return listDatas;

    }


    /*
*
* 针对所有商品条码数据查询
*/
    public synchronized ArrayList<HashMap<String, Object>> querydelall(){

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<HashMap<String, Object>> listDatas = new ArrayList<HashMap<String, Object>>();
        Cursor cursor = db.query("goods", new String[] { "_id", "barcode","number", "code","user","time" }, "",
                new String[] {}, null, null, null);
        int is = cursor.getColumnCount();
        while (cursor.moveToNext()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            for (int i = 0; i < is; i++) {
                map.put("_id", cursor.getString(0));
                map.put("barcode", cursor.getString(1));
                map.put("number", cursor.getString(2));
                map.put("code", cursor.getString(3));
                map.put("user", cursor.getString(4));
                map.put("time", cursor.getString(5));
            }
            listDatas.add(map);
        }

        cursor.close();
        return listDatas;

    }


    public void Delete(String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            db = getWritableDatabase();
        }
        db.delete(TBL_NAME, "code=?", new String[]{barcode});
        db.close();
    }


    public void Deleteform(){
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null) {
            db = getWritableDatabase();
        }
        db.execSQL("DELETE FROM goods");
//        db.delete(TBL_NAME,"",new String []{});

        db.close();

    }

    //修改条码的数量功能
    public void update(int number,String barcode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String where = "code" + "=?";
        String[] whereValue = { barcode };
        ContentValues cv = new ContentValues();
        cv.put("number", number);
        db.update(TBL_NAME, cv, where, whereValue);
        db.close();
    }


    //


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

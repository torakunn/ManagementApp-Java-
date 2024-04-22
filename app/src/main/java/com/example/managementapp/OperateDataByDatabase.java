package com.example.managementapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OperateDataByDatabase {
    private DatabaseHelper helper;
    private DatabaseHelper2 helper2;




    public OperateDataByDatabase(DatabaseHelper helper){
        this.helper = helper;
    }
    public OperateDataByDatabase(DatabaseHelper helper,DatabaseHelper2 helper2){
        this.helper = helper;
        this.helper2 = helper2;
    }
    public  OperateDataByDatabase(DatabaseHelper2 helper2){ this.helper2 = helper2; }



    public String GetDataById(int id,String attribute){
        SQLiteDatabase db = helper.getWritableDatabase();

        String sql = "SELECT * FROM plans where _id = "+id;

        Cursor cursor = db.rawQuery(sql,null);

        String plan = "";

        while(cursor.moveToNext()){
            int idxId = cursor.getColumnIndex(attribute);
            plan = cursor.getString(idxId);
        }
        return plan;
    }

    public ArrayList<Map<String, String>> GetAllData(String classification){
        if(classification == "すべて") return GetAllData();

        ArrayList<Map<String, String>> planList = new ArrayList<>();
        Map<String, String> plan;

//        データベースからclassの値を指定してデータを取得
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "SELECT * FROM plans WHERE class = ?";
        String[] binder = {classification};
        Cursor cursor = db.rawQuery(sql, binder);

//        カーソルから順にデータを取り出しリストに登録
        String txt = "";
        int _id = 0;
        while (cursor.moveToNext()){
//            mapの初期化
            plan = new HashMap<>();

//            memoの取得
            int idxMemo = cursor.getColumnIndex("memo");
            txt = cursor.getString(idxMemo);

//            _idの取得
            int idx_id = cursor.getColumnIndex("_id");
            _id = cursor.getInt(idx_id);

//            mapにidとplanを追加
            plan.put("id",Integer.toString(_id));
            plan.put("plan",txt);

//            リストにmapを追加
            planList.add(plan);
        }
        return planList;
    }


    public void DeleteDataById(int id){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlDelete = "DELETE FROM plans WHERE _id = ?";
        SQLiteStatement stmt = db.compileStatement(sqlDelete);
        stmt.bindLong(1,id);
        stmt.executeUpdateDelete();
    }

    public void AddData(int id, String content, String selectedClass){
        SQLiteDatabase db = helper.getWritableDatabase();
        String sqlInsert = "INSERT INTO plans (_id, memo, class) VALUES (?, ?, ?)";
        SQLiteStatement stmt = db.compileStatement(sqlInsert);
        stmt.bindLong(1,id);
        stmt.bindString(2,content);
        stmt.bindString(3,selectedClass);
        stmt.executeInsert();
    }

    public void AddData(String classification){
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sqlInsert = "INSERT INTO class (class) VALUES (?)";
        SQLiteStatement stmt = db.compileStatement(sqlInsert);
        stmt.bindString(1,classification);
        stmt.executeInsert();
    }


    public ArrayList<String> GetAllDataInClass(){
        ArrayList<String> classList = new ArrayList<>();

        if(helper2 == null) return classList;

//        データベースからデータを取得
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sql = "SELECT * FROM class";
        Cursor cursor = db.rawQuery(sql, null);

//        カーソルから順にデータを取り出しリストに登録
        String clTxt = "";
        while (cursor.moveToNext()){
//            memoの取得
            int idxClass = cursor.getColumnIndex("class");
            clTxt = cursor.getString(idxClass);
            classList.add(clTxt);
        }
        return classList;
    }

    public void DeleteDataInClass(String txt){
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sqlDelete = "DELETE FROM class WHERE class = ?";
        SQLiteStatement stmt = db.compileStatement(sqlDelete);
        stmt.bindString(1,txt);
        stmt.executeUpdateDelete();
    }

    private ArrayList<Map<String, String>> GetAllData(){
        ArrayList<Map<String, String>> planList = new ArrayList<>();
        Map<String, String> plan;

//        データベースからclassの値を指定してデータを取得
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "SELECT * FROM plans";
        Cursor cursor = db.rawQuery(sql, null);

//        カーソルから順にデータを取り出しリストに登録
        String txt = "";
        int _id = 0;
        while (cursor.moveToNext()){
//            mapの初期化
            plan = new HashMap<>();

//            memoの取得
            int idxMemo = cursor.getColumnIndex("memo");
            txt = cursor.getString(idxMemo);

//            _idの取得
            int idx_id = cursor.getColumnIndex("_id");
            _id = cursor.getInt(idx_id);

//            mapにidとplanを追加
            plan.put("id",Integer.toString(_id));
            plan.put("plan",txt);

//            リストにmapを追加
            planList.add(plan);
        }
        return planList;
    }

    public void deleteAllClassData(){
        SQLiteDatabase db = helper2.getWritableDatabase();
        String sqlDelete = "DELETE FROM class";
        SQLiteStatement stmt = db.compileStatement(sqlDelete);
        stmt.executeUpdateDelete();
    }





//    21億以上の予定が追加される可能性はめちゃ低いから、この処理は一旦忘れる
    public void arrangeElements(){
//idを前に詰める

//        データベースから全データを取得
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "SELECT * FROM plans";
        Cursor cursor = db.rawQuery(sql,null);


//        データベースから全データを削除
        String sqlDelete = "DELETE FROM plans";
        SQLiteStatement stmtDelete = db.compileStatement(sqlDelete);
        stmtDelete.executeUpdateDelete();
//        カーソルから順にデータを取り出し、idを再度割り振ってデータベースに再登録する
        int _id = 0;
        String txt = "";

        while (cursor.moveToNext()){
//            memoの取得
            int idxMemo = cursor.getColumnIndex("memo");
            txt = cursor.getString(idxMemo);

//            データベースにデータを挿入
            String sqlInsert = "INSERT INTO plans (_id, memo) VALUES (?, ?)";
            SQLiteStatement stmt = db.compileStatement(sqlInsert);
            stmt.bindLong(1,_id);
            stmt.bindString(2,txt);
            stmt.executeInsert();

//            _idをインクリメント
            _id++;
        }
    }
}

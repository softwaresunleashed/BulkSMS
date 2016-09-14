package com.unleashed.android.helpers.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by gupta on 8/4/2015.
 */

public class DBHelper {

    public static final String Id = "Id";
    public static final String JobId = "JobId";

    public static final String PhnNums = "PhnNums";
    public static final String Msg = "Msg";

    private static final String databasename = "BulkSMSDB";
    private static final String tablename = "JobTable";
    private static final int databaseversion = 1;
    private static final String create_table = "create table JobTable (Id integer primary key AUTOINCREMENT, " + "JobId text not null, PhnNums text not null, Msg text not null);";

    private final Context ct;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

//    public abstract class DBReference{
//        public DBHelper db_reference;
//
//        public DBHelper getDb_reference(){
//            return db_reference;
//        }
//    }



    public DBHelper(Context context) {
        this.ct = context;
        dbHelper = new DatabaseHelper(ct);
    }


    // Declaring the connect() method to connect to the database
    public DBHelper connect() throws SQLException {
        database = dbHelper.getWritableDatabase();
        return this;
    }

    // Declaring the disconnect() method to close the database
    public void disconnect() {
        dbHelper.close();
    }

    // Declaring the insertEmployee() method to add the employee details into the database
    public long insertNewJob(String jobid, String phnums, String msg) {
        ContentValues cv = new ContentValues();

        // Pack data into a ContentValue object
        cv.put(JobId, jobid);
        cv.put(PhnNums, phnums);
        cv.put(Msg, msg);

        // Connect to database before performing any operation.
        try {
            this.connect();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return database.insert(tablename, null, cv);
    }

    // Declaring the retrieveAllEmployees() method to retrieve all the employee details from the database.
    public Cursor retrieveAllJobs() {


        try{
            this.connect();
        }catch (SQLException sqlex){
            sqlex.printStackTrace();
        }

        return database.query(tablename, new String[]{Id, JobId, PhnNums, Msg}, null, null, null, null, null);
    }

    // Declaring the retrieveEmployee() method to retrieve the details of an employee.
    public Cursor retrieveJob(String jobid) {

        try{
            this.connect();
        }catch (SQLException sqlex){
            sqlex.printStackTrace();
        }

        Cursor c = database.query(true, tablename, new String[]{Id, JobId, PhnNums, Msg}, JobId + "=" + "\"" + jobid + "\"", null, null, null, null, null);

        if (c != null) {
            c.moveToFirst();
        }

        return c;
    }


    // Declaring the deleteEmployee() method to delete an employee detail
    public boolean deleteJob(String jobid){

        try{
            this.connect();
        }catch (SQLException sqlex){
            sqlex.printStackTrace();
        }

        return database.delete(tablename, JobId + "=" + "\"" + jobid + "\"", null) > 0;

    }


    // Declaring the updateEmployee() method to update an employee details from the database.
//    public boolean updateEmployee(long id, String empname, int empsal) throws SQLException {
//        this.connect();
//
//        ContentValues cvalues = new ContentValues();
//        cvalues.put(PhnNums, empname);
//        cvalues.put(Msg, empsal);
//
//        return database.update(tablename, cvalues, JobId + "=" + id, null) > 0;
//    }

    // CLASS DatabaseHelper (internal class)
    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context c) {
            super(c, databasename, null, databaseversion);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {

            database.execSQL(create_table);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
            database.execSQL("DROP TABLE IF EXISTS JobTable");
            onCreate(database);
        }
    }


}



package com.example.myapplication.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.myapplication.model.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String NAME = "toDoListDatabase";
    private static final String TODO_TABLE = "todo";
    private static final String ID = "id";
    private static final String TASK = "task";
    private static final String STATUS = "status";
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + TASK + " TEXT, "
            + STATUS + " INTEGER)";

    private SQLiteDatabase db;

    public DatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }
    public void openDatabase() {
        db = this.getWritableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TODO_TABLE);
        // Create tables again
        onCreate(db);
    }

    public void insertTask(ToDoModel task) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(TASK, task.getTask());
            cv.put(STATUS, 0);
            db.insert(TODO_TABLE, null, cv);
        } catch (SQLiteException e) {
            // Handle the exception, e.g., log the error or provide a user-friendly message
            e.printStackTrace();
        }
    }

    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        try (Cursor cur = db.query(TODO_TABLE, null, null, null, null, null, null, null)) {
            if (cur != null && cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                } while (cur.moveToNext());
            }
        }
        return taskList;
    }

    public boolean updateStatus(int id, int status) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(STATUS, status);
            return db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)}) > 0;
        } catch (SQLiteException e) {
            // Handle the exception, e.g., log the error or provide a user-friendly message
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateTask(int id, String task) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(TASK, task);
            return db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)}) > 0;
        } catch (SQLiteException e) {
            // Handle the exception, e.g., log the error or provide a user-friendly message
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTask(int id) {
        try {
            return db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)}) > 0;
        } catch (SQLiteException e) {
            // Handle the exception, e.g., log the error or provide a user-friendly message
            e.printStackTrace();
            return false;
        }
    }
}
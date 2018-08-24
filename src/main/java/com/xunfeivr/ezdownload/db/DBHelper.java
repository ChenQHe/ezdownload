package com.xunfeivr.ezdownload.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.xunfeivr.ezdownload.util.LogUtil;
import com.xunfeivr.ezdownload.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-21 上午10:04
 */
public class DBHelper extends SQLiteOpenHelper implements DBInterface {

    private static final String DB_NAME = "download-task.db";

    private static final int DB_VERSION = 1;

    private static DBHelper instance = null;

    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext(), DB_NAME, null, DB_VERSION);
        }
        return instance;
    }

    private DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        createTable(db);
    }

    private void createTable(SQLiteDatabase db) {
        String sql = "create table if not exists task_info(id integer primary key autoincrement,url text,path text,name text,current integer(128),length integer(128))";
        db.execSQL(sql);
        sql = "create table if not exists break_point(id integer primary key autoincrement,task_id integer,start integer(128),current integer(128),length integer(128))";
        db.execSQL(sql);
    }

    private void deleteTable(SQLiteDatabase db) {
        String sql = "drop table task_info";
        db.execSQL(sql);
        sql = "drop table break_point";
        db.execSQL(sql);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (db.needUpgrade(newVersion)) {
            deleteTable(db);
            createTable(db);
        }
    }

    @Override
    public synchronized int saveTaskInfo(TaskInfo taskInfo) {
        int result;
        TaskInfo info = getTaskInfo(taskInfo.getId());
        if (info != null) {
            result = updateTaskInfoCurrent(taskInfo.getId(), taskInfo.getCurrent());
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put("url", Util.md5(taskInfo.getUrl()));
            contentValues.put("name", taskInfo.getFileName());
            contentValues.put("path", taskInfo.getParent());
            contentValues.put("current", taskInfo.getCurrent());
            contentValues.put("length", taskInfo.getLength());
            result = (int) getReadableDatabase().insert("task_info", null, contentValues);
            contentValues.clear();
        }
        if (result == -1) {
            LogUtil.e("插入失败！");
        } else {
            LogUtil.e("插入断点成功！");
        }
        return result;
    }

    @Override
    public synchronized int saveBreakPointInfo(BreakPointInfo breakPointInfo) {
        int result;
        BreakPointInfo info = getBreakPointInfo(breakPointInfo.id);
        if (info == null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("start", breakPointInfo.start);
            contentValues.put("task_id", breakPointInfo.taskId);
            contentValues.put("current", breakPointInfo.offset);
            contentValues.put("length", breakPointInfo.length);
            result = (int) getReadableDatabase().insert("break_point", null, contentValues);
            contentValues.clear();
        } else {
            result = updateBreakPointInfoCurrent(breakPointInfo.id,breakPointInfo.offset);
        }
        if (result == -1) {
            LogUtil.e("插入失败！");
        } else {
            LogUtil.e("插入任务断点成功！");
        }
        return result;
    }

    @Override
    public synchronized TaskInfo getTaskInfo(int id) {
        String sql = "select * from task_info where id=" + id;
        Cursor cursor = null;
        TaskInfo taskInfo = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                long current = cursor.getLong(cursor.getColumnIndex("current"));
                long length = cursor.getLong(cursor.getColumnIndex("length"));
                taskInfo = new TaskInfo();
                taskInfo.setId(id);
                taskInfo.setUrl(url);
                taskInfo.setFileName(name);
                taskInfo.setParent(path);
                taskInfo.setLength(length);
                taskInfo.setCurrent(current);
                taskInfo.setBreakPointInfoList(getBreakPointList(id));
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return taskInfo;
    }

    public synchronized List<TaskInfo> getTaskInfoList() {
        String sql = "select * from task_info";
        Cursor cursor = null;
        List<TaskInfo> list = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                long current = cursor.getLong(cursor.getColumnIndex("current"));
                long length = cursor.getLong(cursor.getColumnIndex("length"));
                TaskInfo taskInfo = new TaskInfo();
                taskInfo.setId(id);
                taskInfo.setUrl(url);
                taskInfo.setFileName(name);
                taskInfo.setParent(path);
                taskInfo.setLength(length);
                taskInfo.setCurrent(current);
                taskInfo.setBreakPointInfoList(getBreakPointList(id));
                list.add(taskInfo);
            }
        } catch (SQLException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    public synchronized TaskInfo getTaskInfo(String url) {
        String sql = "select * from task_info where url=" + "\"" + Util.md5(url) + "\"";
        Cursor cursor = null;
        TaskInfo taskInfo = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                long current = cursor.getLong(cursor.getColumnIndex("current"));
                long length = cursor.getLong(cursor.getColumnIndex("length"));
                taskInfo = new TaskInfo();
                taskInfo.setId(id);
                taskInfo.setUrl(url);
                taskInfo.setFileName(name);
                taskInfo.setParent(path);
                taskInfo.setCurrent(current);
                taskInfo.setLength(length);
                taskInfo.setBreakPointInfoList(getBreakPointList(id));
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return taskInfo;
    }

    @Override
    public synchronized List<BreakPointInfo> getBreakPointList(int id) {
        List<BreakPointInfo> list = null;
        String sql = "select * from break_point where task_id=" + id;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            while (cursor.moveToNext()) {
                if (list == null) {
                    list = new ArrayList<>();
                }
                BreakPointInfo breakPointInfo = new BreakPointInfo();
                breakPointInfo.id = cursor.getInt(cursor.getColumnIndex("id"));
                breakPointInfo.offset = cursor.getLong(cursor.getColumnIndex("current"));
                breakPointInfo.length = cursor.getLong(cursor.getColumnIndex("length"));
                breakPointInfo.start = cursor.getLong(cursor.getColumnIndex("start"));
                breakPointInfo.taskId = id;
                list.add(breakPointInfo);
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return list;
    }

    @Override
    public synchronized BreakPointInfo getBreakPointInfo(int id) {
        String sql = "select * from break_point where id=" + id;
        Cursor cursor = null;
        BreakPointInfo breakPointInfo = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, null);
            if (cursor.moveToNext()) {
                breakPointInfo = new BreakPointInfo();
                breakPointInfo.id = id;
                breakPointInfo.taskId = cursor.getInt(cursor.getColumnIndex("task_id"));
                breakPointInfo.offset = cursor.getLong(cursor.getColumnIndex("current"));
                breakPointInfo.length = cursor.getLong(cursor.getColumnIndex("length"));
                breakPointInfo.start = cursor.getLong(cursor.getColumnIndex("start"));
            }
        } catch (SQLiteException e) {
            LogUtil.e(e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return breakPointInfo;
    }

    @Override
    public synchronized void deleteTaskInfo(int id) {
        getReadableDatabase().delete("task_info", "id=?", new String[]{String.valueOf(id)});
        getReadableDatabase().delete("break_point", "task_id=?", new String[]{String.valueOf(id)});
    }

    @Override
    public synchronized int updateTaskInfoCurrent(int id, long current) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("current", current);
        int result = getReadableDatabase().update("task_info", contentValues, "id=?", new String[]{String.valueOf(id)});
        if (result == -1) {
            LogUtil.e("更新失败!");
        }
        contentValues.clear();
        return result;
    }

    @Override
    public synchronized int updateBreakPointInfoCurrent(int id, long current) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("current", current);
        int result = getReadableDatabase().update("break_point", contentValues, "id=?", new String[]{String.valueOf(id)});
        if (result == -1) {
            LogUtil.e("更新失败!");
        }
        return result;
    }

}

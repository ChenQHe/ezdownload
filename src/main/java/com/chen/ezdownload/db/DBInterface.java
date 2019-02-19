package com.chen.ezdownload.db;

import java.util.List;

/**
 * Created by android studio.
 * company:Xunfei Magic Technology Co., Ltd.
 * author:ChenHe
 * Time:18-8-21 AM:9:55
 */
public interface DBInterface {

    /**
     * Save a task to the database
     * @param taskInfo  Task information
     * @return Database saved id
     */
    int saveTaskInfo(TaskInfo taskInfo);

    /**
     * Save a breakpoint information to the database
     * @param breakPointInfo  Breakpoint information
     * @return Database saved id
     */
    int saveBreakPointInfo(BreakPointInfo breakPointInfo);

    /**
     * Get task information from the database by id If none is returned null
     * @param id Task id
     * @return Task information
     */
    TaskInfo getTaskInfo(int id);

    /**
     * Get all breakpoint information of the task from the database by task id
     * @param id Task id
     * @return All breakpoint information for this task
     */
    List<BreakPointInfo> getBreakPointList(int id);

    /**
     * Get breakpoint information from the database with breakpoint id
     * @param id Breakpoint id
     * @return Breakpoint information
     */
    BreakPointInfo getBreakPointInfo(int id);

    /**
     * Delete a task information from the database
     * @param id Task id
     */
    int deleteTaskInfo(int id);

    /**
     * Update the progress of a task in the database
     * @param id Task id
     * @param current Current progress
     * @return Database saved task id
     */
    int updateTaskInfoCurrent(int id, long current);

    /**
     * Update the progress of a breakpoint in the database
     * @param id Breakpoint id
     * @param current Current progress
     * @return Data save breakpoint id
     */
    int updateBreakPointInfoCurrent(int id, long current);
}

package com.xunfeivr.ezdownload.db;

import java.util.List;

/**
 * Created by android studio.
 * company:讯飞幻境科技有限公司
 * author:ChenHe
 * Time:18-8-21 上午9:55
 */
public interface DBInterface {

    /**
     * 将一个任务保存到数据库
     * @param taskInfo  任务信息
     * @return 数据库保存的 id
     */
    int saveTaskInfo(TaskInfo taskInfo);

    /**
     * 将一个断点信息保存到数据库
     * @param breakPointInfo  断点信息
     * @return 数据库保存的 id
     */
    int saveBreakPointInfo(BreakPointInfo breakPointInfo);

    /**
     * 通过id从数据库中获取 任务信息  如果没有 返回 null
     * @param id 任务 id
     * @return 任务信息
     */
    TaskInfo getTaskInfo(int id);

    /**
     * 通过 任务 id从数据库获取 任务的所有断点信息
     * @param id 任务 id
     * @return 该任务的所有断点信息
     */
    List<BreakPointInfo> getBreakPointList(int id);

    /**
     * 通过断点 id 从数据库中获取断点信息
     * @param id 断点 id
     * @return 断点信息
     */
    BreakPointInfo getBreakPointInfo(int id);

    /**
     * 从数据库中删除一条 任务信息
     * @param id 任务 id
     */
    void deleteTaskInfo(int id);

    /**
     * 更新数据库中 一条任务的 进度
     * @param id 任务 id
     * @param current 当前进度
     * @return 数据库保存的任务 id
     */
    int updateTaskInfoCurrent(int id, long current);

    /**
     * 更新数据库中 一个断点的 进度
     * @param id 断点 id
     * @param current 当前进度
     * @return 数据保存的断点 id
     */
    int updateBreakPointInfoCurrent(int id, long current);
}

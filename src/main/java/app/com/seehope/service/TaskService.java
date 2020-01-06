package app.com.seehope.service;

import app.com.seehope.entities.TaskInfo;
import app.com.seehope.vo.PageVo;

import java.util.List;

public interface TaskService {

    /**
     * 获取指定页数的任务
     * @param pageVo
     * @return
     */
    public List<TaskInfo> getTaskList(PageVo pageVo);

    /**
     * 更新老任务以及新任务状态
     * @return
     */
    public boolean updateTaskStatus();

    /**
     * 增加新任务
     * @param taskInfo
     * @return
     */
    public boolean addTask(String employeeeName,TaskInfo taskInfo);

    /**
     * 通过任务状态获取所有任务
     * @param status
     * @param pageVo
     * @return
     */
    public List<TaskInfo> getAllTaskByStatus(Integer status, PageVo pageVo);

    /**
     * 根据指定员工姓名查出所有发布的任务（不包括已被冻结的员工发布的任务）
     * @param employeeName
     * @param pageVo
     * @return
     */
    public List<TaskInfo> getAllTaskByEmployee(String employeeName , PageVo pageVo);

    /**
     * 更新指定的任务内容
     * @param taskInfo
     * @return
     */
    public boolean updateTask(String employeeName,TaskInfo taskInfo);

    /**
     * 判断时间可靠性
     * @param taskInfo
     * @param TIME
     */
    public void judgeTaskTime(TaskInfo taskInfo,final long TIME);

}

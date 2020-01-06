package app.com.seehope.service;

import app.com.seehope.entities.AttendDetailed;
import app.com.seehope.entities.EmployeeInfo;

import java.util.List;

/**
 * @Author: 龍右
 * @Date: 2019/12/22 19:43
 * @Description:
 */
public interface AttendDetailedService {

    /**
     * 创建新任务的所有员工打卡详情记录
     * @param taskId
     * @return
     */
    public boolean createAttendDetailed(Integer taskId);

    /**
     * 根据打卡记录创建指定任务的所有员工打卡详情记录
     * @param taskId
     * @return
     */
    public boolean createAttendDetailedById(Integer taskId);

    /**
     * 更新指定任务的所有员工的打卡详情
     * @param taskId
     * @return
     */
    public boolean updateAttendDetailed(Integer taskId);

    /**
     * 根据任务id查询签到详情记录
     * @param taskId
     * @param page
     * @param pageSize
     * @return
     */
    public List<AttendDetailed> findAttendDetailedByTaskId(Integer taskId,Integer page,Integer pageSize);

    /**
     * 通过打卡状态获取出席详情记录
     * @param status
     * @param taskId
     * @param page
     * @param pageSize
     * @return
     */
    public List<AttendDetailed> findAttendDetailedByStatus(Integer status,Integer taskId,Integer page,Integer pageSize);

    /**
     * 通过早退状态获取出席详情记录
     * @param late
     * @param taskId
     * @param page
     * @param pageSize
     * @return
     */
    public List<EmployeeInfo> findAttendDetatiledByLate(Integer late, Integer taskId, Integer page, Integer pageSize);

    /**
     * 通过早退状态获取出席详情记录
     * @param early
     * @param taskId
     * @param page
     * @param pageSize
     * @return
     */
    public List<EmployeeInfo> findAttendDetailedByEarly(Integer early, Integer taskId, Integer page, Integer pageSize);

    /**
     * 根据打卡异地状态获取出席详情记录
     * @param differentPlaces
     * @param taskId
     * @param page
     * @param pageSize
     * @return
     */
    public List<EmployeeInfo> findAttendDetailByDifferentPlaces(Integer differentPlaces, Integer taskId, Integer page, Integer pageSize);

    /**
     * 删除指定的任务出席的详情记录
     * @param taskId
     * @return
     */
    public boolean deleteAttendDetailed(Integer taskId);
}

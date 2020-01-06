package app.com.seehope.service;

import app.com.seehope.entities.AttendInfo;
import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.utils.CommonResponse;
import app.com.seehope.vo.AttendVo;
import io.swagger.models.auth.In;

import java.sql.Timestamp;
import java.util.List;

public interface AttendService {

    /**
     * 更新员工打卡信息
     * @param attendVo
     * @return
     */
    public boolean updateAttend(AttendVo attendVo);

    /**
     * 根据员工编号查找对应出席记录
     * @param page
     * @param pageSize
     * @param employeeNumber
     * @return
     */
    public List<AttendVo> findAttendByEmployeeNumber(Integer page,Integer pageSize,Integer employeeNumber);

    /**
     * 根据任务名称查询打卡记录
     * @param taskName
     * @param page
     * @param pageSize
     * @return
     */
    public List<AttendVo> findAttendByTaskName(String taskName, Integer page, Integer pageSize);

    /**
     * 根据任务id获取员工未打两次卡的记录
     * @param taskId
     * @return
     */
    public List<EmployeeInfo> getNoPunchCardEmployee(Integer taskId);

    public List<EmployeeInfo> getPunchCardEmployee(Integer taskId);

    /**
     * 根据任务名称获取员工未打两次卡的记录
     * @param taskName
     * @return
     */
    public List<EmployeeInfo> getNoPunchCardEmployee(String taskName);

    /**
     * 根据任务打卡时间范围来查询对应打卡的员工
     * @param taskId
     * @param beginTime
     * @param endTime
     * @return
     */
    public List<EmployeeInfo> getAttendByTime(Integer taskId, Timestamp beginTime, Timestamp endTime);

    /**
     * 查询指定员工的正常打卡的记录(包括签到和签退)
     * @param list
     * @return
     */
    public List<AttendInfo> findNormalPunchCardAttend(List<Integer> list,Integer taskId);

    /**
     * 创建员工出勤记录
     * @param attendVo
     * @return
     */
    public boolean createAttendInfo(AttendVo attendVo);

    /**
     * 查询指定任务的出席员工记录
     * @param taskId
     * @return
     */
    public List<Integer>  findAttendEmployee(Integer taskId);

    /**
     * 查询在第一次打卡时间内的员工
     * @param list
     * @param taskId
     * @return
     */
    public List<Integer> findStartAttendEmployee(List<Integer> list,Integer taskId);

    /**
     * 查询在第二次打卡时间内的员工
     * @param list
     * @param taskId
     * @return
     */
    public List<Integer> findEndAttendEmployee(List<Integer> list, Integer taskId);
}

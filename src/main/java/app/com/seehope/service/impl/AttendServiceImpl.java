package app.com.seehope.service.impl;

import app.com.seehope.entities.AttendDetailed;
import app.com.seehope.entities.AttendInfo;
import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.entities.TaskInfo;
import app.com.seehope.service.AttendService;
import app.com.seehope.utils.*;
import app.com.seehope.vo.AttendVo;
import jmms.core.Entities;
import leap.core.value.Record;
import leap.lang.Collections2;
import leap.lang.New;
import leap.orm.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AttendServiceImpl implements AttendService {

    @Autowired
    private Dao dao;

    @Autowired
    private Entities entities;


    @Override
    public boolean createAttendInfo(AttendVo attendVo) {
        final long TIME = System.currentTimeMillis();
        //---------条件判断-----------
        List<EmployeeInfo> list = dao.createCriteriaQuery(EmployeeInfo.class)
                .where(New.hashMap("employeeName", attendVo.getEmployeeName())).list();
        //判断是否有此员工
        if(list.size() != 1 ){
            ExceptionUtils.solveException(ErrorConstant.INTERNALSERVERERROR,"未找到此员工");
        }
        List<TaskInfo> taskInfos = dao.createCriteriaQuery(TaskInfo.class)
                .where(New.hashMap("taskName", attendVo.getTaskName())).list();
        TaskInfo taskInfo = taskInfos.get(0);
        //判断任务状态是否有效
        if(taskInfo.getTaskStatus() == 2){
            ExceptionUtils.solveException(ErrorConstant.INTERNALSERVERERROR,"打卡任务已失效");
        }
        //判断该员工是否已经打卡了（签到签退都打卡了）
        List<AttendInfo> attendInfos = dao.createCriteriaQuery(AttendInfo.class)
                .where(New.hashMap("empAttend",list.get(0).getEmployeeId(),
                        "taskAttend",taskInfo.getTaskId())).list();
        if (attendInfos.size() ==  2){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "此次任务您已成功签到签退了");
        }
        //Integer integer = TimeUtils.judgeOneOrTwo(new Timestamp(TIME), taskInfo);
        Integer integer = TimeUtils.judgeOneOrTwo(attendVo.getAttendInfo().getAttendTime(), taskInfo);
        if (integer == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "打卡时间未到，请到指定时间再来");
        }
        //-----------正式打卡-------------
        //添加出席记录
        AttendInfo attendInfo = new AttendInfo();
        attendInfo.setEmpAttend(list.get(0).getEmployeeId());
        attendInfo.setTaskAttend(taskInfo.getTaskId());
        //attendInfo.setAttendTime(new Timestamp(TIME));
        attendInfo.setAttendTime(attendVo.getAttendInfo().getAttendTime());
        attendInfo.setAttendAddress(attendVo.getAddress());
        int insert = dao.insert(AttendInfo.class, attendInfo);
        if (insert>0){
            return true;
        }
        return false;
    }

    @Override
    public List<Integer> findAttendEmployee(Integer taskId) {
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, taskId);
        List<AttendInfo> attendInfoList = dao.createSqlQuery(AttendInfo.class, "SELECT emp_attend from attend_info " +
                "where task_attend =  " + taskId +
                " GROUP BY emp_attend HAVING COUNT(emp_attend) = 1").list();
        List<Integer> list = new ArrayList<>();
        attendInfoList.forEach(attendInfo -> {
            list.add(attendInfo.getEmpAttend());
        });
        return list;
    }

    @Override
    public List<Integer> findStartAttendEmployee(List<Integer> list, Integer taskId) {
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, taskId);
        if (Collections2.isNotEmpty(list)){
            List<AttendInfo> attendInfoList1 = new ArrayList<>();
            list.forEach(integer -> {
                List<AttendInfo> attendInfoList = dao.createSqlQuery(AttendInfo.class, "SELECT emp_attend from attend_info " +
                        "where attend_time BETWEEN  '" + taskInfo.getTaskStartTime() + "' and '"
                        + taskInfo.getTaskStartOver() +
                        "' and task_attend = " + taskId +" and emp_attend = "+integer).list();
                attendInfoList1.addAll(attendInfoList);
            });
            attendInfoList1.forEach(System.out::println);
            return attendInfoList1.stream().map(AttendInfo::getEmpAttend).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public List<Integer> findEndAttendEmployee(List<Integer> list, Integer taskId) {
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, taskId);
        if (Collections2.isNotEmpty(list)){
            List<AttendInfo> attendInfoList1 = new ArrayList<>();
            list.forEach(integer -> {
                List<AttendInfo> attendInfoList = dao.createSqlQuery(AttendInfo.class, "SELECT emp_attend from attend_info " +
                        "where attend_time BETWEEN  '" + taskInfo.getTaskEndTime() + "' and '"
                        + taskInfo.getTaskEndOver() +
                        "' and task_attend = " + taskId +" and emp_attend = "+integer).list();
                attendInfoList1.addAll(attendInfoList);
            });
            attendInfoList1.forEach(System.out::println);
            return attendInfoList1.stream().map(AttendInfo::getEmpAttend).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean updateAttend(AttendVo attendVo) {
        return false;
    }

    @Override
    public List<AttendVo> findAttendByEmployeeNumber(Integer page, Integer pageSize, Integer employeeNumber) {
        List<AttendInfo> attendInfoList;
        List<EmployeeInfo> employeeInfos = dao.createCriteriaQuery(EmployeeInfo.class).where(New.hashMap("employeeNumber", employeeNumber)).list();
        if (employeeInfos == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到此员工编号");
        }
        if(page == 1){
            attendInfoList = dao.createCriteriaQuery(AttendInfo.class)
                    .where(New.hashMap("empAttend",employeeInfos.get(0).getEmployeeId()))
                    .limit(page,pageSize).list();
        }else{
            attendInfoList = dao.createCriteriaQuery(AttendInfo.class)
                    .where(New.hashMap("empAttend",employeeInfos.get(0).getEmployeeId()))
                    .limit((page - 1) * pageSize+1,page*pageSize)
                    .list();
        }
        List<AttendVo> attendVoList = new ArrayList<>();
        attendInfoList.forEach(attendInfo -> {
            AttendVo attendVo = new AttendVo();
            attendVo.setAttendInfo(attendInfo);
            attendVo.setEmployeeName(employeeInfos.get(0).getEmployeeName());
            attendVo.setAddress(attendInfo.getAttendAddress());
            attendVo.setTaskName(dao.findOrNull(TaskInfo.class,
                    New.hashMap("taskId",attendInfo.getTaskAttend()))
                    .getTaskName());
            attendVoList.add(attendVo);
        });
        return attendVoList;
    }

    @Override
    public List<AttendVo> findAttendByTaskName(String taskName, Integer page, Integer pageSize) {
        List<AttendInfo> attendInfoList;
        List<TaskInfo> taskInfos = dao.createCriteriaQuery(TaskInfo.class).where(New.hashMap("taskName", taskName)).list();;
        if (taskInfos == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到对应的任务");
        }
        if(page == 1){
            attendInfoList = dao.createCriteriaQuery(AttendInfo.class)
                    .where(New.hashMap("taskAttend",taskInfos.get(0).getTaskId()))
                    .limit(page,pageSize).list();
        }else{
            attendInfoList = dao.createCriteriaQuery(AttendInfo.class)
                    .where(New.hashMap("taskAttend",taskInfos.get(0).getTaskId()))
                    .limit((page - 1) * pageSize+1,page*pageSize)
                    .list();
        }
        attendInfoList.forEach(System.out::println);
        List<AttendVo> attendVoList = new ArrayList<>();
        attendInfoList.forEach(attendInfo -> {
            AttendVo attendVo = new AttendVo();
            attendVo.setAttendInfo(attendInfo);
            attendVo.setAddress(attendInfo.getAttendAddress());
            attendVo.setTaskName(taskName);
            attendVo.setEmployeeName(dao.find(EmployeeInfo.class,New.hashMap("employeeId",attendInfo.getEmpAttend())).getEmployeeName());
            attendVoList.add(attendVo);
        });
        return attendVoList;
    }

    @Override
    public List<EmployeeInfo> getNoPunchCardEmployee(Integer taskId) {
        List<EmployeeInfo> list = dao.createSqlQuery(EmployeeInfo.class,
                "select * from employee_info where employee_id not  in " +
                        "(select emp_attend from attend_info where task_attend = "
                        +taskId+")").list();
        return list;
    }

    @Override
    public List<EmployeeInfo> getPunchCardEmployee(Integer taskId) {
        List<EmployeeInfo> list = dao.createSqlQuery(EmployeeInfo.class,
                "select * from employee_info where employee_id in " +
                        "(select emp_attend from attend_info where task_attend = "
                        +taskId+" GROUP BY emp_attend " +
                        " HAVING COUNT(emp_attend) = 2)").list();
        return list;
    }

    @Override
    public List<EmployeeInfo> getNoPunchCardEmployee(String taskName) {
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, New.hashMap("taskName", taskName));
        if(taskInfo == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到对应的任务");
        }
        List<EmployeeInfo> list = dao.createSqlQuery(EmployeeInfo.class,
                "select * from employee_info where employee_id not  in " +
                        "(select emp_attend from attend_info where task_attend = "
                        +taskInfo.getTaskId()+")").list();
        return list;
    }

    @Override
    public List<EmployeeInfo> getAttendByTime(Integer taskId, Timestamp beginTime, Timestamp endTime) {
        List<EmployeeInfo> employeeInfoList = dao.createSqlQuery(EmployeeInfo.class, "select * from employee_info where employee_id in " +
                "(select emp_attend from attend_info where attend_time  " +
                " between  '" + beginTime + "' and '" + endTime + "' and task_attend = " + taskId + " )").list();
        return employeeInfoList;
    }

    @Override
    public List<AttendInfo> findNormalPunchCardAttend(List<Integer> list,Integer taskId) {
        if (list.size() == 0){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未指定员工id");
        }
        List<AttendInfo> attendInfoList = null;
        //判断list是否只有一个
        if (list.size() == 1){
            attendInfoList = dao.createSqlQuery(AttendInfo.class,
                    "select * from attend_info where emp_attend ="+list.get(0)+" and task_attend = "+taskId).list();
            if (attendInfoList.size() != 2){
                attendInfoList = null;
            }
        }
        //若list大于1个
        if(list.size() > 1){
            attendInfoList = dao.createSqlQuery(AttendInfo.class,
                    "select * from attend_info where emp_attend in (select emp_attend from attend_info " +
                    "where emp_attend in "+ StringUtils.listToStringFromSQL(list)
                    +" and task_attend = " +taskId+
                    " GROUP BY emp_attend HAVING COUNT(emp_attend)>1) and task_attend = " +taskId).list();
        }
        return attendInfoList;
    }


}

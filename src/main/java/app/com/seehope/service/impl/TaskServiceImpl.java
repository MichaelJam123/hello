package app.com.seehope.service.impl;

import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.entities.TaskInfo;
import app.com.seehope.service.AttendDetailedService;
import app.com.seehope.service.TaskService;
import app.com.seehope.utils.ErrorConstant;
import app.com.seehope.utils.ExceptionUtils;
import app.com.seehope.utils.LoggerUtils;
import app.com.seehope.utils.TimeUtils;
import app.com.seehope.vo.PageVo;
import leap.lang.New;
import leap.orm.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private Dao dao;

    @Autowired
    private AttendDetailedService attendDetailedService;

    @Override
    public List<TaskInfo> getTaskList(PageVo pageVo) {
        List<TaskInfo> list = null;
        if(pageVo.getPage() == 1){
           list = dao.createCriteriaQuery(TaskInfo.class).selectExclude("")
                   .limit(pageVo.getPage(),pageVo.getPageSize()).list();
        }else{
            list = dao.createCriteriaQuery(TaskInfo.class).selectExclude("")
                    .limit((pageVo.getPage()-1)*pageVo.getPageSize()+1,pageVo.getPage()*pageVo.getPageSize()).list();
        }
        return list;
    }

    @Override
    public boolean updateTaskStatus() {
        final long TIME = System.currentTimeMillis();
        //查出状态为 0（代表未开始进行）的任务
        List<TaskInfo> taskList0 = dao.createCriteriaQuery(TaskInfo.class).where(New.hashMap("taskStatus", "0")).list();
        //查询比当前系统时间更早的任务，就返回数据，否则不返回数据
        List<TaskInfo> taskInfoList0 = taskList0.stream().filter(task -> task.getTaskStartTime()
                .compareTo(new Timestamp(TIME)) < 0).collect(Collectors.toList());
        //则将任务状态更新为 1(正在执行的状态)
        taskInfoList0.forEach(taskInfo -> {
            taskInfo.setTaskStatus(1);
        });
        //进行批量更新数据
        dao.batchUpdate(TaskInfo.class, taskInfoList0);
        //查出状态为1的任务
        List<TaskInfo> taskList1 = dao.createCriteriaQuery(TaskInfo.class)
                .where(New.hashMap("taskStatus", "1")).list();
        //查询比当前系统时间更晚的任务，就返回数据，否则不返回数据
        List<TaskInfo> taskInfoList1 = taskList1.stream().filter(task -> task.getTaskEndOver()
                .compareTo(new Timestamp(TIME)) < 0).collect(Collectors.toList());
        //则将任务状态更新为 2(已结束的状态)
        taskInfoList1.forEach(taskInfo -> {
            taskInfo.setTaskStatus(2);
        });
        //进行批量更新数据
        dao.batchUpdate(TaskInfo.class, taskInfoList1);
        return true;
    }

    @Override
    public boolean addTask(String employeeeName,TaskInfo taskInfo) {
        final long TIME = System.currentTimeMillis();
        //查出对应员工信息且该员工状态不能为冻结
        List<EmployeeInfo> employeeInfos = dao.createCriteriaQuery(EmployeeInfo.class)
                .where(New.hashMap("employee_name", employeeeName)).whereAnd("employee_status <> 1").list();
        //若不止一个员工或者未找到对应员工，则返回异常信息
        if(employeeInfos.size()<0 || employeeInfos.size() != 1){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未查找出员工信息（员工状态异常）或者员工信息不止一个，请重新输入");
        }
        taskInfo.setTaskCreate(employeeInfos.get(0).getEmployeeId());
        //判断创建签到任务时候的时间是否靠谱
        judgeTaskTime(taskInfo, TIME);
        //流式输出
        employeeInfos.forEach(employeeInfo -> {
            taskInfo.setTaskCreate(employeeInfo.getEmployeeId());
        });
        taskInfo.setTaskStatus(0);
        taskInfo.setTaskCreateTime(new Timestamp(TIME));
        if( dao.insert(TaskInfo.class,taskInfo) > 0 ){
            List<TaskInfo> list = dao.createCriteriaQuery(TaskInfo.class).where(New.hashMap("taskCreate", taskInfo.getTaskCreate(),
                    "taskCreateTime", taskInfo.getTaskCreateTime())).list();
            LoggerUtils.LOGGER.info(list.get(0).toString());
            boolean flag = attendDetailedService.createAttendDetailed(list.get(0).getTaskId());
            return flag;
        }
        return false;
    }

    @Override
    public List<TaskInfo> getAllTaskByStatus(Integer status, PageVo pageVo) {
        List<TaskInfo> taskInfos;
        if (pageVo.getPage() == 1) {
            taskInfos = dao.createCriteriaQuery(TaskInfo.class)
                    .where(New.hashMap("taskStatus", status))
                    .limit(pageVo.getPage(), pageVo.getPageSize()).list();
        } else {

            taskInfos = dao.createCriteriaQuery(TaskInfo.class)
                    .where(New.hashMap("taskStatus", status))
                    .limit((pageVo.getPage() - 1) * pageVo.getPageSize() + 1, pageVo.getPage()
                            * pageVo.getPageSize()).list();
        }
        taskInfos.sort(Comparator.comparing(TaskInfo::getTaskStartTime));
        return taskInfos;
    }

    @Override
    public List<TaskInfo> getAllTaskByEmployee(String employeeName, PageVo pageVo) {
        List<TaskInfo> taskList;
        //首先查出对应员工的名字
        List<EmployeeInfo> employeeInfoList = dao.createSqlQuery(EmployeeInfo.class,
                "select * from employee_info where employee_name like '%"
                        + employeeName + "%' and employee_status <> 1").list();
        //若不止一个员工或者未找到对应员工，则返回异常信息
        if(employeeInfoList.size()<0 || employeeInfoList.size() != 1){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未查找出员工信息或者员工信息不止一个，请重新输入");
        }
        EmployeeInfo employeeInfos = new EmployeeInfo();
        employeeInfoList.forEach(employeeInfo -> {
            employeeInfos.setEmployeeId(employeeInfo.getEmployeeId());
        });
        //根据page进行查询
        if (pageVo.getPage() == 1){
            taskList = dao.createCriteriaQuery(TaskInfo.class)
                    .where(New.hashMap("taskCreate", employeeInfos.getEmployeeId()))
                    .limit(pageVo.getPage(), pageVo.getPageSize()).list();
        }else{
            taskList = dao.createCriteriaQuery(TaskInfo.class)
                    .where(New.hashMap("taskCreate", employeeInfos.getEmployeeId()))
                    .limit((pageVo.getPage() - 1) * pageVo.getPageSize() + 1, pageVo.getPage() * pageVo.getPageSize()).list();
        }
        //根据任务开始时间先后进行排序
        taskList.sort(Comparator.comparing(TaskInfo::getTaskStartTime));
        return taskList;
    }

    @Override
    public boolean updateTask(String employeeName,TaskInfo taskInfo) {
        final long TIME = System.currentTimeMillis();
        //根据员工姓名找员工信息
        List<EmployeeInfo> employeeInfos = dao.createCriteriaQuery(EmployeeInfo.class).where(
                New.hashMap("employeeName", employeeName,
                        "employeeStatus",0)).list();
        if (employeeInfos.size() == 0){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到此员工的信息");
        }
        taskInfo.setTaskCreate(employeeInfos.get(0).getEmployeeId());
        taskInfo.setTaskCreateTime(new Timestamp(TIME));
        if (taskInfo.getTaskStatus() == null){
            taskInfo.setTaskStatus(0);
        }
        judgeTaskTime(taskInfo, TIME);
        int update = dao.update(TaskInfo.class, taskInfo);
        if (update > 0){
            return true;
        }
        return false;
    }

    @Override
    public void judgeTaskTime(TaskInfo taskInfo,final long TIME) {
        //判断创建签到任务时候的时间是否靠谱
        if (taskInfo.getTaskStartTime() == null || taskInfo.getTaskStartOver() == null) {
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "签到打卡时间未输入，请重新输入");
        }
        if (!TimeUtils.compareTwoTime(new Timestamp(TIME), taskInfo.getTaskStartTime()) ||
                !TimeUtils.compareTwoTime(taskInfo.getTaskStartTime(), taskInfo.getTaskStartOver())) {
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "签到打卡开始时间比结束时间还晚，时间错误");
        }
        //判断创建签退任务时候的时间是否靠谱
        if (taskInfo.getTaskEndTime() == null || taskInfo.getTaskEndOver() == null) {
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "签退打卡时间未输入，请重新输入");
        }
        if (!TimeUtils.compareTwoTime(new Timestamp(TIME), taskInfo.getTaskEndTime()) ||
                !TimeUtils.compareTwoTime(taskInfo.getTaskEndTime(), taskInfo.getTaskEndOver())) {
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "签退打卡开始时间比结束时间还晚，时间错误");
        }
    }


}

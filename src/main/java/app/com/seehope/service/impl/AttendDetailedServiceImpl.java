package app.com.seehope.service.impl;

import app.com.seehope.entities.AttendDetailed;
import app.com.seehope.entities.AttendInfo;
import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.entities.TaskInfo;
import app.com.seehope.service.AttendDetailedService;
import app.com.seehope.service.AttendService;
import app.com.seehope.service.EmployeeService;
import app.com.seehope.utils.ErrorConstant;
import app.com.seehope.utils.ExceptionUtils;
import app.com.seehope.utils.StringUtils;
import jmms.core.Entities;
import leap.core.annotation.Transactional;
import leap.lang.Collections2;
import leap.lang.New;
import leap.orm.dao.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author: 龍右
 * @Date: 2019/12/10 19:44
 * @Description:
 */
@Service
public class AttendDetailedServiceImpl implements AttendDetailedService {

    @Autowired
    private Dao dao;

    @Autowired
    private Entities entities;

    @Autowired
    private AttendService attendService;

    @Autowired
    private EmployeeService employeeService;

    @Override
    public boolean createAttendDetailed(Integer taskId) {
        //获取指定任务
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, New.hashMap("taskId", taskId));
        List<AttendDetailed> attendDetailedList1 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adTask", taskId)).list();
        if (Collections2.isNotEmpty(attendDetailedList1)) {
            return false;
        }
        if (taskInfo == null ){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "没找到指定任务");
        }
        //查询是否已经生成对应的签到详情表
        List<AttendDetailed> attendDetailedList = dao.createCriteriaQuery(AttendDetailed.class)
                .where(New.hashMap("adTask", taskInfo.getTaskId())).list();
        if(attendDetailedList.size() != 0){ //代表已经生成好了
            return false;
        }
        //查询现拥有的员工
        List<EmployeeInfo> allEmployee = employeeService.findAllEmployee();
        allEmployee.forEach(employeeInfo -> {
            AttendDetailed attendDetailed = new AttendDetailed();
            attendDetailed.setAdStatus(0);
            attendDetailed.setAdEmp(employeeInfo.getEmployeeId());
            attendDetailed.setAdNumber(1);
            attendDetailed.setAdDifferentPlaces(0);
            attendDetailed.setAdEarly(0);
            attendDetailed.setAdLate(0);
            attendDetailed.setAdTask(taskId);
            attendDetailed.setAdTime(new Timestamp(System.currentTimeMillis()));
            dao.insert(AttendDetailed.class,attendDetailed);
            attendDetailed.setAdTime(new Timestamp(System.currentTimeMillis()));
            attendDetailed.setAdNumber(2);
            dao.insert(AttendDetailed.class,attendDetailed);
        });
        return true;
    }

    @Transactional
    @Override
    public boolean createAttendDetailedById(Integer taskId) {
        final long TIME = System.currentTimeMillis();
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, taskId);
        if (taskInfo == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到指定的任务");
        }
        List<AttendDetailed> attendDetailedList1 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adTask", taskId)).list();
        if (Collections2.isNotEmpty(attendDetailedList1)) {
            return false;
        }
        //找出所有缺勤的员工（一次打卡都没有的）
        List<EmployeeInfo> noPunchCardEmployee = attendService.getNoPunchCardEmployee(taskId);
        if (noPunchCardEmployee.size() != 0){
            noPunchCardEmployee.forEach(employeeInfo -> {
                AttendDetailed attendDetailed = new AttendDetailed();
                attendDetailed.setAdStatus(0);
                attendDetailed.setAdTime(new Timestamp(TIME));
                attendDetailed.setAdStatus(0);
                attendDetailed.setAdLate(0);
                attendDetailed.setAdEarly(0);
                attendDetailed.setAdNumber(1);
                attendDetailed.setAdTask(taskId);
                attendDetailed.setAdDifferentPlaces(0);
                attendDetailed.setAdEmp(employeeInfo.getEmployeeId());
                //创建所有缺勤员工信息
                dao.insert(AttendDetailed.class, attendDetailed);
                attendDetailed.setAdNumber(2);
                dao.insert(AttendDetailed.class, attendDetailed);
            });
        }

        //找出所有勤快打卡的员工
        List<EmployeeInfo> punchCardEmployee = attendService.getPunchCardEmployee(taskId);
        if (punchCardEmployee.size() != 0){
            punchCardEmployee.forEach(employeeInfo -> {
                AttendDetailed attendDetailed = new AttendDetailed();
                List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                        "empAttend", employeeInfo.getEmployeeId())).list();
                //比对地址
                if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                    attendDetailed.setAdDifferentPlaces(0);
                }else{
                    attendDetailed.setAdDifferentPlaces(1);
                }
                attendDetailed.setAdTask(taskId);
                attendDetailed.setAdTime(new Timestamp(TIME));
                attendDetailed.setAdStatus(1);
                attendDetailed.setAdEarly(0);
                attendDetailed.setAdLate(0);
                attendDetailed.setAdAttend(list.get(0).getAttendId());
                attendDetailed.setAdNumber(1);
                attendDetailed.setAdEmp(employeeInfo.getEmployeeId());
                dao.insert(AttendDetailed.class,attendDetailed);
                attendDetailed.setAdNumber(2);
                attendDetailed.setAdAttend(list.get(1).getAttendId());
                if (list.get(1).getAttendAddress().equals(taskInfo.getTaskAddress())){
                    attendDetailed.setAdDifferentPlaces(0);
                }else{
                    attendDetailed.setAdDifferentPlaces(1);
                }
                dao.insert(AttendDetailed.class,attendDetailed);
            });
        }

        //找出早退以及迟到员工
        //查询只打卡一次的员工id
        List<Integer> attendEmployeeId = attendService.findAttendEmployee(taskId);
        if (attendEmployeeId.size() != 0){
            //查出只在第一次打卡的员工
            List<Integer> beginAttendEmployee = attendService.findStartAttendEmployee(attendEmployeeId, taskId);
            if (beginAttendEmployee!=null){
                List<EmployeeInfo> employeeList = dao.findList(EmployeeInfo.class, beginAttendEmployee.toArray());
                List<AttendDetailed> beginAttendDetailedList = new CopyOnWriteArrayList<>();
                employeeList.forEach(employeeInfo -> {
                    AttendDetailed attendDetailed = new AttendDetailed();
                    List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                            "empAttend", employeeInfo.getEmployeeId())).list();
                    if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                        attendDetailed.setAdDifferentPlaces(0);
                    }else{
                        attendDetailed.setAdDifferentPlaces(1);
                    }
                    attendDetailed.setAdEmp(employeeInfo.getEmployeeId());
                    attendDetailed.setAdTask(taskId);
                    attendDetailed.setAdNumber(1);
                    attendDetailed.setAdAttend(list.get(0).getAttendId());
                    attendDetailed.setAdStatus(1);
                    attendDetailed.setAdEarly(0);
                    attendDetailed.setAdLate(0);
                    attendDetailed.setAdTime(new Timestamp(TIME));
                    dao.insert(AttendDetailed.class,attendDetailed);
                    attendDetailed.setAdDifferentPlaces(0);
                    attendDetailed.setAdNumber(2);
                    attendDetailed.setAdAttend(0);
                    attendDetailed.setAdStatus(0);
                    attendDetailed.setAdEarly(1);
                    dao.insert(AttendDetailed.class, attendDetailed);
                });
            }
            //查出只在第二次打卡的员工
            List<Integer> endAttendEmployee = attendService.findEndAttendEmployee(attendEmployeeId, taskId);
            if (endAttendEmployee!=null){
                List<EmployeeInfo> employeeList = dao.findList(EmployeeInfo.class, endAttendEmployee.toArray());
                List<AttendDetailed> endAttendDetailedList = new CopyOnWriteArrayList<>();
                employeeList.forEach(employeeInfo -> {
                    AttendDetailed attendDetailed = new AttendDetailed();
                    List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                            "empAttend", employeeInfo.getEmployeeId())).list();
                    attendDetailed.setAdTask(taskId);
                    attendDetailed.setAdEmp(employeeInfo.getEmployeeId());
                    attendDetailed.setAdAttend(0);
                    attendDetailed.setAdNumber(1);
                    attendDetailed.setAdDifferentPlaces(0);
                    attendDetailed.setAdStatus(0);
                    attendDetailed.setAdLate(1);
                    attendDetailed.setAdEarly(0);
                    attendDetailed.setAdTime(new Timestamp(TIME));
                    dao.insert(AttendDetailed.class,attendDetailed);
                    if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                        attendDetailed.setAdDifferentPlaces(0);
                    }else{
                        attendDetailed.setAdDifferentPlaces(1);
                    }
                    attendDetailed.setAdNumber(2);
                    attendDetailed.setAdStatus(1);
                    attendDetailed.setAdLate(0);
                    attendDetailed.setAdAttend(list.get(0).getAttendId());
                    dao.insert(AttendDetailed.class,attendDetailed);
                });
            }
        }
        return true;

    }

    @Transactional
    @Override
    public boolean updateAttendDetailed(Integer taskId) {
        final long TIME = System.currentTimeMillis();
        TaskInfo taskInfo = dao.findOrNull(TaskInfo.class, taskId);
        if (taskInfo == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到指定的任务");
        }
        List<AttendDetailed> attendDetailedList4 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adTask", taskId)).list();
        if (Collections2.isEmpty(attendDetailedList4)) {
            return false;
        }
        //找出所有缺勤的员工（一次打卡都没有的）
        List<EmployeeInfo> noPunchCardEmployee = attendService.getNoPunchCardEmployee(taskId);
        if (noPunchCardEmployee.size() != 0){
            noPunchCardEmployee.forEach(employeeInfo -> {
                List<AttendDetailed> attendDetailedList1 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adEmp", employeeInfo.getEmployeeId(),
                        "adTask", taskId)).list();
                attendDetailedList1.get(0).setAdStatus(0);
                attendDetailedList1.get(0).setAdTime(new Timestamp(TIME));
                attendDetailedList1.get(1).setAdTime(new Timestamp(TIME));
                attendDetailedList1.get(1).setAdStatus(0);
                attendDetailedList1.get(0).setAdLate(0);
                attendDetailedList1.get(1).setAdLate(0);
                attendDetailedList1.get(0).setAdEarly(0);
                attendDetailedList1.get(1).setAdEarly(0);
                attendDetailedList1.get(0).setAdDifferentPlaces(0);
                attendDetailedList1.get(1).setAdDifferentPlaces(0);
                //批量更新所有缺勤员工信息
                dao.batchUpdate(AttendDetailed.class, attendDetailedList1);
            });
        }

        //找出所有勤快打卡的员工
        List<EmployeeInfo> punchCardEmployee = attendService.getPunchCardEmployee(taskId);
        if (punchCardEmployee.size() != 0){
            punchCardEmployee.forEach(employeeInfo -> {
                List<AttendDetailed> attendDetailedList3 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adEmp", employeeInfo.getEmployeeId(),
                        "adTask", taskId)).list();
                List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                        "empAttend", employeeInfo.getEmployeeId())).list();
                //比对地址
                if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                    attendDetailedList3.get(0).setAdDifferentPlaces(0);
                }else{
                    attendDetailedList3.get(0).setAdDifferentPlaces(1);
                }
                if (list.get(1).getAttendAddress().equals(taskInfo.getTaskAddress())){
                    attendDetailedList3.get(1).setAdDifferentPlaces(0);
                }else{
                    attendDetailedList3.get(1).setAdDifferentPlaces(1);
                }
                attendDetailedList3.get(0).setAdAttend(list.get(0).getAttendId());
                attendDetailedList3.get(1).setAdAttend(list.get(1).getAttendId());
                attendDetailedList3.get(0).setAdStatus(1);
                attendDetailedList3.get(1).setAdStatus(1);
                attendDetailedList3.get(1).setAdTime(new Timestamp(TIME));
                attendDetailedList3.get(0).setAdTime(new Timestamp(TIME));
                attendDetailedList3.get(0).setAdEarly(0);
                attendDetailedList3.get(1).setAdEarly(0);
                attendDetailedList3.get(0).setAdLate(0);
                attendDetailedList3.get(1).setAdLate(0);
                //批量更新所有勤快员工的详情
                dao.batchUpdate(AttendDetailed.class, attendDetailedList3);
            });
        }

        //找出早退以及迟到员工
        //查询只打卡一次的员工id
        List<Integer> attendEmployeeId = attendService.findAttendEmployee(taskId);
        if (attendEmployeeId.size() != 0){
            //查出只在第一次打卡的员工
            List<Integer> beginAttendEmployee = attendService.findStartAttendEmployee(attendEmployeeId, taskId);
            if (beginAttendEmployee!=null){
                List<EmployeeInfo> employeeList = dao.findList(EmployeeInfo.class, beginAttendEmployee.toArray());
                List<AttendDetailed> beginAttendDetailedList = new CopyOnWriteArrayList<>();
                employeeList.forEach(employeeInfo -> {
                    List<AttendDetailed> attendDetailedList1 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adEmp", employeeInfo.getEmployeeId(),
                            "adTask", taskId)).list();
                    List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                            "empAttend", employeeInfo.getEmployeeId())).list();
                    if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                        attendDetailedList1.get(0).setAdDifferentPlaces(0);
                    }else{
                        attendDetailedList1.get(0).setAdDifferentPlaces(1);
                    }
                    attendDetailedList1.get(0).setAdAttend(list.get(0).getAttendId());
                    attendDetailedList1.get(1).setAdDifferentPlaces(0);
                    attendDetailedList1.get(0).setAdStatus(1);
                    attendDetailedList1.get(1).setAdStatus(0);
                    attendDetailedList1.get(0).setAdEarly(0);
                    attendDetailedList1.get(1).setAdEarly(1);
                    attendDetailedList1.get(0).setAdLate(0);
                    attendDetailedList1.get(1).setAdLate(0);
                    attendDetailedList1.get(0).setAdTime(new Timestamp(TIME));
                    attendDetailedList1.get(1).setAdTime(new Timestamp(TIME));
                    dao.batchUpdate(AttendDetailed.class, attendDetailedList1);
                });
            }
            //查出只在第二次打卡的员工
            List<Integer> endAttendEmployee = attendService.findEndAttendEmployee(attendEmployeeId, taskId);
            if (endAttendEmployee!=null){
                List<EmployeeInfo> employeeList = dao.findList(EmployeeInfo.class, endAttendEmployee.toArray());
                List<AttendDetailed> endAttendDetailedList = new CopyOnWriteArrayList<>();
                employeeList.forEach(employeeInfo -> {
                    List<AttendDetailed> attendDetailedList2 = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adEmp", employeeInfo.getEmployeeId(),
                            "adTask", taskId)).list();
                    List<AttendInfo> list = dao.createCriteriaQuery(AttendInfo.class).where(New.hashMap("taskAttend", taskId,
                            "empAttend", employeeInfo.getEmployeeId())).list();
                    if (list.get(0).getAttendAddress().equals(taskInfo.getTaskAddress())){
                        attendDetailedList2.get(1).setAdDifferentPlaces(0);
                    }else{
                        attendDetailedList2.get(1).setAdDifferentPlaces(1);
                    }
                    attendDetailedList2.get(1).setAdAttend(list.get(0).getAttendId());
                    attendDetailedList2.get(0).setAdDifferentPlaces(0);
                    attendDetailedList2.get(1).setAdStatus(1);
                    attendDetailedList2.get(0).setAdStatus(0);
                    attendDetailedList2.get(1).setAdLate(0);
                    attendDetailedList2.get(0).setAdLate(1);
                    attendDetailedList2.get(0).setAdEarly(0);
                    attendDetailedList2.get(1).setAdEarly(0);
                    attendDetailedList2.get(1).setAdTime(new Timestamp(TIME));
                    attendDetailedList2.get(0).setAdTime(new Timestamp(TIME));
                    dao.batchUpdate(AttendDetailed.class, attendDetailedList2);
                });
            }
        }
        return true;
    }

    @Override
    public List<AttendDetailed> findAttendDetailedByTaskId(Integer taskId, Integer page, Integer pageSize) {
        List<AttendDetailed> list = null;
        if (page == 1){
            list = dao.createCriteriaQuery(AttendDetailed.class)
                    .where(New.hashMap("adTask",taskId))
                    .limit(page,pageSize).list();
        }else {
            list = dao.createCriteriaQuery(AttendDetailed.class)
                    .where(New.hashMap("adTask",taskId))
                    .limit((page - 1) * pageSize+1,page*pageSize).list();
        }
        return list;
    }

    @Override
    public List<AttendDetailed> findAttendDetailedByStatus(Integer status, Integer taskId, Integer page, Integer pageSize) {
        List<AttendDetailed> list = null;
        if (page == 1){
            list = dao.createCriteriaQuery(AttendDetailed.class)
                    .where(New.hashMap("adTask",taskId,"adStatus",status))
                    .limit(page,pageSize).list();
        }else {
            list = dao.createCriteriaQuery(AttendDetailed.class)
                    .where(New.hashMap("adTask",taskId,"adStatus",status))
                    .limit((page - 1) * pageSize+1,page*pageSize).list();
        }
        return list;
    }

    @Override
    public List<EmployeeInfo> findAttendDetatiledByLate(Integer late, Integer taskId, Integer page, Integer pageSize) {
        List<EmployeeInfo> list= null;
        List<AttendDetailed> attendDetailedList = dao.createCriteriaQuery(AttendDetailed.class)
                .where(New.hashMap("adTask", taskId, "adLate", late)).list();
        if (Collections2.isEmpty(attendDetailedList)){
            return null;
        }
        List<Integer> integerList = attendDetailedList.stream().map(AttendDetailed::getAdEmp).collect(Collectors.toList());
        if (page == 1){
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit(page, pageSize).list();
        }else {
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit((page - 1) * pageSize+1,page*pageSize).list();
        }
        return list;
    }

    @Override
    public List<EmployeeInfo> findAttendDetailedByEarly(Integer early, Integer taskId, Integer page, Integer pageSize) {
        List<EmployeeInfo> list= null;
        List<AttendDetailed> attendDetailedList = dao.createCriteriaQuery(AttendDetailed.class)
                .where(New.hashMap("adTask", taskId, "adEarly", early)).list();
       if (Collections2.isEmpty(attendDetailedList)){
           return null;
       }
        List<Integer> integerList = attendDetailedList.stream().map(AttendDetailed::getAdEmp).collect(Collectors.toList());
        if (page == 1){
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit(page, pageSize).list();
        }else {
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit((page - 1) * pageSize+1,page*pageSize).list();
        }
        return list;
    }

    @Override
    public List<EmployeeInfo> findAttendDetailByDifferentPlaces(Integer differentPlaces, Integer taskId, Integer page, Integer pageSize) {
        List<EmployeeInfo> list= null;
        List<AttendDetailed> attendDetailedList = dao.createCriteriaQuery(AttendDetailed.class)
                .where(New.hashMap("adTask", taskId, "adDifferentPlaces", differentPlaces)).list();
        if (Collections2.isEmpty(attendDetailedList)){
            return null;
        }
        List<Integer> integerList = attendDetailedList.stream().map(AttendDetailed::getAdEmp).collect(Collectors.toList());
        if (page == 1){
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit(page, pageSize).list();
        }else {
            list = dao.createSqlQuery(EmployeeInfo.class,
                    "select * from employee_info where employee_id in " + StringUtils.listToStringFromSQL(integerList))
                    .limit((page - 1) * pageSize+1,page*pageSize).list();
        }
        return list;
    }

    @Override
    public boolean deleteAttendDetailed(Integer taskId) {
        int delete = dao.createCriteriaQuery(AttendDetailed.class).where(New.hashMap("adTask", taskId)).delete();
        if (delete>0){
            return true;
        }
        return false;
    }

}

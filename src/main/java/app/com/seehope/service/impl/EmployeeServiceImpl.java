package app.com.seehope.service.impl;

import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.exception.MyException;
import app.com.seehope.service.EmployeeService;
import app.com.seehope.utils.ErrorConstant;
import app.com.seehope.utils.ExceptionUtils;
import app.com.seehope.utils.JwtTokenUtils;
import app.com.seehope.utils.LoggerUtils;
import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.log.LogUtils;
import jmms.core.Entities;
import jmms.core.modules.EntityModule;
import leap.lang.Collections2;
import leap.lang.New;
import leap.orm.dao.Dao;
import leap.web.exception.ResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private Dao dao;

    @Autowired
    private Entities entities;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public List<EmployeeInfo> findAllEmployee() {
        List<EmployeeInfo> employeeInfos = dao.findAll(EmployeeInfo.class);
        employeeInfos.forEach(System.out::println); ;
        return employeeInfos;
    }

    @Override
    public EmployeeInfo findEmployeeById(Integer id) {
        //dao.find(EmployeeInfo.class, New.hashMap("employeeId", id))如果查到为null，则直接报错
        //EmployeeInfo employeeInfo = dao.find(EmployeeInfo.class, New.hashMap("employeeId", id));
        EmployeeInfo employeeInfo = dao.findOrNull(EmployeeInfo.class, New.hashMap("employeeId", id));
        return employeeInfo;
    }

    @Override
    public boolean createEmployee(EmployeeInfo employeeInfo) {
        boolean flag = false;
        int insert = dao.insert(EmployeeInfo.class, employeeInfo);
        if(insert>0){
            flag=true;
        }
        return flag;
    }

    @Override
    public boolean updateEmployeeById(EmployeeInfo employeeInfo) {
        int update = dao.update(employeeInfo);
        //EntityModule entityModule = entities.require("EmployeeInfo");
        //entityModule.update("xxx","xxx") 更新指定字段的所属值
        //dao.batchUpdate(List<EmployeeInfo>) 代表批量更新
        if(update>0){
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEmployeeById(Integer id) {
        HashMap<String, Integer> objectHashMap = new HashMap<>();
        int delete = dao.delete(EmployeeInfo.class, New.hashMap("employeeId",id));
        if(delete>0){
            return true;
        }
        return false;
    }

    @Override
    public EmployeeInfo findEmployeeBuEmplpoyeeName(String employeeName) {
        EmployeeInfo employeeInfo = dao.createCriteriaQuery(EmployeeInfo.class)
                .where(New.hashMap("employeeeName", employeeName,
                        "employeeStatus",0)).first();
       if (employeeInfo == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到此员工的信息");
       }
        return employeeInfo;
    }

    @Override
    public EmployeeInfo findEmployeeBuEmplpoyeeUserName(String employeeUserName) {
        EmployeeInfo employeeInfo =  dao.createCriteriaQuery(EmployeeInfo.class)
                .where(New.hashMap("employeeUsername", employeeUserName,
                        "employeeStatus",0)).first();
        if (employeeInfo == null){
            ExceptionUtils.solveException(ErrorConstant.SERVICEUNAVAILABLE,
                    "未找到此员工的信息");
        }
        return employeeInfo;
    }

    @Override
    public String findEmployeeByUsernameAndPassword(EmployeeInfo employeeInfo) {
        List<EmployeeInfo> list = dao.createCriteriaQuery(EmployeeInfo.class)
                .where(New.hashMap("employeeUsername",employeeInfo.getEmployeeUsername(),
                        "employeePassword",employeeInfo.getEmployeePassword())).list();
        if (Collections2.isEmpty(list)){
            return null;
        }
        return JwtTokenUtils.builder().msg(JSON.toJSONString(list.get(0))).build().creatJwtToken();
    }

}

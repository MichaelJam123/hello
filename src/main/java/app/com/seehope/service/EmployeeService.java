package app.com.seehope.service;

import app.com.seehope.entities.EmployeeInfo;

import java.util.List;

public interface EmployeeService {

    public List<EmployeeInfo> findAllEmployee();

    public EmployeeInfo findEmployeeById(Integer id);

    public boolean createEmployee(EmployeeInfo employeeInfo);

    public boolean updateEmployeeById(EmployeeInfo employeeInfo);

    public boolean deleteEmployeeById(Integer id);

    public EmployeeInfo findEmployeeBuEmplpoyeeName(String employeeName);

    public EmployeeInfo findEmployeeBuEmplpoyeeUserName(String employeeUserName);

    public String findEmployeeByUsernameAndPassword(EmployeeInfo employeeInfo);
}

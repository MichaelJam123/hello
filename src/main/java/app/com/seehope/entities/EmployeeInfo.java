package app.com.seehope.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import leap.orm.annotation.Column;
import leap.orm.annotation.Id;
import leap.orm.annotation.Table;
import leap.web.api.annotation.ApiProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Table("employee_info")
@Api("员工表类")
public class EmployeeInfo implements Serializable {

    @Id
    @ApiProperty("员工表id")
    private Integer employeeId;
    @ApiProperty("员工表账号")
    @Column("employee_username")
    private String employeeUsername;
    @Column("employee_password")
    @ApiProperty("员工表密码")
    private String employeePassword;
    @Column("employee_name")
    @ApiProperty("员工姓名")
    private String employeeName;
    @Column("employee_sex")
    @ApiProperty("员工性别")
    private String employeeSex;
    @Column("employee_dept")
    @ApiProperty("员工部门")
    private String employeeDept;
    @Column("employee_email")
    private String employeeEmail;
    @Column("employee_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("员工入职时间")
    private Timestamp employeeTime;
    /**
     * 0为未冻结，1为已冻结
     */
    @Column("employee_status")
    @ApiProperty("员工状态，0为未冻结，1为已冻结")
    private String employeeStatus;
    @Column("employee_number")
    @ApiProperty("员工编号")
    private String employeeNumber;
    @Column("employee_position")
    @ApiProperty("员工职位")
    private String employeePosition;


}

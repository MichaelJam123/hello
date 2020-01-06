package app.com.seehope.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import leap.orm.annotation.Column;
import leap.orm.annotation.Id;
import leap.orm.annotation.Table;
import leap.web.api.annotation.ApiProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Table("attend_info")
@Api("出席表类")
public class AttendInfo implements Serializable {

    @ApiProperty("出席id")
    @Id
    private int attendId;
    @ApiProperty("出席时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @Column("attend_time")
    private Timestamp attendTime;
    @ApiProperty("出席地址")
    @Column("attend_address")
    private String attendAddress;
    @ApiProperty("出席的员工")
    @Column("emp_attend")
    private int empAttend;
    @ApiProperty("出席的任务")
    @Column("task_attend")
    private int taskAttend;


}

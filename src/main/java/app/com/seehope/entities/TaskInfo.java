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
@Table("task_info")
@Api("任务表类")
public class TaskInfo implements Serializable {

    @Id
    @ApiProperty("任务表id")
    private int taskId;
    @Column("task_name")
    @ApiProperty("任务名称")
    private String taskName;
    @Column("task_start_time")
    @ApiProperty("任务第一打卡时间开始时")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Timestamp taskStartTime;
    @Column("task_start_over")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("任务第一打卡时间结束时")
    private Timestamp taskStartOver;
    @Column("task_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("任务第二打卡时间开始时")
    private Timestamp taskEndTime;
    @Column("task_end_over")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("任务第二打卡时间结束时")
    private Timestamp taskEndOver;
    @Column("task_create")
    @ApiProperty("任务创建者，关联员工id")
    private Integer taskCreate;
    @Column("task_create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("任务创建时间")
    private Timestamp taskCreateTime;
    /**
     * 0 为任务未进行
     * 1 为任务开始
     * 2 为任务结束
     */
    @Column("task_status")
    @ApiProperty("任务状态:0为任务未进行  1 为任务正开始 2 为任务结束")
    private Integer taskStatus;
    @Column("task_type")
    @ApiProperty("任务类型")
    private String taskType;
    @Column("task_address")
    @ApiProperty("任务地址")
    private String taskAddress;


}

package app.com.seehope.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import leap.orm.annotation.Column;
import leap.orm.annotation.Id;
import leap.orm.annotation.Table;
import leap.web.api.annotation.ApiProperty;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Table("attend_detailed")
@Api("出席详细表")
public class AttendDetailed implements Serializable {

    @Id
    @ApiProperty("出席详细id")
    private int adId;
    @Column("ad_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    @ApiProperty("出席时间")
    private Timestamp adTime;
    @Column("ad_number")
    @ApiProperty("出席卡数: 1.第一次打卡 2.第二次打卡")
    private Integer adNumber;
    @ApiProperty("打卡状态: 0.未打卡 1.已打卡 ")
    @Column("ad_status")
    private int adStatus;
    @ApiProperty("地址状态: 0.非异地打卡 1.异地打卡")
    @Column("ad_differentPlaces")
    private int adDifferentPlaces;
    @ApiProperty("迟到状态: 0.非迟到 1.迟到")
    @Column("ad_late")
    private int adLate;
    @ApiProperty("早退状态: 0.非早退 1.早退")
    @Column("ad_early")
    private int adEarly;
    @Column("ad_attend")
    @ApiProperty("绑定出席表id")
    private int adAttend;
    @Column("ad_emp")
    @ApiProperty("绑定员工表id")
    private int adEmp;
    @Column("ad_task")
    @ApiProperty("绑定任务表id")
    private int adTask;

}

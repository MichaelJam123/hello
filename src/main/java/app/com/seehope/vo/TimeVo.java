package app.com.seehope.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

/**
 * @Author: 龍右
 * @Date: 2019/12/20 21:54
 * @Description:
 */
@Data
public class TimeVo {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Timestamp beginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",  timezone="GMT+8")
    private Timestamp endTime;
}

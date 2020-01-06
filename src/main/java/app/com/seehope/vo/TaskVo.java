package app.com.seehope.vo;

import app.com.seehope.entities.TaskInfo;
import lombok.Data;

/**
 * @Author: 龍右
 * @Date: 2019/12/10 11:15
 * @Description:
 */
@Data
public class TaskVo {
    private TaskInfo taskInfo;
    private String employeeName;
}

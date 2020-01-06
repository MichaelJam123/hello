package app.com.seehope.vo;

import app.com.seehope.entities.AttendInfo;
import lombok.Data;

/***
 * 从前端实际接收的attend对象（也是实际返回给用户的视图对象）
 */
@Data
public class AttendVo {

    private String employeeName;

    private String taskName;

    private String address;

    private AttendInfo attendInfo;

}

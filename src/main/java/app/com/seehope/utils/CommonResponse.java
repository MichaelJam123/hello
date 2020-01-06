package app.com.seehope.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: 龍右
 * @Date: 2019/12/17 10:53
 * @Description: 返回页面响应封装类
 */
@Data
public class CommonResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * 表示当前相应的状态是成功或者失败
     */
    private Boolean status;
    /**
     * 表示当响应失败之后给前端的错误提示
     */
    private String msg;
    /**
     * 表示当响应成功之后返回给前端的数据
     */
    private Object data;

    /**
     * 成功时返回数据
     * @param data
     * @return
     */
    public static CommonResponse isOk(Object data) {
        return new CommonResponse(true, "请求成功", data);
    }

    /**
     * 失败时返回数据
     * @param msg
     * @return
     */
    public static CommonResponse error(String msg) {
        return new CommonResponse(false, msg, null);
    }

    /**
     *
     */
    public CommonResponse() {
        // TODO Auto-generated constructor stub
    }

    /**
     * @param status
     * @param msg
     * @param data
     */
    public CommonResponse(Boolean status, String msg, Object data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

}


package app.com.seehope.utils;

import leap.web.exception.ResponseException;

import java.io.Serializable;

/**
 * 异常油条配置类
 */
public class ExceptionUtils{

    /**
     * 自定义异常信息
     * @param status 状态码
     * @param content 内容
     * @return
     */
    public static Object solveException(Integer status,String content){
        throw new ResponseException(status,content);
    }

    /**
     * 解决400参数错误返回信息
     * @return
     */
    public static Object solve400Exception(){
        throw new ResponseException(ErrorConstant.BADREQUEST,"请求参数错误");
    }

    /**
     * 解决503调用方法即服务未实现返回信息
     * @return
     */
    public static Object solve503Exception(){
        throw new ResponseException(ErrorConstant.SERVICEUNAVAILABLE,"请求服务未实现");
    }

}

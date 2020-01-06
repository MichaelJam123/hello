package app.com.seehope.utils;

/**
 * 错误信息常量类
 */
public class ErrorConstant {

    /**
     * 请求方输入的参数或者格式不正确
     */
    public static final Integer BADREQUEST=400;

    /**
     * 请求方未经过身份认证
     */
    public static final Integer UNAUTHORIZED=401;

    /**
     * 请求方没有权限访问
     */
    public static final Integer FORBIDDEN=403;

    /**
     * 访问的接口或数据不存在
     */
    public static final Integer NOTFOUND=404;

    /**
     * 服务端产生非预期的错误，具体需要看错误代码和描述
     */
    public static final Integer INTERNALSERVERERROR=500;

    /**
     * 请求的接口未实现
     */
    public static final Integer NOTIMPLEMENTED=501;

    /**
     * 服务端无法处理请求
     */
    public static final Integer SERVICEUNAVAILABLE=503;
}

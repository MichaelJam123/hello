package app.com.seehope.utils;

import lombok.Getter;

import java.math.BigDecimal;

/**
 * @Author: 龍右
 * @Date: 2019/12/10 13:19
 * @Description: 错误信息枚举类
 */
public enum ErrorEnum {

    BADREQUEST(400,"请求方输入的参数或者格式不正确"),UNAUTHORIZED(401,"请求方未经过身份认证"),
    FORBIDDEN(403,"请求方没有权限访问"),NOTFOUND(404,"访问的接口或数据不存在"),
    INTERNALSERVERERROR(500,"服务端产生非预期的错误，具体需要看错误代码和描述"),
    NOTIMPLEMENTED(501,"请求的接口未实现"),SERVICEUNAVAILABLE(503,"服务端无法处理请求");

    @Getter private Long errorCode;

    @Getter private String errorMessage;

    ErrorEnum(long errorCode,String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * 判断输入的错误码与之对应的枚举
     * @param errorCode
     * @return
     */
    public static ErrorEnum judgeErrorInfo(long errorCode){
        ErrorEnum[] errorEnums = ErrorEnum.values();
        for (ErrorEnum errorEnum:errorEnums) {
            if (errorCode == errorEnum.getErrorCode()){
                return errorEnum;
            }
        }
        return null;
    }


}

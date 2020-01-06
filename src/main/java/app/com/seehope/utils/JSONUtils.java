package app.com.seehope.utils;


import com.alibaba.fastjson.JSONObject;

/**
 * JSON油条配置类
 */
public class JSONUtils {

    /**
     * 适合一个字符串和错误码的形式转换
     * @param msg
     * @param conent
     * @return
     */
    public static String putJson(Integer errCode,String msg, String conent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("errCode",errCode);
        jsonObject.put(msg,conent);
        return jsonObject.toString();
    }

    /**
     * 适合一个字符串的形式转换
     * @param msg
     * @param conent
     * @return
     */
    public static String putJson(String msg, String conent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(msg,conent);
        return jsonObject.toString();
    }

    /**
     * 适合将对象转换为json
     * @param msg
     * @param conent
     * @return
     */
    public static String putBeanJson(String msg, Object conent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(msg,conent);
        return jsonObject.toString();
    }


}

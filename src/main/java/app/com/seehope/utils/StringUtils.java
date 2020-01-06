package app.com.seehope.utils;

import org.springframework.web.context.request.ServletWebRequest;

import java.util.Iterator;
import java.util.List;

/**
 * @Author: 龍右
 * @Date: 2019/12/20 15:51
 * @Description: String油条配置类
 */
public class StringUtils {

    /**
     * 根据(1,2,3,...)来组合String
     * @param list
     * @return
     */
    public static String listToStringFromSQL(List<Integer> list){
        String m= "(";
        Iterator<Integer> it=list.iterator();
        while (it.hasNext()){
            m = m +it.next()+",";
        }
        int indexb = m.lastIndexOf(","); //去掉最后一个','号
        return m.substring(0, m.length() - 1) + ")";
    }

}

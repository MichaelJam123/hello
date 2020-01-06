package app.com.seehope.exception;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CustomerExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        String result = "系统发生异常了，请联系管理员！";
        //自定义异常处理
        if(e instanceof MyException){
            result = ((MyException)e).getMsg();
        }
        ModelAndView mav = new ModelAndView();
        mav.addObject("msg", result);
        //设置跳转到msg.html
        mav.setViewName("msg");
        return mav;
    }
}

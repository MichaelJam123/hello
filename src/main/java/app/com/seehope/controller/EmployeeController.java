package app.com.seehope.controller;

import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.service.EmployeeService;
import app.com.seehope.utils.*;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.*;
import leap.web.api.annotation.ApiProperty;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

@Api(description = "员工控制类")
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @ApiProperty("这是一个EmployeeService的员工服务类成员变量")
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    RedisTemplate<Object,Object> redisTemplate;

    @ApiOperation(value = "这是获取所有员工的方法",httpMethod = "GET",response = List.class)
    @GetMapping("/getAllEmployee")
    public List<EmployeeInfo> getAllEmployee() {
        return employeeService.findAllEmployee();
    }

    @ApiOperation(value = "根据指定id获取员工的方法",httpMethod = "GET",response = EmployeeInfo.class)
    @GetMapping("/getEmployeeById/{id}")
    public String getEmployeeById(@ApiParam(name = "id",value = "指定员工id")
       @PathVariable("id") Integer id) {
        if(id == null){
            ExceptionUtils.solve400Exception();
        }
        EmployeeInfo employeeById = employeeService.findEmployeeById(id);
        if(employeeById != null){
            return JSONUtils.putBeanJson("employeeById",employeeById);
        }else{
            return JSONUtils.putJson(ErrorConstant.SERVICEUNAVAILABLE,"msg","未找到对应员工信息");
        }
    }

    @ApiOperation(value = "员工登录",httpMethod = "POST",response = CommonResponse.class)
    @PostMapping(value = "/findEmployee")
    public CommonResponse findEmployee(@RequestBody EmployeeInfo employeeInfo,HttpServletRequest request){
        if(employeeInfo.getEmployeeUsername() == null || employeeInfo.getEmployeePassword() == null){
            ExceptionUtils.solve400Exception();
        }
        HttpSession session = request.getSession(true);
        //EmployeeInfo employeeInfo1 = employeeService.findEmployeeBuEmplpoyeeUserName(employeeInfo.getEmployeeUsername());
        if (!StringUtils.isEmpty((String)redisTemplate.opsForValue().get("access_token:emplpoyee-"+session.getId())) ){
            ExceptionUtils.solveException(ErrorConstant.FORBIDDEN,"检测到您已登录，若要切换账号，请退出登录");
        }
        String token = employeeService.findEmployeeByUsernameAndPassword(employeeInfo);
        redisTemplate.opsForValue().set("access_token:emplpoyee-"+session.getId(),token);
        LoggerUtils.LOGGER.info(token );
        LoggerUtils.LOGGER.info(session.getId());
        return CommonResponse.isOk(token);
    }

    @ApiOperation("查看当前登录的员工")
    @GetMapping("/findLogEmployee")
    public CommonResponse findLogEmployee(HttpServletRequest request){
        HttpSession session = request.getSession();
        String access_token = (String) redisTemplate.opsForValue().get("access_token:emplpoyee-"+session.getId());
        if (StringUtils.isEmpty(access_token)){
            ExceptionUtils.solveException(ErrorConstant.INTERNALSERVERERROR,"您还未登录");
        }
        String freeJwt = JwtTokenUtils.builder().build().freeJwt(access_token);
        EmployeeInfo employeeInfo = JSONObject.parseObject(freeJwt, EmployeeInfo.class);
        return CommonResponse.isOk(employeeInfo);
    }

    @ApiOperation(value = "员工退出")
    @DeleteMapping("/logout/{id}")
    public CommonResponse logout(@PathVariable("id") Integer id,HttpServletRequest request){
        HttpSession session = request.getSession();
        String access_token = (String) redisTemplate.opsForValue().get("access_token:emplpoyee-"+session.getId());
        LoggerUtils.LOGGER.info(session.getId());

        String freeJwt = JwtTokenUtils.builder().build().freeJwt(access_token);
        LoggerUtils.LOGGER.info(freeJwt);
        EmployeeInfo employeeInfo = JSONObject.parseObject(freeJwt, EmployeeInfo.class);
        if (!employeeInfo.getEmployeeId().equals(id)){
            ExceptionUtils.solveException(ErrorConstant.NOTFOUND,"指定的账号id与存储账号id不匹配");
        }
        redisTemplate.delete("access_token:emplpoyee-"+session.getId());
        return CommonResponse.isOk("退出成功");
    }

    @ApiOperation("创建员工的方法")
    @PostMapping("/createEmployee")
    public String createEmployee(@RequestBody EmployeeInfo employeeInfo){
        if(employeeInfo == null){
            ExceptionUtils.solve400Exception();
        }
        if(employeeService.createEmployee(employeeInfo)){
            return JSONUtils.putJson("msg","创建成功");
        }else{
            return JSONUtils.putJson(ErrorConstant.SERVICEUNAVAILABLE,"msg","创建失败");
        }
    }

    @ApiOperation("修改员工的方法")
    @PatchMapping("/updateEmployee/{id}")
    public String updateEmployeeById(@PathVariable("id") Integer id,
          @RequestBody EmployeeInfo employeeInfo){
        if(employeeInfo == null){
            ExceptionUtils.solve400Exception();
        }
        employeeInfo.setEmployeeId(id);
        if(employeeService.updateEmployeeById(employeeInfo)){
            return JSONUtils.putJson("msg","更新成功");
        }else{
            return JSONUtils.putJson(ErrorConstant.SERVICEUNAVAILABLE,"msg","更新失败");
        }
    }

    @ApiOperation("删除员工方法")
    @DeleteMapping("/deleteEmployee/{id}")
    public String deleteEmployeeById(@PathVariable("id") Integer id){
        if(id == null){
            ExceptionUtils.solve400Exception();
        }
        if(employeeService.deleteEmployeeById(id)){
            return JSONUtils.putJson("msg","删除成功");
        }else{
            return JSONUtils.putJson(ErrorConstant.SERVICEUNAVAILABLE,"msg","删除失败");
        }
    }
}

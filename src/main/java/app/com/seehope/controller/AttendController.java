package app.com.seehope.controller;

import app.com.seehope.entities.AttendInfo;
import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.service.AttendService;
import app.com.seehope.utils.*;
import app.com.seehope.vo.AttendVo;
import app.com.seehope.vo.ListVo;
import app.com.seehope.vo.TimeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@Api(description = "出席控制类")
@RestController
@RequestMapping("/attend")
public class AttendController {

    @Autowired
    private AttendService attendService;

    @ApiOperation("创建打卡记录")
    @PostMapping("/createAttend")
    public String createAttend(@ApiParam(name = "attendVo",value = "这个是出席表类") @RequestBody AttendVo attendVo){
        if(attendVo == null){
            ExceptionUtils.solve400Exception();
        }
        boolean flag = attendService.createAttendInfo(attendVo);
        if(flag){
            return JSONUtils.putJson("msg","创建成功");
        }
        return JSONUtils.putJson(ErrorConstant.SERVICEUNAVAILABLE,"msg","创建失败");
    }

    @ApiOperation("根据员工编号查出出席记录")
    @GetMapping("/getAttendByEmployeeNumber/{page}/{pageSize}/{employeeNumber}")
    public CommonResponse getAttendByEmployeeNumber(@PathVariable("page") Integer page, @PathVariable("pageSize") Integer pageSize,
                                                    @PathVariable("employeeNumber") Integer employeeNumber){
        if (page == 0 || pageSize == 0 || employeeNumber == 0
        || page == null || pageSize == null || employeeNumber == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,"传入参数信息错误,请重新传入");
        }
        List<AttendVo> attendByEmployeeNumber = attendService.findAttendByEmployeeNumber(page, pageSize, employeeNumber);
        if (attendByEmployeeNumber == null){
            return CommonResponse.error("未查出该员工编号的出席记录");
        }
        return CommonResponse.isOk(attendByEmployeeNumber);

    }

    @ApiOperation("根据任务名称查询出席记录")
    @GetMapping("/getAttendByTaskName/{page}/{pageSize}/{taskName}")
    public CommonResponse getAttendByTaskName(@PathVariable("taskName") String taskName, @PathVariable("page") Integer page,
                                              @PathVariable("pageSize") Integer pageSize){
        if (page == 0 || pageSize == 0
                || page == null || pageSize == null || taskName == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,"传入参数信息错误,请重新传入");
        }
        System.out.println(taskName);
        List<AttendVo> attendByTaskName = attendService.findAttendByTaskName(taskName, page, pageSize);
        if (attendByTaskName == null){
            return CommonResponse.error("未查出该任务名称下的出席记录");
        }
        return CommonResponse.isOk(attendByTaskName);
    }

    @ApiOperation("获取任务指定时间内的出席记录")
    @PostMapping("/getAttendByTime/{id}")
    public CommonResponse getAttendByTime(@PathVariable("id") Integer id,@RequestBody TimeVo timeVo){
        LoggerUtils.LOGGER.info(""+id+"  "+timeVo.getBeginTime()+"  "+timeVo.getEndTime());
        if (id == null || timeVo.getBeginTime() == null || timeVo.getEndTime() == null){
            ExceptionUtils.solve400Exception();
        }
        List<EmployeeInfo> attendByTime = attendService.getAttendByTime(id, timeVo.getBeginTime(), timeVo.getEndTime());
        if (attendByTime == null){
            return CommonResponse.error("未找到对应的出席记录");
        }
        return CommonResponse.isOk(attendByTime);
    }

    @ApiOperation("查询勤快打卡的员工出席记录")
    @PostMapping("/getNormalPunchCardAttend/{taskId}")
    public CommonResponse getNormalPunchCardAttend(@RequestBody ListVo list, @PathVariable("taskId") Integer taskId){
        list.getList().forEach(System.out::println);
        if (list.getList() == null || taskId == null){
            ExceptionUtils.solve400Exception();
        }
        List<AttendInfo> normalPunchCardAttend = attendService.findNormalPunchCardAttend(list.getList(), taskId);
        if (normalPunchCardAttend == null){
            return CommonResponse.error("未找到对应的出席记录");
        }
        return CommonResponse.isOk(normalPunchCardAttend);
    }
}

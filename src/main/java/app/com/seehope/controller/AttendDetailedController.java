package app.com.seehope.controller;

import app.com.seehope.entities.AttendDetailed;
import app.com.seehope.entities.EmployeeInfo;
import app.com.seehope.service.AttendDetailedService;
import app.com.seehope.utils.CommonResponse;
import app.com.seehope.utils.ErrorConstant;
import app.com.seehope.utils.ExceptionUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: 龍右
 * @Date: 2019/12/14 15:40
 * @Description:
 */
@RestController
@Api(description = "任务详情类")
@RequestMapping("/attendDetailed")
public class AttendDetailedController {

    @Autowired
    private AttendDetailedService attendDetailedService;

    @ApiOperation("创建指定任务的所有员工的出席详情记录")
    @PostMapping("/createAttendDetailed/{taskId}")
    public CommonResponse createAttendDetailed(@PathVariable("taskId") Integer taskId){
        if (taskId == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "指定任务为空");
        }
        boolean attendDetailed = attendDetailedService.createAttendDetailedById(taskId);
        if (attendDetailed){
            return CommonResponse.isOk("创建成功");
        }
        return CommonResponse.error("已有出席记录数据或创建失败");
    }

    @ApiOperation("更新指定任务的出席详情表")
    @PatchMapping("/updateAttendDetailed/{id}")
    public CommonResponse updateAttendDetailed(@PathVariable("id") Integer taskId){
        if (taskId == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "指定任务为空");
        }
        boolean b = attendDetailedService.updateAttendDetailed(taskId);
        if (b){
            return CommonResponse.isOk("更新成功");
        }
        return CommonResponse.error("请先创建任务详情记录或更新失败");
    }

    @ApiOperation("根据任务id查询签到详情记录")
    @GetMapping("/getAttendDetailedById/{page}/{pageSize}/{taskId}")
    public CommonResponse getAttendDetailedById(@PathVariable("taskId") Integer taskId,
                                                @PathVariable("page") Integer page,
                                                @PathVariable("pageSize") Integer pageSize){
        if (taskId == null || taskId == 0 ||
                page == null || page == 0 ||
                pageSize == null || pageSize == 0){
            ExceptionUtils.solve400Exception();
        }
        List<AttendDetailed> attendDetailedByTaskId = attendDetailedService.findAttendDetailedByTaskId(taskId, page, pageSize);
        if (attendDetailedByTaskId != null){
            return CommonResponse.isOk(attendDetailedByTaskId);
        }
        return CommonResponse.error("未查询出签到详情记录");
    }

    @ApiOperation("通过打卡状态获取出席详情记录")
    @GetMapping("/getAttendDetailedByStatus/{page}/{pageSize}/{taskId}/{status}")
    public CommonResponse getAttendDetailedByStatus(@PathVariable("status") Integer status,
                                                    @PathVariable("taskId") Integer taskId,
                                                    @PathVariable("page") Integer page,
                                                    @PathVariable("pageSize") Integer pageSize){
        if (status == null ||
                taskId == null || taskId == 0 ||
                page == null || page == 0 ||
                pageSize == null || pageSize == 0){
            ExceptionUtils.solve400Exception();
        }
        List<AttendDetailed> attendDetailedByTaskId = attendDetailedService.findAttendDetailedByStatus(status,taskId,page,pageSize);
        if (attendDetailedByTaskId != null){
            return CommonResponse.isOk(attendDetailedByTaskId);
        }
        return CommonResponse.error("未查询出签到详情记录");
    }

    @ApiOperation("通过早退状态获取出席详情记录")
    @GetMapping("/getAttendDetailedByEarly/{page}/{pageSize}/{taskId}/{early}")
    public CommonResponse getAttendDetailedByEarly(@PathVariable("early") Integer early,
                                                   @PathVariable("taskId") Integer taskId,
                                                   @PathVariable("page") Integer page,
                                                   @PathVariable("pageSize") Integer pageSize){
        if (early == null ||
                taskId == null || taskId == 0 ||
                page == null || page == 0 ||
                pageSize == null || pageSize == 0){
            ExceptionUtils.solve400Exception();
        }
        List<EmployeeInfo> employeeInfoList = attendDetailedService.findAttendDetailedByEarly(early,taskId,page,pageSize);
        if (employeeInfoList != null){
            return CommonResponse.isOk(employeeInfoList);
        }
        return CommonResponse.error("未查询出签到详情记录");

    }

    @ApiOperation("通过早退状态获取出席详情记录")
    @GetMapping("/getAttendDetailedByLate/{page}/{pageSize}/{taskId}/{late}")
    public CommonResponse getAttendDetailedByLate(@PathVariable("late") Integer late,
                                                   @PathVariable("taskId") Integer taskId,
                                                   @PathVariable("page") Integer page,
                                                   @PathVariable("pageSize") Integer pageSize){
        if (late == null ||
                taskId == null || taskId == 0 ||
                page == null || page == 0 ||
                pageSize == null || pageSize == 0){
            ExceptionUtils.solve400Exception();
        }
        List<EmployeeInfo> employeeInfoList = attendDetailedService.findAttendDetatiledByLate(late,taskId,page,pageSize);
        if (employeeInfoList != null){
            return CommonResponse.isOk(employeeInfoList);
        }
        return CommonResponse.error("未查询出签到详情记录");
    }

    @ApiOperation("删除指定的任务出席的详情记录")
    @DeleteMapping("/deleteAttendDetatiled/{taskId}")
    public CommonResponse deleteAttendDetatiled(@PathVariable("taskId") Integer taskId){
        if (taskId == null || taskId == 0){
            ExceptionUtils.solve400Exception();
        }
        boolean flag = attendDetailedService.deleteAttendDetailed(taskId);
        if (flag){
            return CommonResponse.isOk("删除成功");
        }
        return CommonResponse.error("删除失败");
    }

    @ApiOperation("根据打卡异地状态获取出席详情记录")
    @GetMapping("/getAttendDetailByDifferentPlaces/{page}/{pageSize}/{taskId}/{differentPlaces}")
    public CommonResponse getAttendDetailByDifferentPlaces(@PathVariable("differentPlaces") Integer differentPlaces,
                                                           @PathVariable("taskId") Integer taskId,
                                                           @PathVariable("page") Integer page,
                                                           @PathVariable("pageSize") Integer pageSize){
        if (differentPlaces == null ||
                taskId == null || taskId == 0 ||
                page == null || page == 0 ||
                pageSize == null || pageSize == 0){
            ExceptionUtils.solve400Exception();
        }
        List<EmployeeInfo> attendDetailByDifferentPlaces = attendDetailedService.findAttendDetailByDifferentPlaces(differentPlaces, taskId, page, pageSize);
        if (attendDetailByDifferentPlaces != null){
            return CommonResponse.isOk(attendDetailByDifferentPlaces);
        }
        return CommonResponse.error("未查询出签到详情记录");
    }


}

package app.com.seehope.controller;

import app.com.seehope.entities.TaskInfo;
import app.com.seehope.service.TaskService;
import app.com.seehope.utils.CommonResponse;
import app.com.seehope.utils.ErrorConstant;
import app.com.seehope.utils.ExceptionUtils;
import app.com.seehope.utils.LoggerUtils;
import app.com.seehope.vo.PageVo;
import app.com.seehope.vo.TaskVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(description = "任务控制类")
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @ApiOperation("创建任务方法")
    @PostMapping("/createTask")
    public CommonResponse createTask(@RequestBody TaskVo taskVo){
        LoggerUtils.LOGGER.info(taskVo+"");
        if (taskVo == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "传入参数非法或为空");
        }
        boolean flag = taskService.addTask(taskVo.getEmployeeName(), taskVo.getTaskInfo());
        if (flag){
            return CommonResponse.isOk("创建任务成功");
        }
        return CommonResponse.error("创建任务失败");
    }

    @ApiOperation("更新任务状态")
    @PatchMapping("/updateTaskStatus")
    public CommonResponse updateTaskStatus(){
        boolean flag = taskService.updateTaskStatus();
        if (flag){
            return CommonResponse.isOk("更新任务状态成功");
        }
        return CommonResponse.error("更新任务状态失败");
    }

    @ApiOperation("更新指定任务")
    @PatchMapping("/updateTask/{id}")
    public CommonResponse updateTask(@PathVariable("id") Integer id,@RequestBody TaskVo taskVo){
        if (taskVo == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "传入参数非法或为空");
        }
        taskVo.getTaskInfo().setTaskId(id);
        boolean flag = taskService.updateTask(taskVo.getEmployeeName(), taskVo.getTaskInfo());
        if (flag){
            return CommonResponse.isOk("更新任务成功");
        }
        return CommonResponse.error("更新任务失败");
    }

    @ApiOperation(value = "根据创建人获取指定任务",httpMethod = "GET",response = CommonResponse.class)
    @GetMapping("/getAllTaskByEmployee/{page}/{pageSize}/{employeeName}")
    public CommonResponse getAllTaskByEmployee(@ApiParam(name = "employeeName",value = "创建人姓名") @PathVariable("employeeName") String employeeName,
                                               @ApiParam(name = "page",value = "页码") @PathVariable("page") Integer page,
                                               @ApiParam(name = "pageSize",value = "当前页列数") @PathVariable("pageSize") Integer pageSize){
        LoggerUtils.LOGGER.info(employeeName);
        if (page == 0 || pageSize == 0|| employeeName == null || page == null || pageSize == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "传入参数非法或为空");
        }
        PageVo pageVo = new PageVo();
        pageVo.setPage(page);
        pageVo.setPageSize(pageSize);
        List<TaskInfo> allTaskByEmployee = taskService.getAllTaskByEmployee(employeeName, pageVo);
        if (allTaskByEmployee.size() == 0){
            return CommonResponse.isOk("没有数据");
        }
        return CommonResponse.isOk(allTaskByEmployee);
    }

    @ApiOperation(value = "根据指定状态获取指定任务",httpMethod = "GET",response = CommonResponse.class)
    @GetMapping("/getAllTaskByStatus/{page}/{pageSize}/{status}")
    public CommonResponse getAllTaskByStatus(@ApiParam(name = "status",value = "指定状态") @PathVariable("status") Integer status,
                                             @ApiParam(name = "page",value = "页码") @PathVariable("page") Integer page,
                                             @ApiParam(name = "pageSize",value = "当前页列数") @PathVariable("pageSize") Integer pageSize){
        LoggerUtils.LOGGER.info("getAllTaskByStatus："+status);
        if (page == 0 || pageSize == 0||status == null || page == null || pageSize == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "传入参数非法或为空");
        }
        PageVo pageVo = new PageVo();
        pageVo.setPage(page);
        pageVo.setPageSize(pageSize);
        List<TaskInfo> allTaskByEmployee = taskService.getAllTaskByStatus(status,pageVo);
        if (allTaskByEmployee.size() == 0){
            return CommonResponse.isOk("没有数据");
        }
        return CommonResponse.isOk(allTaskByEmployee);
    }

    @ApiOperation(value = "获取所有任务列表",httpMethod = "GET",response = CommonResponse.class)
    @GetMapping("/getTaskList/{page}/{pageSize}")
    public CommonResponse getTaskList(@ApiParam(name = "page",value = "页码") @PathVariable("page") Integer page,
                                      @ApiParam(name = "pageSize",value = "当前页列数") @PathVariable("pageSize") Integer pageSize){
        if (page == 0 || pageSize == 0|| page == null || pageSize == null){
            ExceptionUtils.solveException(ErrorConstant.BADREQUEST,
                    "传入参数非法或为空");
        }
        PageVo pageVo = new PageVo();
        pageVo.setPage(page);
        pageVo.setPageSize(pageSize);
        List<TaskInfo> taskList = taskService.getTaskList(pageVo);
        if (taskList.size() == 0){
            return CommonResponse.isOk("没有数据");
        }
        return CommonResponse.isOk(taskList);
    }
}

package app.com.seehope.utils;

import app.com.seehope.entities.TaskInfo;
import org.springframework.beans.BeanUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 测试类
 */
public class AllTest {

    public static void main(String[] args) {
        /*TaskInfo taskInfo2 = new TaskInfo();
        taskInfo2.setTaskStartTime(Timestamp.valueOf("2019-12-20 10:30:30"));
        taskInfo2.setTaskStartOver(Timestamp.valueOf("2019-12-25 10:30:30"));
        taskInfo2.setTaskEndTime(Timestamp.valueOf("2019-12-26 10:30:30"));
        taskInfo2.setTaskEndOver(Timestamp.valueOf("2019-12-29 10:30:30"));
        System.out.println(TimeUtils.judgeEarlyOrLate(Timestamp.valueOf("2019-12-27 12:30:30"),taskInfo2));
        System.out.println(taskInfo2.getTaskStartTime());*/
        System.out.println(ErrorEnum.judgeErrorInfo(400).getErrorMessage());
    }

    public static void TestListCompareTo() {
        List<TaskInfo> taskListZero = new ArrayList<>();
        TaskInfo taskInfo = new TaskInfo();
        TaskInfo taskInfo1 = new TaskInfo();
        TaskInfo taskInfo2 = new TaskInfo();
        taskInfo.setTaskStartTime(Timestamp.valueOf("2019-12-18 10:30:30"));
        taskInfo1.setTaskStartTime(Timestamp.valueOf("2019-12-29 10:30:30"));
        taskInfo2.setTaskStartTime(Timestamp.valueOf("2019-12-25 10:30:30"));
        taskListZero.add(taskInfo);
        taskListZero.add(taskInfo1);
        taskListZero.add(taskInfo2);
        List<TaskInfo> taskInfoList = taskListZero.stream().filter(task -> task.getTaskStartTime()
                .compareTo(new Timestamp(System.currentTimeMillis())) > 0).collect(Collectors.toList());
        taskInfoList.forEach(System.out::println);
    }

    public static void TestListCopy() {
        List<String> list1= new ArrayList<>();
        list1.add("good");
        List<String> list2 = new ArrayList<>();
        list1.forEach(task->{
            list2.add(task);
        });
        list2.forEach(System.out::println);
    }
}

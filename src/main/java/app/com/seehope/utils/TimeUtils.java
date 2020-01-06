package app.com.seehope.utils;

import app.com.seehope.entities.TaskInfo;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: 龍右
 * @Date: 2019/12/10 15:56
 * @Description: 时间处理类
 */
public class TimeUtils {

    /**
     * 比较两个时间
     * @param t1
     * @param t2
     * @return true 说明t1时间在t2时间前
     *         false 说明t2时间在t1时间前
     */
    public static boolean compareTwoTime(Timestamp t1,Timestamp t2){
        return t1.before(t2);
    }

    /**
     * 判断某个时间是否在[startTime, endTime]区间
     *
     * @param nowTime 当前时间
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true 代表是在区间内
     *         false 代表不在区间内
     */
    public static boolean isEffectiveDate(Timestamp nowTime, Timestamp startTime, Timestamp endTime) {

        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断指定时间是否有效或判断第几次打卡
     * @param nowTime
     * @param taskInfo
     * @return 1 为第一次打卡
     *         2 为第二次打卡
     */
    public static Integer judgeOneOrTwo(Timestamp nowTime, TaskInfo taskInfo){
        if (TimeUtils.isEffectiveDate(nowTime,
                taskInfo.getTaskStartTime(),taskInfo.getTaskEndTime())){
            //说明在第一个时间段内打卡
            return 1;
        }
        if (TimeUtils.isEffectiveDate(nowTime,
                taskInfo.getTaskEndTime(),taskInfo.getTaskEndOver())){
            //说明在第二个时间段内打卡
            return 2;
        }
        return null;
    }

    /**
     * 判断指定时间是迟到还是早退
     * @param nowTime
     * @param taskInfo
     * @return 1 迟到
     *         2 早退
     *         3 正常签到
     *         4 不在打卡时间
     */
    public static Integer judgeEarlyOrLate(Timestamp nowTime, TaskInfo taskInfo){
        Integer i = null ;
        //判断任务时间是否在指定区间内打卡
        if (!TimeUtils.compareTwoTime(nowTime,taskInfo.getTaskStartOver())){
            //说明当前打卡时间比打卡时间晚（迟到）
            i = 1;
        }
        if (!TimeUtils.compareTwoTime(nowTime,taskInfo.getTaskEndTime())){
            //说明当前打卡时间比打卡时间晚（早退）
            i = 2;
        }
        if (TimeUtils.isEffectiveDate(nowTime,
                taskInfo.getTaskStartTime(),taskInfo.getTaskStartOver())
            || TimeUtils.isEffectiveDate(nowTime,
                taskInfo.getTaskEndTime(),taskInfo.getTaskEndOver())){
            //说明是在规定时间内打卡
            i = 3;
        }
        if (i == null){
            //还有早到和晚退（不在任务打卡时间）
            i =4;
        }
        return i;
    }
}

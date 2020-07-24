package com.ajiew.phonecallapp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScheduleDateUtil {

    public static long dateToStamp(String date, String pattern) {
//        String str = "2019-03-13 13:54:00";
//        "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date1 = null;
        try {
            date1 = simpleDateFormat.parse(date);
            return date1.getTime();
        } catch (Exception e) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                date1 = simpleDateFormat.parse(date);
                return date1.getTime();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * @param time 输入格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Calendar stringDateToCalendar(String time) {
        long stamp = ScheduleDateUtil.dateToStamp(time, "yyyy-MM-dd HH:mm:ss");
        return stampToCalendar(stamp);
    }


    /**
     * @param time 输入格式为yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Calendar stampToCalendar(long time) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * @param mills
     * @param pattern "yyyy-MM-dd HH:mm:ss"
     * @return
     */
    public static String stampToDate(long mills, String pattern) {
//        String str = "2019-03-13 13:54:00";
//        "yyyy-MM-dd HH:mm:ss"
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);//这个是你要转成后的时间的格式
        return sdf.format(new Date(mills));   // 时间戳转换成时间
    }

    /**
     * 日期为yyyy-MM-dd
     *
     * @param dateTime
     * @return
     */
    public static String getDayofWeek(String dateTime) {
        Calendar cal = Calendar.getInstance();
        if (dateTime.equals("")) {
            cal.setTime(new Date(System.currentTimeMillis()));
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date;
            try {
                date = sdf.parse(dateTime);
            } catch (ParseException e) {
                date = null;
                e.printStackTrace();
            }
            if (date != null) {
                cal.setTime(new Date(date.getTime()));
            }
        }
        String temp = "";
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                temp = "周日";
                break;
            case 2:
                temp = "周一";
                break;
            case 3:
                temp = "周二";
                break;
            case 4:
                temp = "周三";
                break;
            case 5:
                temp = "周四";
                break;
            case 6:
                temp = "周五";
                break;
            case 7:
                temp = "周六";
                break;
        }
        return temp;
    }

    /**
     * 日程详情页显示时间规则
     *
     * @param beginTime yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getShowTime(String beginTime) {
        Calendar calendar = stringDateToCalendar(beginTime);
        Calendar today = Calendar.getInstance();
        StringBuilder buffer = new StringBuilder();

        //是否是今年
        if (calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {  //不是今年显示  2019年12月1日 12:30
            buffer.append(calendar.get(Calendar.YEAR)).append("年").append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else if (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {  //同年  是否是今天
            buffer.append("今天 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else if ((calendar.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)) == 1) { //同年  明天
            buffer.append("明天 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else if ((calendar.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR)) == -1) { //同年  昨天
            buffer.append("昨天 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else {   //同年跨度很大  直接显示月日分时
            buffer.append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        }
        return buffer.toString();
    }

    /**
     * 日程详情页全天的显示时间规则, 不显示时分秒
     *
     * @param beginTime yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getAllDayShowTime(String beginTime) {
        Calendar calendar = stringDateToCalendar(beginTime);
        Calendar today = Calendar.getInstance();
        StringBuilder buffer = new StringBuilder();

        //是否是今年
        if (calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {  //不是今年显示  2019年12月1日 12:30
            buffer.append(calendar.get(Calendar.YEAR)).append("年").append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日");
        } else {   //同年跨度很大  直接显示月日分时
            buffer.append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日");
        }
        return buffer.toString();
    }

    /**
     * 截取全天显示规则
     *
     * @return 1今天  2今天-明天
     */
    public static String getAllDayShowTimeduration(String startTime, String endTime) {
        if (startTime.equals(endTime)) {
            return startTime;
        } else {
            return startTime + "-" + endTime;
        }
    }


    /**
     * 新建日程时间选择后显示规则
     *
     * @param day yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getNewScheduleShowTime(String day) {
        long stamp = ScheduleDateUtil.dateToStamp(day, "yyyy-MM-dd");
        if (stamp < 0) {
            return "";
        }
        Date date = new Date(stamp);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        Calendar today = Calendar.getInstance();
        StringBuilder buffer = new StringBuilder();

        //是否是今年
        if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
            buffer.append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(getDayofWeek(day));
        } else {
            buffer.append(calendar.get(Calendar.YEAR)).append("年").append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ");
        }

        return buffer.toString();
    }


    /**
     * 小时和分钟补0
     *
     * @return
     */
    public static String transHouOrMinute(int hourOrMinute) {
        String minuteTemp = hourOrMinute + "";
        if (0 <= hourOrMinute && hourOrMinute < 10) {
            minuteTemp = "0" + hourOrMinute;
        }
        return minuteTemp;

    }


    /**
     * 日程列表显示负责
     *
     * @param time
     * @return
     */
    public static String scheduleListShowTime(String time) {
        Calendar calendar = stringDateToCalendar(time);
        Calendar today = Calendar.getInstance();
        StringBuilder buffer = new StringBuilder();

        //是否是今年
        if (calendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {  //不同年显示  2019年12月22日 10:11
            buffer.append(calendar.get(Calendar.YEAR)).append("年").append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else if (calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {  //同年 是今天  显示11:12

            buffer.append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        } else {  //同年不是今天 显示4月22日 11:33
            buffer.append(calendar.get(Calendar.MONTH) + 1).append("月").append(calendar.get(Calendar.DAY_OF_MONTH)).append("日 ").append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE)));
        }
        return buffer.toString();

    }

    /**
     * 日程是否跨天
     *
     * @param beginTime
     * @param endTime
     * @return true 跨天了
     */
    public static boolean isOverDay(String beginTime, String endTime, String CalendarTime) {
        Calendar startCalendar = stringDateToCalendar(beginTime);
        Calendar endCalendar = stringDateToCalendar(endTime);
        Calendar today = stringDateToCalendar(CalendarTime);
        boolean isOverDay;

        //是否是今年
        if (startCalendar.get(Calendar.YEAR) != today.get(Calendar.YEAR) || endCalendar.get(Calendar.YEAR) != today.get(Calendar.YEAR)) {  //开始时间或者结束时间不在今年
            isOverDay = true;
        } else {
            //开始时间和结束时间都是今天
            isOverDay = (startCalendar.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR) || (endCalendar.get(Calendar.DAY_OF_YEAR) != today.get(Calendar.DAY_OF_YEAR)));
        }

        return isOverDay;

    }


    /**
     * 拼接已经显示为今天的日期, 去掉重复的今天
     *
     * @param time
     * @return
     */
    public static String schedueDateDuration(String time) {

        String[] todays = time.split("今天");
        String[] yesterdays = time.split("昨天");
        String[] tomorrows = time.split("明天");

        String Temp = time;
        if (todays.length == 3) {
            Temp = "今天 " + todays[1].trim() + todays[2].trim();
        } else if (yesterdays.length == 3) {
            Temp = "昨天 " + yesterdays[1].trim() + yesterdays[2].trim();
        } else if (tomorrows.length == 3) {
            Temp = "明天 " + tomorrows[1].trim() + tomorrows[2].trim();
        }
        return Temp;
    }

    /**
     * @param
     * @return 2020-8-8-2
     */
    public static String getCalendarToDate(Calendar calendar) {

        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameDay(String beginTime, String endTime) {
        Calendar begin = stringDateToCalendar(beginTime);
        Calendar end = stringDateToCalendar(endTime);
        return begin.get(Calendar.YEAR) == end.get(Calendar.YEAR)
                && begin.get(Calendar.MONTH) == end.get(Calendar.MONTH)
                && begin.get(Calendar.DAY_OF_MONTH) == end.get(Calendar.DAY_OF_MONTH);
    }

    public static String getHourAndMinuteFromDate(String date) {
        Calendar calendar = stringDateToCalendar(date);
        return new StringBuilder().append(transHouOrMinute(calendar.get(Calendar.HOUR_OF_DAY))).append(":").append(transHouOrMinute(calendar.get(Calendar.MINUTE))).toString();

    }


    public static String getFukDuration(boolean isAllDay, boolean isShowAllday, String beginTime, String endtime) {
        String time = "";
        if (isAllDay) {
            time = new StringBuilder(isShowAllday ? "全天：" : "").append(ScheduleDateUtil.getAllDayShowTimeduration(ScheduleDateUtil.getAllDayShowTime(beginTime),
                    ScheduleDateUtil.getAllDayShowTime(endtime))).toString();
        } else if (ScheduleDateUtil.isSameDay(beginTime, endtime)) {
            time = ScheduleDateUtil.getShowTime(beginTime) + " - " + ScheduleDateUtil.getHourAndMinuteFromDate(endtime);
        } else {
            //设置显示时间
            time = ScheduleDateUtil.getShowTime(beginTime) + " - " + ScheduleDateUtil.getShowTime(endtime);
        }

        return time;
    }
}

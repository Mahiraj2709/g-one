package customer.glympse.glympse.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static boolean isToday(long timeMilliseconds) {
        Calendar calendar = Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH);
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTimeInMillis(timeMilliseconds);
        return calendar.get(Calendar.YEAR) == todayYear &&
                calendar.get(Calendar.MONTH) == todayMonth &&
                calendar.get(Calendar.DAY_OF_MONTH) == todayDay;
    }
    public static String getCurrentDate(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        return formatter.format(date);
    }
    public static String getBackDate(){
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        return formatter.format(cal.getTime());
    }
}

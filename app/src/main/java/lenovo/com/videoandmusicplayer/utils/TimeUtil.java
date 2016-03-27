package lenovo.com.videoandmusicplayer.utils;

import android.text.format.DateFormat;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/3/21.
 */
public class TimeUtil {

    public static CharSequence formatLong(long duration){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, (int) duration);
        Date time = calendar.getTime();
        int hour = (int) (duration / 1000 / 60 / 60);

        CharSequence format = (hour > 0 ) ? "kk:mm:ss" : "mm:ss";
        return DateFormat.format(format,time);
    }

    public static CharSequence formatInt(int duration){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.add(Calendar.MILLISECOND, duration);
        Date time = calendar.getTime();
        int hour =duration / 1000 / 60 / 60;

        CharSequence format = (hour > 0 ) ? "kk:mm:ss" : "mm:ss";
        return DateFormat.format(format,time);
    }
}

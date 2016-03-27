package lenovo.com.videoandmusicplayer.utils;

import android.util.Log;

/**
 * Created by Administrator on 2016/3/21.
 */
public class LogUtil {
    private final static boolean ISOPEN = true;

    public static void i(String tag,String msg){
          if(ISOPEN){
              Log.i(tag,msg);
          }
    }

}

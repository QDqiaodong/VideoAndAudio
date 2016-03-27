package lenovo.com.videoandmusicplayer.utils;

import android.content.Context;
import android.view.WindowManager;

/**
 * Created by Administrator on 2016/3/22.
 */
public class Device {

    public static int getSceenWidth(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        LogUtil.i("qd","屏幕的宽=="+width);
        return width;
    }

    public static int getSceenHeight(Context context){
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        LogUtil.i("qd","屏幕的高ssss=="+height);
        return height;
    }
}

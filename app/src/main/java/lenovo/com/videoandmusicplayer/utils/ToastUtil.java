package lenovo.com.videoandmusicplayer.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/3/21.
 */
public class ToastUtil {
    private static Toast toast = null;
    public static void show(Context context,String msg){
        if(toast == null){
            toast = Toast.makeText(context,msg,Toast.LENGTH_SHORT);
        }
        toast.show();
    }
}

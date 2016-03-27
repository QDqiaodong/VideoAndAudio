package lenovo.com.videoandmusicplayer;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;

/**
 * Created by Administrator on 2016/3/10.
 */
public class AppContext extends Application{

    private static Context mContext;
    private static Resources resources;
    private static final String NIGHT_SWITCH = "night_switch";
    private static final String FIRST_START = "first_start";
    private static final String SharePref = "share.pref";
    public static final int PAGE_SIZE = 7;
    public static final String BASE_URL = "http://192.168.155.1:8080/zhbj/";
    public static final String FONTS = "fontSize";
    private int loginUid;
    private boolean isLogin;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        resources = mContext.getResources();
        //设置全局异常的捕捉类
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler(this));

    }



    public PackageInfo getPackageInfo(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(packageInfo == null){
            packageInfo = new PackageInfo();
        }

        return packageInfo;
    }

    public static AppContext getContext(){
        return (AppContext) mContext;
    }

    public static boolean getNigithSwitch(){
        return getPreferences().getBoolean(NIGHT_SWITCH,false);
    }

    public static SharedPreferences getPreferences(){
        return getContext().getSharedPreferences(SharePref, Context.MODE_PRIVATE);
    }

    public static void setNightSwitch(boolean b){
        getPreferences().edit().putBoolean(NIGHT_SWITCH,b).commit();
    }

    public boolean isFirstOpenApp(){
        return getPreferences().getBoolean(FIRST_START,false);
    }

    public void setFirstStart(boolean b){
        getPreferences().edit().putBoolean(FIRST_START,b).commit();
    }

    public static int getInt(String key,int defvalue){
        return getPreferences().getInt(key,defvalue);
    }

    public static void setInt(String key,int value){
        getPreferences().edit().putInt(key,value).commit();
    }
}

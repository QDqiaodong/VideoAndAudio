package lenovo.com.videoandmusicplayer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.Date;

import lenovo.com.videoandmusicplayer.utils.UIHelper;


/**
 * 应用程序异常：用于捕获异常和提示错误信息
 * 
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @author kymjs (kymjs123@gmali.com)
 * @created 2014年9月25日 下午5:34:05
 * 
 */
@SuppressWarnings("serial")
public class AppException extends Exception implements UncaughtExceptionHandler {


    /** 系统默认的UncaughtException处理类 */
    private AppContext mContext;

    private AppException(Context context) {
        this.mContext = (AppContext) context;
        Log.i("qd","启动了异常捕获类");
    }

    /**
     * 获取APP异常崩溃处理对象
     *
     * @param context
     * @return
     */
    public static AppException getAppExceptionHandler(Context context) {
        return new AppException(context.getApplicationContext());
    }

    @Override
      public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex)) {
            System.exit(0);
        }
    }

    /**
     * 自定义异常处理:收集错误信息&发送错误报告
     * 
     * @param ex
     * @return true:处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null || mContext == null) {
            return false;
        }
        boolean success = true;
        try {
            success = saveToSDCard(ex);
        } catch (Exception e) {
        } finally {
            if (!success) {
                return false;
            } else {
                final Context context = AppManager.getAppManager()
                        .currentActivity();
                // 显示异常信息&发送报告
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        // dialog对话框的形式，友好的提示用户发生了异常,对话框必须依附Activity,所以从ActivityMananger中获取
                        UIHelper.sendAppCrashReport(context);
                        Looper.loop();
                    }
                }.start();
            }
        }
        return true;
    }

    //将异常信息保存到本地sd卡
    private boolean saveToSDCard(Throwable ex) throws Exception {
        boolean append = false;
        File file= new File(Environment.getExternalStorageDirectory() + File.separator + "newclient" + File.separator + "OSCLog.log");
        if(file == null){
          file.createNewFile();
        }
        if(System.currentTimeMillis() - file.lastModified() > 5000){
            append = true;
        }
        PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file, append)));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String time = format.format(new Date());
        printWriter.append(time);
        printWriter.println();
        dumpPhoneInfo(printWriter);
        printWriter.println();
        ex.printStackTrace(printWriter);
        printWriter.println();
        printWriter.close();
        return append;
    }

    private void dumpPhoneInfo(PrintWriter pw) throws NameNotFoundException {
        // 应用的版本名称和版本号
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
                PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);
        pw.println();

        // android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);
        pw.println();

        // 手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);
        pw.println();

        // 手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);
        pw.println();

        // cpu架构
        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);
        pw.println();
    }
}

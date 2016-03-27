package lenovo.com.videoandmusicplayer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import lenovo.com.videoandmusicplayer.activity.AudioPlayerActivity;
import lenovo.com.videoandmusicplayer.activity.MainActivity;
import lenovo.com.videoandmusicplayer.activity.VideoPlayerActivity;
import lenovo.com.videoandmusicplayer.activity.VitamioVideoPlayerActivity;
import lenovo.com.videoandmusicplayer.bean.MusicItem;
import lenovo.com.videoandmusicplayer.bean.VideoItem;

/**
 * Created by Administrator on 2016/3/21.
 */
public class UIHelper {

    /**
     * 开启主页
     *
     * @param context
     */
    public static void startMainActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
        Log.i("qd", "启动mainactivty");
    }

    public static void sendAppCrashReport(Context context) {
        DialogHelp.getMessageDialog(context, "程序发生异常", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.exit(-1);
            }
        }).show();
    }

    public static void startVideoActivity(Context context, int position, ArrayList<VideoItem> lists) {
        //普通视频的播放页面
       // Intent intent = new Intent(context,VideoPlayerActivity.class);

        //万能视频的播放页面
        Intent intent = new Intent(context, VitamioVideoPlayerActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("data", lists);
        context.startActivity(intent);
        LogUtil.i("qd","开启了万能activity");
    }

    public static void startMusicActivity(Context context, int position, ArrayList<MusicItem> lists) {
        Intent intent = new Intent(context, AudioPlayerActivity.class);
        intent.putExtra("position", position);
        intent.putExtra("data", lists);
        context.startActivity(intent);
    }
}

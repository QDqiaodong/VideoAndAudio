package lenovo.com.videoandmusicplayer.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import lenovo.com.videoandmusicplayer.AppContext;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.activity.AudioPlayerActivity;
import lenovo.com.videoandmusicplayer.bean.MusicItem;
import lenovo.com.videoandmusicplayer.interfaces.AudioUI;
import lenovo.com.videoandmusicplayer.interfaces.IPlayAudio;
import lenovo.com.videoandmusicplayer.utils.Keys;
import lenovo.com.videoandmusicplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AudioPlayService extends Service implements IPlayAudio{

    public final static int WHAT_UI_INTEFACE = 0 ;
    public final static int WHAT_PLAYSERVICE_INTERFACCE = 0;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_UI_INTEFACE:
                    //拿到，传递过来的UI对象
                    audioUI = (AudioUI) msg.obj;
                    //获取ui对象的信使
                    Messenger uiMessenger = msg.replyTo;
                    //创建消息
                    Message message = Message.obtain(null, WHAT_PLAYSERVICE_INTERFACCE);
                    //把service传递过去
                    message.obj = AudioPlayService.this;
                    //
                    message.arg1 = flag ;
                    //用UI类的信使，发送消息
                    try {
                        uiMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * Service用来传递消息的信使
     * 当接收到消息时，在handler中进行处理
     */
    private Messenger serviceMessenger = new Messenger(mHandler);
    private int flag = -1; //用来标示，开始播放音乐，或者从通知栏点击进来时，后台继续播放音乐，但是刷新播放页面
    private AudioUI audioUI;
    private ArrayList<MusicItem> datas;
    private int position;
    private MusicItem musicItem;
    private MediaPlayer mediaPlayer;
    /** 顺序播放模式 */
    public static final int PLAY_MODE_ORDER = 0;
    /** 单曲循环模式 */
    public static final int PLAY_MODE_SINGLE = 1;
    /** 随机模式 */
    public static final int PLAY_MODE_RANDOM = 2;
    /** 当前播放模式 */
    private int currentPlayMode;
    private Random random;
    /** 点击通知栏的根布局 */
    private static final int NOTIFICATION_ROOT = 3;
    /** 点击通知栏的下一首按钮 */
    private static final int NOTIFICATION_NEXT = 2;
    /** 点击通知栏的上一首按钮 */
    private static final int NOTIFICATION_PRE = 1;
    /** 不打开音频 */
    public static final int NO_OPEN_AUDIO = 1;
    private NotificationManager notificationManager;
    private int notificationId = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        random = new Random();
        flag = -1;
        int what = intent.getIntExtra(Keys.WHAT, -1);
        switch (what){
            case -1:
                Intent ui_intent = intent.getParcelableExtra(Keys.UI_INTENT);
                datas = (ArrayList<MusicItem>)ui_intent.getSerializableExtra("data");
                position = ui_intent.getIntExtra("position", -1);
                break;
            case 1:
                pre();
                break;
            case 2:
                next();
                break;
            case 3:
                flags = NO_OPEN_AUDIO; //从通知栏点击跟布局时。
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return serviceMessenger.getBinder();
    }

    @Override
    public void onDestroy() {
        LogUtil.i("qd","service onDestroy");
        relase();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtil.i("qd","service onUnbind");
        relase();
        stopSelf();
        return super.onUnbind(intent);
    }

    @Override
    public void start() {
        if(mediaPlayer != null){
            try {
                mediaPlayer.start();
                showNotification();

            }catch (Exception e){
                LogUtil.i("qd","e============="+e.getMessage());
            }
        }
    }

    /**
     * 当开始播放音乐时，开启通知
     */
    private void showNotification() {
        CharSequence tickerText = "当前正在播放：" + musicItem.getTitle();
        CharSequence contentTitle = musicItem.getTitle();
        CharSequence contentText = musicItem.getArter();
        long when = System.currentTimeMillis();
        int icon = R.mipmap.icon_notification;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(icon)
                .setTicker(tickerText)
                .setWhen(when)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setOngoing(true);


        if(Build.VERSION.SDK_INT > 10){
            builder.setContent(getRemoteViews());        //通知栏的布局可以自定义
        } else {
            builder.setContentIntent(getActivityPendingIntent()); //只能用系统提供的布局
        }

        notificationManager.notify(notificationId, builder.build());
    }

    /**
     * 自定义通知栏
     * @return
     */
    private RemoteViews getRemoteViews() {
        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.audio_play_notification);
        remoteViews.setTextViewText(R.id.tv_title,musicItem.getTitle());
        remoteViews.setTextViewText(R.id.tv_artist,musicItem.getArter());
        remoteViews.setOnClickPendingIntent(R.id.btn_pre, getServicePendingIntent(NOTIFICATION_PRE));
        remoteViews.setOnClickPendingIntent(R.id.btn_next, getServicePendingIntent(NOTIFICATION_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.ll_root, getActivityPendingIntent());

        return remoteViews;
    }

    /**
     * 点击上一首，下一首，调用service中的方法
     * @param flag
     * @return
     */
    private PendingIntent getServicePendingIntent(int flag) {
        Intent intent = new Intent(this,AudioPlayService.class);
        intent.putExtra(Keys.WHAT,flag);
        PendingIntent pendingIntent = PendingIntent.getService(this, flag, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    /**
     * 利用系统定义的通知模板
     * @return
     */
    private PendingIntent getActivityPendingIntent() {
        Intent intent = new Intent(this, AudioPlayerActivity.class);
        intent.putExtra(Keys.WHAT,NOTIFICATION_ROOT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,NOTIFICATION_ROOT,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    @Override
    public void pause() {
       if(mediaPlayer!= null){
           mediaPlayer.pause();
           notificationManager.cancel(notificationId); //暂停时，取消通知
       }
    }

    @Override
    public void pre() {
        switch (currentPlayMode){
            case PLAY_MODE_ORDER:
                if(position == 0){
                    position = datas.size()-1;
                }else{
                    position --;
                }
                break;
            case PLAY_MODE_SINGLE:
                break;
            case PLAY_MODE_RANDOM:
                int r = random.nextInt(datas.size());
                    LogUtil.i("qd","position random...pre..postion.."+position+" r==="+r);
                    if(position == r){
                        pre();
                        return;
                    }
                    position = r;

                break;
        }
        openAudio();
    }

    @Override
    public void next() {
        switch (currentPlayMode){
            case PLAY_MODE_ORDER:
                if(position == datas.size()-1){
                    position = 0;
                }else{
                    position ++;
                }
                break;
            case PLAY_MODE_SINGLE:
                break;
            case PLAY_MODE_RANDOM:
                int r = random.nextInt(datas.size());
                LogUtil.i("qd","position random...pre..postion.."+position+" r==="+r);
                if(position == r){
                    pre();
                    return;
                }
                position = r;
                break;
        }
        openAudio();
    }

    @Override
    public int getCurrentPosition() {
        if(mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int getDuration() {
        if(mediaPlayer != null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public MusicItem getCurrentMusicItem() {
        return musicItem;
    }


    @Override
    public void openAudio() {
        currentPlayMode = AppContext.getPreferences().getInt(Keys.CURRENT_MODE, 0);
        LogUtil.i("qd", "aduioPlayService 开启了音频播放 当前播放模式为="+currentPlayMode);

        if(datas == null || datas.isEmpty() || position == -1){
            return;
        }
        notificationManager.cancel(notificationId);
        musicItem = datas.get(position);

        //因为要开启音频播放了，所有通知其他音乐播放关闭
        Intent i = new Intent("com.android.music.musicservicecommand");
        i.putExtra("command", "pause");
        sendBroadcast(i);

        //先释放mediaPlayer资源
        relase();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(preparedListener);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        try {
            mediaPlayer.setDataSource(musicItem.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.prepareAsync();
    }

    /**
     * 释放音频资源
     */
    private void relase() {
        audioUI.onRelease();
        if(mediaPlayer != null){
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean isPlaying() {
        if(mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(int position) {
        if(mediaPlayer != null){
            mediaPlayer.seekTo(position);
        }
    }

    /**
     * 切换播放模式
     * 顺序-->单曲-->随机
     * @return
     */
    @Override
    public int switchPlayMode() {
        switch (currentPlayMode){
            case PLAY_MODE_ORDER:
                currentPlayMode = PLAY_MODE_SINGLE;
                break;
            case PLAY_MODE_SINGLE:
                currentPlayMode = PLAY_MODE_RANDOM;
                break;
            case PLAY_MODE_RANDOM:
                currentPlayMode = PLAY_MODE_ORDER;
                break;
        }
        //将当前的播放模式保存起来
        AppContext.getPreferences().edit().putInt(Keys.CURRENT_MODE,currentPlayMode).commit();
        return currentPlayMode;
    }

    @Override
    public int getCurrentPlayMode() {
        return currentPlayMode;
    }

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener(){

        @Override
        public void onPrepared(MediaPlayer mp) {
            start();
            audioUI.updateUI(musicItem);

        }
    };

    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener(){

        @Override
        public void onCompletion(MediaPlayer mp) {
            next();
        }
    };
}

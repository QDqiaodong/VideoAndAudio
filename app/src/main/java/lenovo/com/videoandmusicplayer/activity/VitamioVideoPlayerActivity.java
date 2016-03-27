package lenovo.com.videoandmusicplayer.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import io.vov.vitamio.LibsChecker;
import lenovo.com.videoandmusicplayer.AppContext;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.base.BaseActivity;
import lenovo.com.videoandmusicplayer.bean.VideoItem;
import lenovo.com.videoandmusicplayer.utils.BrightnessTools;
import lenovo.com.videoandmusicplayer.utils.Device;
import lenovo.com.videoandmusicplayer.utils.LogUtil;
import lenovo.com.videoandmusicplayer.utils.TimeUtil;
import io.vov.vitamio.widget.VideoView;
import io.vov.vitamio.MediaPlayer;

/**
 * Created by Administrator on 2016/3/21.
 */
public class VitamioVideoPlayerActivity extends BaseActivity {

    @Bind(R.id.video_view)
    VideoView mVideoView;
    @Bind(R.id.tv_title)
    TextView mTitle;
    @Bind(R.id.tv_duration)
    TextView mDuration;
    @Bind(R.id.tv_current_postion)
    TextView mCurrentPostion;
    @Bind(R.id.tv_system_time)
    TextView mSystemTime;
    @Bind(R.id.iv_battery)
    ImageView mBattery;
    @Bind(R.id.sb_voice)
    SeekBar mSeekBarVoice;
    @Bind(R.id.sb_video)
    SeekBar mSeekBarVideo;
    @Bind(R.id.btn_exit)
    Button mExit;
    @Bind(R.id.btn_fullscreen)
    Button mFullScreen;
    @Bind(R.id.btn_next)
    Button mNext;
    @Bind(R.id.btn_play)
    Button mPlay;
    @Bind(R.id.btn_pre)
    Button mPre;
    @Bind(R.id.btn_voice)
    Button mVoice;
    @Bind(R.id.top_layout)
    View mTopLayout;
    @Bind(R.id.bottom_layout)
    View mBottomLayout;
    @Bind(R.id.loading)
    LinearLayout loading;

    /**
     * 更新时间
     */
    private final static int UPDATE_SYSTEM_TIME = 0;
    private final static int UPDATE_VIDEO_CURRENT_POSITION = 1;
    private final static int HIDE_LAYOUT = 3;
    private AudioManager audioManager;
    private BroadcastReceiver volumeChangeReceiver;
    private int mCurrentVolume;
    private GestureDetector gestureDetector = null;
    private float volumeWidthScale;
    private float brightnessScale;
    private int maxVolume;
    private ArrayList<VideoItem> mDatas;
    private int selectPosition;
    private VideoItem item;

    @Override
    protected void onStart() {
        super.onStart();
        mhandler.sendEmptyMessage(UPDATE_SYSTEM_TIME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(batteryReciver != null){
            unregisterReceiver(batteryReciver);
            batteryReciver = null;
        }
        if(volumeChangeReceiver != null){
            unregisterReceiver(volumeChangeReceiver);
        }
        mhandler.removeMessages(UPDATE_SYSTEM_TIME);
        mhandler.removeMessages(UPDATE_VIDEO_CURRENT_POSITION);

    }

    @Override
    protected void onStop() {
        super.onStop();
        mhandler.removeMessages(UPDATE_SYSTEM_TIME);
        mhandler.removeMessages(UPDATE_VIDEO_CURRENT_POSITION);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_vitamio_video_player;
    }

    @Override
    public void initView() {
        super.initView();
        mExit.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mVoice.setOnClickListener(this);
    }

    @Override
    public void initData() {
        LogUtil.i("qd", "videoactivity initdata");
        //注册电量改变的监听
        registerBatteryReceivce();
        //注册音量变化监听
        registerVolumeChangerReceiver();
        //获取intent传递来的数据
        Intent intent = getIntent();
        selectPosition = intent.getIntExtra("position", -1);
        Serializable data = intent.getSerializableExtra("data");
        mDatas = (ArrayList<VideoItem>) data;

        // 初始化Vitamio SDK
        if (!LibsChecker.checkVitamioLibs(this))
            return;

        //从第三方跳转过来
        Uri uri = getIntent().getData();
        if (uri != null) {
            mVideoView.setVideoURI(uri);
            mTitle.setText(uri.getPath());
            mPre.setEnabled(false);
            mNext.setEnabled(false);
        } else {
            openVideo();
        }

        //初始化一些数据
        brightnessScale = (float) 256 / Device.getSceenWidth(this);


        //初始化音量
        initVolume();


    }

    /**
     * 准备播放视频
     */
    private void openVideo() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }

        mPre.setEnabled(selectPosition != 0);
        mNext.setEnabled(selectPosition != (mDatas.size() - 1));
        item = mDatas.get(selectPosition);
        //播放
        mVideoView.setVideoPath(item.getPath());
    }

    @Override
    protected void initListener() {
        //手势识别器
        gestureDetector = new GestureDetector(this, gestureListener);
        //音量seekbar改变的监听器
        mSeekBarVoice.setOnSeekBarChangeListener(seekBarListener);
        //视频准备好的监听
        mVideoView.setOnPreparedListener(preparedListener);
        //视频播放完的监听
        mVideoView.setOnCompletionListener(completionListener);
        //mSeekBarVideo改变的监听，用来实现快进，快退
        mSeekBarVideo.setOnSeekBarChangeListener(videoSeekBarListener);
        //视频缓冲  ----本地视频不存在缓冲一说
        mVideoView.setOnBufferingUpdateListener(onBufferingUpdateListener);
        //缓冲视频前的准备
        mVideoView.setOnInfoListener(onInfoListener);
    }


    /**
     * 注册音量改变的广播接受者
     */
    private void registerVolumeChangerReceiver() {
        volumeChangeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_TYPE", -1) == AudioManager.STREAM_MUSIC) {
                    int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE", -1);
                    mSeekBarVoice.setProgress(volume);
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.media.VOLUME_CHANGED_ACTION");
        registerReceiver(volumeChangeReceiver, filter);
    }

    /***
     * 初始化音量
     */
    private void initVolume() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mCurrentVolume = getCurrentVolume();
        mSeekBarVoice.setMax(maxVolume);
        mSeekBarVoice.setProgress(mCurrentVolume);

        //计算音量与屏幕宽度的百分比
        volumeWidthScale = (float) maxVolume / Device.getSceenWidth(this);
        LogUtil.i("qd", "volumeWidthScale==" + volumeWidthScale);
    }

    /**
     * 设置当前的音量
     *
     * @param progress
     * @param i        i = 0 :不显示系统音量的悬浮框
     *                 i = 1 :显示系统音量的悬浮框
     */
    private void setVolume(int progress, int i) {
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, i);
    }


    /**
     * 返回当前的  音量
     *
     * @return
     */
    public int getCurrentVolume() {
        return audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    /**
     * 静音
     */
    private void mute() {
        if (getCurrentVolume() > 0) {  //设置成静音
            mCurrentVolume = getCurrentVolume();
            setVolume(0, 0);
            mSeekBarVoice.setProgress(0);
        } else if (getCurrentVolume() == 0) {  //回复音量
            setVolume(mCurrentVolume, 0);
            Log.i("qd", "回复音量mcurrentVolume==" + mCurrentVolume);
            mSeekBarVoice.setProgress(mCurrentVolume);
        }
    }

    /**
     * 注册电量改变的广播接受者
     */
    private void registerBatteryReceivce() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryReciver, filter);
    }


    /**
     * 更新显示的时间
     */
    private void updateSystemTime() {
        CharSequence format = "kk:mm:ss";
        CharSequence date = DateFormat.format(format, new Date());
        mSystemTime.setText(date);
        mhandler.sendEmptyMessageDelayed(UPDATE_SYSTEM_TIME, 1000);
    }

    /**
     * 更新当前播放位置
     */
    private void updateVideoCurrentPosition() {

        /**
         *再次需要注意了，setText必须设置为String类型，如果是int类型的话，他回去找相应的资源，找不到也不报错！！！！！
         */
        mCurrentPostion.setText(TimeUtil.formatLong(mVideoView.getCurrentPosition()));
        mSeekBarVideo.setProgress((int) mVideoView.getCurrentPosition());
        mhandler.sendEmptyMessageDelayed(UPDATE_VIDEO_CURRENT_POSITION, 300);
    }

    /**
     * 隐藏控制面板
     */
    public void hideControlPanel() {
        //相对于自身移动
        ViewPropertyAnimator.animate(mTopLayout).translationY(-mTopLayout.getHeight());
        ViewPropertyAnimator.animate(mBottomLayout).translationY(mBottomLayout.getHeight());
    }

    /**
     * 切换控制面板
     */
    public void switchControlPanel() {
        //显示状态，则隐藏
        if (ViewHelper.getTranslationY(mTopLayout) == 0) {
            hideControlPanel();
        } else {
            showControlPanel();
        }
    }

    /**
     * 显示控制面板
     */
    public void showControlPanel() {
        ViewPropertyAnimator.animate(mTopLayout).translationY(0);
        ViewPropertyAnimator.animate(mBottomLayout).translationY(0);
        sendMessageDelayHideLayout();
    }

    public void sendMessageDelayHideLayout() {
        cancelMessageHideLayout();
        mhandler.sendEmptyMessageDelayed(HIDE_LAYOUT, 5000);
    }

    public void cancelMessageHideLayout() {
        mhandler.removeMessages(HIDE_LAYOUT);
    }

    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_SYSTEM_TIME:
                    updateSystemTime();
                    break;
                case UPDATE_VIDEO_CURRENT_POSITION:
                    updateVideoCurrentPosition();
                    break;
                case HIDE_LAYOUT:
                    hideControlPanel();
                    break;
            }

        }
    };


    /**
     * 改变电量图标
     *
     * @param level
     */
    private void changeBatteryIcon(int level) {
        Log.i("qd", "level======" + level);
        int resId = R.mipmap.ic_battery_0;
        if (level == 0) {
            resId = R.mipmap.ic_battery_0;
        } else if (level > 0 && level <= 10) {
            resId = R.mipmap.ic_battery_10;
        } else if (level > 10 && level <= 20) {
            resId = R.mipmap.ic_battery_20;
        } else if (level > 20 && level <= 40) {
            resId = R.mipmap.ic_battery_40;
        } else if (level > 40 && level <= 60) {
            resId = R.mipmap.ic_battery_60;
        } else if (level > 60 && level <= 80) {
            resId = R.mipmap.ic_battery_80;
        } else {
            resId = R.mipmap.ic_battery_100;
        }
        mBattery.setImageResource(resId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_voice:
                mute();
                break;
            case R.id.btn_exit:
                finish();
                break;
            case R.id.btn_pre:
                pre();
                break;
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_next:
                next();
                break;
            case R.id.btn_fullscreen:
                changeFullScreen();
                break;

            default:
                break;
        }
        sendMessageDelayHideLayout();
    }

    /**
     * 切换全屏
     */
    private void changeFullScreen() {
        mVideoView.switchFullScreen();
        if (mVideoView.isFullscreen()) {
            mFullScreen.setBackgroundResource(R.drawable.selector_video_btn_defaultscreen);
        } else {
            mFullScreen.setBackgroundResource(R.drawable.selector_video_btn_fullscreen);
        }
    }

    /**
     * 播放与暂停
     */
    private void play() {
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        } else {
            mVideoView.start();
        }
        LogUtil.i("qd", "play");
        changePlayBtBg();
    }

    /**
     * 改变play按钮的背景图片
     */
    private void changePlayBtBg() {
        if (mVideoView.isPlaying()) {
            mPlay.setBackgroundResource(R.drawable.selector_video_btn_pause);
        } else {
            mPlay.setBackgroundResource(R.drawable.selector_video_btn_play);
        }
    }


    /**
     * 下一个视频
     */
    private void next() {
        loading.setVisibility(View.VISIBLE);
        if (selectPosition != mDatas.size() - 1) {
            selectPosition++;
        }
        openVideo();
    }

    /**
     * 上一个视频
     */
    private void pre() {
        loading.setVisibility(View.VISIBLE);
        if (selectPosition != 0) {
            selectPosition--;
        }
        openVideo();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cancelMessageHideLayout();
                break;
        }
        return gestureDetector.onTouchEvent(event);
    }

    BroadcastReceiver batteryReciver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
                int level = intent.getIntExtra("level", 0);
                changeBatteryIcon(level);
            }
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                setVolume(progress, 0);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            cancelMessageHideLayout();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendMessageDelayHideLayout();
        }
    };

    SeekBar.OnSeekBarChangeListener videoSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                mVideoView.seekTo(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            cancelMessageHideLayout();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            sendMessageDelayHideLayout();
        }
    };
    MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {

        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            switch (what) {
                case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                    loading.setVisibility(View.VISIBLE);
                    break;
                case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                    loading.setVisibility(View.GONE);
                    break;
            }
            return false;
        }
    };

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer mp) {
            //切记需要，先启动，因为接下来需要获取他播放的位置，否在的话会报异常。
            mVideoView.start();
            if (item != null) {
                mTitle.setText(item.getTitle());
            }

            mDuration.setText(TimeUtil.formatLong(mVideoView.getDuration()));
            mSeekBarVideo.setMax((int) mVideoView.getDuration());
            updateVideoCurrentPosition();
            LogUtil.i("qd", "preparedListener  selectPosition===" + selectPosition);
            loading.setVisibility(View.GONE);
        }
    };

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mhandler.removeMessages(UPDATE_VIDEO_CURRENT_POSITION);
        }
    };

    MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {

        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            float f = (float) percent / 100;
            int secondaryProgress = (int) (f * mVideoView.getDuration());
            mSeekBarVideo.setSecondaryProgress(secondaryProgress);
        }
    };

    GestureDetector.OnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener() {
        private boolean isLeft = false;

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
        }

        //自己处理
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float dy = e1.getY() - e2.getY();
            if (isLeft) {             //屏幕左边上下移动的话，改变屏幕亮度
                changeBrightness(dy);
            } else {
                changeVolume(dy);   //右边，调节音量
            }
            LogUtil.i("qd", "isLeft==" + isLeft);
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            int halfSceenHeight = Device.getSceenWidth(AppContext.getContext()) / 2;
            LogUtil.i("qd", "halfSceenHeight==" + halfSceenHeight);
            isLeft = x < halfSceenHeight;
            return super.onDown(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            switchControlPanel();
            return super.onSingleTapConfirmed(e);
        }
    };

    /**
     * 调节屏幕的亮度
     *
     * @param dy
     */
    private void changeBrightness(float dy) {
        int currentBirghtness = (int) (brightnessScale * dy + BrightnessTools.getScreenBrightness(this));
        LogUtil.i("qd", "currentBirghtness=" + currentBirghtness);
        if (currentBirghtness < 0) {
            currentBirghtness = 10;
        } else if (currentBirghtness > 255) {
            currentBirghtness = 255;
        }
        BrightnessTools.stopAutoBrightness(this);
        BrightnessTools.setBrightness(this, currentBirghtness);
    }

    /**
     * 滑动屏幕 改变电量
     *
     * @param dy
     */
    private void changeVolume(float dy) {
        mCurrentVolume = (int) (volumeWidthScale * dy + getCurrentVolume());
        LogUtil.i("qd", "changeVolume==" + mCurrentVolume +
                "   volumeWidthScale * dy" + volumeWidthScale * dy +
                "  getCurrentVolume()=" + getCurrentVolume());
        if (mCurrentVolume < 0) {
            mCurrentVolume = 0;
        } else if (mCurrentVolume > maxVolume) {
            mCurrentVolume = maxVolume;
        }

        setVolume(mCurrentVolume, 0);
        mSeekBarVoice.setProgress(mCurrentVolume);
    }

}

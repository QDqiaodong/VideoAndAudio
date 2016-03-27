package lenovo.com.videoandmusicplayer.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.bean.MusicItem;
import lenovo.com.videoandmusicplayer.interfaces.AudioUI;
import lenovo.com.videoandmusicplayer.interfaces.BaseInterface;
import lenovo.com.videoandmusicplayer.interfaces.IPlayAudio;
import lenovo.com.videoandmusicplayer.services.AudioPlayService;
import lenovo.com.videoandmusicplayer.utils.Keys;
import lenovo.com.videoandmusicplayer.utils.LogUtil;
import lenovo.com.videoandmusicplayer.utils.LyricLoader;
import lenovo.com.videoandmusicplayer.utils.TimeUtil;
import lenovo.com.videoandmusicplayer.utils.ToastUtil;
import lenovo.com.videoandmusicplayer.widget.LyricsView;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AudioPlayerActivity extends AppCompatActivity implements BaseInterface,
        View.OnClickListener,AudioUI{
    @Bind(R.id.toolbar)
    Toolbar mToolBar;
    @Bind(R.id.iv_visual_effect)
    ImageView mVisualEffect;
    @Bind(R.id.tv_artist)
    TextView mArtist;
    @Bind(R.id.tv_play_time)
    TextView mPlayTime;
    @Bind(R.id.sb_audio)
    SeekBar mAudio;
    @Bind(R.id.btn_play_mode)
    Button mPlayMode;
    @Bind(R.id.btn_pre)
    Button mPre;
    @Bind(R.id.btn_play)
    Button mPlay;
    @Bind(R.id.btn_next)
    Button mNext;
    @Bind(R.id.lyrics)
    LyricsView mLyrics;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case AudioPlayService.WHAT_PLAYSERVICE_INTERFACCE:
                    //Activity 进行了三次握手，创建了连接
                    audioPlayService = (IPlayAudio) msg.obj;

                    if(msg.arg1 == -1){
                        //开启音频
                        audioPlayService.openAudio();
                    }else{
                        //从通知栏，点击进来额，机选刷新activity并不做其他处理
                        updateUI(audioPlayService.getCurrentMusicItem());
                    }

                    break;
                case UPDATE_PLAY_TIME:
                    updatePlayTime();
                    break;
                case UPDATE_LYRICS:
                    updateLyrics();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    /**
     * Activity用来传递消息的信使
     * 当接收到消息时，在handler中进行处理
     */
    private Messenger uiMessenger = new Messenger(mHandler);
    private IPlayAudio audioPlayService;
    private final static int UPDATE_PLAY_TIME = 1;
    private final static int UPDATE_LYRICS = 2;
    private Intent service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);
        ButterKnife.bind(this);
        initToolBar();
        initView();
        initData();
        initListener();
    }

    private void initToolBar() {
        // Title
        mToolBar.setTitle("音乐名称");
        mToolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolBar);
        //Navigation Icon  此设置必须写到setSupportActionBar后面
        mToolBar.setNavigationIcon(R.drawable.selector_back);
    }


    @Override
    public void initView() {
        AnimationDrawable animation = (AnimationDrawable) mVisualEffect.getBackground();
        animation.start();

        mPlayMode.setOnClickListener(this);
        mPre.setOnClickListener(this);
        mPlay.setOnClickListener(this);
        mNext.setOnClickListener(this);
    }

    @Override
    public void initData() {
        service = new Intent(this, AudioPlayService.class);
        int flag = getIntent().getIntExtra(Keys.WHAT, -1);

        if(flag == -1){
            service.putExtra(Keys.UI_INTENT, getIntent()); //从条目点击跳转过来的intent
        }else{
            service.getIntExtra(Keys.WHAT,flag); //从通知栏。点击跳转进来的intent
        }

        startService(service);
        bindService(service, conn, BIND_AUTO_CREATE);

    }
    private void initListener() {
        mAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    audioPlayService.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play_mode:
                switchPlayMode(audioPlayService.switchPlayMode());  //切换播放模式，并且改变背景图片
                break;
            case R.id.btn_pre:
                audioPlayService.pre();
                break;
            case R.id.btn_play:
                play();
                break;
            case R.id.btn_next:
                audioPlayService.next();
                break;
        }
    }

    ServiceConnection conn = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //获取service传递过来的信使
            Messenger serviceMessenger = new Messenger(service);
            //创建一条消息
            Message message = Message.obtain(null, AudioPlayService.WHAT_UI_INTEFACE);
            //携带UI类过去
            message.obj = AudioPlayerActivity.this;
            //告诉service,此UI的信使是哪个（这样，service就能拿到ui的信使，并用此信使，发送消息）
            message.replyTo = uiMessenger;
            //用service的信使，给service发送消息
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void updateUI(MusicItem item) {

        mLyrics.setLyricLists(LyricLoader.loadLyric(item.getPath())); //加载歌词
        updatePlayBt();
        mToolBar.setTitle(item.getTitle());
        mArtist.setText(item.getArter());
        mAudio.setMax(audioPlayService.getDuration());
        updatePlayTime();
        updateLyrics();
        switchPlayMode(audioPlayService.getCurrentPlayMode());  //这里只是改变播放模式的背景图片（可能用户上次选择了模式，新模式保存在sp当中）
    }

    /**
     * 滚动歌词
     */
    private void updateLyrics() {
        cancelLyricsMessage();
        mLyrics.updateCurrentPosition(audioPlayService.getCurrentPosition());
        mHandler.sendEmptyMessageDelayed(UPDATE_LYRICS,50);
    }

    /**
     * 选择当前的播放模式
     * @param currentPlayMode
     */
    private void switchPlayMode(int currentPlayMode) {
        int resId = R.drawable.selector_playmode_order;
        switch (currentPlayMode){
            case AudioPlayService.PLAY_MODE_ORDER:
                resId = R.drawable.selector_playmode_order;
                break;
            case AudioPlayService.PLAY_MODE_SINGLE:
                resId = R.drawable.selector_playmode_single;
                break;
            case AudioPlayService.PLAY_MODE_RANDOM:
                resId = R.drawable.selector_playmode_random;
                break;
        }
        mPlayMode.setBackgroundResource(resId);
    }

    /**
     * 更新播放按钮背景图片
     */
    private void updatePlayBt() {
        if(audioPlayService.isPlaying()){
            mPlay.setBackgroundResource(R.drawable.selector_audio_btn_pause);
        }else{
            mPlay.setBackgroundResource(R.drawable.selector_audio_btn_play);
        }
    }

    /**
     * 播放或者暂停
     */
    public void play(){
        if(audioPlayService.isPlaying()){
            audioPlayService.pause();
            cancelLyricsMessage();
            cancelMessage();
        }else{
            audioPlayService.start();
            updatePlayTime();
            updateLyrics();
        }
        updatePlayBt();
    }


    /**
     * 刷新音频播放的时间
     */
    private void updatePlayTime() {
        //先删除掉存在的消息
        cancelMessage();
        mPlayTime.setText(TimeUtil.formatInt(audioPlayService.getCurrentPosition())+"/"+TimeUtil.formatInt(audioPlayService.getDuration()));
        mAudio.setProgress(audioPlayService.getCurrentPosition());
        mHandler.sendEmptyMessageDelayed(UPDATE_PLAY_TIME, 300);
    }

    @Override
    public void onRelease() {
        cancelMessage();
        cancelLyricsMessage();
    }

    public void cancelMessage(){
        mHandler.removeMessages(UPDATE_PLAY_TIME);
    }
    public void cancelLyricsMessage(){
        mHandler.removeMessages(UPDATE_LYRICS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelMessage();
        cancelLyricsMessage();
        stopService(service);
        unbindService(conn);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //增加对toolbar上 navgationIcon的监听
        if(item.getItemId() == android.R.id.home){
            Intent intent = new Intent(this,MainActivity.class);
            intent.putExtra(Keys.WHAT,1);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }


}

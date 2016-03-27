package lenovo.com.videoandmusicplayer.interfaces;

import lenovo.com.videoandmusicplayer.bean.MusicItem;

/**
 * 播放服务接口
 *
 * @author Administrator
 */
public interface IPlayAudio {

    /**
     * 播放
     */
    void start();

    /**
     * 暂停
     */
    void pause();

    /**
     * 上一首
     */
    void pre();

    /**
     * 下一首
     */
    void next();

    /**
     * 获取当前的播放位置
     */
    int getCurrentPosition();

    /**
     * 获取音频的总时长
     */
    int getDuration();

    /**
     * 获取当前正在播放的音乐信息
     */
    MusicItem getCurrentMusicItem();

    /**
     * 打开音频
     */
    void openAudio();

    /**
     * 是否正在播放
     */
    boolean isPlaying();

    /**
     * 跳转
     *
     * @param position 跳转的位置
     */
    void seekTo(int position);

    /**
     * 切换播放模式
     *
     * @return 返回切换后的模式
     */
    int switchPlayMode();

    /**
     * 获取当前的播放模式
     */
    int getCurrentPlayMode();

}


package lenovo.com.videoandmusicplayer.interfaces;

import lenovo.com.videoandmusicplayer.bean.MusicItem;

/**
 * Created by Administrator on 2016/3/23.
 */
public interface AudioUI {
    /**
     * 更新UI
     * @param item
     */
    void updateUI(MusicItem item);

    /** 当音频资源释放的时候执行 */
    void onRelease();
}

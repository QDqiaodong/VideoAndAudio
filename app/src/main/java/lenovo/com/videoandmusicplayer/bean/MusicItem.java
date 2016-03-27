package lenovo.com.videoandmusicplayer.bean;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MusicItem implements Serializable{
    private String title;
    private String path;
    private String arter;

    public static MusicItem fromCursor(Cursor cursor){
        MusicItem musicItem = new MusicItem();
        musicItem.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
        musicItem.setArter(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
        musicItem.setPath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
        return musicItem;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getArter() {
        return arter;
    }

    public void setArter(String arter) {
        this.arter = arter;
    }
}

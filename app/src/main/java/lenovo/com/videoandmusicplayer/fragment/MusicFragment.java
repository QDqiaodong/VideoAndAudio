package lenovo.com.videoandmusicplayer.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.widget.ListAdapter;

import java.util.ArrayList;

import lenovo.com.videoandmusicplayer.adapter.MusicAdapter;
import lenovo.com.videoandmusicplayer.base.BaseFragment;
import lenovo.com.videoandmusicplayer.bean.MusicItem;
import lenovo.com.videoandmusicplayer.utils.UIHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MusicFragment extends BaseFragment<MusicItem> {

    @Override
    protected ListAdapter getAdapter(FragmentActivity activity, Cursor cursor) {
        return new MusicAdapter(activity,cursor);
    }

    @Override
    protected String getOrderBy() {
        return MediaStore.Audio.Media.TITLE + " asc";
    }

    @Override
    protected String[] getProjection() {
        return new String[]{MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DATA};
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected void sendRedirect(Context context, int position, ArrayList<MusicItem> lists) {
        UIHelper.startMusicActivity(context,position,lists);
    }

    @Override
    protected ArrayList getAllDatas(Cursor cursor) {
        ArrayList<MusicItem> datas = new ArrayList<>();
        cursor.moveToFirst();
        do {
            datas.add(MusicItem.fromCursor(cursor));
        }while(cursor.moveToNext());
        return datas;
    }
}
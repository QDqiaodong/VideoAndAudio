package lenovo.com.videoandmusicplayer.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.widget.ListAdapter;

import java.util.ArrayList;

import lenovo.com.videoandmusicplayer.adapter.VideoAdapter;
import lenovo.com.videoandmusicplayer.base.BaseFragment;
import lenovo.com.videoandmusicplayer.bean.VideoItem;
import lenovo.com.videoandmusicplayer.utils.UIHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public class VideoFragment extends BaseFragment<VideoItem> {


    @Override
    public void initView() {
        super.initView();
    }

    @Override
    protected ListAdapter getAdapter(FragmentActivity activity, Cursor cursor) {
        return new VideoAdapter(activity,cursor);
    }

    @Override
    protected String getOrderBy() {
        return MediaStore.Video.Media.TITLE + " asc";
    }

    @Override
    protected String[] getProjection() {
        return new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Media.TITLE, MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DATA};
    }

    @Override
    protected Uri getUri() {
        return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected void sendRedirect(Context context, int position, ArrayList<VideoItem> lists) {
        UIHelper.startVideoActivity(context,position,lists);
    }


    /**
     * 由Cursor获取所有其中所有的数据
     * @param cursor
     */
    public  ArrayList<VideoItem> getAllDatas(Cursor cursor) {
       ArrayList<VideoItem> datas = new ArrayList<VideoItem>();
        cursor.moveToFirst();
        do {
            datas.add(VideoItem.fromCursor(cursor));
        }while(cursor.moveToNext());
        return datas;
    }
}

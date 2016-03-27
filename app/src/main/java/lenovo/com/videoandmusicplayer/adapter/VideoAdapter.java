package lenovo.com.videoandmusicplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.bean.VideoItem;
import lenovo.com.videoandmusicplayer.utils.TimeUtil;


/**
 * Created by Administrator on 2016/3/21.
 */
public class VideoAdapter extends CursorAdapter {
    public VideoAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.title);
        holder.duration = (TextView) view.findViewById(R.id.duration);
        holder.size = (TextView) view.findViewById(R.id.size);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        VideoItem videoItem = VideoItem.fromCursor(cursor);
        holder.title.setText(videoItem.getTitle());
        holder.duration.setText(TimeUtil.formatLong(videoItem.getDuration()));
        holder.size.setText(Formatter.formatFileSize(context, videoItem.getSize()));
    }

    class ViewHolder{
        public TextView title;
        public TextView duration;
        public TextView size;
    }
}

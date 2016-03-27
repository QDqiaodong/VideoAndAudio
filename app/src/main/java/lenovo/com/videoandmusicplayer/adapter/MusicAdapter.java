package lenovo.com.videoandmusicplayer.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.bean.MusicItem;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MusicAdapter extends CursorAdapter {
    public MusicAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item, null);
        ViewHolder holder = new ViewHolder();
        holder.title = (TextView) view.findViewById(R.id.title);
        holder.author = (TextView) view.findViewById(R.id.autor);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();
        MusicItem musicItem = MusicItem.fromCursor(cursor);
        holder.title.setText(musicItem.getTitle());
        holder.author.setText(musicItem.getArter());
    }

    class ViewHolder{
        public TextView title;
        public TextView author;
    }


}

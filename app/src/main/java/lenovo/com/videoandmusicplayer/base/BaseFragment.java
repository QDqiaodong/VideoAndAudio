package lenovo.com.videoandmusicplayer.base;

import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import lenovo.com.videoandmusicplayer.AppContext;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.activity.VideoPlayerActivity;
import lenovo.com.videoandmusicplayer.interfaces.BaseInterface;
import lenovo.com.videoandmusicplayer.utils.LogUtil;
import lenovo.com.videoandmusicplayer.utils.UIHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public abstract class BaseFragment<T> extends Fragment implements BaseInterface{

    protected View rootView;
    @Bind(R.id.list_view)
    protected ListView mListView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(getLayoutId(), container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, rootView);
        try{
            initView();
            initData();
        }catch (Exception e){
            LogUtil.i("qd","-----"+e.getMessage());
        }

    }

    protected int getLayoutId(){
        return R.layout.fragment;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        LogUtil.i("qd","........initData");
        try{
            /**
             * 系统提供的异步查询数据库的方式，他使用了内容观察者的模式，数据库每次变化时，cursor都会变化
             */
            AsyncQueryHandler task = new AsyncQueryHandler(getActivity().getContentResolver()) {
                @Override
                protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
                    super.onQueryComplete(token, cookie, cursor);
                    if(cursor != null){
                        mListView.setAdapter(getAdapter(getActivity(), cursor));
                    }else{
                        LogUtil.i("qd","audioFragment cursor == null");
                    }
                }
            };
            task.startQuery(0,null, getUri(), getProjection(),null,null,getOrderBy());
            itemClick();
        }catch (Exception e){
            LogUtil.i("qd","........"+e.getMessage());
        }
    }

    protected abstract ListAdapter getAdapter(FragmentActivity activity, Cursor cursor);

    protected abstract String getOrderBy();

    protected abstract String[] getProjection();

    protected abstract Uri getUri();

    /**
     * 条目点击事件
     */
    private void itemClick() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LogUtil.i("qd", "audio 条目 " + position + "被点击了");

                /**cursor已经游到了指定位置，所以接下来需要进行处理*/
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                ArrayList<T> allDatas = getAllDatas(cursor);
                sendRedirect(getActivity(),position,allDatas);
                /**点击进入详情页面的时候，需要传递当前条目的位置，所有数据的集合都需要传过去*/
                LogUtil.i("qd", "数据大小:" + allDatas.size());

            }
        });
    }

    /**
     * 跳转到详情页面
     */
    protected abstract void sendRedirect(Context context,int position,ArrayList<T> lists);


    /**
     * 由Cursor获取所有其中所有的数据
     * @param cursor
     */
    protected  abstract ArrayList<T> getAllDatas(Cursor cursor);

}

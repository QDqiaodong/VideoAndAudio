package lenovo.com.videoandmusicplayer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/3/21.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> mDatas = new ArrayList<Fragment>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);

    }

    public void addItem(Fragment fragment){
        if(fragment != null){
            mDatas.add(fragment);
        }
    }

    @Override
    public Fragment getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public int getCount() {
        return mDatas.size();
    }
}

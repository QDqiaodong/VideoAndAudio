package lenovo.com.videoandmusicplayer.activity;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import butterknife.Bind;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.adapter.FragmentAdapter;
import lenovo.com.videoandmusicplayer.base.BaseActivity;
import lenovo.com.videoandmusicplayer.fragment.VideoFragment;
import lenovo.com.videoandmusicplayer.fragment.MusicFragment;
import lenovo.com.videoandmusicplayer.utils.Keys;
import lenovo.com.videoandmusicplayer.utils.LogUtil;

public class MainActivity extends BaseActivity {

    @Bind(R.id.tv_audio)
    TextView mAudio;
    @Bind(R.id.music)
    TextView mMusic;
    @Bind(R.id.indicator)
    View mIndicator;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    private int pageSize;
    private int mIndicatorWidth;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        mAudio.setOnClickListener(this);
        mMusic.setOnClickListener(this);
    }

    @Override
    public void initData() {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager());
        fragmentAdapter.addItem(new VideoFragment());
        fragmentAdapter.addItem(new MusicFragment());
        mViewPager.setAdapter(fragmentAdapter);
        pageSize = fragmentAdapter.getCount();
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mIndicator.getLayoutParams();
        mIndicatorWidth = getWindowManager().getDefaultDisplay().getWidth() / pageSize;
        params.width = mIndicatorWidth;
        mIndicator.requestLayout();

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtil.i("qd", "position===" + position + "  positionOffset=" + positionOffset + "  positionOffsetPixels=" + positionOffsetPixels);
                scrollIndicator(position, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                changeSelectedIndicator(position == 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Intent intent = getIntent();
        int what = intent.getIntExtra(Keys.WHAT, -1);

        //从splah页面进入了，MainActivity页面
        if(what == -1){
            //初始化，选择音频
            changeSelectedIndicator(true);
            mViewPager.setCurrentItem(0);
        }else{
            //从音乐播放页面，返回来的
            mViewPager.setCurrentItem(1);
        }

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_audio:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.music:
                mViewPager.setCurrentItem(1);
                break;

        }
    }

    /**
     * 滑动指示线
     * @param position
     * @param positionOffsetPixels
     */
    protected void scrollIndicator(int position, int positionOffsetPixels) {
        int translationX = mIndicatorWidth * position + positionOffsetPixels / pageSize ;
        LogUtil.i("qd","translationX =="+translationX );
        ViewHelper.setTranslationX(mIndicator, translationX);

    }

    /**
     * 选择：音频 / 音乐
     * @param isAudio 是选择了音频
     */
    public void changeSelectedIndicator(boolean isAudio){

        mAudio.setSelected(isAudio);
        mMusic.setSelected(!isAudio);

        float ascale = isAudio ? 1.2f : 1.0f;
        float mscale = isAudio ? 1.0f : 1.2f;


        ViewPropertyAnimator.animate(mAudio).scaleX(ascale);
        ViewPropertyAnimator.animate(mAudio).scaleY(ascale);

        ViewPropertyAnimator.animate(mMusic).scaleX(mscale);
        ViewPropertyAnimator.animate(mMusic).scaleY(mscale);
    }
}

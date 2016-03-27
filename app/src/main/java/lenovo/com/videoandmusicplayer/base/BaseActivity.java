package lenovo.com.videoandmusicplayer.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import butterknife.ButterKnife;
import lenovo.com.videoandmusicplayer.AppManager;
import lenovo.com.videoandmusicplayer.interfaces.BaseInterface;

/**
 * Created by Administrator on 2016/3/21.
 */
public class BaseActivity extends FragmentActivity implements View.OnClickListener,BaseInterface{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);
        initView();
        initData();
        initListener();
    }

    protected void initListener() {
    }

    public int getLayoutId(){
        return 0;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }
}

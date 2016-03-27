package lenovo.com.videoandmusicplayer.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import lenovo.com.videoandmusicplayer.AppContext;
import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.utils.LogUtil;
import lenovo.com.videoandmusicplayer.utils.UIHelper;

/**
 * Created by Administrator on 2016/3/21.
 */
public class Splash extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View bg =  findViewById(R.id.splash_bg);
        AlphaAnimation animation = new AlphaAnimation(0.5f, 1.0f);
        animation.setDuration(100);
        animation.setFillAfter(true);
        bg.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                LogUtil.i("qd", "结束");
                UIHelper.startMainActivity(Splash.this);
                finish();
        }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}

package lenovo.com.videoandmusicplayer.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import lenovo.com.videoandmusicplayer.R;

/**
 * Created by Administrator on 2016/3/22.
 */
public class VisitNetWorkActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_network);
    }


    public void onClick(View v) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.setDataAndType(Uri.parse("http://192.168.155.1:8080/demo1.mp4"), "video/*");
        startActivity(intent);
    }
}

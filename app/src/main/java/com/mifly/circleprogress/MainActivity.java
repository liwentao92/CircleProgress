package com.mifly.circleprogress;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mifly.circleprogress.view.CircleProgressButton;

public class MainActivity extends AppCompatActivity {

    CircleProgressButton mButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (CircleProgressButton) findViewById(R.id.button_1);
        mButton.setmPlayTime(10);
        mButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(!mButton.isPause()){

                    mButton.startCartoom();
                    mButton.setBackgroundResource(R.drawable.rp_pause);
                    Log.i("lwt","aaaaaaaaaaaa");
                }else {
                    mButton.pauseCartoom();
                    mButton.setBackgroundResource(R.drawable.rp_play);
                    Log.i("lwt","bbbbbbbb");
                }
            }
        });

        mButton.setOnCompletedListener(new CircleProgressButton.OnCompletedListener() {
            @Override
            public void OnCompleted(CircleProgressButton progressButton, boolean finishflag) {
                mButton.setBackgroundResource(R.drawable.rp_play);
            }
        });
    }
}

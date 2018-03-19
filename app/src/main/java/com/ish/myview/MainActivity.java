package com.ish.myview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ish.mylibrary.mView.ResultView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button startBtn,resetBtn;
    ResultView resultView1;
    ResultView resultView2;
    ResultView resultView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = (Button)findViewById(R.id.start_btn);
        resetBtn = (Button)findViewById(R.id.reset_btn);
        startBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);

        resultView1 = (ResultView)findViewById(R.id.my_view1);
        resultView1.setDuration(1000);
        resultView1.setType(ResultView.TYPE_FAILD);

        resultView2 = (ResultView)findViewById(R.id.my_view2);
        resultView2.setDuration(2000);
        resultView2.setType(ResultView.TYPE_SUCESS);

        resultView3 = (ResultView)findViewById(R.id.my_view3);
        resultView3.setDuration(3000);
        resultView3.setType(ResultView.TYPE_WARN);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_btn:
                resultView1.play();
                resultView2.play();
                resultView3.play();
                break;
            case R.id.reset_btn:
                resultView1.reset();
                resultView2.reset();
                resultView3.reset();
                break;
            default:
        }
    }
}

package com.example.hp.muscle.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.hp.muscle.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hp on 2016/7/21.
 */
public class UpDown extends LinearLayout {
    @InjectView(R.id.button_up)
    ImageButton up;
    @InjectView(R.id.num)
    EditText numView;

    @InjectView(R.id.button_down)
    ImageButton down;
    int num;

    public UpDown(Context context, AttributeSet attrs) {

        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.up_down, this);
        ButterKnife.inject(this);

        this.num = Integer.parseInt(numView.getText().toString());
        up.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
                if (true) {
                    num++;
                    setNumView();
                }
            }
        });
        down.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                init();
                if (num > 1) {
                    num--;
                    setNumView();
                }
            }
        });


    }


    public void setNumView() {

        numView.setText(String.valueOf(num));
    }

    public int getNum() {
        if (numView.getText().toString() != null) {
            return Integer.parseInt(numView.getText().toString());
        } else {
            return 0;
        }
    }

    public void init() {
        num = getNum();
    }

}

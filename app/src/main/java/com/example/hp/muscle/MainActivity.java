package com.example.hp.muscle;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.hp.muscle.helper.ChartDemoFragment;
import com.example.hp.muscle.helper.FeedbackFragment;
import com.example.hp.muscle.helper.ParameterFragment;
import com.example.hp.muscle.helper.RightFragment;
import com.example.hp.muscle.helper.StimulateFragment;
import com.example.hp.muscle.helper.SchemeChooseFragment;


import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity  {
    //定义对应左侧六个菜单的碎片
    private RightFragment fragment1;
    private ChartDemoFragment fragment2;
    private FeedbackFragment fragment3;
    private SchemeChooseFragment fragment4;
    private StimulateFragment fragment5;
    private ParameterFragment fragment6;


    //定义碎片
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    //定义6个按键
    @InjectView(R.id.patientInf)
    RadioButton patientInf;
    @InjectView(R.id.Evaluation)
    RadioButton Evaluation;
    @InjectView(R.id.myoelectrictyfeedback)
    RadioButton Feedback;
    @InjectView(R.id.scheme)
    RadioButton Scheme;
    @InjectView(R.id.simulate)
    RadioButton Simulate;
    @InjectView(R.id.parameter)
    RadioButton Parameter;
    @InjectView(R.id.fragment)
    RadioGroup fragment;

    protected void onCreate(Bundle savedInstanceState) {
        //显示布局
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //使用 Butter Knife
        ButterKnife.inject(this);// 使用butterknife需要加入这一语句

        //显示患者查询碎片
        this.fragmentManager = getFragmentManager();
        this.transaction = fragmentManager.beginTransaction();
        this.fragment1 = new RightFragment();
        transaction.replace(R.id.right_layout, fragment1);
        transaction.commit();

        //按键处理
        fragment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int i) {
                transaction = fragmentManager.beginTransaction();
                switch (i) {
                    case R.id.patientInf:
                    if (fragment1 == null) {
                       fragment1 = new RightFragment();
                    }
                    transaction.replace(R.id.right_layout, fragment1);
                    transaction.commit();
                    break;

                    case R.id.Evaluation:
                        if (fragment2 == null) {
                            fragment2 = new ChartDemoFragment();
                        }
                        transaction.replace(R.id.right_layout, fragment2);
                        transaction.commit();
                        break;

                    case R.id.myoelectrictyfeedback:
                        if (fragment3 == null) {
                            fragment3 = new FeedbackFragment();
                        }
                        transaction.replace(R.id.right_layout, fragment3);
                        transaction.commit();
                        break;

                    case R.id.scheme:
                        if (fragment4 == null) {
                            fragment4 = new SchemeChooseFragment();
                        }
                        transaction.replace(R.id.right_layout, fragment4);
                        transaction.commit();
                        break;

                    case R.id.simulate:
                        if (fragment5 == null) {
                            fragment5 = new StimulateFragment();
                        }
                        transaction.replace(R.id.right_layout, fragment5);
                        transaction.commit();
                        break;

                    case R.id.parameter:
                        if (fragment6 == null) {
                            fragment6=new ParameterFragment();
                        }
                        transaction.replace(R.id.right_layout, fragment6);
                        transaction.commit();
                        break;

                    default:
                        break;

                }
            }
        });
    }


}

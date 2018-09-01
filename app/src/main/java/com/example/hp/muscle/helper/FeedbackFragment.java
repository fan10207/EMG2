package com.example.hp.muscle.helper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.hp.muscle.R;

/**
 * Created by hp on 2016/7/21.
 */
public class FeedbackFragment extends Fragment implements View.OnClickListener {

    private ImageView GameOne, GameTwo, GameThree, GameFour;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.feedback_layout, container, false);
        return view;
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GameOne = (ImageView) getActivity().findViewById(R.id.game_one);
        GameTwo = (ImageView) getActivity().findViewById(R.id.game_two);
        GameThree = (ImageView) getActivity().findViewById(R.id.game_three);
        GameFour = (ImageView) getActivity().findViewById(R.id.game_four);
        GameOne.setOnClickListener(this);
        GameTwo.setOnClickListener(this);
        GameThree.setOnClickListener(this);
        GameFour.setOnClickListener(this);


    }

    //点击事件
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.game_one:
                Toast.makeText(getActivity(), "温馨提示：游戏未上线", Toast.LENGTH_SHORT).show();
                break;

            case R.id.game_two:
                Toast.makeText(getActivity(), "温馨提示：游戏未上线", Toast.LENGTH_SHORT).show();
                break;

            case R.id.game_three:
                Toast.makeText(getActivity(), "温馨提示：游戏未上线", Toast.LENGTH_SHORT).show();
                break;

            case R.id.game_four:
                Toast.makeText(getActivity(), "温馨提示：游戏未上线", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }

    }
}
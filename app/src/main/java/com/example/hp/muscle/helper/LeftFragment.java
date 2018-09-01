package com.example.hp.muscle.helper;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.hp.muscle.R;

import butterknife.InjectView;
/**
 * Created by hp on 2016/7/14.
 */

public class LeftFragment extends Fragment{
    @InjectView(R.id.left_fragment)
     LinearLayout leftfragment;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view = inflater.inflate(R.layout.left_fragment, container, false);
        return view;
    }
}

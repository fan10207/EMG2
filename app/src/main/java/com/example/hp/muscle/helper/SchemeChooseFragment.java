package com.example.hp.muscle.helper;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hp.muscle.MainActivity;
import com.example.hp.muscle.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by hp on 2016/9/27.
 */
public class SchemeChooseFragment extends Fragment {

    private String pur, ind, method, pos;
    private int PositionNum = 0;

    @InjectView(R.id.brain) //脑卒中
    RadioButton brain;
    @InjectView(R.id.neck) //颈椎病
    RadioButton neck;
    @InjectView(R.id.waist) //下腰痛
    RadioButton waist;
    @InjectView(R.id.diaphragm) //膈肌
    RadioButton diaphragm;
    @InjectView(R.id.purpose)
    TextView purpose;
    @InjectView(R.id.testmethod)
    TextView testmethod;
    @InjectView(R.id.index)
    TextView index;
    @InjectView(R.id.position)
    TextView position;
    @InjectView(R.id.symlist) //列表
    ListView symlist;
    @InjectView(R.id.bodydemo1) //图片1
    ImageView bodydemo1;
    @InjectView(R.id.bodydemo2) //图片2
    ImageView bodydemo2;
 /*   private Drawable[] bodyPic={getResources().getDrawable(R.drawable.image1),getResources().getDrawable(R.drawable.image2),
            getResources().getDrawable(R.drawable.image3),getResources().getDrawable(R.drawable.image4)};*/


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.right_fragment, container, false);
        ButterKnife.inject(this,view);
        return view;
    }
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        RadioGroup sym_choose =(RadioGroup) getActivity().findViewById(R.id.sym_choose);
        sym_choose.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.brain:
                       /* ArrayAdapter<String> brainSym = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                                getResources().getStringArray(R.array.brain));*/
                        MyAdapter brainSym = new MyAdapter(getActivity(), R.layout.selector_item, getResources().getStringArray(R.array.brain));
                        symlist.setAdapter(brainSym);
                        symlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        symlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                            setDemo(1,position);
                            }
                        });
                        break;
                    case R.id.neck:
                        MyAdapter neckSym = new MyAdapter(getActivity(), R.layout.selector_item, getResources().getStringArray(R.array.neck));
                        symlist.setAdapter(neckSym);
                        symlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        symlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                              setDemo(2,position);
                            }
                        });
                        break;
                    case R.id.waist:
                        MyAdapter waistSym = new MyAdapter(getActivity(), R.layout.selector_item, getResources().getStringArray(R.array.waist));
                        symlist.setAdapter(waistSym);
                        symlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        symlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                                setDemo(3,position);
                            }
                        });
                        break;

                    case R.id.diaphragm:
                        MyAdapter diaphragmSym = new MyAdapter(getActivity(), R.layout.selector_item, getResources().getStringArray(R.array.diaphragm));
                        symlist.setAdapter(diaphragmSym);
                        symlist.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        symlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                            public void onItemClick(AdapterView<?> parent,View view, int position, long id) {
                                setDemo(4,position);
                            }
                        });
                        break;

                    default:
                        break;
                }
            }
        });

    }

    public class MyAdapter extends ArrayAdapter<String> {
        private int resourceId;

        public MyAdapter(Context context, int textViewResourceId, String[] object) {
            super(context, textViewResourceId, object);
            resourceId = textViewResourceId;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            String string = getItem(position);
            View view;
            ViewHolder viewHolder;
            if(convertView == null) {
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
                viewHolder = new ViewHolder();
                viewHolder.s = (TextView) view.findViewById(R.id.string_3);
                view.setTag(viewHolder); //将viewHolder存储在view中
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag(); // 重新获取ViewHolder;
            }
            viewHolder.s.setText(string);
            return view;
        }

        class ViewHolder {
            TextView s;
        }
    }


    /**
    a为三种主要症状对应的数组
     i为详细症状对应数组的位置
     */
    public void setDemo(int a,int i) {
        switch (a) {
            case 1:
                pur = getResources().getStringArray(R.array.brainpurpose)[i];
                ind = getResources().getStringArray(R.array.brainindex)[i];
                method = getResources().getStringArray(R.array.brainmethod)[i];
                pos = getResources().getStringArray(R.array.brainposition)[i];
                break;

            case 2:
                pur = getResources().getStringArray(R.array.brainpurpose)[i];
                ind = getResources().getStringArray(R.array.brainindex)[i];
                method = getResources().getStringArray(R.array.brainmethod)[i];
                pos = getResources().getStringArray(R.array.brainposition)[i];
                break;

            case 3:
                pur = getResources().getStringArray(R.array.brainpurpose)[i];
                ind = getResources().getStringArray(R.array.brainindex)[i];
                method = getResources().getStringArray(R.array.brainmethod)[i];
                pos = getResources().getStringArray(R.array.brainposition)[i];
                break;

            case 4:
                pur = getResources().getStringArray(R.array.diaphragmpurpose)[i];
                ind = getResources().getStringArray(R.array.diaphragmindex)[i];
                method = getResources().getStringArray(R.array.diaphragmmethod)[i];
                pos = getResources().getStringArray(R.array.diaphragmposition)[i];
                break;

            default:
                break;
        }
        purpose.setText(pur);
        index.setText(ind);
        testmethod.setText(method);
        position.setText(pos);
        DrawableSelect(a,i);
    }

    //选择相应的图片进行显示
    private void DrawableSelect(int TitleNum, int num) {
        if(TitleNum == 1){
            switch (num) {
                case 0:
                case 1:
                case 2:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion12);
                    break;

                case 3:
                case 4:
                case 5:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion1);
                    break;

                case 6:
                case 7:
                case 8:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion11);
                    break;

                case 9:
                case 10:
                case 11:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion13);
                    break;

                case 12:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion12);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion13);
                    break;

                case 13:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion11);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion13);
                    break;

                case 14:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion1);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion13);
                    break;

                case 15:
                case 16:
                case 17:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion14);
                    break;

                case 18:
                case 19:
                case 20:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion15);
                    break;

                case 21:
                case 22:
                case 23:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion16);
                    break;

                case 24:
                case 25:
                case 26:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion17);
                    break;

                case 27:
                case 28:
                case 29:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion18);
                    break;

                case 30:
                case 31:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion14);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion15);
                    break;

                case 32:
                case 33:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion14);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion18);
                    break;

                case 34:
                case 35:
                case 36:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion9);
                    break;

                case 37:
                case 38:
                case 39:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion10);
                    break;

                case 40:
                case 41:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion9);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion10);
                    break;

                case 42:
                case 43:
                case 47:
                case 48:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion3);
                    break;

                case 44:
                case 45:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion6);
                    break;

                case 46:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion4);
                    break;

                case 49:
                case 50:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion7);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion8);
                    break;

                default:
                    break;
            }
        } else if(TitleNum == 2) {
            switch (num) {
                case 0:
                case 1:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion5);
                    break;

                case 2:
                case 3:
                case 5:
                    bodydemo2.setVisibility(View.GONE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion2);
                    break;

                case 4:
                    bodydemo2.setVisibility(View.VISIBLE);
                    bodydemo1.setBackgroundResource(R.drawable.electrodepostion2);
                    bodydemo2.setBackgroundResource(R.drawable.electrodepostion5);
                    break;

                default:
                    break;
            }
        } else if(TitleNum == 3) {
            bodydemo2.setVisibility(View.VISIBLE);
            bodydemo1.setBackgroundResource(R.drawable.electrodepostion7);
            bodydemo2.setBackgroundResource(R.drawable.electrodepostion8);
        } else if(TitleNum == 4) {
            bodydemo2.setVisibility(View.VISIBLE);
            bodydemo1.setBackgroundResource(R.drawable.electrodepostion1);
            bodydemo2.setBackgroundResource(R.drawable.electrodepostion2);
        }
    }
}

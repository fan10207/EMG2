package com.example.hp.muscle.helper.bluetooth;

import android.app.Activity;
import android.app.Application;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by hp on 2016/7/28.
 */
public class SysApplication extends Application {
    private static List<Activity> mList = new LinkedList<>();
    private static SysApplication application;

    public static SysApplication getInstance() {
        return application;
    }

    public void onCreate() {
        super.onCreate();
        application = this;
    }

    public void onTerminate() {
        super.onTerminate();
        exit();
    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public static void exit() {
        try {
            for (Activity activity : mList) {
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

}


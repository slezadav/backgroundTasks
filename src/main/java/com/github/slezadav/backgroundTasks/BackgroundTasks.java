package com.github.slezadav.backgroundTasks;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Created by Davo on 12.6.2015.
 */
public class BackgroundTasks {


    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> void startTask(T activity,Object tag, BaseTask task, Object... params) {
        task.setExecType(BaseTask.ExecutionType.ASYNCTASK);
        getFragment(activity).startTask(tag, task, params);
    }


    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> void startTask(T activity,Object tag, BaseTask task, BaseTask.ExecutionType eType, Object... params) {
        task.setExecType(eType);
        getFragment(activity).startTask(tag, task, params);
    }

    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> void startTaskChain(T activity,TaskChain chain) {
        for (BaseTask task : chain.tasks) {
            task.setExecType(BaseTask.ExecutionType.ASYNCTASK);
        }
        getFragment(activity).startTaskChain(chain);
    }

    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> void startTaskChain(T activity,TaskChain chain, BaseTask.ExecutionType eType) {
        for (BaseTask task : chain.tasks) {
            task.setExecType(eType);
        }
        getFragment(activity).startTaskChain(chain);
    }

    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> void cancelTask(T activity,Object tag) {
        getFragment(activity).cancelTask(tag);
    }

    public static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> boolean isTaskInProgress(T activity,Object tag) {
        return getFragment(activity).isTaskInProgress(tag);
    }


    private static <T extends FragmentActivity & BaseTask.IBaseTaskCallbacks> TaskFragment  getFragment(T activity){
        FragmentActivity fragmentActivity= (FragmentActivity) activity;
        FragmentManager fm = fragmentActivity.getSupportFragmentManager();
        TaskFragment fragment =(TaskFragment) fm.findFragmentByTag(TaskFragment.TASK_FRAGMENT_TAG);
        if(fragment==null){
            fragment = new TaskFragment();
            fm.beginTransaction().add(fragment, TaskFragment.TASK_FRAGMENT_TAG).commit();
        }
        return fragment;
    }

}

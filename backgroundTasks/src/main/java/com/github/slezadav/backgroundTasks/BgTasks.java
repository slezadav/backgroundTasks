package com.github.slezadav.backgroundTasks;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.concurrent.Executor;

/**
 * Class used as static entry point to the library.
 *
 * Created by Davo on 12.6.2015.
 */
public class BgTasks {
    /**
     * Method that starts the task from activity
     *
     * @param activity activity to which the callbacks should be delivered
     * @param tag      tag by which the task is identified
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity, Object tag,
                                                                                 BaseTask task, Object... params) {
        getFragment(activity).startTask(tag, task, params);
    }

    /**
     * Method that starts the task from activity. The task will run on supplied executor
     *
     * @param activity activity to which the callbacks should be delivered
     * @param executor executor to be used
     * @param tag      tag by which the task is identified
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,Executor executor, Object tag,
                                                                                       BaseTask task, Object... params) {
        task.setExecutor(executor);
        getFragment(activity).startTask(tag, task, params);
    }

    /**
     * Method that starts the task from fragment. The task will run on supplied executor
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param executor executor to be used
     * @param tag      tag by which the task is identified
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,Executor executor, Object tag,
                                                                                       BaseTask task, Object... params) {
        task.setExecutor(executor);
        getFragment(fragment).startTask(tag, task, params);
    }

    /**
     * Method that starts the task from fragment
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param tag      tag by which the task is identified
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment, Object tag, BaseTask task,
                                                                         Object... params) {
        Object id=getIdTagOrIdFromFragment(fragment);
        task.setCallbacksId(id);
        getFragment(fragment).startTask(tag, task, params);
    }

    /**
     * Method that starts the task chain
     *
     * @param activity activity to which the callbacks should be delivered
     * @param chain    chain to be started
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTaskChain(T activity, TaskChain
            chain) {
        if (chain.tasks.isEmpty()) {
            throw new IllegalStateException("Cannot execute empty chain");
        }
        getFragment(activity).startTaskChain(chain);
    }

    /**
     * Method that starts the task chain
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param chain    chain to be started
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTaskChain(T fragment, TaskChain chain) {
        Object id=getIdTagOrIdFromFragment(fragment);
        for (BaseTask task : chain.tasks) {
            task.setCallbacksId(id);
        }
        if (chain.tasks.isEmpty()) {
            throw new IllegalStateException("Cannot execute empty chain");
        }
        getFragment(fragment).startTaskChain(chain);
    }

    /**
     * Method that cancels the execution of the task with given tag
     *
     * @param activity activity to which the callback should be delivered
     * @param tag      tag of the task to be cancelled
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void cancelTask(T activity, Object tag) {
        getFragment(activity).cancelTask(tag);
    }

    /**
     * Method that cancels the execution of the task with given tag
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param tag      tag of the task to be cancelled
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void cancelTask(T fragment, Object tag) {
        getFragment(fragment).cancelTask(tag);
    }

    /**
     * Method which determines whether the task is running
     *
     * @param activity current activity
     * @param tag      tag of the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     * @return true if the task with given tag is in progress
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T activity, Object
            tag) {
        return getFragment(activity).isTaskInProgress(tag,true);
    }

    /**
     * Method which determines whether the task is running
     *
     * @param fragment current fragment
     * @param tag      tag of the task
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     * @return true if the task with given tag is in progress
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T fragment, Object tag) {
        return getFragment(fragment).isTaskInProgress(tag,true);
    }

    /**
     * Method to find the fragment used to execute background tasks
     *
     * @param activity current activity
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     * @return TaskFragment instance for performing background tasks
     */
    private static <T extends FragmentActivity & IBgTaskSimpleCallbacks> TaskFragment getFragment(T activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        TaskFragment fragment = (TaskFragment) fm.findFragmentByTag(TaskFragment.TASK_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new TaskFragment();
            fm.beginTransaction().add(fragment, TaskFragment.TASK_FRAGMENT_TAG).commit();
            fm.executePendingTransactions();
        }
        return fragment;
    }

    /**
     * Method to find the fragment used to execute background tasks
     *
     * @param fr  current fragment
     * @param <T> Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     * @return TaskFragment instance for performing background tasks
     */
    private static <T extends Fragment & IBgTaskSimpleCallbacks> TaskFragment getFragment(T fr) {
        FragmentManager fm = fr.getActivity().getSupportFragmentManager();
        TaskFragment fragment = (TaskFragment) fm.findFragmentByTag(TaskFragment.TASK_FRAGMENT_TAG);
        if (fragment == null) {
            fragment = new TaskFragment();
            fm.beginTransaction().add(fragment, TaskFragment.TASK_FRAGMENT_TAG).commit();
            fm.executePendingTransactions();
        }
        return fragment;
    }

    /**
     * Method which gets fragment identifier
     * @param fragment fragment to be identified
     * @return tag or id of the fragment
     */
    private static Object getIdTagOrIdFromFragment(Fragment fragment){
        String ftag=fragment.getTag();
        Integer id=fragment.getId();
        if(ftag!=null){
            return ftag;
        }else if(id!=-1){
            return id;
        }else{
            throw new RuntimeException("Fragment did not specify tag or id");
        }
    }
}

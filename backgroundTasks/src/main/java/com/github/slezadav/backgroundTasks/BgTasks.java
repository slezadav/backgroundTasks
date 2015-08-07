package com.github.slezadav.backgroundTasks;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.github.slezadav.backgroundTasks.BaseTask.CallbackType;

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
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,
                                                                                       BaseTask task, Object...
                                                                                               params) {
        task.setCallbackType(CallbackType.ACTIVITY);
        getFragment(activity).startTask(task, params);
    }


    /**
     * Method that starts the task from fragment
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,BaseTask
            task, Object... params) {
        Object id = getIdTagOrIdFromFragment(fragment);
        task.setCallbacksId(id);
        task.setCallbackType(CallbackType.FRAGMENT);
        getFragment(fragment).startTask(task, params);
    }

    /**
     * Method that starts the task from view
     *
     * @param view   view in which to return callbacks
     * @param task   task to be started
     * @param params optional params for the task
     * @param <T>    Must extend View implement IBgTaskCallbacks
     */
    @SuppressWarnings("unchecked")
    public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view,  BaseTask task,
                                                                           Object... params) {
        task.setCallbackType(CallbackType.VIEW);
        Object id = getIdTagOrIdFromView(view);
        task.setCallbacksId(id);
        getFragment(view).startTask(task, params);
    }

    /**
     * Method that starts the task from activity. The task will run on supplied executor
     *
     * @param activity activity to which the callbacks should be delivered
     * @param executor executor to be used
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity, Executor
            executor,  BaseTask task, Object... params) {
        task.setExecutor(executor);
        startTask(activity, task, params);
    }

    /**
     * Method that starts the task from fragment. The task will run on supplied executor
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param executor executor to be used
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment, Executor executor,
                                                                               BaseTask task,
                                                                               Object... params) {
        task.setExecutor(executor);
        startTask(fragment, task, params);
    }

    /**
     * Method that starts the task from view
     *
     * @param view     view in which to return callbacks
     * @param executor executor to be used
     * @param task     task to be started
     * @param params   optional params for the task
     * @param <T>      Must extend View implement IBgTaskCallbacks
     */
    @SuppressWarnings("unchecked")
    public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view, Executor executor, BaseTask task, Object... params) {
        task.setExecutor(executor);
        startTask(view, task, params);
    }

    /**
     * Method that starts the task from activity
     *
     * @param activity activity to which the callbacks should be delivered
     * @param chain    chain to be started
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,
                                                                                       BgTaskChain chain) {
        chain.run(activity);
    }


    /**
     * Method that starts the task from fragment
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param chain    chain to be started
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,BgTaskChain chain) {
        chain.run(fragment);
    }

    /**
     * Method that starts the task from view
     *
     * @param view   view in which to return callbacks
     * @param chain    chain to be started
     * @param <T>    Must extend View implement IBgTaskCallbacks
     */
    @SuppressWarnings("unchecked")
    public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view, BgTaskChain chain) {
       chain.run(view);
    }

    /**
     * Method that cancels the execution of the task with given tag
     *
     * @param activity activity to which the callback should be delivered
     * @param tag      tag task to be cancelled
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void cancelTask(T activity,String tag) {
        getFragment(activity).cancelTask(tag);
    }

    /**
     * Method that cancels the execution of the task with given tag
     *
     * @param fragment fragment to which the callbacks should be delivered
     * @param tag     tag to be cancelled
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> void cancelTask(T fragment,  String tag) {
        getFragment(fragment).cancelTask(tag);
    }

    /**
     * Method that cancels the execution of the task with given tag
     *
     * @param view view to which the callbacks should be delivered
     * @param tag tag to be cancelled
     * @param <T>  Must extend View and implement IBgTaskCallbacks
     */
    public static <T extends View & IBgTaskSimpleCallbacks> void cancelTask(T view,  String tag) {
        getFragment(view).cancelChain(tag);
    }


    /**
     * Method which determines whether the task is running
     *
     * @param activity current activity
     * @param tag     tag of task
     * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
     * @return true if the task with given tag is in progress
     */
    public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T activity,
                                                                                                 String tag) {
        return getFragment(activity).isTaskInProgress(tag);
    }

    /**
     * Method which determines whether the task is running
     *
     * @param fragment current fragment
     * @param tag     tag of task
     * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
     * @return true if the task with given tag is in progress
     */
    public static <T extends Fragment & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T fragment, String tag) {
        return getFragment(fragment).isTaskInProgress(tag);
    }

    /**
     * Method which determines whether the task is running
     *
     * @param view view in which the task runs
     * @param tag     tag of task
     * @param <T>  Must extend View and implement IBgTaskCallbacks
     * @return true if the task with given tag is in progress
     */
    public static <T extends View & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T view, String tag) {
        return getFragment(view).isTaskInProgress(tag);
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
            fm.beginTransaction().add(fragment, TaskFragment.TASK_FRAGMENT_TAG).commitAllowingStateLoss();
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
     * Method to find the fragment used to execute background tasks
     *
     * @param view view designated to run tasks
     * @param <T>  Must extend android.support.v4.app.Fragment
     * @return TaskFragment instance for performing background tasks
     */
    private static <T extends View & IBgTaskSimpleCallbacks> TaskFragment getFragment(T view) {
        Context context = view.getContext();
        if (context instanceof FragmentActivity) {
            FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
            TaskFragment fragment = (TaskFragment) fm.findFragmentByTag(TaskFragment.TASK_FRAGMENT_TAG);
            if (fragment == null) {
                fragment = new TaskFragment();
                fm.beginTransaction().add(fragment, TaskFragment.TASK_FRAGMENT_TAG).commit();
                fm.executePendingTransactions();
            }
            return fragment;
        } else {
            throw new RuntimeException("View must be hosted by FragmentActivity");
        }
    }

    /**
     * Method which gets fragment identifier
     *
     * @param fragment fragment to be identified
     * @return tag or id of the fragment
     */
    private static Object getIdTagOrIdFromFragment(Fragment fragment) {
        String ftag = fragment.getTag();
        Integer id = fragment.getId();
        if (ftag != null) {
            return ftag;
        } else if (id != -1) {
            return id;
        } else {
            throw new RuntimeException("Fragment did not specify tag or id");
        }
    }

    /**
     * Method which gets view identifier
     *
     * @param view view to be identified
     * @return tag or id of the view
     */
    private static Object getIdTagOrIdFromView(View view) {
        if (view.getTag() != null) {
            return view.getTag();
        } else if (view.getId() != View.NO_ID) {
            return view.getId();
        } else {
            throw new RuntimeException("View must have a valid id or string tag");
        }
    }

    protected static <V extends View & IBgTaskSimpleCallbacks, F extends Fragment & IBgTaskSimpleCallbacks, A
            extends FragmentActivity & IBgTaskSimpleCallbacks> void startFollowingTask(IBgTaskSimpleCallbacks callbacks,BaseTask task, Object params) {
        if (callbacks instanceof FragmentActivity) {
            task.setCallbackType(CallbackType.ACTIVITY);
            getFragment((A) callbacks).startTask(task, params);
        }
        if (callbacks instanceof Fragment) {
            F fragment = (F) callbacks;
            Object id = getIdTagOrIdFromFragment(fragment);
            task.setCallbacksId(id);
            task.setCallbackType(CallbackType.FRAGMENT);
            getFragment(fragment).startTask(task, params);
            task.setCallbackType(CallbackType.FRAGMENT);
        }
        if (callbacks instanceof View) {
            V view = (V) callbacks;
            task.setCallbackType(CallbackType.VIEW);
            Object id = getIdTagOrIdFromView(view);
            task.setCallbacksId(id);
            getFragment(view).startTask( task, params);
        }
    }
}

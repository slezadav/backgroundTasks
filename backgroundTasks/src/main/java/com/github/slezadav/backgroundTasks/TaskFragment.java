package com.github.slezadav.backgroundTasks;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.github.slezadav.backgroundTasks.BaseTask.CallbackType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * A retained fragment managing the task processing.
 *
 * Created by david.slezak on 9.6.2015.
 */
public class TaskFragment extends Fragment {
    /**
     * Tag used for this fragment
     */
    protected final static String TASK_FRAGMENT_TAG = "com.github.slezadav.backgroundTasks.TaskFragment";
    /**
     * Logging tag
     */
    private final static String TAG = "backgroundTasks";
    /**
     * Map of tasks and params
     */
    private HashMap<BaseTask, Object[]> mTasks = new HashMap<>();
    /**
     * Map of results which could not yet be returned
     */
    private HashMap<BaseTask, Object> mUnresolvedResults = new HashMap<>();

    /**
     * This method will only be called once when the retained Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Overrides parent method, starts unfinished tasks and resolves unreturned results
     *
     * @param savedInstanceState savedInstance bundle
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        startUnfinishedTasks();
        resolveUnresolvedResults();
    }

    /**
     * Tries to find the fragment used as callback place
     *
     * @param id fragment id
     * @return Fragment which should be used for callbacks
     */
    @Nullable
    private IBgTaskSimpleCallbacks findFragmentByTagOrId(Object id) {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = null;
        if (id instanceof String) {
            fragment = fm.findFragmentByTag((String) id);
        } else if (id instanceof Integer) {
            fragment = fm.findFragmentById((Integer) id);
        }
        if (fragment != null) {
            return (IBgTaskSimpleCallbacks) fragment;
        }
        return null;
    }

    /**
     * Tries to find the fragment used as callback place
     *
     * @param id fragment id
     * @return Fragment which should be used for callbacks
     */
    @Nullable
    private IBgTaskSimpleCallbacks findViewByTagOrId(Object id) {
        Activity activity = getActivity();
        View view;
        View root = activity.findViewById(android.R.id.content).getRootView();
        view = root.findViewWithTag(id);
        if (view == null && Integer.class.isAssignableFrom(id.getClass())) {
            view = activity.findViewById((Integer) id);
        }
        if (view != null && IBgTaskSimpleCallbacks.class.isAssignableFrom(view.getClass())) {
            return (IBgTaskSimpleCallbacks) view;
        }
        return null;
    }

    /**
     * Starts the task
     *
     * @param task   task to be started
     * @param params optional params passed to task
     */
    protected void startTask(BaseTask task, Object... params) {
        prepareTask(task, params);
        if (task.isReady()) {
            executeTask(task, params);
        }
    }

    /**
     * Executes the task using executor on api>Honeycomb
     *
     * @param task   task to be executed
     * @param params params passed to the task
     */
    private void executeTask(BaseTask task, Object... params) {
        task.executeWithCustomExecutor(params);
    }

    /**
     * Finds the task by its tag
     *
     * @param tag tag searched for
     * @return Task with given tag
     */
    @Nullable
    private ArrayList<BaseTask> getTaskByTag(String tag) {
        ArrayList<BaseTask> tasks = null;
        for (BaseTask task : mTasks.keySet()) {
            if (tag!=null&&task.getTag().equals(tag)) {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Finds the task by its tag
     *
     * @param tag tag searched for
     * @return Task with given tag
     */
    @Nullable
    private ArrayList<BaseTask> getTaskByChainTag(String tag) {
        ArrayList<BaseTask> tasks = null;
        for (BaseTask task : mTasks.keySet()) {
            if (tag!=null&&tag.equals(task.getChainTag())) {
                if (tasks == null) {
                    tasks = new ArrayList<>();
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    /**
     * Finds out if the task is in progress
     *
     * @param tag tag of task
     * @return true if the task is in progress
     */
    protected boolean isTaskInProgress(String tag) {
        return getTaskByTag(tag) != null || getTaskByChainTag(tag) != null;
    }

    /**
     * Cancels task in progress
     *
     * @param tag task to be cancelled
     */
    protected void cancelTask(String tag) {
        ArrayList<BaseTask> tasks = getTaskByTag(tag);
        if (tasks != null) {
            for (BaseTask t : tasks) {
                t.cancel(true);
                mTasks.remove(t);
            }
        }
        cancelChain(tag);
    }

    /**
     * Cancels chain in progress
     *
     * @param tag tag of chain to be cancelled
     */
    protected void cancelChain(String tag) {
        ArrayList<BaseTask> tasks = getTaskByChainTag(tag);
        if (tasks != null) {
            for (BaseTask t : tasks) {
                t.cancel(true);
                mTasks.remove(t);
            }
        }
    }

    /**
     * Starts the tasks that are scheduled to be started
     */
    private void startUnfinishedTasks() {
        for (BaseTask task : mTasks.keySet()) {
            reassignCallbacks(task);
            if (!task.isReady()) {
                task.setReady(true);
                executeTask(task, mTasks.get(task));
            }
        }
    }

    /**
     * Prepares the task for execution
     *
     * @param task   task to be prepared
     * @param params params for the task
     */
    protected void prepareTask(BaseTask task, Object... params) {
        task.setEnclosingFragment(this);
        reassignCallbacks(task);
        if (task.getCallbacks() != null) {
            task.setReady(true);
        }
        mTasks.put(task, params);
    }

    /**
     * Removes the task from the list of tasks
     *
     * @param task task to be removed
     */
    protected void completeTask(BaseTask task) {
        for (BaseTask t : mTasks.keySet()) {
            if (t.equals(task)) {
                mTasks.remove(task);
                break;
            }
        }
    }

    /**
     * Reassigns callbacks to task
     *
     * @param task task whose callbacks are to be reassigned
     */
    private void reassignCallbacks(BaseTask task) {
        Object callbackId = task.getCallbacksId();
        IBgTaskSimpleCallbacks callbacks = null;
        if (task.getCallbackType() == CallbackType.FRAGMENT && callbackId != null && getActivity() != null) {
            callbacks = findFragmentByTagOrId(callbackId);
        } else if (task.getCallbackType() == CallbackType.ACTIVITY && getActivity() != null) {
            callbacks = (IBgTaskSimpleCallbacks) getActivity();
        } else if (task.getCallbackType() == CallbackType.VIEW && callbackId != null && getActivity() != null) {
            callbacks = findViewByTagOrId(callbackId);
        }
        if (callbacks != null) {
            task.setCallbacks(callbacks);
        }
    }

    /**
     * Called when the result could not be returned
     *
     * @param task   task whose result could not be returned
     * @param result result that could not be returned
     */
    public void onUnresolvedResult(BaseTask task, Object result) {
        mUnresolvedResults.put(task, result);
    }

    /**
     * Tries to return the results which could not be returned when their tasks finished
     */
    protected void resolveUnresolvedResults() {
        Iterator<Map.Entry<BaseTask, Object>> iterator = mUnresolvedResults.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<BaseTask, Object> setElement = iterator.next();
            BaseTask task = setElement.getKey();
            Object result = setElement.getValue();
            reassignCallbacks(task);
            handlePostExecute(task, result);
            iterator.remove();
        }
    }

    /**
     * Handles the task after it calls onPostExecute
     *
     * @param task      task
     * @param result    task result
     */
    protected void handlePostExecute(BaseTask task, Object result) {
        if (task.getCallbacks() != null) {
            if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                task.getCallbacks().onTaskFail(task, (Exception) result);
            } else {
                task.getCallbacks().onTaskSuccess(task, result);
                if (task.getFollowingTask() != null) {
                    BgTasks.startFollowingTask(task.getFollowingTask().getCallbacks(),
                            task.getFollowingTask(), result);
                }
            }
            completeTask(task);
        }
    }

    /**
     * Handles the progress publishing of the task
     *
     * @param task      task
     * @param progress  progress published by the task
     */
    protected void handleProgress(BaseTask task, Object... progress) {
        if (task.getCallbacks() == null) {
            return;
        }
        ((IBgTaskCallbacks)task.getCallbacks()).onTaskProgressUpdate(task, (Object[]) progress);
    }

    /**
     * Handles the pre execution of the task
     *
     * @param task      task
     */
    protected void handlePreExecute(BaseTask task) {
        if (task.getCallbacks() != null) {
            ((IBgTaskCallbacks)task.getCallbacks()).onTaskReady(task);
        }
    }

    /**
     * Handles the cancellation of the task
     *
     * @param callbacks callbacks where to notify cancellation
     * @param task      task
     */
    protected void handleCancel(IBgTaskSimpleCallbacks callbacks, BaseTask task, Object result) {
        if (callbacks != null && callbacks instanceof IBgTaskCallbacks) {
            ((IBgTaskCallbacks) callbacks).onTaskCancelled(task, result);
        }
    }
}

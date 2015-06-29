package com.github.slezadav.backgroundTasks;

import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


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
     * Map of task that are contained in chains and will run in the future
     */
    private HashMap<Object, FutureTask> mChainedTasks = new HashMap<>();

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
     * @param id fragment id
     * @return Fragment which should be used for callbacks
     */
    @Nullable
    private IBgTaskSimpleCallbacks findFragmentByTagOrId(Object id) {
        FragmentManager fm=getActivity().getSupportFragmentManager();
        Fragment fragment=null;
        if(id instanceof String){
            fragment=fm.findFragmentByTag((String) id);
        }else if (id instanceof Integer){
            fragment = fm.findFragmentById((Integer) id);
        }
        if(fragment!=null&&IBgTaskSimpleCallbacks.class.isAssignableFrom(fragment.getClass())){
            return (IBgTaskSimpleCallbacks) fragment;
        }
        return null;
    }

    /**
     * Starts the task
     * @param tag tag identified the task
     * @param task task to be started
     * @param params optional params passed to task
     */
    protected void startTask(Object tag, BaseTask task, Object... params) {
        prepareTask(tag, task, params);
        if (task.isReady()) {
            executeTask(task, params);
        }
    }

    /**
     * Executes the task using executor on api>Honeycomb
     * @param task task to be executed
     * @param params params passed to the task
     */
    private void executeTask(BaseTask task, Object... params) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(BaseTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }

    }

    /**
     * Finds the task by its tag
     * @param tag tag searched for
     * @return Task with given tag
     */
    @Nullable
    private BaseTask getTaskByTag(Object tag) {
        for (BaseTask task : mTasks.keySet()) {
            if (task.getTag().equals(tag)) {
                return task;
            }
        }
        return null;
    }

    /**
     * Finds out if the task is in progress
     * @param tag tag of the task
     * @param considerChains considerChains as tasks for this purpose
     * @return true if the task is in progress
     */
    protected boolean isTaskInProgress(Object tag,boolean considerChains) {
        boolean singleTask=getTaskByTag(tag) != null;
        return  singleTask || (considerChains&&isChainInProgress(tag));
    }

    /**
     * Cancels task in progress
     * @param tag tag of the task to be cancelled
     */
    protected void cancelTask(Object tag) {
        BaseTask task = getTaskByTag(tag);
        if (task != null) {
            task.cancel(true);
            mTasks.remove(task);
        }
        cancelChain(tag);
    }

    /**
     * Starts the tasks that are scheduled to be started
     */
    private void startUnfinishedTasks() {
        for (BaseTask task : mTasks.keySet()) {
            Object callbackId = task.getCallbacksId();
            IBgTaskSimpleCallbacks callbacks = null;
            if (callbackId != null) {
                callbacks = findFragmentByTagOrId(callbackId);
            } else if (getActivity() != null &&
                       IBgTaskSimpleCallbacks.class.isAssignableFrom(getActivity().getClass())) {
                callbacks = (IBgTaskSimpleCallbacks) getActivity();
            }
            task.setCallbacks(callbacks);
            if (!task.isReady()) {
                task.setReady(true);
                executeTask(task, mTasks.get(task));
            }
        }
    }

    /**
     * Prepares the task for execution
     * @param tag tag of the task
     * @param task task to be prepared
     * @param params params for the task
     */
    protected void prepareTask(Object tag, BaseTask task, Object... params) {
        if (isTaskInProgress(tag,false)) {
            Log.w(TAG, "Another instance of " + tag.toString() +
                       " already in progress");
            return;
        }
        task.setTag(tag);
        task.setEnclosingFragment(this);
        Object callbackId = task.getCallbacksId();
        IBgTaskSimpleCallbacks callbacks = null;
        if (callbackId != null) {
            callbacks = findFragmentByTagOrId(callbackId);
        } else if (getActivity() != null && IBgTaskSimpleCallbacks.class.isAssignableFrom(getActivity().getClass())) {
            callbacks = (IBgTaskSimpleCallbacks) getActivity();
        }
        if (callbacks != null) {
            task.setReady(true);
            task.setCallbacks(callbacks);
        }
        mTasks.put(task, params);
    }

    /**
     * Removes the task from the list of tasks
     * @param tag task to be removed
     */
    protected void completeTask(Object tag) {
        for (BaseTask task : mTasks.keySet()) {
            if (task.getTag().equals(tag)) {
                mTasks.remove(task);
                break;
            }
        }
    }

    /**
     * Called when the result could not be returned
     * @param task task whose result could not be returned
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
            Object tag = task.getTag();
            Object result = setElement.getValue();
            IBgTaskSimpleCallbacks callbacks = null;
            if (task.getCallbacksId() != null) {
                callbacks = findFragmentByTagOrId(task.getCallbacksId());
            } else if (IBgTaskSimpleCallbacks.class.isAssignableFrom(getActivity().getClass())) {
                callbacks = (IBgTaskSimpleCallbacks) getActivity();
            }
            handlePostExecute(callbacks, tag, result);
            iterator.remove();
        }
    }

    /**
     * Handles the task after it calls onPostExecute
     * @param callbacks callback where to return the result
     * @param tag task tag
     * @param result task result
     */
    protected void handlePostExecute(IBgTaskSimpleCallbacks callbacks, Object tag, Object result) {
        if (callbacks != null) {
            completeTask(tag);
            if (isTaskChained(tag)) {
                if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                    Object failingTag = removeChainResidue(tag);
                    callbacks.onTaskFail(failingTag, (Exception) result);
                } else {
                    continueChain(tag, result);
                    if(callbacks instanceof IBgTaskCallbacks){
                        ((IBgTaskCallbacks)callbacks).onTaskProgressUpdate(getChainFinalTag(tag), result);
                    }

                }
            } else {
                if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                    callbacks.onTaskFail(tag, (Exception) result);
                } else {
                    callbacks.onTaskSuccess(tag, result);
                }
            }
        }

    }

    /**
     * Handles the progress publishing of the task
     * @param callbacks callback where to publish
     * @param tag task tag
     * @param progress progress published by the task
     */
    protected void handleProgress(IBgTaskCallbacks callbacks, Object tag, Object... progress) {
        if (callbacks == null) {
            return;
        }
        if (isTaskChained(tag)) {
            callbacks.onTaskProgressUpdate(getChainFinalTag(tag), (Object[]) progress);
        } else {
            callbacks.onTaskProgressUpdate(tag, (Object[]) progress);
        }
    }

    /**
     * Handles the pre execution of the tag
     * @param callbacks callback where to return
     * @param tag task tag
     */
    protected void handlePreExecute(IBgTaskCallbacks callbacks, Object tag) {
        if (callbacks != null && !isTaskChained(tag)&& !isChainInProgress(tag)) {
            callbacks.onTaskReady(tag);
        }
    }

    /**
     * Handles the cancellation of the task
     * @param callbacks callbacks where to notify cancellation
     * @param tag tag task
     */
    protected void handleCancel(IBgTaskSimpleCallbacks callbacks, Object tag,Object result) {
        if (isTaskChained(tag)) {
            tag = removeChainResidue(tag);

        } else {
            cancelTask(tag);
        }
        if (callbacks != null && callbacks instanceof IBgTaskCallbacks) {
            ((IBgTaskCallbacks)callbacks).onTaskCancelled(tag,result);
        }
    }

    /**
     * Starts task chain
     * @param chain chain to be started
     */
    protected void startTaskChain(TaskChain chain) {
        if (isChainInProgress(chain.finalTag)) {
            Log.w(TAG, "Another instance of " + chain.finalTag.toString() +
                       " already in progress");
            return;
        }
        ArrayList<Object> tags = new ArrayList<>();
        for (int i = 0; i < chain.tasks.size() - 1; i++) {
            String chaintag = UUID.randomUUID().toString();
            tags.add(chaintag);
        }
        tags.add(chain.finalTag);
        for (int i = 1; i < chain.tasks.size(); i++) {
            FutureTask ft = new FutureTask(tags.get(i), chain.tasks.get(i), chain.useHistoryParam.get(i),
                    chain.params.get(i));
            mChainedTasks.put(tags.get(i - 1), ft);
        }
        startTask(tags.get(0), chain.tasks.get(0), (Object[]) chain.params.get(0));
    }

    /**
     * Continues chain by the last used tag
     * @param finishedTag finished partial task tag
     * @param result result of just finished partial task
     */
    private void continueChain(Object finishedTag, Object result) {
        FutureTask ft = mChainedTasks.get(finishedTag);
        if (ft.useHistoryParam) {
            ft.params.add(result);
        }
        startTask(ft.tag, ft.task, (Object[]) ft.getParams());
        mChainedTasks.remove(finishedTag);
    }

    /**
     * Cancels chain in progress
     * @param finalTag tag of the chain to cancel
     */
    private void cancelChain(Object finalTag) {
        for (Map.Entry<Object, FutureTask> ft : mChainedTasks.entrySet()) {
            FutureTask task = ft.getValue();
            if (task.tag.equals(finalTag)) {
                if (isTaskInProgress(ft.getKey(),false)) {
                    cancelTask(ft.getKey());
                }
                cancelChain(ft.getKey());
                return;
            }
        }
    }

    /**
     * Returns whether the task is chained
     * @param tag tag to be checked
     * @return true if the tag belongs to the task in chain
     */
    private boolean isTaskChained(Object tag) {
        return mChainedTasks.containsKey(tag);
    }

    /**
     * Removes the residues of the chain after fail or cancellation
     * @param failingTag tag of the task which failed
     * @return Chain's final tag
     */
    protected Object removeChainResidue(Object failingTag) {
        FutureTask ft;
        while (mChainedTasks.containsKey(failingTag)) {
            ft = mChainedTasks.get(failingTag);
            mChainedTasks.remove(failingTag);
            failingTag = ft.tag;
        }
        return failingTag;
    }

    /**
     * Gets the chain's final tag from any of its partial tags
     * @param partialTag partial tag of the chain
     * @return Chain's final tag
     */
    private Object getChainFinalTag(Object partialTag) {
        Object finalTag = null;
        FutureTask ft;
        while (mChainedTasks.containsKey(partialTag)) {
            ft = mChainedTasks.get(partialTag);
            partialTag = ft.tag;
            finalTag = partialTag;
        }
        return finalTag;
    }

    /**
     * Returns whether the chain is in progress
     * @param tag Chain's tag
     * @return true if chain is in progress
     */
    protected boolean isChainInProgress(Object tag) {
        for (FutureTask ft : mChainedTasks.values()) {
            if (ft.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }


}

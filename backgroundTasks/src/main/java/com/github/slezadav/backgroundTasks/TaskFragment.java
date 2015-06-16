package com.github.slezadav.backgroundTasks;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.github.slezadav.backgroundTasks.BaseTask.ExecutionType;
import com.github.slezadav.backgroundTasks.BaseTask.IBaseTaskCallbacks;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;


/**
 * A retained fragment managing the task processing Created by david.slezak on 9.6.2015.
 */
public class TaskFragment extends Fragment {
    protected final static String TASK_FRAGMENT_TAG = "com.github.slezadav.backgroundTasks.TaskFragment";
    protected final static String TAG = "backgroundTasks";
    private HashMap<BaseTask, Object[]> mTasks = new HashMap<>();
    private HashMap<Object, Object> mUnresolvedResults = new HashMap<>();
    private HashMap<Object, FutureTask> mChainedTasks = new HashMap<>();


    private TaskService mTaskService;
    private Messenger mRemoteTaskService;
    private ResponseHandler rh;

    private ServiceConnection mTaskServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mTaskService = ((TaskService.LocalBinder) service).getServiceInstance();
            startUnfinishedTasks(ExecutionType.SERVICE_LOCAL);
        }

        public void onServiceDisconnected(ComponentName className) {
            mTaskService = null;
        }
    };

    private ServiceConnection mRemoteTaskServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mRemoteTaskService = new Messenger(service);
            startUnfinishedTasks(ExecutionType.SERVICE_REMOTE);

        }

        public void onServiceDisconnected(ComponentName className) {
            mRemoteTaskService = null;
        }
    };

    private void executeTask(BaseTask task, Object... params) {
        if (task.getExecType() == ExecutionType.ASYNCTASK) {
            if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(BaseTask.THREAD_POOL_EXECUTOR, params);
            } else {
                task.execute(params);
            }
        }
        if (task.getExecType() == ExecutionType.SERVICE_LOCAL) {
            if (mTaskService != null) {
                mTaskService.executeTask(task, params);
            } else {
                bindTaskService();
            }
        }
        if (task.getExecType() == ExecutionType.SERVICE_REMOTE) {
            if(!(task.getTag() instanceof String)){
                throw new IllegalStateException("Only String tags supported");
            }
            if (mRemoteTaskService != null) {
                Message msg = Message.obtain();
                msg.replyTo = new Messenger(rh);
                Bundle bundle = new Bundle();
                bundle.putString("cls", task.getClass().getName());
                bundle.putSerializable("tag", (Serializable) task.getTag());
                msg.setData(bundle);
                try {
                    mRemoteTaskService.send(msg);
                } catch (RemoteException e) {
                    if (task.getCallbacks() != null) {
                        task.getCallbacks().onTaskFail(task.getTag(), e);
                    }
                    e.printStackTrace();
                }catch(RuntimeException e){
                    if (task.getCallbacks() != null) {
                        task.getCallbacks().onTaskFail(task.getTag(), new IllegalStateException("Failed to return object from remote service is it serializable ?"));
                    }
                }
            } else {
                bindTaskService();
            }
        }
    }


    protected void startTask(Object tag, BaseTask task, Object... params) {
        prepareTask(tag, task, params);
        if (task.isReady()) {
            executeTask(task, params);
        }
    }

    protected void startTaskChain(TaskChain chain) {
        if (isChainInProgress(chain.finalTag)) {
                Log.w(TAG, "Another instance of " + chain.finalTag.toString() +
                           " already in progress");

            return;
        }
        if(chain.tasks.get(0).getExecType()==ExecutionType.SERVICE_REMOTE){
            Log.w(TAG, "WARNING: Chains are not executed fully remotely yet!");
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

    protected void continueChain(Object finishedTag, Object result) {
        FutureTask ft = mChainedTasks.get(finishedTag);
        if (ft.useHistoryParam) {
            ft.params.add(result);
        }
        startTask(ft.tag, ft.task, (Object[]) ft.getParams());
        mChainedTasks.remove(finishedTag);
    }


    private BaseTask getTaskByTag(Object tag) {
        for (BaseTask task : mTasks.keySet()) {
            if (task.getTag().equals(tag)) {
                return task;
            }
        }

        return null;
    }

    private Object[] getParamsByTag(Object tag) {
        return mTasks.get(getTaskByTag(tag));
    }

    protected boolean isTaskInProgress(Object tag) {
        return getTaskByTag(tag) != null;
    }

    protected boolean isChainInProgress(Object tag) {
        for (FutureTask ft : mChainedTasks.values()) {
            if (ft.tag.equals(tag)) {
                return true;
            }
        }
        return false;
    }


    protected void cancelTask(Object tag) {
        BaseTask task=getTaskByTag(tag);
        if(task!=null){
            if(task.getExecType()==ExecutionType.SERVICE_REMOTE){
                sendCancelMessage(tag);
                return;
            }else {
                task.cancel(true);
                mTasks.remove(task);
            }
        }
        cancelChain(tag);
    }




    private void startUnfinishedTasks(ExecutionType eType) {
        if (BaseTask.IBaseTaskCallbacks.class.isAssignableFrom(getActivity().getClass())) {
            for (BaseTask task : mTasks.keySet()) {
                task.setCallbacks((BaseTask.IBaseTaskCallbacks) getActivity());
                if (!task.isReady() && task.getExecType() == eType) {
                    task.setReady(true);
                    executeTask(task, mTasks.get(task));
                }
            }
        }
    }

    protected void prepareTask(Object tag, BaseTask task, Object... params) {
        if (isTaskInProgress(tag)) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, "Another instance of " + tag.toString() +
                             " already in progress");
            }
            return;
        }
        task.setTag(tag);
        task.setEnclosingFragment(this);
        boolean serviceReadyOrNotNeeded = false;
        switch (task.getExecType()) {
            case ASYNCTASK:
                serviceReadyOrNotNeeded = true;
                break;
            case SERVICE_LOCAL:
                serviceReadyOrNotNeeded = (mTaskService != null);
                break;
            case SERVICE_REMOTE:
                serviceReadyOrNotNeeded = (mRemoteTaskService != null);
                break;
        }
        if (getActivity() != null && serviceReadyOrNotNeeded &&
            BaseTask.IBaseTaskCallbacks.class.isAssignableFrom(getActivity().getClass())) {
            task.setReady(true);
            task.setCallbacks((BaseTask.IBaseTaskCallbacks) getActivity());
        }
        mTasks.put(task, params);
    }

    protected boolean isTaskChained(Object tag) {
        return mChainedTasks.containsKey(tag);
    }

    protected Object removeChainResidue(Object failingTag) {
        FutureTask ft = null;
        while (mChainedTasks.containsKey(failingTag)) {
            ft = mChainedTasks.get(failingTag);
            mChainedTasks.remove(failingTag);
            failingTag = ft.tag;
        }
        return failingTag;
    }

    private Object getChainFinalTag(Object partialTag) {
        Object finalTag = null;
        FutureTask ft = null;
        while (mChainedTasks.containsKey(partialTag)) {
            ft = mChainedTasks.get(partialTag);
            partialTag = ft.tag;
            finalTag = partialTag;
        }
        return finalTag;
    }


    protected void completeTask(Object tag) {
        for (BaseTask task : mTasks.keySet()) {
            if (task.getTag().equals(tag)) {
                mTasks.remove(task);
                break;
            }
        }
    }

    /**
     * Hold a weak reference to the parent Activity so we can report the task's current progress and results. The
     * Android framework will pass us a reference to the newly created Activity after each configuration change.
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        startUnfinishedTasks(ExecutionType.ASYNCTASK);
        bindTaskService();
    }

    private void bindTaskService() {
        if (mTaskService == null) {
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), TaskService.class),
                    mTaskServiceConnection, Context.BIND_AUTO_CREATE);
        }
        if (mRemoteTaskService == null) {
            getActivity().getApplicationContext().bindService(new Intent(getActivity(), RemoteTaskService.class),
                    mRemoteTaskServiceConnection, Context.BIND_AUTO_CREATE);
            rh = new ResponseHandler(TaskFragment.this);
        }

    }


    @Override
    public void onDestroy() {
        if (mTaskService != null) {
            getActivity().getApplicationContext().unbindService(mTaskServiceConnection);
            getActivity().getApplicationContext().stopService(new Intent(getActivity(), TaskService.class));
        }

        if (mRemoteTaskService != null) {
            getActivity().getApplicationContext().unbindService(mRemoteTaskServiceConnection);
            getActivity().getApplicationContext().stopService(new Intent(getActivity(), RemoteTaskService.class));
        }
        super.onDestroy();
    }

    /**
     * This method will only be called once when the retained Fragment is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retain this fragment across configuration changes
        setRetainInstance(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if (IBaseTaskCallbacks.class.isAssignableFrom(activity.getClass())) {
            resolveUnresolvedResults((IBaseTaskCallbacks) activity);
        }

    }

    /**
     * NOT NEEDED as BaseTask holds only WeakReferences Set the callback to null so we don't accidentally leak the
     * Activity instance.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        for (BaseTask task : mTasks.keySet()) {
            task.setCallbacks(null);
        }
    }

    public void onUnresolvedResult(Object tag, Object result) {
        mUnresolvedResults.put(tag, result);
    }

    protected void resolveUnresolvedResults(BaseTask.IBaseTaskCallbacks callbacks) {
        Iterator<Map.Entry<Object, Object>> iterator = mUnresolvedResults.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Object, Object> setElement = iterator.next();
            Object tag = setElement.getKey();
            Object result = setElement.getValue();
            handlePostExecute(callbacks, tag, result);
            iterator.remove();
        }
    }

    protected void handlePostExecute(BaseTask.IBaseTaskCallbacks callbacks, Object tag, Object result) {
        if (callbacks != null) {
            completeTask(tag);
            if (isTaskChained(tag)) {
                if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                    Log.e(TAG, "Failed chain " + tag + "  " + ((Exception) result).getMessage());
                    Object failingTag = removeChainResidue(tag);
                    callbacks.onTaskFail(failingTag, (Exception) result);
                } else {
                    Log.d(TAG, "Continuing chain " + tag);
                    callbacks.onTaskProgressUpdate(getChainFinalTag(tag), result);
                    continueChain(tag, result);
                }
            } else {
                if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                    Log.e(TAG, "Failed task " + tag + "  " + ((Exception) result).getMessage());
                    callbacks.onTaskFail(tag, (Exception) result);
                } else {
                    Log.d(TAG, "Succeeded task " + tag);
                    callbacks.onTaskSuccess(tag, result);
                }
            }
        }

    }

    protected void handleProgress(BaseTask.IBaseTaskCallbacks callbacks, Object tag, Object... progress) {
        if (callbacks == null) {
            return;
        }
        if (isTaskChained(tag)) {
            callbacks.onTaskProgressUpdate(getChainFinalTag(tag), (Object[])progress);
        } else {
            callbacks.onTaskProgressUpdate(tag, (Object[])progress);
        }
    }

    protected void handlePreExecute(BaseTask.IBaseTaskCallbacks callbacks, Object tag) {
        if (callbacks != null && !isTaskChained(tag)) {
            callbacks.onTaskReady(tag);
        }
    }

    protected void handleCancel(BaseTask.IBaseTaskCallbacks callbacks, Object tag) {
        if (isTaskChained(tag)) {
            tag = removeChainResidue(tag);
        } else {
            cancelTask(tag);
        }
        if (callbacks != null) {
            callbacks.onTaskCancelled(tag);
        }
    }

    public void cancelChain(Object finalTag) {
        for (Map.Entry<Object, FutureTask> ft : mChainedTasks.entrySet()) {
            FutureTask task = ft.getValue();
            if (task.tag.equals(finalTag)) {
                if (isTaskInProgress(ft.getKey())) {
                    cancelTask(ft.getKey());
                }
                cancelChain(ft.getKey());
                return;
            }
        }
    }

    private void sendCancelMessage(Object tag) {
        if (mRemoteTaskService != null) {
            Message msg = Message.obtain();
            msg.replyTo = new Messenger(rh);
            Bundle bundle = new Bundle();
            bundle.putString("tag", (String) tag);
            msg.setData(bundle);
            try {
                mRemoteTaskService.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    static class ResponseHandler extends Handler {
        private final WeakReference<TaskFragment> mFragment;

        ResponseHandler(TaskFragment frg) {
            mFragment = new WeakReference<>(frg);
        }

        @Override
        public void handleMessage(Message msg) {
            int respCode = msg.what;
            String tag = msg.getData().getString("tag");
            Serializable res = msg.getData().getSerializable("result");
            BaseTask task = mFragment.get().getTaskByTag(tag);
            if(task==null){
                return;
            }
            switch (respCode) {
                case RemoteTaskService.ON_TASK_READY:
                    mFragment.get().handlePreExecute(task.getCallbacks(),tag);
                    break;
                case RemoteTaskService.ON_TASK_PROGRESS:
                    mFragment.get().handleProgress(task.getCallbacks(),tag,(Object[]) res);
                    break;
                case RemoteTaskService.ON_TASK_CANCEL:
                    mFragment.get().mTasks.remove(task);
                    mFragment.get().handleCancel(task.getCallbacks(),tag);
                    break;
                case RemoteTaskService.ON_TASK_SUCCESS:
                    mFragment.get().handlePostExecute(task.getCallbacks(),tag,res);
                    break;
                case RemoteTaskService.ON_TASK_FAIL:
                    mFragment.get().handlePostExecute(task.getCallbacks(),tag, res);
                    break;


            }
        }
    }


}

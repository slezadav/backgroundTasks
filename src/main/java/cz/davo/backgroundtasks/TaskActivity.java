package cz.davo.backgroundtasks;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cz.davo.backgroundtasks.BaseTask.ExecutionType;

/**
 * Base Activity for handling background tasks Created by david.slezak on 3.3.2015.
 */
public abstract class TaskActivity extends AppCompatActivity implements BaseTask.IBaseTaskCallbacks {
    private TaskFragment mTaskFragment;
    private static final String TAG_TASK_FRAGMENT = "task_fragment";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        mTaskFragment = (TaskFragment) fm.findFragmentByTag(TAG_TASK_FRAGMENT);
        if (mTaskFragment == null) {
            mTaskFragment = new TaskFragment();
            fm.beginTransaction().add(mTaskFragment, TAG_TASK_FRAGMENT).commit();
        }
        mTaskFragment.resolveUnresolvedResults(this);
    }

    @SuppressWarnings("unused")
    public final void startTask(Object tag, BaseTask task, Object... params) {
        task.setExecType(ExecutionType.ASYNCTASK);
        mTaskFragment.startTask(tag, task, params);
    }

    @SuppressWarnings("unused")
    public final void startTask(Object tag, BaseTask task, ExecutionType eType, Object... params) {
        task.setExecType(eType);
        mTaskFragment.startTask(tag, task, params);
    }

    @SuppressWarnings("unused")
    public final void startTaskChain(TaskChain chain) {
        for (BaseTask task : chain.tasks) {
            task.setExecType(ExecutionType.ASYNCTASK);
        }
        mTaskFragment.startTaskChain(chain);
    }

    @SuppressWarnings("unused")
    public final void startTaskChain(TaskChain chain, ExecutionType eType) {
        for (BaseTask task : chain.tasks) {
            task.setExecType(eType);
        }
        mTaskFragment.startTaskChain(chain);
    }

    @SuppressWarnings("unused")
    public final void cancelTask(Object tag) {
        mTaskFragment.cancelTask(tag);
    }

    @Override
    public final void onPreExecute(Object tag) {
        onTaskReady(tag);
    }

    @Override
    public final void onProgressUpdate(Object tag, Object progress) {
        onTaskProgress(tag, progress);
    }

    @Override
    public final void onCancelled(Object tag) {
        mTaskFragment.completeTask(tag);
        onTaskCancelled(tag);
    }

    @Override
    public final void onPostExecute(Object tag, Object result) {
        mTaskFragment.completeTask(tag);
        if (mTaskFragment.isTaskChained(tag)) {
            if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                Log.e("TAG", "Failed chain " + tag + "  " + ((Exception) result).getMessage());
                Object failingTag = mTaskFragment.removeChainResidue(tag);
                onTaskFail(failingTag, (Exception) result);
            } else {
                Log.d("TAG", "Continuing chain " + tag);
                mTaskFragment.continueChain(tag, result);
            }
        } else {

            if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                Log.e("TAG", "Failed task " + tag+ "  " + ((Exception) result).getMessage());
                onTaskFail(tag, (Exception) result);
            } else {
                Log.d("TAG", "Succeeded task " + tag);
                onTaskSuccess(tag, result);
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean isTaskInProgress(Object tag) {
        return mTaskFragment.isTaskInProgress(tag);
    }

    @SuppressWarnings("unused")
    public void onTaskFail(Object tag, Exception exception) {

    }

    @SuppressWarnings("unused")
    public void onTaskSuccess(Object tag, Object result) {

    }

    @SuppressWarnings("unused")
    public void onTaskCancelled(Object tag) {

    }

    @SuppressWarnings("unused")
    public void onTaskProgress(Object tag, Object progress) {

    }

    @SuppressWarnings("unused")
    public void onTaskReady(Object tag) {

    }

//    private static class TaskFragment extends Fragment implements BaseTask.IUnresolvedResult {
//        private HashMap<BaseTask, Object[]> mTasks = new HashMap<>();
//        private HashMap<Object, Object> mUnresolvedResults = new HashMap<>();
//        private HashMap<Object, FutureTask> mChainedTasks = new HashMap<>();
//
//
//        private TaskService mTaskService;
//
//        private ServiceConnection mTaskServiceConnection = new ServiceConnection() {
//            public void onServiceConnected(ComponentName className, IBinder service) {
//                mTaskService = ((TaskService.LocalBinder) service).getServiceInstance();
//                startUnfinishedTasks(ExecutionType.SERVICE_LOCAL);
//            }
//
//            public void onServiceDisconnected(ComponentName className) {
//                mTaskService = null;
//            }
//        };
//
//        private void executeTask(BaseTask task, Object... params) {
//            if (task.getExecType() == ExecutionType.ASYNCTASK) {
//                if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
//                    task.executeOnExecutor(BaseTask.THREAD_POOL_EXECUTOR, params);
//                } else {
//                    task.execute(params);
//                }
//            }
//            if (task.getExecType() == ExecutionType.SERVICE_LOCAL) {
//                if (mTaskService != null) {
//                    mTaskService.executeTask(new TaskHandler(Looper.getMainLooper()), task, params);
//                } else {
//                    bindTaskService();
//                }
//            }
//        }
//
//
//        protected void startTask(Object tag, BaseTask task, Object... params) {
//            prepareTask(tag, task, params);
//            if (task.isReady()) {
//                executeTask(task, params);
//            }
//        }
//
//        protected void startTaskChain(TaskChain chain) {
//            ArrayList<Object> tags = new ArrayList<>();
//            for (int i = 0; i < chain.tasks.size() - 1; i++) {
//                String chaintag = UUID.randomUUID().toString();
//                tags.add(chaintag);
//            }
//            tags.add(chain.finalTag);
//            for (int i = 1; i < chain.tasks.size(); i++) {
//                FutureTask ft = new FutureTask(tags.get(i), chain.tasks.get(i), chain.useHistoryParam.get(i),
//                        chain.params.get(i));
//                mChainedTasks.put(tags.get(i - 1), ft);
//            }
//            startTask(tags.get(0), chain.tasks.get(0));
//        }
//
//        protected void continueChain(Object finishedTag, Object result) {
//            FutureTask ft = mChainedTasks.get(finishedTag);
//            if (ft.useHistoryParam) {
//                ft.params.add(result);
//            }
//            startTask(ft.tag, ft.task, ft.params);
//            mChainedTasks.remove(finishedTag);
//        }
//
//
//        private BaseTask getTaskByTag(Object tag) {
//            for (BaseTask task : mTasks.keySet()) {
//                if (task.getTag().equals(tag)) {
//                    return task;
//                }
//            }
//            return null;
//        }
//
//        private Object[] getParamsByTag(Object tag) {
//            return mTasks.get(getTaskByTag(tag));
//        }
//
//        protected boolean isTaskInProgress(Object tag) {
//            return getTaskByTag(tag) != null;
//        }
//
//        protected void cancelTask(Object tag) {
//            Iterator iterator = mTasks.keySet().iterator();
//            while (iterator.hasNext()) {
//                BaseTask task = (BaseTask) iterator.next();
//                if (task.getTag().equals(tag)) {
//                    task.cancel(true);
//                    iterator.remove();
//                }
//            }
//        }
//
//        private void startUnfinishedTasks(ExecutionType eType) {
//            if (BaseTask.IBaseTaskCallbacks.class.isAssignableFrom(getActivity().getClass())) {
//                for (BaseTask task : mTasks.keySet()) {
//                    task.setCallbacks((BaseTask.IBaseTaskCallbacks) getActivity());
//                    if (!task.isReady() && task.getExecType() == eType) {
//                        task.setReady(true);
//                        executeTask(task, mTasks.get(task));
//                    }
//                }
//            }
//        }
//
//        protected void prepareTask(Object tag, BaseTask task, Object... params) {
//            if (isTaskInProgress(tag)) {
//                if (BuildConfig.DEBUG) {
//                    Log.w("TAG", "Another instance of " + tag.toString() +
//                                 " already in progress");
//                }
//                return;
//            }
//            task.setTag(tag);
//            task.setUnresolvedCallback(this);
//            boolean serviceReadyOrNotNeeded =
//                    task.getExecType() == ExecutionType.ASYNCTASK || mTaskService != null;
//            if (getActivity() != null && serviceReadyOrNotNeeded &&
//                BaseTask.IBaseTaskCallbacks.class.isAssignableFrom(getActivity().getClass())) {
//                task.setReady(true);
//                task.setCallbacks((BaseTask.IBaseTaskCallbacks) getActivity());
//            }
//            mTasks.put(task, params);
//        }
//
//        protected boolean isTaskChained(Object tag) {
//            return mChainedTasks.containsKey(tag);
//        }
//
//        protected Object removeChainResidue(Object failingTag) {
//            while (mChainedTasks.containsKey(failingTag)) {
//                FutureTask ft = mChainedTasks.get(failingTag);
//                mChainedTasks.remove(failingTag);
//                failingTag = ft.tag;
//            }
//            return failingTag;
//        }
//
//
//        protected void completeTask(Object tag) {
//            for (BaseTask task : mTasks.keySet()) {
//                if (task.getTag().equals(tag)) {
//                    mTasks.remove(task);
//                    break;
//                }
//            }
//        }
//
//        /**
//         * Hold a weak reference to the parent Activity so we can report the task's current progress and results.
//         * The Android framework will pass us a reference to the newly created Activity after each configuration
//         * change.
//         */
//        @Override
//        public void onAttach(Activity activity) {
//            super.onAttach(activity);
//            startUnfinishedTasks(ExecutionType.ASYNCTASK);
//            bindTaskService();
//        }
//
//        private void bindTaskService() {
//            if (mTaskService == null) {
//                getActivity().getApplicationContext().bindService(new Intent(getActivity(), TaskService.class),
//                        mTaskServiceConnection, Activity.BIND_AUTO_CREATE);
//            }
//        }
//
//
//        @Override
//        public void onDestroy() {
//            getActivity().getApplicationContext().unbindService(mTaskServiceConnection);
//            getActivity().getApplicationContext().stopService(new Intent(getActivity(), TaskService.class));
//            super.onDestroy();
//        }
//
//        /**
//         * This method will only be called once when the retained Fragment is first created.
//         */
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            // Retain this fragment across configuration changes
//            setRetainInstance(true);
//
//        }
//
//        /**
//         * NOT NEEDED as BaseTask holds only WeakReferences Set the callback to null so we don't accidentally leak
//         * the Activity instance.
//         */
//        @Override
//        public void onDetach() {
//            super.onDetach();
//            for (BaseTask task : mTasks.keySet()) {
//                task.setCallbacks(null);
//            }
//        }
//
//        private class TaskHandler extends Handler {
//            public TaskHandler(Looper looper) {
//                super(looper);
//            }
//
//            @Override
//            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
//                String tag = msg.getData().getString("tag");
//                Object result = ((HashMap<String, Object>) msg.getData().getSerializable("result")).get("result");
//                BaseTask task = getTaskByTag(tag);
//                if (task == null) {
//                    return;
//                }
//                IBaseTaskCallbacks callbacks = task.getCallbacks();
//                if (callbacks != null && !task.isCancelled()) {
//                    ((TaskActivity) getActivity()).onPostExecute(tag, result);
//                } else if (!task.isCancelled()) {
//                    mUnresolvedResults.put(tag, result);
//                }
//            }
//        }
//
//        @Override
//        public void onUnresolvedResult(Object tag, Object result) {
//            mUnresolvedResults.put(tag, result);
//        }
//
//        protected void resolveUnresolvedResults(BaseTask.IBaseTaskCallbacks callbacks) {
//            Iterator<Map.Entry<Object, Object>> iterator = mUnresolvedResults.entrySet().iterator();
//            while (iterator.hasNext()) {
//                Map.Entry<Object, Object> setElement = iterator.next();
//                callbacks.onPostExecute(setElement.getKey(), setElement.getValue());
//                iterator.remove();
//            }
//        }
//
//        private class FutureTask {
//            Object tag;
//            ArrayList<Object> params = new ArrayList<>();
//            BaseTask task;
//            boolean useHistoryParam;
//
//            public FutureTask(Object tag, BaseTask task, boolean useHistoryParam, Object[] params) {
//                this.tag = tag;
//                this.useHistoryParam = useHistoryParam;
//                this.task = task;
//                Collections.addAll(this.params, params);
//            }
//
//            public Object[] getParams() {
//                Object[] tmp = new Object[params.size()];
//                for (int i = 0; i < params.size(); i++) {
//                    tmp[i] = params.get(i);
//                }
//                return tmp;
//            }
//
//
//        }
//    }


}


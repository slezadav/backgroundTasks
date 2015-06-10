package cz.slezadav.backgroundTasks;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import cz.slezadav.backgroundTasks.BaseTask.ExecutionType;

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

}


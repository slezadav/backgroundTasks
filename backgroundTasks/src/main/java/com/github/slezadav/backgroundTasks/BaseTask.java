package com.github.slezadav.backgroundTasks;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;

import com.github.slezadav.backgroundTasks.executor.ParallelExecutor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.concurrent.Executor;

/**
 * Task class meant to be extended and used with TaskFragment. This is a base class for all BackgroundTasks
 *
 * Created by david.slezak on 3.3.2015.
 */
public abstract class BaseTask extends AsyncTask<Object, Object, Object> {
    /**
     * Weak reference to activity or fragment serving as callbacks
     */
    private WeakReference<IBgTaskSimpleCallbacks> mCallbacks;
    /**
     * Weak reference to the TaskFragment which started this task
     */
    private WeakReference<TaskFragment> mEnclosingFragment;
    /**
     * Tag associated with this task
     */
    private Object mTag;
    /**
     * Flag if this task is ready for execution
     */
    private boolean mReady;
    /**
     * Tag or id of the fragment used as a callback point, null if callback is an activity
     */
    private Object mCallbacksId;


    private CallbackType mCallbackType;


    protected CallbackType getCallbackType() {
        return mCallbackType;
    }

    protected void setCallbackType(CallbackType type) {
        this.mCallbackType = type;
    }

    /**
     * Gets the id of the callback fragment.
     *
     * @return Id of the callback fragment. Null if the callbacks are in activity
     */
    protected Object getCallbacksId() {
        return mCallbacksId;
    }

    /**
     * Sets the id of the fragment used as a callback target for this task
     *
     * @param callbacksId id of the fragment used as a callback target for this task
     */
    protected void setCallbacksId(Object callbacksId) {
        this.mCallbacksId = callbacksId;
    }

    /**
     * Indicates whether simple or full callbacks are used
     */
    private boolean fullCallBacks;

    /**
     * Custom executor to be used
     */
    private Executor mExecutor;

    /**
     * Task to be started after this one
     */
    private BaseTask followingTask;

    /**
     * Sets the task following after this one
     * @param followingTask task to follow
     */
    protected BaseTask addFollowingTask(Object tag, BaseTask followingTask) {
        this.followingTask = followingTask;
        followingTask.setTag(tag);
        return followingTask;
    }

    /**
     * Gets the following task
     * @return the following task
     */
    public BaseTask getFollowingTask() {
        return followingTask;
    }

    /**
     * Sets the callbacks for this task
     *
     * @param clb Callbacks to be used with this task
     */
    protected void setCallbacks(IBgTaskSimpleCallbacks clb) {
        fullCallBacks = clb instanceof IBgTaskCallbacks;
        this.mCallbacks = new WeakReference<>(clb);
    }

    /**
     * Method to get this task's callbacks
     *
     * @return task's callbacks
     */
    protected IBgTaskSimpleCallbacks getCallbacks() {
        if (mCallbacks != null) {
            return mCallbacks.get();
        } else {
            return null;
        }
    }

    /**
     * Sets the fragment used to run this task
     *
     * @param enclosingFragment TaskFragment instance used to start this task
     */
    protected void setEnclosingFragment(TaskFragment enclosingFragment) {
        this.mEnclosingFragment = new WeakReference<>(enclosingFragment);
    }

    /**
     * Gets this task's tag
     *
     * @return this task's tag
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Gets the tasks's executor
     *
     * @return Executor if specified or default one
     */
    protected Executor getExecutor() {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            return mExecutor != null ? mExecutor : ParallelExecutor.getExecutorInstance();
        } else {
            return null;
        }
    }

    /**
     * Sets the executor to this task
     *
     * @param executor executor to be set
     */
    protected void setExecutor(Executor executor) {
        this.mExecutor = executor;
    }

    public final AsyncTask<Object, Object, Object> executeWithCustomExecutor(Object... params) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            return executeOnExecutor(getExecutor(), params);
        } else {
            try {
                Field ex = AsyncTask.class.getDeclaredField("sExecutor");
                ex.setAccessible(true);
                ex.set(this, getExecutor());
                execute(params);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }


    /**
     * Sets this task's tag
     *
     * @param tag tag to be used with this task
     */
    protected void setTag(Object tag) {
        this.mTag = tag;
    }

    /**
     * Flag if this task is ready for execution
     *
     * @return true if the task is ready
     */
    protected boolean isReady() {
        return mReady;
    }

    /**
     * Sets this task ready or not
     *
     * @param ready sets this task ready or not
     */
    protected void setReady(boolean ready) {
        this.mReady = ready;
    }

    @Override
    protected void onPreExecute() {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks() && fullCallBacks) {
            mEnclosingFragment.get().handlePreExecute((IBgTaskCallbacks) mCallbacks.get(), this);
        }
    }


    @Override
    protected void onProgressUpdate(Object... progress) {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks() && fullCallBacks) {
            mEnclosingFragment.get().handleProgress((IBgTaskCallbacks) mCallbacks.get(), this,
                    (Object[]) progress);
        }
    }

    @Override
    protected void onCancelled(Object result) {
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            if (!canAskForCallbacks()) {
                return;
            }
            if (canUseCallbacks()) {
                mEnclosingFragment.get().handleCancel(mCallbacks.get(), getTag(), result);
            }
        }

    }

    @Override
    protected void onCancelled() {
        if (Build.VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
            if (!canAskForCallbacks()) {
                return;
            }
            if (canUseCallbacks()) {
                mEnclosingFragment.get().handleCancel(mCallbacks.get(), getTag(), null);
            }
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks() && !isCancelled()) {
            mEnclosingFragment.get().handlePostExecute(mCallbacks.get(), this, result);
        } else if (mEnclosingFragment.get() != null && !isCancelled()) {
            mEnclosingFragment.get().onUnresolvedResult(this, result);
        }
    }

    /**
     * Finds whether it is possible to find any type of callback
     *
     * @return true if any callback can be fired
     */
    private boolean canAskForCallbacks() {
        return mCallbacks != null && mEnclosingFragment != null;
    }

    /**
     * Finds whether it is possible to fire any type of callback
     *
     * @return true if any callback can be fired
     */
    private boolean canUseCallbacks() {
        IBgTaskSimpleCallbacks clb = mCallbacks.get();
        boolean callbacksValid = clb != null;
        boolean detached = false;
        if (clb instanceof Fragment) {
            detached = ((Fragment) clb).isDetached();
        }
        boolean taskFragmentValid = mEnclosingFragment.get() != null;
        return callbacksValid && taskFragmentValid && !detached;
    }

    protected enum CallbackType {
        ACTIVITY, FRAGMENT, VIEW
    }

}

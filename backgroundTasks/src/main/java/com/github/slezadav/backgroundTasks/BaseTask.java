package com.github.slezadav.backgroundTasks;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;
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
     *Tag or id of the fragment used as a callback point, null if callback is an activity
     */
    private Object mCallbacksId;

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
     * Sets the callbacks for this task
     *
     * @param clb Callbacks to be used with this task
     */
    protected void setCallbacks(IBgTaskSimpleCallbacks clb) {
        fullCallBacks=clb instanceof IBgTaskCallbacks;
        this.mCallbacks = new WeakReference<>(clb);
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
     * @return Executor if specified or default one
     */
    protected Executor getExecutor(){
        if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
            return mExecutor != null ? mExecutor : THREAD_POOL_EXECUTOR;
        }else{
            return null;
        }
    }

    /**
     * Sets the executor to this task
     * @param executor executor to be set
     */
    protected void setExecutor(Executor executor){
        this.mExecutor=executor;
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
        if (canUseCallbacks()&&fullCallBacks) {
            mEnclosingFragment.get().handlePreExecute((IBgTaskCallbacks) mCallbacks.get(), getTag());
        }
    }


    @Override
    protected void onProgressUpdate(Object... progress) {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks()&&fullCallBacks) {
            mEnclosingFragment.get().handleProgress((IBgTaskCallbacks) mCallbacks.get(), getTag(), (Object[]) progress);
        }
    }

    @Override
    protected void onCancelled(Object result) {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks()) {
            mEnclosingFragment.get().handleCancel(mCallbacks.get(), getTag(),result);
        }

    }

    @Override
    protected void onPostExecute(Object result) {
        if (!canAskForCallbacks()) {
            return;
        }
        if (canUseCallbacks() && !isCancelled()) {
            mEnclosingFragment.get().handlePostExecute(mCallbacks.get(), getTag(), result);
        } else if (mEnclosingFragment.get() != null && !isCancelled()) {
            mEnclosingFragment.get().onUnresolvedResult(this, result);
        }
    }

    /**
     * Finds whether it is possible to find any type of callback
     * @return true if any callback can be fired
     */
    private boolean canAskForCallbacks(){
        return mCallbacks != null && mEnclosingFragment != null;
    }

    /**
     * Finds whether it is possible to fire any type of callback
     * @return true if any callback can be fired
     */
    private boolean canUseCallbacks(){
        IBgTaskSimpleCallbacks clb=mCallbacks.get();
        boolean callbacksValid=clb != null;
        boolean detached=false;
        if(clb instanceof Fragment){
            detached=((Fragment)clb).isDetached();
        }
        boolean taskFragmentValid=mEnclosingFragment.get() != null;
        return callbacksValid&&taskFragmentValid&&!detached;
    }
}

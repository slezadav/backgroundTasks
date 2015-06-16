package com.github.slezadav.backgroundTasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Task class meant to be extended and used with TaskActivity
 * Created by david.slezak on 3.3.2015.
 */
public abstract class BaseTask  extends AsyncTask<Object, Object, Object>{
    private WeakReference<IBaseTaskCallbacks> mCallbacks;
    private WeakReference<TaskFragment> mEnclosingFragment;
    private Object mTag;
    private boolean mReady;
    private ExecutionType execType;

    public IBaseTaskCallbacks getCallbacks(){
        return mCallbacks.get();
    }

    public ExecutionType getExecType() {
        return execType;
    }

    public void setExecType(ExecutionType execType) {
        this.execType = execType;
    }

    public void setCallbacks(IBaseTaskCallbacks clb){
        this.mCallbacks=new WeakReference<>(clb);
    }

    public void setEnclosingFragment(TaskFragment enclosingFragment){
        this.mEnclosingFragment=new WeakReference<>(enclosingFragment);
    }


    public Object getTag() {
        return mTag;
    }

    public void setTag(Object mTag) {
        this.mTag = mTag;
    }

    public boolean isReady() {
        return mReady;
    }


    public void setReady(boolean started) {
        this.mReady = started;
    }

    @Override
    protected void onPreExecute() {
        if(execType==ExecutionType.SERVICE_REMOTE){
            mCallbacks.get().onTaskReady(getTag());
            return;
        }
        if(mCallbacks==null||mEnclosingFragment==null){
            return;
        }
        if (mCallbacks.get() != null&&mEnclosingFragment.get()!=null) {
            mEnclosingFragment.get().handlePreExecute(mCallbacks.get(),getTag());
        }
    }


    @Override
    protected void onProgressUpdate(Object... progress) {
        if(execType==ExecutionType.SERVICE_REMOTE){
            mCallbacks.get().onTaskProgressUpdate(getTag(),(Object[]) progress);
            return;
        }
        if(mCallbacks==null||mEnclosingFragment==null){
            return;
        }
        if (mCallbacks.get() != null&&mEnclosingFragment.get()!=null) {
            mEnclosingFragment.get().handleProgress(mCallbacks.get(), getTag(),(Object[]) progress);
        }
    }

    @Override
    protected void onCancelled() {
        if(execType==ExecutionType.SERVICE_REMOTE){
            mCallbacks.get().onTaskCancelled(getTag());
            return;
        }
        if(mCallbacks==null||mEnclosingFragment==null){
            return;
        }
        if(mCallbacks.get() != null&&mEnclosingFragment.get()!=null) {
                mEnclosingFragment.get().handleCancel(mCallbacks.get(), getTag());
            }

    }

    @Override
    protected void onPostExecute(Object result) {
        if(execType==ExecutionType.SERVICE_REMOTE){
            if (result != null && Exception.class.isAssignableFrom(result.getClass())) {
                mCallbacks.get().onTaskFail(getTag(), (Exception) result);
            }else{
                mCallbacks.get().onTaskSuccess(getTag(),result);
            }

            return;
        }
        if(mCallbacks==null||mEnclosingFragment==null){
            return;
        }
        if(mEnclosingFragment.get()!=null&&mCallbacks.get() != null&&!isCancelled()) {
            mEnclosingFragment.get().handlePostExecute(mCallbacks.get(),getTag(),result);
        }else if(mEnclosingFragment.get()!=null&&!isCancelled()){
            mEnclosingFragment.get().onUnresolvedResult(mTag,result);
        }
    }

    public enum ExecutionType {
        ASYNCTASK,SERVICE_LOCAL,SERVICE_REMOTE
    }

    public interface IBaseTaskCallbacks {
        void onTaskReady(Object tag);
        void onTaskProgressUpdate(Object tag, Object... progress);
        void onTaskCancelled(Object tag);
        void onTaskSuccess(Object tag, Object result);
        void onTaskFail(Object tag, Exception exception);
    }


}

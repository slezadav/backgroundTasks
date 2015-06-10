package cz.slezadav.backgroundTasks;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Task class meant to be extended and used with TaskActivity
 * Created by david.slezak on 3.3.2015.
 */
public abstract class BaseTask  extends AsyncTask<Object, Object, Object> {
    private WeakReference<IBaseTaskCallbacks> mCallbacks;
    private IUnresolvedResult mUnresolvedCallback;
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

    public void setUnresolvedCallback(IUnresolvedResult clb){
        this.mUnresolvedCallback=clb;
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
        if(mCallbacks==null){
            return;
        }
        if (mCallbacks.get() != null) {
            mCallbacks.get().onPreExecute(mTag);
        }
    }


    @Override
    protected void onProgressUpdate(Object... progress) {
        if(mCallbacks==null){
            return;
        }
        if (mCallbacks.get() != null) {
            mCallbacks.get().onProgressUpdate(mTag, progress);
        }
    }

    @Override
    protected void onCancelled() {
        if(mCallbacks==null){
            return;
        }
        if (mCallbacks.get() != null) {
            mCallbacks.get().onCancelled(mTag);
        }
    }

    @Override
    protected void onPostExecute(Object result) {
        if(mCallbacks==null){
            return;
        }
        if (mCallbacks.get() != null&&!isCancelled()) {
            mCallbacks.get().onPostExecute(mTag, result);
        }else if(mUnresolvedCallback!=null&&!isCancelled()){
            mUnresolvedCallback.onUnresolvedResult(mTag,result);
        }
    }

    public enum ExecutionType {
        ASYNCTASK,SERVICE_LOCAL
    }

    public interface IBaseTaskCallbacks {
        void onPreExecute(Object tag);
        void onProgressUpdate(Object tag, Object progress);
        void onCancelled(Object tag);
        void onPostExecute(Object tag, Object result);
    }

    public interface IUnresolvedResult {
        void onUnresolvedResult(Object tag, Object result);
    }
}

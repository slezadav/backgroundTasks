package com.github.slezadav.backgroundTasks;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.github.slezadav.backgroundTasks.BaseTask.ExecutionType;
import com.github.slezadav.backgroundTasks.BaseTask.IBaseTaskCallbacks;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.HashMap;

/**
 * Created by david.slezak on 16.6.2015.
 */
public class RemoteTaskService extends TaskService implements IBaseTaskCallbacks{
    final Messenger myMessenger = new Messenger(new IncomingHandler(this));
    HashMap<String,Messenger> mMap=new HashMap<>();
    private HashMap<BaseTask, Object[]> mTasks = new HashMap<>();
    public static final int ON_TASK_READY=0;
    public static final int ON_TASK_PROGRESS=1;
    public static final int ON_TASK_SUCCESS=2;
    public static final int ON_TASK_FAIL=3;
    public static final int ON_TASK_CANCEL=4;

    @Override
    public IBinder onBind(Intent intent) {
        return myMessenger.getBinder();
    }

    public void handleMessage(Message msg) {

        Bundle data = msg.getData();
        String tag=data.getString("tag");
        if(!data.containsKey("cls")){
             BaseTask task=getTaskByTag(tag);
            if(task!=null){
             task.cancel(true);
            }
            mTasks.remove(task);
            return;
        }
        String cls=data.getString("cls");
        Object[] params= (Object[]) data.getSerializable("params");
        mMap.put(tag,msg.replyTo);
        BaseTask task = null;
        try {
            task = (BaseTask) Class.forName(cls).newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(task==null){
            return;
        }
        task.setExecType(ExecutionType.SERVICE_REMOTE);
        task.setTag(tag);
        task.setCallbacks(this);
        mTasks.put(task,params);
        executeTask(task,params);
    }

    private void send(int code,String tag,Object result){
        if(code==ON_TASK_FAIL||code==ON_TASK_SUCCESS||code==ON_TASK_CANCEL){
            mTasks.remove(tag);
        }
        Message resp=Message.obtain();
        resp.what=code;
        Bundle bundle = new Bundle();
        bundle.putString("tag", tag);
        bundle.putSerializable("result", (Serializable) result);
        Messenger msg=mMap.get(tag);
        resp.setData(bundle);
        try {
            msg.send(resp);
        } catch (RemoteException e) {
            e.printStackTrace();
        }catch (RuntimeException e){
            e.printStackTrace();
            send(ON_TASK_FAIL, tag,
                    new IllegalStateException("Failed to return object from remote service is it serializable ?"));
        }
    }

    @Override
    public void onTaskReady(Object tag) {
        send(ON_TASK_READY,tag.toString(),null);
    }

    @Override
    public void onTaskProgressUpdate(Object tag, Object... progress) {
        send(ON_TASK_PROGRESS,tag.toString(),progress);
    }

    @Override
    public void onTaskCancelled(Object tag) {
        send(ON_TASK_CANCEL,tag.toString(),null);
    }

    @Override
    public void onTaskSuccess(Object tag, Object result) {
        send(ON_TASK_SUCCESS,tag.toString(),result);
    }

    @Override
    public void onTaskFail(Object tag, Exception exception) {
        send(ON_TASK_FAIL,tag.toString(),exception);
    }

    private BaseTask getTaskByTag(Object tag) {
        for (BaseTask task : mTasks.keySet()) {
            if (task.getTag().equals(tag)) {
                return task;
            }
        }

        return null;
    }

    static class IncomingHandler extends Handler {
        private final WeakReference<RemoteTaskService> mService;

        IncomingHandler(RemoteTaskService service) {
            mService = new WeakReference<>(service);
        }
        @Override
        public void handleMessage(Message msg)
        {
            RemoteTaskService service = mService.get();
            if (service != null) {
                service.handleMessage(msg);
            }
        }
    }
}



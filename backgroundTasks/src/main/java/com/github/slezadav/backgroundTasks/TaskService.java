package com.github.slezadav.backgroundTasks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

/**
 * Service to run task with service execution type
 * Created by david.slezak on 20.5.2015.
 */
public class TaskService extends Service {
    IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public TaskService getServiceInstance() {
            return TaskService.this;
        }
    }

    public void executeTask(final BaseTask task, final Object... params) {
        Log.i("TAG","Executing in service");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                        task.executeOnExecutor(BaseTask.THREAD_POOL_EXECUTOR, params).get();
                    } else {
                        task.execute(params).get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

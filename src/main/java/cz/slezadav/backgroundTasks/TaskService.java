package cz.slezadav.backgroundTasks;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.HashMap;

/**
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

    public void executeTask(final Handler handler, final BaseTask task, final Object... params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Object result = null;
                try {
                    if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
                        result = task.executeOnExecutor(BaseTask.THREAD_POOL_EXECUTOR, params).get();
                    } else {
                        result = task.execute(params).get();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendMessage(makeReplyMessage(task.getTag(), result));
            }
        }).start();
    }


    private Message makeReplyMessage(Object tag, Object result) {
        Message message = Message.obtain();
        Bundle bundle = new Bundle();
        HashMap<String,Object> map=new HashMap<>();
        map.put("result",result);
        bundle.putSerializable("result",map);
        bundle.putString("tag", tag.toString());
        message.setData(bundle);
        return message;
    }

}

package com.example.backgroundtaskstest;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTaskChain;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;


public class MainActivity extends FragmentActivity implements IBgTaskCallbacks {

    public static final String TASKTAG = "task";
    public static final String CHAINTAG = "chain";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button taskb = (Button) findViewById(R.id.button_task);
        taskb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.startTask(MainActivity.this, new TestTask().withTag(TASKTAG));
            }
        });
        Button chainb = (Button) findViewById(R.id.button_chain);
        chainb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {                ;
                BgTasks.startTask(MainActivity.this,
                        new BgTaskChain().addTask(new TestTask().withTag(TASKTAG)).addTask(new TestTask()).withTag(
                                CHAINTAG));
            }
        });
        Button cchain = (Button) findViewById(R.id.button_cancel_chain);
        cchain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this, CHAINTAG);
            }
        });
        Button ctask = (Button) findViewById(R.id.button_cancel_task);
        ctask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(MainActivity.this,TASKTAG);
            }
        });
    }



    @Override
    public void onTaskReady(BaseTask task) {
        Log.i("TAG", "onTaskReady " + task.getTag());
        if(task.getChainTag()!=null){
            Log.i("TAG", "onTaskReady " + task.getChainTag());
        }

    }

    @Override
    public void onTaskProgressUpdate(BaseTask task, Object... progress) {
        Log.i("TAG", "onTaskProgress " + task.getTag() + "   " +
                     progress[0]);
        if(task.getChainTag()!=null) {
            Log.i("TAG", "onTaskProgress " + task.getChainTag() + "   " +
                         progress[0]);
        }
    }

    @Override
    public void onTaskCancelled(BaseTask task, Object result) {
        Log.i("TAG", "onTaskCancel " + task.getTag() + "   " + result);
        if(task.getChainTag()!=null) {
            Log.i("TAG", "onTaskCancel " + task.getChainTag() + "   " + result);
        }
    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {
        Log.i("TAG", "onTaskSuccess " + task.getTag() + "   " + result);
        if(task.getChainTag()!=null) {
            Log.i("TAG", "onTaskSuccess " + task.getChainTag() + "   " + result);
        }
    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {
        Log.i("TAG", "onTaskFail " + task.getTag());
        if(task.getChainTag()!=null) {
            Log.i("TAG", "onTaskFail " + task.getChainTag());
        }
    }
}

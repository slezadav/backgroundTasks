package com.example.backgroundtaskstest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.BgTaskChain;
import com.github.slezadav.backgroundTasks.IBgTaskCallbacks;


/**
 * Fragment used for testing
 * Created by david.slezak on 19.6.2015.
 */
public class TestFragment extends Fragment implements IBgTaskCallbacks {
    public static final String TASKTAG="task in fragment";
    public static final String CHAINTAG="chain in fragment";
    View mRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRoot=inflater.inflate(R.layout.fragment,
                container, false);
        mRoot.findViewById(R.id.button_taskf).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.startTask(TestFragment.this, TASKTAG, new TestTask());
            }
        });
        mRoot.findViewById(R.id.button_chainf).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new BgTaskChain(TestFragment.this).addTask(CHAINTAG, new TestTask()).addTask(CHAINTAG,
                        new TestTask()).addTask(CHAINTAG, new TestTask()).run();
            }
        });
        mRoot.findViewById(R.id.button_cancel_taskf).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(TestFragment.this, TASKTAG);
            }
        });
        mRoot.findViewById(R.id.button_cancel_chainf).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BgTasks.cancelTask(TestFragment.this, CHAINTAG);
            }
        });
        return mRoot;
    }

    @Override
    public void onTaskReady(BaseTask task) {
        Log.i("TAG", "onTaskReady " + task.getTag());
    }

    @Override
    public void onTaskProgressUpdate(BaseTask task, Object... progress) {
        Log.i("TAG","onTaskProgress "+task.getTag()+"   "+progress[0]);
    }

    @Override
    public void onTaskCancelled(Object tag, Object result) {
        Log.i("TAG","onTaskCancel "+tag+"    "+result);
    }

    @Override
    public void onTaskSuccess(BaseTask task, Object result) {
        Log.i("TAG","onTaskSuccess "+task.getTag()+"   "+result);
    }

    @Override
    public void onTaskFail(BaseTask task, Exception exception) {
        Log.i("TAG","onTaskFail "+task.getTag());
    }
}

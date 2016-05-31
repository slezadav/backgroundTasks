package com.example.backgroundtaskstest;

import android.content.Context;
import android.util.Log;
import com.github.slezadav.backgroundTasks.BaseTask;
import com.github.slezadav.backgroundTasks.BgTasks;
import com.github.slezadav.backgroundTasks.IBgTasksFullCallbacks;

import java.lang.ref.WeakReference;

/**
 * Created by david.slezak on 31.5.2016.
 */
public class TestObject implements IBgTasksFullCallbacks {
	WeakReference<Context> activityRef;

	public TestObject(Context context) {
		this.activityRef = new WeakReference<>(context);
	}

	public void start(){
		BgTasks.startTask(this,new TestTask());
	}

	@Override
	public Context getContext() {
		return activityRef.get();
	}

	@Override
	public IBgTasksFullCallbacks getSelfFromActivity() {
		MainActivity activity= (MainActivity)activityRef.get();
		return activity==null?null:activity.getTestObject();
	}

	@Override
	public void onTaskReady(BaseTask task) {
		Log.i("TAG", "onTaskReady " + task.getTag()+" "+task.getTaskNumber());
	}

	@Override
	public void onTaskProgressUpdate(BaseTask task,
									 Object... progress) {
		Log.i("TAG", "onTaskProgress " + task.getTag()+" "+task.getTaskNumber() + "   " +
		  progress[0]);
	}

	@Override
	public void onTaskCancelled(BaseTask task,
								Object result) {
		Log.i("TAG", "onTaskCancel " + task.getTag()+" "+task.getTaskNumber() + "   " + result);
	}

	@Override
	public void onTaskSuccess(BaseTask task,
							  Object result) {
		Log.i("TAG", "onTaskSuccess " + task.getTag()+" "+task.getTaskNumber() + "   " + result);
	}

	@Override
	public void onTaskFail(BaseTask task,
						   Exception exception) {
		Log.i("TAG", "onTaskFail " + task.getTag()+" "+task.getTaskNumber());
	}
}

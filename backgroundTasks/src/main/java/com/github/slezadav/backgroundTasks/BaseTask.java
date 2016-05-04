package com.github.slezadav.backgroundTasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.v4.app.Fragment;
import com.github.slezadav.backgroundTasks.executor.ParallelExecutor;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * Task class meant to be extended and used with TaskFragment. This is a base class for all
 * BackgroundTasks
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
	private WeakReference<TaskFragment>           mEnclosingFragment;
	/**
	 * Tag associated with this task
	 */
	private String                                mTag;
	/**
	 * Tag associated with this task
	 */
	private String                                mChainTag;
	/**
	 * Flag if this task is ready for execution
	 */
	private boolean                               mReady;
	/**
	 * Tag or id of the fragment used as a callback point, null if callback is an activity
	 */
	private Object                                mCallbacksId;

	/**
	 * Callback type
	 */
	private CallbackType mCallbackType;
	/**
	 * task number
	 */
	private int mTaskNumber = 0;

	/**
	 * Gets callback type
	 *
	 * @return clb type
	 */
	CallbackType getCallbackType() {
		return mCallbackType;
	}

	/**
	 * Sets the callback type
	 *
	 * @param type type of callback
	 */
	void setCallbackType(CallbackType type) {
		this.mCallbackType = type;
	}

	/**
	 * Gets the id of the callback fragment.
	 *
	 * @return Id of the callback fragment. Null if the callbacks are in activity
	 */
	Object getCallbacksId() {
		return mCallbacksId;
	}

	/**
	 * Sets the id of the fragment used as a callback target for this task
	 *
	 * @param callbacksId id of the fragment used as a callback target for this task
	 */
	void setCallbacksId(Object callbacksId) {
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
	 * Constructor
	 */
	public BaseTask() {
		this.mTag = UUID.randomUUID()
						.toString();
	}

	public final BaseTask addTag(String tag) {
		this.mTag = tag;
		return this;
	}

	void setTaskNumber(int number) {
		this.mTaskNumber = number;
	}

	public int getTaskNumber() {
		return mTaskNumber;
	}

	public Activity getWrappingActivity() {
		if (mEnclosingFragment != null) {
			TaskFragment fragment = mEnclosingFragment.get();
			if (fragment != null) {
				return fragment.getActivity();
			}
		}
		return null;
	}

	/**
	 * Sets the task following after this one
	 *
	 * @param followingTask task to follow
	 */
	final BaseTask addFollowingTask(BaseTask followingTask) {
		followingTask.setTaskNumber(getTaskNumber() + 1);
		this.followingTask = followingTask;
		return followingTask;
	}

	/**
	 * Gets the following task
	 *
	 * @return the following task
	 */
	final BaseTask getFollowingTask() {
		return followingTask;
	}

	public final String getChainTag() {
		return mChainTag;
	}

	void setChainTag(String mChainTag) {
		this.mChainTag = mChainTag;
	}

	/**
	 * Sets the callbacks for this task
	 *
	 * @param clb Callbacks to be used with this task
	 */
	void setCallbacks(IBgTaskSimpleCallbacks clb) {
		fullCallBacks = clb instanceof IBgTaskCallbacks;
		this.mCallbacks = new WeakReference<>(clb);
		if (followingTask != null) {
			followingTask.setCallbacks(clb);
		}
	}

	/**
	 * Method to get this task's callbacks
	 *
	 * @return task's callbacks
	 */
	IBgTaskSimpleCallbacks getCallbacks() {
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
	void setEnclosingFragment(TaskFragment enclosingFragment) {
		this.mEnclosingFragment = new WeakReference<>(enclosingFragment);
	}

	/**
	 * Gets this task's tag
	 *
	 * @return this task's tag
	 */
	public String getTag() {
		return mTag;
	}

	/**
	 * Gets the tasks's executor
	 *
	 * @return Executor if specified or default one
	 */
	Executor getExecutor() {
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
	void setExecutor(Executor executor) {
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
	 * Flag if this task is ready for execution
	 *
	 * @return true if the task is ready
	 */
	boolean isReady() {
		return mReady;
	}

	/**
	 * Sets this task ready or not
	 *
	 * @param ready sets this task ready or not
	 */
	void setReady(boolean ready) {
		this.mReady = ready;
	}

	@Override
	protected final void onPreExecute() {
		doOnPreExecute();
		if (!canAskForCallbacks()) {
			return;
		}
		if (canUseCallbacks() && fullCallBacks) {
			mEnclosingFragment.get()
							  .handlePreExecute(this);
		}
	}

	protected void doOnPreExecute() {
	}

	@Override
	protected final void onProgressUpdate(Object... progress) {
		doOnProgressUpdate((Object[])progress);
		if (!canAskForCallbacks()) {
			return;
		}
		if (canUseCallbacks() && fullCallBacks) {
			mEnclosingFragment.get()
							  .handleProgress(this, (Object[])progress);
		}
	}

	protected void doOnProgressUpdate(Object... progress) {
	}

	@Override
	protected final void onCancelled(Object result) {
		if (Build.VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			doOnCancelled(result);
			if (!canAskForCallbacks()) {
				return;
			}
			if (canUseCallbacks()) {
				mEnclosingFragment.get()
								  .handleCancel(mCallbacks.get(), this, result);
			}
		}
	}

	protected void doOnCancelled(Object result) {
	}

	@Override
	protected final void onCancelled() {
		if (Build.VERSION.SDK_INT < VERSION_CODES.HONEYCOMB) {
			doOnCancelled();
			if (!canAskForCallbacks()) {
				return;
			}
			if (canUseCallbacks()) {
				mEnclosingFragment.get()
								  .handleCancel(mCallbacks.get(), this, null);
			}
		}
	}

	protected void doOnCancelled() {
	}

	@Override
	protected final void onPostExecute(Object result) {
		doOnPostExecute(result);
		if (!canAskForCallbacks()) {
			return;
		}
		if (canUseCallbacks() && !isCancelled()) {
			mEnclosingFragment.get()
							  .handlePostExecute(this, result);
		} else if (mEnclosingFragment.get() != null && !isCancelled()) {
			mEnclosingFragment.get()
							  .onUnresolvedResult(this, result);
		}
	}

	protected void doOnPostExecute(Object result) {

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
		if (!canAskForCallbacks()) {
			return false;
		}
		IBgTaskSimpleCallbacks clb = mCallbacks.get();
		boolean callbacksValid = clb != null;
		boolean detached = false;
		if (clb instanceof Fragment) {
			detached = ((Fragment)clb).isDetached();
		}
		boolean taskFragmentValid = mEnclosingFragment.get() != null;
		return callbacksValid && taskFragmentValid && !detached;
	}

	protected void startTaskInSameContext(BaseTask task,
										Object... params) {
		task.setCallbacksId(getCallbacksId());
		task.setCallbackType(getCallbackType());
		if (canUseCallbacks()) {
			mEnclosingFragment.get()
							  .startTask(task, params);
		}

	}

	enum CallbackType {
		ACTIVITY, FRAGMENT, VIEW
	}

}

package com.github.slezadav.backgroundTasks;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import com.github.slezadav.backgroundTasks.BaseTask.CallbackType;

import java.util.concurrent.Executor;

/**
 * Class used as static entry point to the library.
 *
 * Created by Davo on 12.6.2015.
 */
public final class BgTasks {
	/**
	 * Method that starts the task from activity
	 *
	 * @param object object to which the callbacks should be delivered
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must implement IBgTasksFullCallbacks
	 */
	public static <T extends IBgTasksFullCallbacks> void startTask(T object,
																   BaseTask task,
																   Object... params) {
		task.setCallbacksId(object);
		task.setCallbackType(CallbackType.OBJECT);
		TaskFragment taskFragment = getFragment(object);
		if (taskFragment != null) {

			taskFragment.startTask(task, params);
		} else {
			task.onCancelled();
		}
	}

	/**
	 * Method that starts the task from activity
	 *
	 * @param activity activity to which the callbacks should be delivered
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 */
	public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,
																					   BaseTask
																						 task,
																					   Object...
																						 params) {
		task.setCallbackType(CallbackType.ACTIVITY);
		TaskFragment taskFragment = getFragment(activity);
		if (taskFragment != null) {
			taskFragment.startTask(task, params);
		} else {
			task.onCancelled();
		}
	}

	/**
	 * Method that starts the task from fragment
	 *
	 * @param fragment fragment to which the callbacks should be delivered
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
	 */
	public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,
																			   BaseTask task,
																			   Object... params) {
		Object id = getIdTagOrIdFromFragment(fragment);
		task.setCallbacksId(id);
		task.setCallbackType(CallbackType.FRAGMENT);

		TaskFragment taskFragment = getFragment(fragment.getActivity());
		if (taskFragment != null) {
			taskFragment.startTask(task, params);
		} else {
			task.onCancelled();
		}
	}

	/**
	 * Method that starts the task from view
	 *
	 * @param view   view in which to return callbacks
	 * @param task   task to be started
	 * @param params optional params for the task
	 * @param <T>    Must extend View implement IBgTaskCallbacks
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view,
																		   BaseTask task,
																		   Object... params) {
		task.setCallbackType(CallbackType.VIEW);
		Object id = getIdTagOrIdFromView(view);
		task.setCallbacksId(id);
		TaskFragment taskFragment = getFragment(view);
		if (taskFragment != null) {
			taskFragment.startTask(task, params);
		} else {
			task.onCancelled();
		}
	}

	/**
	 * Method that starts the task from activity. The task will run on supplied executor
	 *
	 * @param activity activity to which the callbacks should be delivered
	 * @param executor executor to be used
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 */
	public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,
																					   Executor
																						 executor,
																					   BaseTask
																						 task,
																					   Object...
																						 params) {
		task.setExecutor(executor);
		startTask(activity, task, params);
	}

	/**
	 * Method that starts the task from fragment. The task will run on supplied executor
	 *
	 * @param fragment fragment to which the callbacks should be delivered
	 * @param executor executor to be used
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 */
	public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,
																			   Executor executor,
																			   BaseTask task,
																			   Object... params) {
		task.setExecutor(executor);
		startTask(fragment, task, params);
	}

	/**
	 * Method that starts the task from view
	 *
	 * @param view     view in which to return callbacks
	 * @param executor executor to be used
	 * @param task     task to be started
	 * @param params   optional params for the task
	 * @param <T>      Must extend View implement IBgTaskCallbacks
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view,
																		   Executor executor,
																		   BaseTask task,
																		   Object... params) {
		task.setExecutor(executor);
		startTask(view, task, params);
	}

	/**
	 * Method that starts the task from activity
	 *
	 * @param activity activity to which the callbacks should be delivered
	 * @param chain    chain to be started
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 */
	public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void startTask(T activity,
																					   BgTaskChain
																						 chain) {
		chain.run(activity);
	}

	/**
	 * Method that starts the task from fragment
	 *
	 * @param fragment fragment to which the callbacks should be delivered
	 * @param chain    chain to be started
	 * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
	 */
	public static <T extends Fragment & IBgTaskSimpleCallbacks> void startTask(T fragment,
																			   BgTaskChain chain) {
		chain.run(fragment);
	}

	/**
	 * Method that starts the task from view
	 *
	 * @param view   view in which to return callbacks
	 * @param chain    chain to be started
	 * @param <T>    Must extend View implement IBgTaskCallbacks
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View & IBgTaskSimpleCallbacks> void startTask(T view,
																		   BgTaskChain chain) {
		chain.run(view);
	}
	/**
	 * Method that cancels the execution of the task with given tag
	 *
	 * @param object object to which the callback should be delivered
	 * @param tag      tag task to be cancelled
	 * @param <T>      Must implement IBgTasksFullCallbacks
	 */
	public static <T extends IBgTasksFullCallbacks> void cancelTask(T object,
																	String tag) {
		TaskFragment tf = getFragment(object);
		if (tf != null) {
			tf.cancelTask(tag);
		}
	}

	/**
	 * Method that cancels the execution of the task with given tag
	 *
	 * @param activity activity to which the callback should be delivered
	 * @param tag      tag task to be cancelled
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 */
	public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> void cancelTask(T activity,
																						String
																						  tag) {
		TaskFragment tf = getFragment(activity);
		if (tf != null) {
			tf.cancelTask(tag);
		}
	}

	/**
	 * Method that cancels the execution of the task with given tag
	 *
	 * @param fragment fragment to which the callbacks should be delivered
	 * @param tag     tag to be cancelled
	 * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
	 */
	public static <T extends Fragment & IBgTaskSimpleCallbacks> void cancelTask(T fragment,
																				String tag) {
		TaskFragment tf = getFragment(fragment.getActivity());
		if (tf != null) {
			tf.cancelTask(tag);
		}
	}

	/**
	 * Method that cancels the execution of the task with given tag
	 *
	 * @param view view to which the callbacks should be delivered
	 * @param tag tag to be cancelled
	 * @param <T>  Must extend View and implement IBgTaskCallbacks
	 */
	public static <T extends View & IBgTaskSimpleCallbacks> void cancelTask(T view,
																			String tag) {
		TaskFragment tf = getFragment(view);
		if (tf != null) {
			tf.cancelTask(tag);
		}
	}

	/**
	 * Method which determines whether the task is running
	 *
	 * @param activity current activity
	 * @param tag     tag of task
	 * @param <T>      Must extend FragmentActivity and implement IBgTaskCallbacks
	 * @return true if the task with given tag is in progress
	 */
	public static <T extends FragmentActivity & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T
																								   activity,
																								 String tag) {
		TaskFragment tf = getFragment(activity);
		return tf != null && tf.isTaskInProgress(tag);
	}

	/**
	 * Method which determines whether the task is running
	 *
	 * @param fragment current fragment
	 * @param tag     tag of task
	 * @param <T>      Must extend android.support.v4.app.Fragment and implement IBgTaskCallbacks
	 * @return true if the task with given tag is in progress
	 */
	public static <T extends Fragment & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T
																						   fragment,
																						 String
																						   tag) {
		TaskFragment tf = getFragment(fragment.getActivity());
		return tf != null && tf.isTaskInProgress(tag);
	}

	/**
	 * Method which determines whether the task is running
	 *
	 * @param view view in which the task runs
	 * @param tag     tag of task
	 * @param <T>  Must extend View and implement IBgTaskCallbacks
	 * @return true if the task with given tag is in progress
	 */
	public static <T extends View & IBgTaskSimpleCallbacks> boolean isTaskInProgress(T view,
																					 String tag) {
		TaskFragment tf = getFragment(view);
		return tf != null && tf.isTaskInProgress(tag);
	}

	/**
	 * Method to find the fragment used to execute background tasks
	 *
	 * @param activity current activity
	 * @param <T>      Must extend FragmentActivity
	 * @return TaskFragment instance for performing background tasks
	 */
	private static <T extends FragmentActivity> TaskFragment getFragment(T activity) {
		FragmentManager fm = activity.getSupportFragmentManager();
		TaskFragment fragment = (TaskFragment)fm.findFragmentByTag(TaskFragment.TASK_FRAGMENT_TAG);
		if (fragment == null) {
			fragment = new TaskFragment();
			try {
				fm.beginTransaction()
				  .add(fragment, TaskFragment.TASK_FRAGMENT_TAG)
				  .commitAllowingStateLoss();
				fm.executePendingTransactions();
			} catch (IllegalStateException e) {
				return null;
			}
		}
		return fragment;
	}

	/**
	 * Method to find the fragment used to execute background tasks
	 *
	 * @param view view designated to run tasks
	 * @param <T>  Must extend android.support.v4.app.Fragment
	 * @return TaskFragment instance for performing background tasks
	 */
	private static <T extends View & IBgTaskSimpleCallbacks> TaskFragment getFragment(T view) {
		Activity activity = getActivityFromContext(view.getContext());
		if (activity instanceof FragmentActivity) {
			return getFragment((FragmentActivity)activity);
		} else {
			throw new RuntimeException("View must be hosted by FragmentActivity");
		}
	}
	/**
	 * Method to find the fragment used to execute background tasks
	 *
	 * @param object view designated to run tasks
	 * @param <T>  Must extend IBgTasksFullCallbacks
	 * @return TaskFragment instance for performing background tasks
	 */
	private static <T extends IBgTasksFullCallbacks> TaskFragment getFragment(T object) {
		Activity activity = getActivityFromContext(object.getContext());
		if (activity instanceof FragmentActivity) {
			return getFragment((FragmentActivity)activity);
		} else {
			throw new RuntimeException("getContext() must return FragmentActivity context");
		}
	}

	/**
	 * Method which gets fragment identifier
	 *
	 * @param fragment fragment to be identified
	 * @return tag or id of the fragment
	 */
	private static Object getIdTagOrIdFromFragment(Fragment fragment) {
		String ftag = fragment.getTag();
		Integer id = fragment.getId();
		if (ftag != null) {
			return ftag;
		} else if (id != -1) {
			return id;
		} else {
			throw new RuntimeException("Fragment must have a valid id or string tag");
		}
	}

	/**
	 * Method which gets view identifier
	 *
	 * @param view view to be identified
	 * @return tag or id of the view
	 */
	private static Object getIdTagOrIdFromView(View view) {
		if (view.getTag() != null) {
			return view.getTag();
		} else if (view.getId() != View.NO_ID) {
			return view.getId();
		} else {
			throw new RuntimeException("View must have a valid id or string tag");
		}
	}

	protected static <V extends View & IBgTaskSimpleCallbacks, F extends Fragment & IBgTaskSimpleCallbacks,
	  A extends FragmentActivity & IBgTaskSimpleCallbacks, O extends IBgTasksFullCallbacks> void
	startFollowingTask(IBgTaskSimpleCallbacks callbacks,BaseTask task,Object params) {
		if (callbacks instanceof FragmentActivity) {
			task.setCallbackType(CallbackType.ACTIVITY);
			TaskFragment taskFragment = getFragment((A)callbacks);
			if (taskFragment != null) {
				taskFragment.startTask(task, params);
			} else {
				task.doOnCancelled();
			}
		}
		if (callbacks instanceof Fragment) {
			F fragment = (F)callbacks;
			Object id = getIdTagOrIdFromFragment(fragment);
			task.setCallbacksId(id);
			task.setCallbackType(CallbackType.FRAGMENT);
			TaskFragment taskFragment = getFragment(fragment.getActivity());
			if (taskFragment != null) {
				taskFragment.startTask(task, params);
			} else {
				task.doOnCancelled();
			}
		}
		if (callbacks instanceof View) {
			V view = (V)callbacks;
			task.setCallbackType(CallbackType.VIEW);
			Object id = getIdTagOrIdFromView(view);
			task.setCallbacksId(id);
			TaskFragment taskFragment = getFragment(view);
			if (taskFragment != null) {
				taskFragment.startTask(task, params);
			} else {
				task.doOnCancelled();
			}
		}
		if (callbacks instanceof IBgTasksFullCallbacks) {
			O object = (O)callbacks;
			task.setCallbackType(CallbackType.OBJECT);
			task.setCallbacksId(object);
			TaskFragment taskFragment = getFragment(object);
			if (taskFragment != null) {
				taskFragment.startTask(task, params);
			} else {
				task.doOnCancelled();
			}
		}

	}

	private static Activity getActivityFromContext(Context context) {
		while (context instanceof ContextWrapper) {
			if (context instanceof Activity) {
				return (Activity)context;
			}
			context = ((ContextWrapper)context).getBaseContext();
		}
		return null;
	}
}

package com.github.slezadav.backgroundTasks;

import android.support.v4.app.FragmentActivity;

/**
 * Created by davo on 30.5.16.
 */
public interface IBgTasksFullCallbacks extends IBgTaskCallbacks {
	FragmentActivity getActivity();
	IBgTasksFullCallbacks getSelfFromActivity();

}

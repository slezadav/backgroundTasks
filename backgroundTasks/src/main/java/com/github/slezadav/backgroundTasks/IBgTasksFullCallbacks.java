package com.github.slezadav.backgroundTasks;

import android.content.Context;

/**
 * Created by davo on 30.5.16.
 */
public interface IBgTasksFullCallbacks extends IBgTaskCallbacks {
	Context getContext();
	IBgTasksFullCallbacks getSelfFromActivity();
}

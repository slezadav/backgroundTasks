package com.example.backgroundtaskstest;

import com.github.slezadav.backgroundTasks.BaseTask;

/**
 * Created by david.slezak on 4.5.2016.
 */
public class TestTask2 extends BaseTask {
	int tagNumber = 0;

	public TestTask2(int tagNumber) {
		this.tagNumber = tagNumber;
	}

	@Override
	protected Object doInBackground(Object... params) {
		try {
			Thread.sleep(2000);
			publishProgress(true);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public String getTag() {
		return "task "+tagNumber;
	}
}

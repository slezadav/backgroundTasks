package com.example.backgroundtaskstest;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.robotium.solo.Solo;

/**
 * Test cases for sample application
 * Created by Davo on 14.6.2015.
 */
public class BackgroundTasksTest
        extends ActivityInstrumentationTestCase2<MainActivity> {
    private Solo solo;

    public BackgroundTasksTest() {
        super(MainActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testCase(){
        singleAsync();
        singleAsyncCancel();
        chainAsync();
        chainAsyncCancel();
        singleAsyncFragment();
        singleAsyncFragmentCancel();
        chainAsyncFragment();
        chainAsyncFragmentCancel();
        singleAsyncView();
        singleAsyncViewFragment();
        singleAsyncViewCancel();
        singleAsyncViewFragmentCancel();
        chainAsyncView();
        chainAsyncViewCancel();
        chainAsyncViewFragment();
        chainAsyncFragmentViewCancel();
    }


    public void singleAsync() {
        Log.d("BackgroundTasksTest", "singleAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+MainActivity.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.TASKTAG,3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess "+MainActivity.TASKTAG,5000));
    }

    public void chainAsync() {
        Log.d("BackgroundTasksTest", "chainAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess "+MainActivity.CHAINTAG,15000));
    }

    public void singleAsyncCancel() {
        Log.d("BackgroundTasksTest", "singleAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+MainActivity.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel "+MainActivity.TASKTAG,3000));
    }

    public void chainAsyncCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel "+MainActivity.CHAINTAG,3000));
    }



    public void singleAsyncFragment() {
        Log.d("BackgroundTasksTest", "singleAsyncFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Frg Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+TestFragment.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.TASKTAG,3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess "+TestFragment.TASKTAG,5000));
    }

    public void chainAsyncFragment() {
        Log.d("BackgroundTasksTest", "chainAsyncFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Chain");
        assertTrue("Chain was not ready", solo.waitForLogMessage("onTaskReady " + TestFragment.CHAINTAG, 3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskSuccess "+TestFragment.CHAINTAG,3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess "+TestFragment.CHAINTAG,15000));
    }

    public void singleAsyncFragmentCancel() {
        Log.d("BackgroundTasksTest", "singleAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Task");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+TestFragment.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg Cancel Task");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel "+TestFragment.TASKTAG,3000));
    }

    public void chainAsyncFragmentCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg Chain");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg Cancel Chain");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }

    public void singleAsyncView() {
        Log.d("BackgroundTasksTest", "singleAsyncView");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+MainActivity.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.TASKTAG,3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess "+MainActivity.TASKTAG,5000));
    }

    public void singleAsyncViewFragment() {
        Log.d("BackgroundTasksTest", "singleAsyncViewFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.waitForActivity(MainActivity.class,
                5000);
        solo.clickOnButton("Frg task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+MainActivity.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Task did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.TASKTAG,3000));
        assertTrue("Task did not succeed",solo.waitForLogMessage("onTaskSuccess "+MainActivity.TASKTAG,5000));
    }

    public void singleAsyncViewCancel() {
        Log.d("BackgroundTasksTest", "singleAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+MainActivity.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Task view");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel "+MainActivity.TASKTAG,3000));
    }

    public void singleAsyncViewFragmentCancel() {
        Log.d("BackgroundTasksTest", "singleAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg task view");
        assertTrue("Task not started",solo.waitForLogMessage("onTaskReady "+TestFragment.TASKTAG,1000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg task view");
        assertTrue("Task did not cancel",solo.waitForLogMessage("onTaskCancel "+TestFragment.TASKTAG,3000));
    }


    public void chainAsyncViewFragment() {
        Log.d("BackgroundTasksTest", "chainAsyncFragment");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg chain view");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+TestFragment.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess "+TestFragment.CHAINTAG,15000));
    }

    public void chainAsyncFragmentViewCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncFragmentCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Frg chain view");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Frg chain view");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel chain",3000));
    }

    public void chainAsyncView() {
        Log.d("BackgroundTasksTest", "chainAsync");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain view");
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.PORTRAIT);
        assertTrue("Chain did not continue",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        assertTrue("Chain did not publish progress",solo.waitForLogMessage("onTaskProgress "+MainActivity.CHAINTAG,3000));
        solo.setActivityOrientation(Solo.LANDSCAPE);
        assertTrue("Chain did not succeed",solo.waitForLogMessage("onTaskSuccess "+MainActivity.CHAINTAG,15000));
    }

    public void chainAsyncViewCancel() {
        Log.d("BackgroundTasksTest", "chainAsyncCancel");
        solo.setActivityOrientation(Solo.PORTRAIT);
        solo.clickOnButton("Chain view");
        solo.sleep(1000);
        solo.setActivityOrientation(Solo.LANDSCAPE);
        solo.clickOnButton("Chain view");
        assertTrue("Chain did not cancel",solo.waitForLogMessage("onTaskCancel "+MainActivity.CHAINTAG,3000));
    }


    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }
}
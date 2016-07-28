# backgroundTasks
Library providing solution to Android asynchronous request dealing with leaks and configuration changes

# Gradle Dependency (jCenter)

Add this library to dependencies in your module's `build.gradle` file:

```Gradle
dependencies {
    compile 'com.github.slezadav:backgroundTasks:1.6.1'
}
```

# Defining a task
First of all, note that all background tasks used with this library have to extend `BaseTask` class which is based on Android's `AsyncTask`.Right now `BaseTask` is not typed and behaves like `AsyncTask<Object,Object,Object>`. Using custom types might be included in future releases. All tasks are run in a retained fragment. 
The simplest example of a custom task is something like this:

```java
import com.github.slezadav.backgroundTasks.BaseTask;

public class MyTask extends BaseTask {
    @Override
    protected Object doInBackground(Object... params) {
        // do your stuff here
        return true;
    }
     //optional, but useful
     @Override
     public String getTag(){
     return "MyTag";
     }
}
```
As with AsyncTask you can you can use methods like `publishProgress(Object object)`,`cancel(Boolean mayInterruptIfRunning)`,`get()` and so on.

If you want to use methods like `cancel` or `isTaskRunning`, your implementation shoul override `getTag` method of the `BaseTask`.

#Task callbacks
In order to start a task your `Activity` , `Fragment` , `View` or `Object` must implement `IBgTaskCallbacks`, `IBgTaskSimpleCallbacks` or `IBgTaskFullCallbacks`  interface, by which the results are delivered. The `IBgTaskCallbacks` interface consists of the following methods:

* `onTaskReady(BaseTask task)` - called after `onPreExecute()` of the task was completed
* `onTaskProgressUpdate(BaseTask task,Object.. progress)` - called after the task has called `publishProgress(Progress... values)`
* `onTaskCancelled(BaseTask task,Object result)` - called after the task has been cancelled 
* `onTaskSuccess(BaseTask task, Object result)` - called after the task has succesfully completed (did not throw `Exception` during its process)
* `onTaskFail(BaseTask task, Exception exception)` - called after the task has not completed succesfully (threw `Exception` during its process)
* `Context getContext()` - used to get the Activity from an object, only part of `IBgTaskFullCallbacks`
* `IBgTasksFullCallbacks getSelfFromActivity()` - used for the object to find itself in an Activity, only part of `IBgTaskFullCallbacks`

If you use `IBgTaskSimpleCallbacks` only `onTaskSuccess(BaseTask task, Object result)` and `onTaskFail(BaseTask task, Exception exception)` are in use. While starting a task from an object which is not `Activity` , `Fragment` or `View` the `IBgTaskFullCallbacks` must be used.

These callbacks will be delivered even if orientation change occurs. In case the task should deliver the callback in the exact moment when the activity is recreated, it will be delivered during `onResume()` lifecycle call.
These callbacks are kept as `WeakReference`s and reassigned whenever the activity or fragment is recreated so the leaks should never occur.

# Starting a task

Tasks are started via static methods in `BgTasks` class by calling:

```java
BgTasks.startTask(activity/*(fragment,view,object)*/,task,params)
```
or

```java
BgTasks.startTask(activity/*(fragment,view,object)*/,task,params)
```
The `tag` parameter in the call accepts a String which is then used to identify the task.

Last parameter is a varargs used for parameters to the task.It works the same way as in AsyncTask's execute. It is also possible to pass the params to the task before it is started (constructor,setters).

Currently only compatibility version of fragments and activities are supported so a task can be started with `android.support.v4.app.FragmentActivity`, `android.support.v4.app.Fragment` or `Views` attached to them and everything that extends the previous.
Tasks can also be used as an ordinary `AsyncTask` from anywhere else (in this case orientation changes and leaks are not managed by the library !).

Simple example of starting a task from activity:
```java
public class MainActivity extends FragmentActivity implements IBgTaskSimpleCallbacks {
public static final String TASKTAG="my_task";

 private void start(){
     BgTasks.startTask(this,new MyTask());
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
```

Task can be cancelled at any time by calling:
```java
BgTasks.cancelTask(activity/*(fragment,view)*/,tag);
```

It is also possible to use custom executor by using :
```java
BgTasks.startTask(activity/*(fragment,view)*/,executor,task,params);
```

or provided serial and parallel executors by `SerialExecutor.getExecutorInstance()` and `ParallelExecutor.getExecutorInstance()`

# Task chains
A `Chain` is a mechanism, taht allows you to start multiple tasks, taht will be processed sequentially. It consists of one or more tasks. For these tasks there are same rules as described before. Every task in chain (except the first one) can use the results of a previous task as its parameters.
Example of constructing and starting a chain:
```java
BgTasks.startTask(MainActivity.this,new BgTaskChain().addTask(new TestTask()).addTask(new TestTask()));
```
you can also use tags with chains as follows

```java
BgTasks.startTask(MainActivity.this,new BgTaskChain().addTask(new TestTask().withTag(TASKTAG)).addTask(new TestTask()).withTag(CHAINTAG));
```
Here the chain itself and the first task were given custom tags, whereas the second tag was not.


package fr.projet.finalselfenergy.asyncs;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by caroline on 23/07/17.
 */

public class AsyncTool {
    public static final String TAG = "AsyncTool";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static void startMyTask(@NonNull AsyncTask asyncTask) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { // Android 4.4 (API 19) and above
            // Parallel AsyncTasks are possible, with the thread-pool size dependent on device
            // hardware
            asyncTask.execute();
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            Log.i(TAG, "use asyncTask executor");
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            asyncTask.execute();
        }
    }
}

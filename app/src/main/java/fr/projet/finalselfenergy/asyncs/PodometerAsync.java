package fr.projet.finalselfenergy.asyncs;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.concurrent.TimeUnit;

import fr.projet.finalselfenergy.activities.MainActivity;


/**
 * Created by caroline on 10/06/17.
 */

public class PodometerAsync extends AsyncTask<Object, Value, Long> implements OnDataPointListener, ResultCallback {

    private GoogleApiClient mClient;
    private MainActivity mainActivity;
    private static final String TAG = "PodometerAsync";

    public PodometerAsync(GoogleApiClient client, MainActivity mainActivity) {
        super();
        this.mClient = client;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Long doInBackground(Object... params) {
        Log.i(TAG, "doInBackground");
        invokeFitnessAPIs();

        return 1L;
    }

    @Override
    protected void onProgressUpdate(Value... progress) {
        Log.i(TAG, "onProgressUpdate");
        int val = progress[0].asInt();
        this.mainActivity.updatePodometerView(val);
    }

    @Override
    protected void onPostExecute(Long aLong) {
        Log.i(TAG, "onPostExecute");
        super.onPostExecute(aLong);
        //Total steps covered for that day
        Log.i(TAG, "Total steps: " + aLong);

    }

    protected void invokeFitnessAPIs() {
        Log.i(TAG, "invokeFitnessAPIs");
        SensorRequest req = new SensorRequest.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();
        PendingResult<com.google.android.gms.common.api.Status> res = Fitness.SensorsApi.add(this.mClient, req, this);
        res.setResultCallback(this);
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        Log.i(TAG, "onDataPoint" + dataPoint.toString());
        for (Field field : dataPoint.getDataType().getFields()) {
            Value val = dataPoint.getValue(field);
            publishProgress(val);
        }
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.i(TAG, "onResult" + result.toString());
    }
}
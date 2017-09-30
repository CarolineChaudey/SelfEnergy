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
 * Created by caroline on 23/07/17.
 */

public class CaloriesAsync extends AsyncTask<Object, Value, Double> implements OnDataPointListener, ResultCallback {

    private GoogleApiClient mClient;
    private MainActivity mainActivity;
    private static final String TAG = "CaloriesAsync";

    CaloriesAsync(GoogleApiClient client, MainActivity mainActivity) {
        super();
        Log.i(TAG, "CaloriesAsync");
        this.mClient = client;
        this.mainActivity = mainActivity;
    }

    @Override
    protected Double doInBackground(Object... params) {
        Log.i(TAG, "doInBackground");
        invokeFitnessAPIs();

        return 1D;
    }

    @Override
    protected void onProgressUpdate(Value... progress) {
        Log.i(TAG, "onProgressUpdate");
        Float val = progress[0].asFloat();
        //sthis.mainActivity.updateCaloriesView(val);
    }

    @Override
    protected void onPostExecute(Double aDouble) {
        Log.i(TAG, "onPostExecute");
        super.onPostExecute(aDouble);
        //Total steps covered for that day
        Log.i(TAG, "Total calories: " + aDouble);

    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.i(TAG, "onResult " + result.toString());
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        Log.i(TAG, "onDataPoint" + dataPoint.toString());
        for (Field field : dataPoint.getDataType().getFields()) {
            Value val = dataPoint.getValue(field);
            publishProgress(val);
        }
    }

    protected void invokeFitnessAPIs() {
        Log.i(TAG, "invokeFitnessAPIs");
        SensorRequest req = new SensorRequest.Builder()
                .setDataType(DataType.TYPE_CALORIES_EXPENDED)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();
        PendingResult<com.google.android.gms.common.api.Status> res = Fitness.SensorsApi.add(this.mClient, req, this);
        res.setResultCallback(this);
    }
}

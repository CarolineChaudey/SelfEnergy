package fr.projet.finalselfenergy.activities;


import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import java.text.ParseException;
import java.util.Date;

import fr.projet.finalselfenergy.PodometerWidget;
import fr.projet.finalselfenergy.asyncs.AsyncTool;
import fr.projet.finalselfenergy.asyncs.PodometerAsync;
import fr.projet.finalselfenergy.R;
import fr.projet.finalselfenergy.data.DailyStat;
import fr.projet.finalselfenergy.data.DatabaseHandler;
import fr.projet.finalselfenergy.fragments.GoalFragment;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoalFragment.OnFragmentInteractionListener {

    private DatabaseHandler db = DatabaseHandler.getInstance(this);
    private GoogleApiClient mClient = null;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private boolean authInProgress = false;
    private static final String TAG = "MainActivity";
    private static final String NB_STEPS = "nbSteps";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(Fitness.SCOPE_ACTIVITY_READ)
                .addScope(Fitness.SCOPE_ACTIVITY_READ_WRITE)
                .useDefaultAccount()
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        this.mClient.connect();

        initButton();
    }

    private void initButton() {
        Button button = (Button) findViewById(R.id.historyButton);
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Connected");
        PodometerAsync podometer = new PodometerAsync(this.mClient, this);
        AsyncTool.startMyTask(podometer);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "ConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "ConnectionFailed");
        Log.i(TAG, "reason : " + connectionResult.getResolution().toString());
        Log.i(TAG, "code : " + connectionResult.getErrorCode());
        if (!connectionResult.hasResolution()) {
            //GooglePlayServicesUtil.showErrorDialogFragment(1, this, null, 1, null);
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization dialog is displayed to the user.
        if (!authInProgress) {
            try {
                Log.i(TAG, "Attempting to resolve failed connection");
                authInProgress = true;
                connectionResult.startResolutionForResult(this, REQUEST_PERMISSIONS_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Exception while starting resolution activity", e);
            }
        }
    }

    public void updatePodometerView(int val) {
        Log.i(TAG, "updatePodometerView");
        Log.i(TAG, val + "");
        TextView dailyStepsView = (TextView) findViewById(R.id.dailysteps);
        Integer dailySteps = Integer.parseInt(dailyStepsView.getText().toString());
        dailySteps += val;
        saveStats(dailySteps);
        displayPodometerView(dailySteps);
        sendToWidget();
    }

    private void sendToWidget() {
        Intent intent = new Intent(this, PodometerWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = {R.xml.podometer_widget_info};
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
    }
/*
    public void updateCaloriesView(Float val) {
        Log.i(TAG, "updateCaloriesView");
        Log.i(TAG, val + "");
        TextView caloriesView = (TextView) findViewById(R.id.dailyCal);
        Float dailyCals = Float.parseFloat(caloriesView.getText().toString());
        dailyCals += val;
        displayCaloriesView(dailyCals);
    }
*/
    public void displayPodometerView(int val) {
        Log.i(TAG, "displayPodometerView");
        Log.i(TAG, val + "");
        TextView dailyStepsView = (TextView) findViewById(R.id.dailysteps);
        dailyStepsView.setText(val + "");
    }
/*
    public void displayCaloriesView(Float val) {
        Log.i(TAG, "displayCaloriesView");
        Log.i(TAG, val + "");
        TextView dailyCaloriesView = (TextView) findViewById(R.id.dailyCal);
        dailyCaloriesView.setText(val + "");
    }
*/
    private Integer getPodometerCount() {
        Log.i(TAG, "getPodometerCount");
        TextView dailyStepsView = (TextView) findViewById(R.id.dailysteps);
        return Integer.parseInt(dailyStepsView.getText().toString());
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        //saveStats();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        recoverSteps();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Integer nbSteps = getPodometerCount();
        outState.putInt(NB_STEPS, nbSteps);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Integer nbSteps = savedInstanceState.getInt(NB_STEPS);
        updatePodometerView(nbSteps);
    }

    private void saveStats(Integer steps) {
        DailyStat dailyStat = new DailyStat(new Date(), steps);
        DailyStat found = null;
        try {
            found = db.getDailyStat(dailyStat.getFormattedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null == found) {
            db.addDailyStat(dailyStat);
        } else {
            db.updateDailyStat(dailyStat);
        }
    }

    private void recoverSteps() {
        Integer nbSteps = getTodaySteps();
        displayPodometerView(nbSteps);
    }

    private Integer getTodaySteps() {
        String today = getFormattedToday();
        DailyStat result = null;
        try {
            result = db.getDailyStat(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (null == result) {
            return 0;
        }
        Log.i(TAG, "recovered steps = " + result.toString());
        return result.get_steps();
    }

    private String getFormattedToday() {
        Date today = new Date();
        return DailyStat.DATE_FORMAT.format(today);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
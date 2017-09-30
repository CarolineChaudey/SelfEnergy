package fr.projet.finalselfenergy.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.text.ParseException;
import java.util.List;

import fr.projet.finalselfenergy.DailyStatsAdapter;
import fr.projet.finalselfenergy.R;
import fr.projet.finalselfenergy.data.DailyStat;
import fr.projet.finalselfenergy.data.DatabaseHandler;

public class HistoryActivity extends AppCompatActivity {

    private ListView dailyStatsListView;
    private DatabaseHandler databaseHandler = DatabaseHandler.getInstance(this);
    private static final String TAG = "HistoryActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        dailyStatsListView = (ListView) findViewById(R.id.listDailyData);
        fillList();
        initButton();
    }

    private void fillList() {
        List<DailyStat> dailyStats = getAllDailyStats();
        Log.i(TAG, "got" + dailyStats.toString());
        //ArrayAdapter<DailyStat> dailyStatArrayAdapter = new ArrayAdapter<DailyStat>(this, R.layout.activity_history, dailyStats);
        //this.dailyStatsListView.setAdapter(dailyStatArrayAdapter);
        DailyStatsAdapter adapter = new DailyStatsAdapter(this, R.layout.activity_history, dailyStats);
        this.dailyStatsListView.setAdapter(adapter);
    }

    private void initButton() {
        Button button = (Button) findViewById(R.id.toPodometer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                HistoryActivity.this.startActivity(intent);
            }
        });
    }

    private List<DailyStat> getAllDailyStats() {
        try {
            return this.databaseHandler.getAllDailyStats();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}

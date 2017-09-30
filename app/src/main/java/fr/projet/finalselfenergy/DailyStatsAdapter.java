package fr.projet.finalselfenergy;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

import fr.projet.finalselfenergy.data.DailyStat;

/**
 * Created by caroline on 23/07/17.
 */

public class DailyStatsAdapter extends ArrayAdapter<DailyStat> {

    private List<DailyStat> dailyStats;
    LayoutInflater lInflater;
    private static final String TAG = "DailyStatsAdapter";

    public DailyStatsAdapter(Context context, int textViewResourceId, List<DailyStat> objects) {
        super(context, textViewResourceId, objects);
        dailyStats = objects;
        lInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = lInflater.inflate(R.layout.custom_list_item, parent, false);
        }
        TextView dayView = (TextView) v.findViewById(R.id.rowDay);
        TextView stepsView = (TextView) v.findViewById(R.id.rowSteps);
        DailyStat dailyStat = dailyStats.get(position);
        Log.i(TAG, "got : " + dailyStat);
        dayView.setText(dailyStat.getFormattedDate());
        stepsView.setText(dailyStat.get_steps().toString());
        return v;
    }

}
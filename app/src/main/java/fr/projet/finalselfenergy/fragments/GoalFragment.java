package fr.projet.finalselfenergy.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import fr.projet.finalselfenergy.R;


public class GoalFragment extends Fragment {

    private TextView goalField;
    private OnFragmentInteractionListener mListener;

    public GoalFragment() {
        // Required empty public constructor
    }

    public static GoalFragment newInstance(String param1, String param2) {
        GoalFragment fragment = new GoalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_goal, container, false);
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        initGoal();
        super.onActivityCreated(savedInstanceState);
    }

    private void initGoal() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.nb_steps_oms);
        String goalValue = getString(R.string.goal_preference);
        Integer highScore = sharedPref.getInt(goalValue, Integer.parseInt(defaultValue));

        this.goalField = (TextView) getView().findViewById(R.id.goalField);
        this.goalField.setText(highScore.toString());
    }

    @Override
    public void onPause() {
        super.onPause();
        saveGoal();
    }

    private void saveGoal() {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        try {
            Integer stepsGoal = Integer.parseInt(this.goalField.getText().toString());
            editor.putInt(getString(R.string.goal_preference), stepsGoal);
            editor.commit();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

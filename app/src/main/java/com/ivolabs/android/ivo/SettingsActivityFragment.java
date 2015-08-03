package com.ivolabs.android.ivo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.parse.ParseUser;


/**
 * A placeholder fragment containing a simple view.
 */
public class SettingsActivityFragment extends Fragment {

    public SettingsActivityFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SeekBar rangeBar = (SeekBar) view.findViewById(R.id.rangeSeekBar);
        final SharedPreferences prefs = getActivity().getApplicationContext().getSharedPreferences("Ivo", Context.MODE_PRIVATE);
        float curProgress = prefs.getFloat("range", 250.0f);
        if (curProgress == 250.0f) {
            rangeBar.setProgress(0);
        } else if (curProgress == 1250.0f) {
            rangeBar.setProgress(1);
        } else if (curProgress == 6250.0f) {
            rangeBar.setProgress(2);
        }


        rangeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Editor editor = prefs.edit();

                if (progress == 0) {
                    editor.putFloat("range", 250.0f);
                } else if (progress == 1) {
                    editor.putFloat("range", 1250.0f);
                } else if (progress == 2) {
                    editor.putFloat("range", 6250.0f);
                }

                editor.commit();

            }
        });


        Button logoutButton = (Button) view.findViewById(R.id.logoutUser_button);

        logoutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                ParseUser.logOut();

                Intent intent = new Intent(SettingsActivityFragment.this.getActivity(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        return view;
    }
}

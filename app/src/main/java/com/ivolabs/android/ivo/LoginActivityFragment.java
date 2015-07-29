package com.ivolabs.android.ivo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    private EditText userNameEditText;
    private EditText passWordEditText;

    public LoginActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        userNameEditText = (EditText) view.findViewById(R.id.username_for_login);
        passWordEditText = (EditText) view.findViewById(R.id.password_for_login);

        Button registerButton = (Button) view.findViewById(R.id.login_user_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                loginUser();
            }
        });

        return view;
    }

    public void loginUser() {
        String username = userNameEditText.getText().toString();
        String password = passWordEditText.getText().toString();

        final ProgressDialog dialog = new ProgressDialog(LoginActivityFragment.this.getActivity());
        dialog.setMessage("Logging in...");
        dialog.show();
        // Call the Parse login method
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(LoginActivityFragment.this.getActivity(), "Username or Password is Incorrect!", Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(LoginActivityFragment.this.getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}

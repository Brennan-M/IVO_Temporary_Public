package com.ivolabs.android.ivo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


/**
 * A placeholder fragment containing a simple view.
 */
public class SignupActivityFragment extends Fragment {


    private EditText newUsername;
    private EditText newPassword;
    private EditText passwordConfirmation;

    public SignupActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        newUsername = (EditText) view.findViewById(R.id.new_username);
        newPassword = (EditText) view.findViewById(R.id.new_password);
        passwordConfirmation = (EditText) view.findViewById(R.id.new_password_confirmation);

        Button registerButton = (Button) view.findViewById(R.id.register_new_user);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                registerNewUser();
            }
        });

        return view;
    }

    private void registerNewUser() {
        String username = newUsername.getText().toString().trim();
        String password = newPassword.getText().toString().trim();
        String passwordDuplicate = passwordConfirmation.getText().toString().trim();

        boolean registrationError = false;

        StringBuilder registrationErrorMessage = new StringBuilder();

        // TODO: Restrict username based on certain characters, force password to be secure, make sure Username is unique
        if (username.length() == 0) {
            registrationError = true;
            registrationErrorMessage.append("Username cannot be blank!");
        }
        if (password.length() == 0) {
            registrationError = true;
            registrationErrorMessage.append("Password cannot be blank!");
        }
        if (username.length() < 5) {
            registrationError = true;
            registrationErrorMessage.append("Username must be longer than 5 characters.");
        }
        if (password.length() < 5) {
            registrationError = true;
            registrationErrorMessage.append("Password must be longer than 5 characters.");
        }
        if (!password.equals(passwordDuplicate)) {
            registrationError = true;
            registrationErrorMessage.append("Passwords do not match!");
        }

        if (registrationError) {
            Toast.makeText(SignupActivityFragment.this.getActivity(), registrationErrorMessage.toString(), Toast.LENGTH_LONG).show();
        }

        ParseUser newUser = new ParseUser();
        newUser.setUsername(username);
        newUser.setPassword(password);

        // Call the Parse signup method
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Toast.makeText(SignupActivityFragment.this.getActivity(), "Error: Please try again...", Toast.LENGTH_LONG).show();
                } else {
                    // Route back to main activity which should then lead to our homescreen activity.
                    Intent intent = new Intent(SignupActivityFragment.this.getActivity(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }
}

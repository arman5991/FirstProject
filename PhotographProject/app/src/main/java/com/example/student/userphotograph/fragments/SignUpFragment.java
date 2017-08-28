package com.example.student.userphotograph.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.student.userphotograph.R;
import com.example.student.userphotograph.activityes.HomeActivity;
import com.example.student.userphotograph.utilityes.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private EditText mNameEd;
    private EditText mEmailEd;
    private EditText mPasswordEd;
    private static SignUpFragment instance;
    private DatabaseReference mRef;

    public SignUpFragment() {
    }

    public static SignUpFragment newInstance() {
        if (instance == null) instance = new SignUpFragment();
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference().child(Constants.PHOTOGRAPHS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        findViewsSetLIsteners(rootView);
        return rootView;
    }

    private void findViewsSetLIsteners(View rootView) {
        mNameEd = (EditText) rootView.findViewById(R.id.full_name_sign_up);
        mEmailEd = (EditText) rootView.findViewById(R.id.email_sign_up);
        mPasswordEd = (EditText) rootView.findViewById(R.id.password_sign_up);
        Button mSignUpBtn = (Button) rootView.findViewById(R.id.button_sign_up);
        mSignUpBtn.setOnClickListener(this);
    }

    private boolean isValidateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(mEmailEd.getText().toString())) {
            mEmailEd.setError("Required");
            valid = false;
        }

        if (TextUtils.isEmpty(mPasswordEd.getText().toString())) {
            mPasswordEd.setError("Required");
            valid = false;
        }

        if (TextUtils.isEmpty(mNameEd.getText().toString())) {
            mNameEd.setError("Required");
            valid = false;
        }

        return valid;
    }

    public void registration() {
        String email = mEmailEd.getText().toString();
        String password = mPasswordEd.getText().toString();

        if (isValidateForm()) {
            final ProgressDialog mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setTitle("Loading to sign up");
            mProgressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                mProgressDialog.dismiss();
                                Toast.makeText(getContext(), "Successful registration", Toast.LENGTH_SHORT).show();

                                mUser = mAuth.getCurrentUser();
                                mRef.child(mUser.getUid()).child(Constants.NAME).setValue(mNameEd.getText().toString());
                                mRef.child(mUser.getUid()).child(Constants.RATING).setValue(0);
                                Intent goToHomeActivity = new Intent(getContext(), HomeActivity.class);
                                startActivity(goToHomeActivity);
                            } else
                                mProgressDialog.dismiss();
                            Toast.makeText(getContext(), "Unsuccessful registration", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_sign_up) registration();
    }
}

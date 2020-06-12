package com.adminturnos.UserManagment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.Listeners.ListenerAuthenticator;
import com.adminturnos.R;

import static com.adminturnos.Values.RC_SIGN_UP;

public class FragmentUserManagement extends Fragment {
    Authenticator authenticatorGoogle;

    public FragmentUserManagement() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authenticatorGoogle = new AuthenticatorGoogle(getActivity(), new ListenerAuthenticatorGoogle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.google_sign_in_button).setOnClickListener(new OnClickListenerGoogleSignUp());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        authenticatorGoogle.onActivityResult(data);
    }

    private class OnClickListenerGoogleSignUp implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                startActivityForResult(authenticatorGoogle.getSignUpIntent(), RC_SIGN_UP);
            } catch (ExceptionEmailInUse exceptionEmailInUse) {
                exceptionEmailInUse.printStackTrace();
            }
        }
    }

    private class ListenerAuthenticatorGoogle implements ListenerAuthenticator {

        @Override
        public void onComplete(int resultCode) {
            getActivity().setResult(resultCode, null);
            getActivity().finish();
        }
    }

}
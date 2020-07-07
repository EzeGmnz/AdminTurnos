package com.adminturnos.Activities.SignIn;

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
import com.adminturnos.UserManagment.Authenticator;
import com.adminturnos.UserManagment.AuthenticatorGoogle;
import com.adminturnos.UserManagment.UserManagment;

import static com.adminturnos.Values.RC_SIGN_UP;

public class FragmentUserManagement extends Fragment {

    public FragmentUserManagement() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Authenticator authenticatorGoogle = new AuthenticatorGoogle(getActivity(), new ListenerAuthenticatorGoogle());
        //TODO
        UserManagment.getInstance().setAuthenticator(authenticatorGoogle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.google_sign_in_button).setOnClickListener(new OnClickListenerGoogleSignIn());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UserManagment.getInstance().getAuthenticator().onActivityResult(data);
    }

    private class OnClickListenerGoogleSignIn implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            try {
                startActivityForResult(UserManagment.getInstance().getAuthenticator().getSignUpIntent(), RC_SIGN_UP);
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
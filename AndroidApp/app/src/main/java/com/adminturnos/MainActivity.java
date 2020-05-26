package com.adminturnos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adminturnos.Exceptions.ExceptionEmailInUse;
import com.adminturnos.Listeners.ListenerUserManagement;
import com.adminturnos.ObjectInterfaces.ServiceProvider;
import com.adminturnos.UserManagment.SignUp;
import com.adminturnos.UserManagment.SignUpGoogle;

public class MainActivity extends AppCompatActivity {
    SignUp signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        signUp = new SignUpGoogle(this);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    signUp.signUp(new ListenerUserManagement() {
                        @Override
                        public void onComplete(ServiceProvider e) {

                        }

                        @Override
                        public void onFailure(String message) {

                        }
                    });
                } catch (ExceptionEmailInUse exceptionEmailInUse) {
                    exceptionEmailInUse.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        signUp.onActivityResult(requestCode, data);
    }
}

package com.adminturnos.Activities.MainScreen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.adminturnos.Activities.JobRequest.JobRequestActivity;
import com.adminturnos.Activities.NewPlace.NewPlaceActivity;
import com.adminturnos.Activities.Place.AdminPlacesActivity;
import com.adminturnos.Activities.SignIn.ActivitySignIn;
import com.adminturnos.R;
import com.adminturnos.UserManagment.AuthenticatorGoogle;
import com.adminturnos.UserManagment.UserManagment;
import com.adminturnos.Values;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private FragmentMainJobs fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        checkUserIsAuthenticated();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void checkUserIsAuthenticated() {
        GoogleSignInAccount account = getAuthenticatedUser();
        if (account != null) {
            displayAuthenticatedUI();
        } else {
            startLogInActivity();
        }
    }

    private GoogleSignInAccount getAuthenticatedUser() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        return account;
    }

    private void startLogInActivity() {
        Intent intent = new Intent(this, ActivitySignIn.class);
        startActivityForResult(intent, Values.RC_SIGN_IN_ACTIVITY);
    }

    private void displayAuthenticatedUI() {
        SharedPreferences sharedPreferences = getSharedPreferences(Values.SHARED_PREF_NAME, MODE_PRIVATE);
        String accessToken = sharedPreferences.getString(Values.SHARED_PREF_ACCESS_TOKEN, null);
        UserManagment.getInstance().setAccessToken(accessToken);
        //TODO
        UserManagment.getInstance().setAuthenticator(new AuthenticatorGoogle(this, null));

        setUpDrawer();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshContent();
            }
        }, 100);
    }

    private void setUpDrawer() {
        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.hamburger_black);

        drawerLayout = findViewById(R.id.drawerMain);
        NavigationView navigationView = (findViewById(R.id.nav_view));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(new NavigationItemSelectedListener());

        TextView textViewName = navigationView.getHeaderView(0)
                .findViewById(R.id.textViewName);

        setTitle("");
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        textViewName.setText(account.getGivenName());
    }

    private void refreshContent() {
        if (fragmentMain == null) {
            fragmentMain = new FragmentMainJobs();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_main_screen, fragmentMain);
            transaction.commit();
        } else {
            fragmentMain.refresh();
        }
    }

    private void signOut() {
        UserManagment.getInstance().signOut(new SignOutListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case Values.RC_SIGN_IN_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    checkUserIsAuthenticated();
                } else {
                    finish();
                }
                break;
            case Values.RC_NEW_PLACE:
                if (resultCode == Activity.RESULT_OK) {
                    refreshContent();
                }
                break;
        }
    }

    private void startNewPlaceActivity() {
        Intent intent = new Intent(this, NewPlaceActivity.class);
        startActivityForResult(intent, Values.RC_NEW_PLACE);
    }

    private void startJobRequestsActivity() {
        Intent intent = new Intent(this, JobRequestActivity.class);
        startActivity(intent);
    }

    private void startAdminPlacesActivity() {
        Intent intent = new Intent(this, AdminPlacesActivity.class);
        startActivity(intent);
    }

    private class SignOutListener implements OnCompleteListener<Void> {

        @Override
        public void onComplete(@NonNull Task<Void> task) {
            startLogInActivity();
            UserManagment.getInstance().invalidate();
        }
    }

    private class NavigationItemSelectedListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.nav_new_place:
                    startNewPlaceActivity();
                    break;
                case R.id.nav_job_request:
                    startJobRequestsActivity();
                    break;
                case R.id.nav_admin_places:
                    startAdminPlacesActivity();
                    break;
                case R.id.nav_sign_out:
                    signOut();
                    break;
            }

            drawerLayout.closeDrawer(GravityCompat.START);

            return true;
        }
    }
}

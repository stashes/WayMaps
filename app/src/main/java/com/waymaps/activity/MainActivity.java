package com.waymaps.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.maps.model.LatLng;
import com.waymaps.R;
import com.waymaps.api.RetrofitService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.LogoutCredentials;
import com.waymaps.data.requestEntity.UpdateCredentials;
import com.waymaps.data.responseEntity.User;
import com.waymaps.fragment.BalanceFragment;
import com.waymaps.fragment.GMapFragment;
import com.waymaps.intent.SessionUpdateServiceIntent;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.JSONUtil;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.LocalPreferencesManagerUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public static User authorisedUser;
    public static Fragment currentFragment;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getUserFromIntent();
        startServices();

        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        displaySelectedScreen(R.id.nav_map);
    }

    private void startServices() {
        SessionUpdateServiceIntent sessionUpdateServiceIntent = new SessionUpdateServiceIntent(this);
        UpdateCredentials updateCredentials = new UpdateCredentials(Action.SESSION_UPDATE);
        updateCredentials.setUser_id(authorisedUser.getId());
        updateCredentials.setIdentificator(SystemUtil.getWifiMAC(this));
        updateCredentials.setOs(SystemUtil.getAndroidVersion());
        try {
            sessionUpdateServiceIntent.putExtra("sessionUpdate", JSONUtil.getObjectMapper().writeValueAsString(updateCredentials));
            this.startService(sessionUpdateServiceIntent);
        } catch (JsonProcessingException e) {
            logger.debug("Update service haven\'t started");
        }


    }

    private void getUserFromIntent() {
        try {
            authorisedUser = JSONUtil.getObjectMapper().readValue(getIntent().getExtras()
                    .getCharSequence("user").toString(), User.class);
            logger.debug("User {} reads successfully", authorisedUser.getId());
        } catch (IOException e) {
            logger.debug("Something went wrong while try to parse user");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.search_button) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        displaySelectedScreen(id);
        return true;
    }

    private void displaySelectedScreen(int id) {
        if (id == R.id.nav_map) {
            map();
        } else if (id == R.id.nav_history) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_save_location) {
            saveLocation();
        } else if (id == R.id.nav_balance) {
            balance();
        } else if (id == R.id.nav_tech_supp) {

        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    private void saveLocation() {
        Toast toast = null;
        if (currentFragment instanceof GMapFragment){
            LatLng target = ((GMapFragment) currentFragment).getmMap().getCameraPosition().target;
            float zoom = ((GMapFragment) currentFragment).getmMap().getCameraPosition().zoom;
            LocalPreferenceManager.saveLatLonZoom(this,target.latitude,target.longitude,zoom);
            toast = Toast.makeText(getApplicationContext(),
                    R.string.location_saved,
                    Toast.LENGTH_SHORT);
        } else{
            toast = Toast.makeText(getApplicationContext(),
                    R.string.error_saving_location,
                    Toast.LENGTH_SHORT);
        }

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void balance() {
        Bundle bundle = null;
        try {
            bundle = ApplicationUtil.setValueToBundle(new Bundle(),"user", authorisedUser);
        } catch (JsonProcessingException e) {
            logger.debug("Error while trying write to bundle");
        }
        currentFragment= new BalanceFragment();
        setActionBarTitleColor("Balance");
        setFragmentActive(currentFragment, bundle);
    }

    private void map() {
        Bundle bundle = null;
        try {
            bundle = ApplicationUtil.setValueToBundle(new Bundle(),"user", authorisedUser);
        } catch (JsonProcessingException e) {
            logger.debug("Error while trying write to bundle");
        }
        currentFragment= new GMapFragment();
        setActionBarTitleColor("");
        setFragmentActive(currentFragment, bundle);
    }

    private void setFragmentActive(Fragment fragment, Bundle bundle){
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_main, fragment);
        ft.commit();

    }

    private void logout() {
        LogoutCredentials logoutCredentials = getLogoutCredential();
        RetrofitService.getWayMapsService().logoutProcedure(logoutCredentials.getAction(), logoutCredentials.getUserId(),
                logoutCredentials.getIdentificator()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                logger.info("Successful logout procedure");
                LocalPreferencesManagerUtil.clearCredentials(MainActivity.this);
                setResult(1);
                finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logger.info("Failed logout procedure");
                LocalPreferencesManagerUtil.clearCredentials(MainActivity.this);
                setResult(0);
                finish();
            }
        });

    }

    private LogoutCredentials getLogoutCredential() {
        return new LogoutCredentials(Action.LOGOUT, SystemUtil.getWifiMAC(this), authorisedUser.getId());
    }

    public void setActionBarTitleColor(String title) {
        getSupportActionBar().setTitle(title);
    }
}


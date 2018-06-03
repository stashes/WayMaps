package com.waymaps.activity;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.Gravity;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;
import com.waymaps.R;
import com.waymaps.api.RetrofitService;
import com.waymaps.api.WayMapsService;
import com.waymaps.data.requestEntity.Action;
import com.waymaps.data.requestEntity.LogoutCredentials;
import com.waymaps.data.requestEntity.Procedure;
import com.waymaps.data.requestEntity.UpdateCredentials;
import com.waymaps.data.responseEntity.FinGet;
import com.waymaps.data.responseEntity.User;
import com.waymaps.fragment.AbstractFragment;
import com.waymaps.fragment.BalanceFragment;
import com.waymaps.fragment.ChooseMapTypeFragment;
import com.waymaps.fragment.GMapFragment;
import com.waymaps.fragment.GetCurrentFragment;
import com.waymaps.fragment.GroupFragment;
import com.waymaps.fragment.HistoryFragment;
import com.waymaps.fragment.TicketListFragment;
import com.waymaps.intent.LoginActivityIntent;
import com.waymaps.intent.SessionUpdateServiceIntent;
import com.waymaps.util.ApplicationUtil;
import com.waymaps.util.JSONUtil;
import com.waymaps.util.LocalPreferenceManager;
import com.waymaps.util.LocalPreferencesManagerUtil;
import com.waymaps.util.SystemUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

    public static Boolean isGroupAvaible;

    public static Boolean firstLaunch = true;

    public ArrayList<Handler> handlers;

    public Boolean backgroundTaskExecuting = false;


    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    private boolean doubleTap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getUserFromIntent();
        startServices();

        isGroupAvaible = false;

        navigationView.setNavigationItemSelectedListener(this);


        TextView userTitle = (TextView) navigationView.getHeaderView(0).findViewById(R.id.userTitle_nav_bar);
        TextView firmTitle = navigationView.getHeaderView(0).findViewById(R.id.firmTitle_nav_bar);
        userTitle.setText(authorisedUser.getUser_title());
        firmTitle.setText(authorisedUser.getFirm_title());

        Menu menu = navigationView.getMenu();


        menu.findItem(R.id.nav_map).setVisible(false);


        if ("0".equals(authorisedUser.getManager()) & "0".equals(authorisedUser.getDiler())) {
            menu.findItem(R.id.nav_balance).setVisible(false);
            menu.findItem(R.id.nav_tech_supp).setVisible(false);
        } else {
            getBalance(menu.findItem(R.id.nav_balance));
        }
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
        List fragmentList = getSupportFragmentManager().getFragments();

        boolean handled = false;
        if (fragmentList.get(fragmentList.size()-1) instanceof AbstractFragment){
            handled = ((AbstractFragment) fragmentList.get(fragmentList.size()-1)).onBackPressed();
        }

        if (!handled) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                if ("0".equals(authorisedUser.getDiler())
                        && (getSupportFragmentManager().getBackStackEntryCount() == 0)) {
                    if (doubleTap) {
                        firstLaunch = true;
                        /*Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);*/
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                    } else {
                        ApplicationUtil.showToast(this, getString(R.string.press_one_more_time));
                        doubleTap = true;
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                doubleTap = false;
                            }
                        }, 2000);
                        return;
                    }

                }
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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
        deleteAllBackgroundTasks();
        if (backgroundTaskExecuting) {
            do {
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } while (backgroundTaskExecuting);
        }

        if (id == R.id.nav_map) {
            map();
        }/* else if (id == R.id.nav_history) {
            history();
        }*/ else if (id == R.id.nav_settings) {
            chooseProvider();
        } else if (id == R.id.nav_save_location) {
            saveLocation();
        } else if (id == R.id.nav_balance) {
            balance();
        } else if (id == R.id.nav_tech_supp) {
            showTicketList();
        } else if (id == R.id.nav_logout) {
            logout();
        }

        drawer.closeDrawer(GravityCompat.START);
    }

    private void history() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        /*if (isGroupAvaible){
            currentFragment = new GroupFragment(new GetCurrentFragment());
        } else*/
        ft.addToBackStack("history");
        currentFragment = new GetCurrentFragment();
        try {
            currentFragment.setArguments(ApplicationUtil.setValueToBundle
                    (new Bundle(), "user", authorisedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorisedUser.toString());
        }
        ft.replace(R.id.content_main, currentFragment);
        ft.commit();
    }


    private void chooseProvider() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new ChooseMapTypeFragment();
        try {
            currentFragment.setArguments(ApplicationUtil.setValueToBundle
                    (new Bundle(), "user", authorisedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorisedUser.toString());
        }
        ft.replace(R.id.content_main, currentFragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    private void saveLocation() {
        Toast toast = null;
        if (currentFragment instanceof GMapFragment) {
            LatLng target = ((GMapFragment) currentFragment).getmMap().getCameraPosition().target;
            float zoom = ((GMapFragment) currentFragment).getmMap().getCameraPosition().zoom;
            LocalPreferenceManager.saveLatLonZoom(this, target.latitude, target.longitude, zoom);
            toast = Toast.makeText(getApplicationContext(),
                    R.string.location_saved,
                    Toast.LENGTH_SHORT);
        } else {
            toast = Toast.makeText(getApplicationContext(),
                    R.string.error_saving_location,
                    Toast.LENGTH_SHORT);
        }

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    private void balance() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("balance");
        currentFragment = new BalanceFragment();
        try {
            currentFragment.setArguments(ApplicationUtil.setValueToBundle
                    (new Bundle(), "user", authorisedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorisedUser.toString());
        }
        ft.replace(R.id.content_main, currentFragment);
        ft.commit();
    }

    private void map() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        currentFragment = new GMapFragment();
        try {
            currentFragment.setArguments(ApplicationUtil.setValueToBundle
                    (new Bundle(), "user", authorisedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorisedUser.toString());
        }
        if (firstLaunch) {
            getSupportFragmentManager().popBackStackImmediate();
        } else if (!firstLaunch) {
            ft.addToBackStack(null);
        }
        ft.replace(R.id.content_main, currentFragment);
        firstLaunch = false;
        ft.commit();


    }

    public void showTicketList() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("ticketList");
        currentFragment = new TicketListFragment();
        try {
            currentFragment.setArguments(ApplicationUtil.setValueToBundle
                    (new Bundle(), "user", authorisedUser));
        } catch (JsonProcessingException e) {
            logger.error("Error writing user {}", authorisedUser.toString());
        }

        ft.replace(R.id.content_main, currentFragment);
        ft.commit();
    }

    private void setFragmentActive(Fragment fragment) {
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
                LoginActivityIntent intent = new LoginActivityIntent(MainActivity.this);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                //  setResult(1);
                //  finish();
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                logger.info("Failed logout procedure");
                LocalPreferencesManagerUtil.clearCredentials(MainActivity.this);
//                setResult(0);
                startActivity(new LoginActivityIntent(MainActivity.this));
                finish();
            }
        });

    }

    private LogoutCredentials getLogoutCredential() {
        return new LogoutCredentials(Action.LOGOUT, SystemUtil.getWifiMAC(this), authorisedUser.getId());
    }

    public DrawerLayout getDrawer() {
        return drawer;
    }


    public void registerHandler(Handler handler) {
        if (handlers == null) {
            handlers = new ArrayList<>();
        }
        handlers.add(handler);
    }

    public void deleteAllBackgroundTasks() {
        if (handlers == null || handlers.size() == 0) {
            return;
        }
        for (Handler h : handlers) {
            h.removeCallbacksAndMessages(null);
        }
    }

    /*@Override
    protected void onPause() {
        super.onPause();
        firstLaunch = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }*/


    public Boolean getBackgroundTaskExecuting() {
        return backgroundTaskExecuting;
    }

    public void setBackgroundTaskExecuting(Boolean backgroundTaskExecuting) {
        this.backgroundTaskExecuting = backgroundTaskExecuting;
    }

    private void getBalance(final MenuItem item) {
        Procedure procedure = new Procedure(Action.CALL);
        procedure.setFormat(WayMapsService.DEFAULT_FORMAT);
        procedure.setIdentficator(SystemUtil.getWifiMAC(this));
        procedure.setName(Action.FIN_GET);
        procedure.setUser_id(authorisedUser.getId());
        procedure.setParams(authorisedUser.getFirm_id());
        Call<FinGet[]> call = RetrofitService.getWayMapsService().finGetProcedure(procedure.getAction(), procedure.getName(),
                procedure.getIdentficator(), procedure.getUser_id(), procedure.getFormat(), procedure.getParams());
        call.enqueue(new Callback<FinGet[]>() {
            @Override
            public void onResponse(Call<FinGet[]> call, Response<FinGet[]> response) {
                FinGet[] finGets = response.body();
                logger.debug("Balance load successfully.");

                double bal = 0;

                if (finGets == null){
                    finGets = new FinGet[0];
                }
                if (finGets.length == 0){
                    bal = 0;
                } else {
                    bal = new Double(finGets[0].getBalance());
                }

                String html1 = null;
                String html2 = null;
                String saldo = null;
                String text = getResources().getString(R.string.balance) + ": ";
                saldo = new DecimalFormat("#,###.00").format(bal) + " " + getString(R.string.uah);
                html1 = "<font>" + text + "</font>";

                if (bal > 0) {
                    html2 = "<font color=#12b90f>" + saldo + "</font>";
                }else if (bal == 0) {
                    html2 = "<font>" + saldo + "</font>";
                } else {
                    html2 = "<font color=#e11a24>" + saldo + "</font>";
                }
                item.setTitle(Html.fromHtml(html1 + html2));
            }

            @Override
            public void onFailure(Call<FinGet[]> call, Throwable t) {
                logger.debug("Failed while trying to load balance.");
            }
        });

    }

    /*   protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_EXIT) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(R.string.exit_dialog);
            adb.setMessage(R.string.exit_message);
            adb.setIcon(android.R.drawable.ic_dialog_info);
            adb.setPositiveButton(R.string.yes, myClickListener);
            adb.setNegativeButton(R.string.no, myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }
    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                // +
                case Dialog.BUTTON_POSITIVE:
                    startActivity();
                    break;
                // -
                case Dialog.BUTTON_NEGATIVE:
                    break;
                // +-
            }
        }
    };
*/

}


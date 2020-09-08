package com.neteru.mobileussdcodex.activities;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.neteru.mobileussdcodex.R;
import com.neteru.mobileussdcodex.classes.InitValues;
import com.neteru.mobileussdcodex.classes.LoadingDialog;
import com.neteru.mobileussdcodex.classes.database.DatabaseManager;
import com.neteru.mobileussdcodex.fragments.CountryFragment;
import com.neteru.mobileussdcodex.fragments.OthercodeFragment;
import com.vorlonsoft.android.rate.AppRate;
import com.vorlonsoft.android.rate.StoreType;
import com.vorlonsoft.android.rate.Time;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private SharedPreferences preferences;
    private DatabaseManager databaseManager;
    private String[] countryName, codeISO;
    LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        loadingDialog = new LoadingDialog(MainActivity.this);

        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        databaseManager = new DatabaseManager(this);
        countryName = getResources().getStringArray(R.array.countryArray);
        codeISO = getResources().getStringArray(R.array.countryValues);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        // Personnalisation de la couleur de l'indicateur
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.colorPrimary));

        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getSupportActionBar() != null) {
            // Customisation de la couleur de la barre d'outils
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.white)));
        }

        try {

            if (preferences.getInt("updateCode", 0) < getPackageManager().getPackageInfo(getPackageName(), 0).versionCode
                    || preferences.getBoolean("firstLog", true)){

                new Initialization(!preferences.getBoolean("firstLog", true)).execute();

            }else {
                byDefault();
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setAppRateSystem();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull final MenuItem item) {

        drawer.closeDrawer(GravityCompat.START);

        new Handler().postDelayed(() -> {

            switch (item.getItemId()){
                case R.id.nav_benin:
                    setTitle(countryName[0]);
                    loadFragment(new CountryFragment(), codeISO[0]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(true);
                    break;

                case R.id.nav_cameroun:
                    setTitle(countryName[1]);
                    loadFragment(new CountryFragment(), codeISO[1]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setChecked(true);
                    break;

                case R.id.nav_ci:
                    setTitle(countryName[2]);
                    loadFragment(new CountryFragment(), codeISO[2]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(2).setChecked(true);
                    break;

                case R.id.nav_mali:
                    setTitle(countryName[3]);
                    loadFragment(new CountryFragment(), codeISO[3]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(3).setChecked(true);
                    break;

                case R.id.nav_niger:
                    setTitle(countryName[4]);
                    loadFragment(new CountryFragment(), codeISO[4]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(4).setChecked(true);
                    break;

                case R.id.nav_nigeria:
                    setTitle(countryName[5]);
                    loadFragment(new CountryFragment(), codeISO[5]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(5).setChecked(true);
                    break;

                case R.id.nav_senegal:
                    setTitle(countryName[6]);
                    loadFragment(new CountryFragment(), codeISO[6]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(6).setChecked(true);
                    break;

                case R.id.nav_togo:
                    setTitle(countryName[7]);
                    loadFragment(new CountryFragment(), codeISO[7]);
                    navigationView.getMenu().getItem(0).getSubMenu().getItem(7).setChecked(true);
                    break;

                case R.id.nav_search:
                    Intent i_ = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(i_);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_favoris:
                    Intent i_0 = new Intent(MainActivity.this, FavoritesActivity.class);
                    startActivity(i_0);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_other:
                    setTitle(getString(R.string.others_str));
                    loadFragment(new OthercodeFragment(), null);
                    navigationView.getMenu().getItem(1).getSubMenu().getItem(2).setChecked(true);
                    break;

                case R.id.nav_settings:
                    Intent i_1 = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(i_1);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_evaluate:
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    }else{

                        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);

                    }

                    try {
                        startActivity(goToMarket);
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" +getPackageName())));
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    break;

                case R.id.nav_share:
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_app_msg,":\n\n https://play.google.com/store/apps/details?id=com.neteru.mobileussdcodex \n\n"));
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app_title)));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    break;
            }

        }, 450);

        return true;
    }

    /**
     * Initialisation du système de notation
     */
    private void setAppRateSystem() {

        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY)
                .setTimeToWait(Time.DAY, (short) 10) // default is 10 days, 0 means install millisecond, 10 means app is launched 10 or more time units later than installation
                .setLaunchTimes((byte) 10)          // default is 10, 3 means app is launched 3 or more times
                .setRemindTimeToWait(Time.DAY, (short) 1) // default is 1 day, 1 means app is launched 1 or more time units after neutral button clicked
                .setRemindLaunchesNumber((byte) 0)  // default is 0, 1 means app is launched 1 or more times after neutral button clicked
                .setSelectedAppLaunches((byte) 1)   // default is 1, 1 means each launch, 2 means every 2nd launch, 3 means every 3rd launch, etc
                .setShowLaterButton(true)           // default is true, true means to show the Neutral button ("Remind me later").
                .setVersionCodeCheck(false)          // default is false, true means to re-enable the Rate Dialog if a new version of app with different version code is installed
                .setVersionNameCheck(false)          // default is false, true means to re-enable the Rate Dialog if a new version of app with different version name is installed
                .setDebug(false)                    // default is false, true is for development only, true ensures that the Rate Dialog will be shown each time the app is launched
                .setOnClickButtonListener(which -> Log.d(MainActivity.this.getLocalClassName(), Byte.toString(which)))
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

    }

    public void loadFragment(Fragment f, String s){
        Bundle bundle = new Bundle();
        if (s != null){
            bundle.putString("code", s);
            f.setArguments(bundle);
        }

        // Chargement du fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, f);
        fragmentTransaction.commit();

        invalidateOptionsMenu(); // Invalidation du menu option

        uncheckedAllItem(); // Retrait general de focus

        // Fermeture automatique du volet après le chargement du fragment
        drawer.closeDrawer(GravityCompat.START);
    }

    public void uncheckedAllItem(){
        for (int i = 0; i < 2; i++){
            if (i == 0){
                for (int y = 0; y < 8; y++){
                    navigationView.getMenu().getItem(i).getSubMenu().getItem(y).setChecked(false);
                }
            }else{
                for (int y = 0; y < 3; y++){
                    navigationView.getMenu().getItem(i).getSubMenu().getItem(y).setChecked(false);
                }
            }
        }
    }

    public void byDefault(){

        loadFragment(new CountryFragment(), preferences.getString("currentCountry", codeISO[0]));
        navigationView.getMenu().getItem(0).getSubMenu().getItem(Arrays.asList(codeISO).indexOf(preferences.getString("currentCountry", codeISO[0]))).setChecked(true);
        setTitle(countryName[Arrays.asList(codeISO).indexOf(preferences.getString("currentCountry", codeISO[0]))]);

    }

    public void setTitle(String title){
        if (getSupportActionBar() != null){

            // Customisation du titre de la barre d'outils
            Spannable spannableTitle = new SpannableString(title);
            spannableTitle.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.black)), 0, title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            getSupportActionBar().setTitle(spannableTitle);

        }
    }

    @SuppressLint("StaticFieldLeak")
    class Initialization extends AsyncTask<Integer, Void, Void>{
        boolean reset;

        Initialization(boolean r){
            reset = r;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadingDialog.show();

            if (reset){
                loadingDialog.setMsg(getString(R.string.updating));
            }else{
                loadingDialog.setMsg(getString(R.string.initialization));
            }
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            if (reset){ databaseManager.db_removeValueByIsNative(); }

            InitValues.getInstance(databaseManager).setValues();

            SharedPreferences.Editor editor = preferences.edit();

            try {
                editor
                        .putBoolean("firstLog", false)
                        .putInt("updateCode", getPackageManager().getPackageInfo(getPackageName(), 0).versionCode)
                        .apply();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            byDefault();

            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        loadingDialog.dismiss();
    }
}

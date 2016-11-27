package umd.solarmap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import umd.solarmap.AccountManager.SolarAccountManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /**
     * Instace fields
     */
    private static final int LOCATION_PERMISSION_CODE = 1;
    private static final int MAP_FRAGMENT_ID = R.id.nav_map;
    private static final int SAVED_LOCATION_FRAGMENT_ID = R.id.nav_saved_locations;

    // Drawer components
    private NavigationView navigationView = null;

    // Drawer fragments
    private final MapFragment mapFragment = new MapFragment();


    private static int RESULT_LOAD_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Switch to login activity if user's not logged in, this will later be moved to onStart()
        //Intent intent = new Intent(this, StartupLoginActivity.class);
        //startActivity(intent);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        this.navigationView = navigationView;

        // Setup all components in the header view
        setupHeader();

        // Ask for user LOCATION permission
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MainActivity.LOCATION_PERMISSION_CODE);
        }

        // Default startup fragment
        this.switchFragment(MAP_FRAGMENT_ID);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == MAP_FRAGMENT_ID) {
            this.switchFragment(MAP_FRAGMENT_ID);
        } else if (id == SAVED_LOCATION_FRAGMENT_ID) {

        } else if (id == R.id.nav_achievements) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Switch the fragment of the drawer
     * @param FRAGMENT_ID
     */
    private void switchFragment(final int FRAGMENT_ID) {

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Switch to the map fragment
        fragmentManager.beginTransaction().replace(R.id.content_main, mapFragment).commit();

        // Highlight selected row
        ((NavigationView) findViewById(R.id.nav_view)).getMenu().getItem(FRAGMENT_ID % MAP_FRAGMENT_ID).setChecked(true);
    }

    /**
     * Setup the components for the drawer header view
     */
    private void setupHeader() {

        // Get the header view of the drawer
        View headerView = navigationView.getHeaderView(0);

        // Avatar image view
        ImageView avatarView = (ImageView) headerView.findViewById(R.id.avatarView);
        avatarView.setOnTouchListener((view, motionEvent)->{

            return true;
        });

        // Change the text view to display account's email address
        TextView account_email_address = (TextView) headerView.findViewById(R.id.userName);
        try {
            account_email_address.setText(SolarAccountManager.appAccountManager().getEmail().toString());
        } catch (IllegalAccessException e) {
            account_email_address.setText("");
            e.printStackTrace();
        }
    }
}

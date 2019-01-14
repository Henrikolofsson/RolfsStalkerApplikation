package henrik.mau.rolfsstalkerapplikation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import Interfaces.ActivityListener;

public class MainActivity extends AppCompatActivity {
    private Controller controller;
    private FragmentManager fm;
    private ArrayList<ActivityListener> activityListeners = new ArrayList<>();

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSystem();
        initializeComponents();
    }

    /*
       Initializes the fragment manager and checks if
       the user have granted permission to share their location.
    */
    private void initializeSystem(){
        fm = getSupportFragmentManager();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        controller = new Controller(this);
        activityListeners.add(controller);
    }

    /*
       Initializes the toolbar and navigation layouts.
       Sets listener on navigation drawer.
     */
    private void initializeComponents(){
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.nav_start:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        for(ActivityListener l : activityListeners){
                            l.setMainFragment();
                        }
                        return true;

                    case R.id.nav_register_group:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        for(ActivityListener l : activityListeners){
                            l.setRegisterGroupFragment();
                        }
                        return true;

                    case R.id.nav_unregister_group:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        for(ActivityListener l : activityListeners){
                            l.setUnregisterFragment();
                        }
                        return true;

                    case R.id.nav_map:
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        for(ActivityListener l : activityListeners){
                            l.setMapFragment();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    /*
       Sets a fragment in the main container for the application.
     */
    public void setFragment(Fragment fragment, String tag){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, fragment, tag);
        ft.commit();
    }

    /*
       Used for a reference to dataFragment, since it is an "invisible" fragment.
     */
    public void addFragment(Fragment fragment, String tag){
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(fragment, tag);
        ft.commit();
    }

    /*
       Checks with controller what fragment that will be put in the main container
        in case the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        if(controller.onBackPressed()){
            super.onBackPressed();
        }

    }

    /*
       Returns the fragment.
     */
    public Fragment getFragment(String tag){
        return fm.findFragmentByTag(tag);

    }


    /*
       If the onResume is put, it will call controllers onResume.
       This is because controller is not overriding onResume method.
       -- NOT USED --
     */
    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();

    }

    /*
       If the button in the toolbar is pressed it opens the drawer layout.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

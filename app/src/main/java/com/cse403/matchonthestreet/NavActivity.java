/**
 * Extend this class to implement the Sidebar functionality
 * Using example layout now, pressing the tools option opens the add event view.
 * For more info regarding the source look at: http://developer.android.com/guide/topics/resources/menu-resource.html
 *
 * Lance Ogoshi
 */

package com.cse403.matchonthestreet;

import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private String TAG = "NavActivity";

    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setContentView(final int layoutResID) {
        DrawerLayout fullLayout= (DrawerLayout) getLayoutInflater()
                .inflate(R.layout.content_nav, null);
        LinearLayout actContent= (LinearLayout) fullLayout.findViewById(R.id.content);

        DrawerLayout mDrawerLayout = (DrawerLayout) fullLayout.findViewById(R.id.drawer_layout);

        Toolbar toolbar = (Toolbar) fullLayout.findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        NavigationView navigationView = (NavigationView) fullLayout.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        ) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
               // getActionBar().setTitle(R.string.app_name);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // getActionBar().setTitle(R.string.app_name);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

      //  getActionBar().setDisplayHomeAsUpEnabled(true);
      //  getActionBar().setHomeButtonEnabled(true);

        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            Log.d(TAG, "Nav_Camera selected");
        } else if (id == R.id.nav_gallery) {
            Log.d(TAG, "Nav_Gallery selected");
        } else if (id == R.id.nav_slideshow) {
            Log.d(TAG, "Nav_Slideshow selected");
        } else if (id == R.id.nav_manage) {
            Log.d(TAG, "Nav_Manage selected");
            if (this.getClass() != AddEventActivity.class) {
                Intent intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
            }
        } else if (id == R.id.nav_share) {
            Log.d(TAG, "Nav_Share selected");
        } else if (id == R.id.nav_send) {
            Log.d(TAG, "Nav_Send selected");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

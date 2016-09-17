package com.qmatica.arsen.aglistview;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class ActivityMain extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    DBManager dbManager = new DBManager(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.w(" Main OnCreate", "Called");

        FloatingActionButton fabMain = (FloatingActionButton) findViewById(R.id.fab_main);
        assert fabMain != null;

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Exit", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        assert drawer != null;
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        assert navigationView != null;
        navigationView.setNavigationItemSelectedListener(this);

//      initialize Fulcrum product list
        ProductList.create(this);

        NetworkStatus networkStatus = new NetworkStatus(ActivityMain.this);
        networkStatus.show();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.w( " Main onNewIntent", "called");
        setIntent(intent);//must store the new intent unless getIntent() will return the old one
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_profile:
                editProfile();
                break;
            case R.id.nav_sync:
                showNotImplemented();
                break;
            case R.id.nav_history:
                Intent intent = new Intent(ActivityMain.this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_upload:
                showNotImplemented();
                break;
            case R.id.nav_config:
                editConfig();
                break;
            case R.id.nav_debug:
                debugMain();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void selectActivity(View view) {
        long count = dbManager.getOrderCount();
        Intent intent;


        if(count == 0) {  // if database Orders table is empty, start with Products
            intent = new Intent(ActivityMain.this, ProductActivity.class);
        }
        else {            // otherwise start with Orders
            intent = new Intent(ActivityMain.this, OrderActivity.class);
        }
        startActivity(intent);
    }

    public void finishApp(MenuItem item)        // TODO startActivityForResult and wait for answer before own finish
    {
        finish();
        Intent intent = new Intent(ActivityMain.this, ProductActivity.class);
        intent.putExtra(A.INTENT_FINISH, true);
        startActivity(intent);
        intent = new Intent(ActivityMain.this, OrderActivity.class);
        intent.putExtra(A.INTENT_FINISH, true);
        startActivity(intent);
        intent = new Intent(ActivityMain.this, HistoryActivity.class);
        intent.putExtra(A.INTENT_FINISH, true);
        startActivity(intent);
    }



//  helper methods

    private void showNotImplemented() {
        Toast.makeText(ActivityMain.this, "To be implemented", Toast.LENGTH_LONG).show();
    }

    private void editProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.profile_dialog, null);
        builder.setView(dialogView);

        builder.setTitle(getResources().getString(R.string.title_nav_profile));
//            builder.setMessage("Edit");

        final EditText name     = (EditText) dialogView.findViewById(R.id.et_name);
        final EditText company  = (EditText) dialogView.findViewById(R.id.et_company);
        final EditText sendTo   = (EditText) dialogView.findViewById(R.id.et_sendto);
        final EditText copyTo   = (EditText) dialogView.findViewById(R.id.et_copyto);


        final UserProfile profile = dbManager.readProfile();

        name.setText(profile.getName());
        company.setText(profile.getCompany());
        sendTo.setText(profile.getSendTo());
        copyTo.setText(profile.getCopyTo());

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                profile.setName(name.getText().toString());
                profile.setCompany(company.getText().toString());
                profile.setSendTo(sendTo.getText().toString());
                profile.setCopyTo(copyTo.getText().toString());
                dbManager.saveProfile(profile);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void editConfig() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.config_dialog, null);
        builder.setView(dialogView);

        builder.setTitle(getResources().getString(R.string.title_nav_config));
        builder.setMessage("Use external email client or enter Fulcrum username and password");    // TODO move to strings

        final UserProfile profile = dbManager.readProfile();
        final EditText username = (EditText) dialogView.findViewById(R.id.et_username);
        final EditText password = (EditText) dialogView.findViewById(R.id.et_password);
        final CheckBox mode     = (CheckBox) dialogView.findViewById(R.id.cb_mode);

        mode.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setEnableUserPass(username, password, !mode.isChecked());
            }
        });

        mode.setChecked(profile.getMode() == A.EXTERNAL_MODE);
        username.setText(profile.getUsername());
        password.setText(profile.getPassword());
        setEnableUserPass(username, password, !mode.isChecked());

        builder.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                profile.setUsername(username.getText().toString());
                profile.setPassword(password.getText().toString());
                profile.setMode(mode.isChecked() ? A.EXTERNAL_MODE : A.INTERNAL_MODE);
                dbManager.saveProfile(profile);
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        builder.show();
    }

    private void setEnableUserPass(EditText username, EditText password, boolean flag) {
        username.setEnabled(flag);
        password.setEnabled(flag);
    }

    private void debugMain() {

        AlertDialog.Builder alertSubmit;
        alertSubmit = new AlertDialog.Builder(ActivityMain.this);

        alertSubmit.setTitle("Delete order history");
        alertSubmit.setMessage("All orders will be deleted from the device database");

        alertSubmit.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dbManager.deleteAllOrders();
                dbManager.deleteAllHistoryAndDetail();
            }
        });
        alertSubmit.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alertSubmit.show();
    }

}


//        Log.w(" ID " + String.valueOf(profile.getID()), profile.getName()+ " " + profile.getCompany() + " " + profile.getSendTo() + " " + profile.getCopyTo());
//                Log.w(" ID " + String.valueOf(profile.getID()), profile.getName()+ " "
//                        + profile.getCompany() + " " + profile.getSendTo() + " "
//                        + profile.getCopyTo() + " " + profile.getUsername() + " " + profile.getPassword());

//        System.exit(0);

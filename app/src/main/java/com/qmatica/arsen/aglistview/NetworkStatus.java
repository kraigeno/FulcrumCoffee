package com.qmatica.arsen.aglistview;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class NetworkStatus {

    private Context context;
    private boolean isAvailable;

    public NetworkStatus(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//      isAvailable = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        isAvailable = activeNetwork != null && activeNetwork.isConnected();

    }
    public boolean isOn() {
        return isAvailable;
    }

    public void show() {

        if(context == null)
            return;

        if(isAvailable) {
            Toast.makeText(context, "Connected to active network", Toast.LENGTH_LONG).show();
        }
        else
        {
            AlertDialog.Builder alertNetwork;
            alertNetwork = new AlertDialog.Builder(context);

            alertNetwork.setTitle("No network connection");
            alertNetwork.setMessage("Orders cannot be submitted directly at this time. " +
                    "It is possible to place order in the outbox of the external email client, it be automatically submitted when the internet connection is restored. ");

            alertNetwork.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });

            alertNetwork.show();
        }
    }
}

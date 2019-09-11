package com.practice.solarcalculator.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.HandlerThread;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.practice.solarcalculator.R;

public class LocationManager implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_CODE = 0;

    private static final long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private static final long FASTEST_INTERVAL = 10000; /* 10 secs */

    private static final String[] mRequiredPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private final Context mContext;

    // location handler thread for update in background
    private final HandlerThread handlerThread = new HandlerThread("LocationThread");

    private boolean canGetLocation = false;
    private GoogleApiClient mApiClient;
    private onReceivedLocationListener mListener;

    public LocationManager(Context mContext) {
        this.mContext = mContext;
        initiate();
    }

    private void initiate() {
        Logger.info("Initiating Google Api Client");
        mApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
    }

    public void setListener(onReceivedLocationListener mListener) {
        this.mListener = mListener;
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onConnected(@Nullable Bundle bundle) {
        Logger.info("All Permisson %s", PermissionUtils.hasPermissions(mContext, mRequiredPermissions));
        canGetLocation = PermissionUtils.hasPermissions(mContext, mRequiredPermissions);
    }

    public void enableRationalDialog() {
        Logger.info("Enabling rational dialog");
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setMessage(R.string.permission_rational)
                .setPositiveButton(R.string.common_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(
                                (Activity) mContext, mRequiredPermissions, REQUEST_CODE);
                    }
                }).create();
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    public void startLocationUpdates() {
        Logger.info("Start Listening for location updates");
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mApiClient,
                mLocationRequest,
                this
        );
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Logger.info("Inside request permission updates");
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                int coarsePerm = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION);
                int finePerm = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);
                if (coarsePerm == PackageManager.PERMISSION_GRANTED && finePerm == PackageManager.PERMISSION_GRANTED) {
                    Logger.info("All permission has been granted");
                    if (mApiClient == null) {
                        initiate();
                    }
                    startLocationUpdates();
                }
            } else {
                Toast.makeText(mContext, "permission denied",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void stopLocationUpdates() {
        if (mApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mApiClient, this);
            mApiClient.disconnect();
            handlerThread.quit();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Logger.info("Location Captured: lat: %s, Long: %s",
                location.getLatitude(), location.getLongitude());
        this.canGetLocation = true;
        if (mListener != null) {
            mListener.onReceived(location);
        }
        stopLocationUpdates();
    }

    public interface onReceivedLocationListener {
        void onReceived(Location location);
    }
}


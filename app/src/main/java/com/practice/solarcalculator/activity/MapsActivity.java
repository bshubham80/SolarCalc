package com.practice.solarcalculator.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.practice.solarcalculator.R;
import com.practice.solarcalculator.adapter.RecentLocationAdapter;
import com.practice.solarcalculator.db.DatabaseHandler;
import com.practice.solarcalculator.db.model.RecentLocation;
import com.practice.solarcalculator.fragment.CalculationBottomDialogFragment;
import com.practice.solarcalculator.fragment.RecentLocationDialogFragment;
import com.practice.solarcalculator.utils.LocationManager;
import com.practice.solarcalculator.utils.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, LocationManager.onReceivedLocationListener, View.OnClickListener,
        RecentLocationAdapter.OnItemClickListener {

    private static final int AUTOCOMPLETE_REQUEST_CODE = 2;
    private RecentLocation mRecentObj = new RecentLocation();
    private TextView mLocationText;
    private GoogleMap mMap;
    private Marker mMarker;
    private LocationManager mLocationManager;
    private DatabaseHandler mDBHandler;
    private DialogFragment mDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mLocationText = findViewById(R.id.text_location);
        mLocationText.setOnClickListener(this);

        ImageView mClearImage = findViewById(R.id.image_cross);
        mClearImage.setOnClickListener(this);

        // initialize db instance
        mDBHandler = new DatabaseHandler(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // initialize location manager
        mLocationManager = new LocationManager(this);
        mLocationManager.setListener(this);

        // Initialize the SDK
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));

        // Create a new Places client instance
        PlacesClient client = Places.createClient(this);
    }

    /**
     * This
     */
    private void invalidateViews() {
        invalidateMap();
        invalidateName();
        showCalculationBottomSheet();
    }

    private void showCalculationBottomSheet() {
        // remove previous bottom sheet if exist.
        Fragment prev = getSupportFragmentManager().findFragmentByTag("bottom_sheet");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (prev != null) {
            transaction.remove(prev);
            transaction.addToBackStack(null);
        }
        BottomSheetDialogFragment bottomSheet = CalculationBottomDialogFragment.getInstance(mRecentObj);
        bottomSheet.show(getSupportFragmentManager(), "bottom_sheet");
    }

    private void invalidateMap() {
        // remove previous one if exist.
        if (mMarker != null)
            mMarker.remove();

        // Add a marker on captured location and move the camera
        LatLng currentLocation = new LatLng(mRecentObj.getLatitude(), mRecentObj.getLongitude());

        try {
            Geocoder v = new Geocoder(this);
            List<Address> a = v.getFromLocation(mRecentObj.getLatitude(), mRecentObj.getLongitude(), 1);
            if (a.size() > 0)
                mRecentObj.setLocationName(a.get(0).getAddressLine(0));
            else
                mRecentObj.setLocationName("My Location");
        } catch (IOException e) {
            e.printStackTrace();
            Logger.error("Error in Geocoder %s", e.getMessage());
        }

        mMarker = mMap.addMarker(
                new MarkerOptions()
                        .draggable(true)
                        .position(currentLocation)
                        .title(mRecentObj.getLocationName()));


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
    }

    private void invalidateName() {
        mLocationText.setText(mRecentObj.getLocationName());
    }

    /**
     * @return true if both location are same otherwise false.
     */
    private boolean checkLocationAreSame(Location obj) {
        return obj != null && mRecentObj.getLatitude() == obj.getLatitude()
                && mRecentObj.getLongitude() == obj.getLongitude();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_location:
                Logger.info("Active location %s", mRecentObj);
                mDBHandler.addLocation(mRecentObj);
                return true;

            case R.id.menu_view_location:
                // initiate dialog fragment
                mDialogFragment = new RecentLocationDialogFragment();
                mDialogFragment.show(getSupportFragmentManager(), "dialogFragment");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Logger.info("Map is ready to use");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                mRecentObj.setLocationCoordinates(marker.getPosition().latitude, marker.getPosition().longitude);
                invalidateViews();
            }
        });

        if (!mLocationManager.canGetLocation()) {
            mLocationManager.enableRationalDialog();
            return;
        }

        mLocationManager.startLocationUpdates();
    }

    private void updatePlaceDetails(Place place) {
        Logger.info("Captured Place's Lat Lng is %s", place.getLatLng());

        if (place.getLatLng() == null)
            return;

        // update Place name to search view
        mRecentObj.setLocationName(place.getName());
        // update place location and invalidate views
        mRecentObj.setLocationCoordinates(place.getLatLng().latitude, place.getLatLng().longitude);

        invalidateViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null)
            return;

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                updatePlaceDetails(place);
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Logger.error("Error while getting place details %s",status.getStatusMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onReceived(Location location) {
        if (checkLocationAreSame(location)) {
            Logger.info("New location is same as previous no changes required");
            return;
        }

        mRecentObj.setLocationCoordinates(location.getLatitude(), location.getLongitude());
        invalidateViews();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.text_location) {
            // Set the fields to specify which types of place data to
            // return after the user has made a selection.
            List<Place.Field> fields = Arrays.asList(
                    Place.Field.ID,
                    Place.Field.NAME,
                    Place.Field.LAT_LNG
            );

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);

        } else if (id == R.id.image_cross) {
            mLocationText.setText(null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.stopLocationUpdates();
    }

    @Override
    public void onItemClicked(RecentLocation obj) {
        // remove dialog when any item got selected
        mDialogFragment.dismiss();

        mRecentObj = obj;
        invalidateViews();
    }
}

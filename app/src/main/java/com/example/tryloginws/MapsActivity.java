package com.example.tryloginws;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    //untuk map kosongan
    private GoogleMap mMap;

    //untuk ambil lokasi user
    private FusedLocationProviderClient mFusedLocationCLient;
    private static final String[] LOCATIONS_PERM = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int LOCATIONS_REQCODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationCLient = LocationServices.getFusedLocationProviderClient(this);
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));

        getUserLocationTask();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    boolean hasLocationsPermissions() {
        return EasyPermissions.hasPermissions(this, LOCATIONS_PERM);
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATIONS_REQCODE)
    public void getUserLocationTask() {
        if (hasLocationsPermissions()) {
            //have the permissions, do the thing!
            Toast.makeText(this, "Have Permissions, do the thing!", Toast.LENGTH_SHORT).show();
            mFusedLocationCLient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if(location!=null){
                                LatLng currLatLng = new LatLng(location.getLatitude(),location.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(currLatLng).title("Current Position"));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLatLng,20));
                            }
                        }
                    });
        }else{
            EasyPermissions.requestPermissions(
                    this,
                    "Please allow us to locate your current position",
                    LOCATIONS_REQCODE,
                    LOCATIONS_PERM
            );
        }
    }

    //PLACE PICKER STUFF HERE
    public static final int PLACE_PICKER_REQUEST = 134;

    public void startPlacePicker(View view) {

        PlacePicker.IntentBuilder builder =
                new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(
                    builder.build(this),
                    PLACE_PICKER_REQUEST
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PLACE_PICKER_REQUEST){
            if(resultCode==RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                LatLng destLatLng = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(destLatLng).title("Destination"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destLatLng,20));
            }
        }
    }
}

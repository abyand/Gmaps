package com.example.abyandafa.mapku;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mapku;
    private SupportMapFragment fragment;
    private Boolean mLocationPermissionGranted;
    private LocationManager locationManager;

    private EditText latitude, longitude, zoom, tempat;
    private Button mulai, cari;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pilihanpeta, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.normal:
                mapku.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybrid:
                mapku.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.terrain:
                mapku.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case R.id.satelit:
                mapku.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.none:
                mapku.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        zoom = (EditText) findViewById(R.id.zoom);
        tempat = (EditText) findViewById(R.id.tempat);

        mulai = (Button) findViewById(R.id.mulai);

        mulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Double lat = Double.parseDouble(latitude.getText().toString());
                Double longi = Double.parseDouble(longitude.getText().toString());
                Float z = Float.parseFloat(zoom.getText().toString());

                goToLokasi(lat, longi, z);

            }
        });

        cari = (Button) findViewById(R.id.cari);
        cari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cariLatLng();
            }
        });

        fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        Log.d("CREATE", "onCreate: ");
        fragment.getMapAsync(this);

    }

    private void cariLatLng() {
        Geocoder g = new Geocoder(getBaseContext());

        try {
            List<Address> daftar = g.getFromLocationName(tempat.getText().toString(), 1);
            Address alamat = daftar.get(0);
            goToLokasi(alamat.getLatitude(), alamat.getLongitude(), 15);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void goToLokasi(Double lati, Double longi, float z)
    {
        LatLng lokasiBaru = new LatLng(lati, longi);
        mapku.addMarker(new MarkerOptions().position(lokasiBaru).title("Marker in " +lati + ":" +longi));
        mapku.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiBaru, z));
    }

    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        Log.d("PERMISSION", "getLocationPermission: ");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            getDeviceLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 20);

        }
    }


    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        Log.d("LOCATION", "getDeviceLocation: ");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Log.d("BENAR", "getDeviceLocation: ");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 10, this);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MAP", "onMapReady: ");
        mapku = googleMap;
        Log.d("  ", "onMapReady: ");

//        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        getLocationPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("RESULT", "onRequestPermissionsResult: ");
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case 20: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();

                }
            }
        }

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("CHANGED", "onLocationChanged: ");
        LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());

//        mapku.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Sydney"));
//        mapku.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));


        locationManager.removeUpdates(this);


    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

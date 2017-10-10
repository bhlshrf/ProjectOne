package com.example.c.testwebapi;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends ActionBarActivity
{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    public static Data.Service[] PassedDatas;


    private LocationManager locationManager;
    private static final long MIN_TIME = 400;
    private static final float MIN_DISTANCE = 1000;

    private double myLat,myLng, myLat2,myLng2;



    public static boolean isForCreateService = false;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    private void CheckLocationServiceTOGetUesrLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String provider = LocationManager.NETWORK_PROVIDER;
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            provider = LocationManager.NETWORK_PROVIDER;
        }
         else if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            provider = LocationManager.GPS_PROVIDER;
        }
        else if(locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER))
        {
            provider = LocationManager.PASSIVE_PROVIDER;
        }
        else
            print("Pleace make sure Location Service is turned on");

        Location location = locationManager.getLastKnownLocation(provider);
        if(location!=null)
        {
            myLat = location.getLatitude();
            myLng = location.getLongitude();
        }
        else
        {
            myLat = ServerSDK._myLatitude;
            myLng = ServerSDK._myLongitude;
        }

        locationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, new LocationListener()
        {
            @Override
            public void onLocationChanged(Location location)
            {
                myLat = location.getLatitude();
                myLng = location.getLongitude();

                locationManager.removeUpdates(this);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras)
            {

            }

            @Override
            public void onProviderEnabled(String provider)
            {

            }

            @Override
            public void onProviderDisabled(String provider)
            {

            }
        });


    }

    public void print(String str){
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        CheckLocationServiceTOGetUesrLocation();

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null)
        {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null)
            {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap()
    {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(myLat, myLng), 15));
        mMap.addMarker(new MarkerOptions().position(new LatLng(myLat, myLng)).title("You")).showInfoWindow();

        if(PassedDatas!=null)
        {
            for (Data.Service srvc : PassedDatas)
            {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(srvc.laititude, srvc.longitude))
                        .title(srvc.name)
                        .snippet(srvc.type)).showInfoWindow();
            }
        }

        if(isForCreateService)
        {
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(myLat, myLng))
                    .title("the New Service").draggable(true)).showInfoWindow();

            print("tap and hold on marker then move it");

            mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener()
            {
                @Override
                public void onMarkerDragStart(Marker marker)
                {

                }

                @Override
                public void onMarkerDrag(Marker marker)
                {
                    myLat2 = marker.getPosition().latitude;
                    myLng2 = marker.getPosition().longitude;
                }

                @Override
                public void onMarkerDragEnd(Marker marker)
                {

                }
            });
        }

        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("ZeroZero"));
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        if(isForCreateService)
        {
            getMenuInflater().inflate(R.menu.menu_select_service_position_from_map, menu);
            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.mapMenuSave)
        {
            setResult(RESULT_OK,
                    new Intent()
                    .putExtra("lat", myLat2)
                    .putExtra("lng", myLng2)
            );

            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy()
    {
        isForCreateService = false;
        super.onDestroy();
    }


}

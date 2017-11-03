package com.onetallprogrammer.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager mLocationManager;

    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (Build.VERSION.SDK_INT < 23) {

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        }else {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

                Location lastKnownLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if(lastKnownLocation != null){

                    updateLocation(lastKnownLocation);

                }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            startListening();

        }
    }

    private void startListening() {

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);

        }

    }

    private void updateLocation(Location location) {

        TextView latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);

        TextView longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);

        TextView accuracyTextView = (TextView) findViewById(R.id.accuracyTextView);

        TextView altitudeTextView = (TextView) findViewById(R.id.altitudeTextView);

        TextView addressTextView = (TextView) findViewById(R.id.addressTextView);

        latitudeTextView.setText(String.format(Locale.getDefault(), "%s %5.2f", getString(R.string.latitude), location.getLatitude()));

        longitudeTextView.setText(String.format(Locale.getDefault(), "%s %5.2f", getString(R.string.longitude), location.getLongitude()));

        accuracyTextView.setText(String.format(Locale.getDefault(), "%s %5.2f", getString(R.string.accuracy), location.getAccuracy()));

        altitudeTextView.setText(String.format(Locale.getDefault(), "%s %5.2f", getString(R.string.altitude), location.getAltitude()));

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {

            String address = getString(R.string.could_not_find_address);

            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if(addresses != null){

                address = "";

                Log.i("Address", addresses.get(0).toString());

                if(addresses.get(0).getSubThoroughfare() != null){

                    address += addresses.get(0).getSubThoroughfare() + " ";

                }

                if(addresses.get(0).getThoroughfare() != null){

                    address += addresses.get(0).getThoroughfare() + "\n";

                }

                if(addresses.get(0).getLocality() != null){

                    address += addresses.get(0).getLocality() + "\n";

                }

                if(addresses.get(0).getPostalCode() != null){

                    address += addresses.get(0).getPostalCode() + "\n";

                }

                if(addresses.get(0).getCountryName() != null){

                    address += addresses.get(0).getCountryName();

                }

            }

            addressTextView.setText(String.format(Locale.getDefault(), "%s \n%s", getString(R.string.address), address));

        } catch (IOException e) {

            e.printStackTrace();

        }

    }
}

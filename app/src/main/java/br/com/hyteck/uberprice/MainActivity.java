package br.com.hyteck.uberprice;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import br.com.hyteck.uberprice.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends Activity implements LocationListener {

    LocationManager locationManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        int REQUEST_PERMISSION_LOCALIZATION = 221;


        if (hasPermission()) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSION_LOCALIZATION);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        super.onCreate(savedInstanceState);
        br.com.hyteck.uberprice.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        refreshValues();
    }

    @SuppressLint("MissingPermission")
    public void refreshValues() {
        if (hasPermission()) {
            return;
        }
        Location location = locationManager
                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        Button refreshButton = findViewById(R.id.refreshBtn);
        refreshButton.setOnClickListener(view -> {
            if (location != null) {
                TextView txtLat = (TextView) findViewById(R.id.location);
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = null;
                String add="";
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    Address obj = addresses.get(0);
                    add = obj.getAddressLine(0);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                txtLat.setText(String.format("Origem: %s", add));


            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }

            if (location != null) {
                retrofitConverter(location.getLatitude()+","+ location.getLongitude());
            }

        });
    }

    private boolean hasPermission() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    public void retrofitConverter(String origin) {

        RetrofitService service= ServiceGenerator.createService(RetrofitService.class);


        Call<List<String>> call = service.getPrice(origin);

        Toast.makeText(getApplicationContext(),"Buscando dados", Toast.LENGTH_LONG).show();

            call.enqueue(new Callback<List<String>>() {
                @Override
                public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                    if (response.isSuccessful()) {
                        TextView resultSearch = findViewById(R.id.resultSearch);
                        List<String> resp = response.body();

                         resultSearch.setText(String.join(",\n", resp));

                    }
                }

                @Override
                public void onFailure(Call<List<String>> call, Throwable t) {
                    Toast.makeText(getApplicationContext(),"Erro na chamada ao servidor", Toast.LENGTH_SHORT).show();
                }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        TextView txtLat = (TextView) findViewById(R.id.location);
        txtLat.setText(String.format("Latitude:%s, Longitude:%s", location.getLatitude(), location.getLongitude()));

    }

    @Override
    public void onFlushComplete(int requestCode) {
        LocationListener.super.onFlushComplete(requestCode);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
        LocationListener.super.onProviderEnabled(provider);
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        LocationListener.super.onProviderDisabled(provider);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

}



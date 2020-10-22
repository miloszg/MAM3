package pl.milosz.mam_1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    // dupa
    private LocationManager lm;

    // Pozycja
    Location currentLocation;
    Location previousLocation;

    Location GGLocation;
    Location WETILocation;
    Location OBCLocation;
    ArrayList<Location> locations = new ArrayList<>();
    TextView speedText, directionText, currentLocationText, previousLocationText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
            }
        }

        currentLocation = new Location("Provider");

        speedText = findViewById(R.id.speedText);
        directionText = findViewById(R.id.directionText);
        currentLocationText = findViewById(R.id.currentLocationText);
        previousLocationText = findViewById(R.id.previousLocationText);

        getLocation();
    }

    public void getLocation() {
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, this);
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setTime(location.getTime());
        currentLocationText.setText("lat: "+currentLocation.getLatitude() +" lon: "+currentLocation.getLongitude());
        if(previousLocation!=null){
            float bearing = previousLocation.bearingTo(currentLocation);
            float distance = previousLocation.distanceTo(currentLocation);
            float time = currentLocation.getTime() - previousLocation.getTime();
            Log.i("DUPA", String.valueOf(distance));
            Log.i("DUPA", String.valueOf(currentLocation.getTime()));
            Log.i("DUPA", String.valueOf(previousLocation.getTime()));
            Log.i("DUPA", String.valueOf(time));

            float speed = (float) (distance*1000 / (currentLocation.getTime() - previousLocation.getTime()));

            speedText.setText(String.valueOf(speed)+" m/s");
            directionText.setText(String.valueOf(bearing));
            previousLocationText.setText("lat: "+previousLocation.getLatitude() +" lon: "+previousLocation.getLongitude());

        }
        if(currentLocation!=null){
            previousLocation = new Location("Provider");
            previousLocation.setLongitude(currentLocation.getLongitude());
            previousLocation.setLatitude(currentLocation.getLatitude());
            previousLocation.setTime(currentLocation.getTime());
        }


    }

//    private void GetLocation() {
//        final LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(3000);
//        locationRequest.setFastestInterval(1000);
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 4);
//            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 5);
//        }
//        LocationServices.getFusedLocationProviderClient(this)
//               .requestLocationUpdates(locationRequest, new LocationCallback(){
//                   @Override
//                   public void onLocationResult(LocationResult locationResult) {
//                       super.onLocationResult(locationResult);
//                       if(locationResult !=null && locationResult.getLocations().size() > 0){
//                           Toast.makeText(MainActivity.this, "jest 1 "+locationResult.getLocations().size(), Toast.LENGTH_SHORT).show();
//                           int latestLocationIndex = locationResult.getLocations().size()-1;
//                           double lat = locationResult.getLocations().get(latestLocationIndex).getLatitude();
//                           double lon = locationResult.getLocations().get(latestLocationIndex).getLongitude();
//                           previousLocation.setLatitude(currentLocation.getLatitude());
//                           previousLocation.setLongitude(currentLocation.getLatitude());
//                           previousLocationText.setText("lat: "+previousLocation.getLatitude() +" lon: "+previousLocation.getLongitude());
//                           currentLocation.setLatitude(lat);
//                           currentLocation.setLongitude(lon);
//                           currentLocationText.setText("lat: "+lat +" lon: "+lon);
//                       }
//                   }
//               }, Looper.getMainLooper());
//    }
}


package pl.milosz.mam_1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements LocationListener {
    private LocationManager lm;
    private static final String FILENAME = "MAM3.txt";
    // Pozycja
    Location currentLocation;
    Location previousLocation;

    TextView speedText, directionText, currentLocationText, previousLocationText, statusText;

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
        statusText = findViewById(R.id.statusText);
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
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 3, this);
            lm.addGpsStatusListener(new GpsStatus.Listener() {
                @Override
                public void onGpsStatusChanged(int event) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        GpsStatus gps = lm.getGpsStatus(null);
                        String status = "";
                        for(GpsSatellite sat:gps.getSatellites()){
                            status+= "PRN:"+sat.getPrn()+"\tSNR:" + sat.getSnr()+"\n";
                        }
                        statusText.setText(status);
                    }
                }
            });
        }
    }

    private void writeToFile(String filename,String content){
        FileOutputStream fos = null;
        try {
            fos = openFileOutput(FILENAME, MODE_PRIVATE);
            fos.write(content.getBytes());
            Toast.makeText(this, "Zapisano do: "+getFilesDir() +"/"+FILENAME, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(fos!=null){
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(4);
        currentLocation.setLatitude(location.getLatitude());
        currentLocation.setLongitude(location.getLongitude());
        currentLocation.setTime(location.getTime());
        currentLocationText.setText("lat: "+df.format(currentLocation.getLatitude()) +" lon: "+df.format(currentLocation.getLongitude()));
        if(previousLocation!=null){
            float bearing = previousLocation.bearingTo(currentLocation);
            float distance = previousLocation.distanceTo(currentLocation); // jednostka metry
            float time = currentLocation.getTime() - previousLocation.getTime(); // jednostka milisekundy
            float speed = (float) (distance*1000 / (currentLocation.getTime() - previousLocation.getTime()));
            speedText.setText(df.format((speed))+" m/s");
            directionText.setText(df.format((bearing)));
            previousLocationText.setText("lat: "+df.format(previousLocation.getLatitude()) +" lon: "+df.format(previousLocation.getLongitude()));
            String content="";
            content += "Status: "+statusText.getText()+"\n";
            content += "Pozycja aktulana: "+currentLocationText.getText()+"\n";
            content += "Pozycja poprzednia: "+previousLocationText.getText()+"\n";
            content += "Prędkość poruszania: "+speedText.getText()+"\n";
            content += "Kierunek poruszania: "+directionText.getText()+"\n";
            writeToFile("MAM3",content);
        }
        if(currentLocation!=null){
            previousLocation = new Location("Provider");
            previousLocation.setLongitude(currentLocation.getLongitude());
            previousLocation.setLatitude(currentLocation.getLatitude());
            previousLocation.setTime(currentLocation.getTime());
        }
    }

    // inna opcja na pobieranie lokacji
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


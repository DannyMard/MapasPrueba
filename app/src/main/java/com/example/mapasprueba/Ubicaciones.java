package com.example.mapasprueba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Ubicaciones extends AppCompatActivity {

    public static final String user = "names";
    TextView txtUser;

    TextView latitud,longitud;
    TextView direccion;

    DatabaseReference midatos;

    //variables velocidad
    LocationManager locationManager;//
    SQLiteDatabase db;//initialize variable of sqlite database type
    TextView mvelocidad;
    SeekBar seekbar1, seekbar2;
    Button button1,button2;
    long start,finish,time;//
    double lat1,lon1,lat2,lon2,alt1,alt2,time1, speed=1;
    public String[] radiuspois;//an array in which I will save if I am within radius of each poi
    int counts;
    //´para calculo de velocidad promedio

    double[] velocidad_Prome = new double[10];
    double suma_velo;
    double prome_vel=1;

    double distance = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicaciones);
        txtUser = (TextView) findViewById(R.id.txt_comunicacion);

        String user = getIntent().getStringExtra("names");
        txtUser.setText(" Bienvenido " + user + " :)");

        midatos = FirebaseDatabase.getInstance().getReference();
        mvelocidad = (TextView)findViewById(R.id.tv_velocidad);
        latitud = (TextView) findViewById(R.id.txtLatitud);
        longitud = (TextView) findViewById(R.id.txtLongitud);
        direccion = (TextView) findViewById(R.id.txtDireccion);



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }


    }
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        latitud.setText("Localización agregada");
        direccion.setText("");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }

    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    direccion.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        Ubicaciones mainActivity;
        public Ubicaciones getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(Ubicaciones mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc ) {


/* prueba de velocidad mediante speed integrada por android.
            if (loc==null){

                mvelocidad.setText("1.0 km/h");
            } else {
                double nCurrentSpeed = loc.getSpeed() * 3.6f;
                speed = nCurrentSpeed*16.66666;
                mvelocidad.setText(String.format("%.2f", speed)+ " m/min" );
                //mvelocidad.setText(Double.toString(nCurrentSpeed ));
            }*/

//// sacamos la velocidad de cambio de puntos mediante calculo con formula de haversine
            if(counts==0) {
                start = System.nanoTime();// comienza timer
                lat1 = loc.getLatitude();//tguarda coordenadas
                lon1 = loc.getLongitude();
                distance = 0;
                counts +=1;
            }
            else if (counts == 5){
                finish = System.nanoTime();//finaliza timer
                lat2 = loc.getLatitude();//guarda coordenadas
                lon2 = loc.getLongitude();
                time = finish - start;//guardamos el tiempo en nano segundos
                time1 = (double)time / 1_000_000_000.0;//en segundos
                distance = distance + measureDistance(lat1,lat2,lon1,lon2);//calculamos la distacia entre dos puntos llamando al metdo de calculo de distancia
                speed = distance / time1;//calculo de distacia en m/s
                speed = speed * 3.6;//de m/s a km/h
                speed = (int) speed;//quitamos decimales convirtiendo a entero
                mvelocidad.setText(Double.toString(speed));// se muestra la velocidad
                start = 0;//reiniciamos el contador
                finish = 0;
                counts=0;

            }
            else{
                lat2 = loc.getLatitude();
                lon2 = loc.getLongitude();
                distance = distance + measureDistance(lat1,lat2,lon1,lon2);
                lat1 = lat2;
                lon1 = lon2;
                counts +=1;
            }



            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();

            String sLatitud = String.valueOf(loc.getLatitude());
            String sLongitud = String.valueOf(loc.getLongitude());
            latitud.setText(sLatitud);
            longitud.setText(sLongitud);
            this.mainActivity.setLocation(loc);




            String userId = FirebaseAuth.getInstance().getUid();
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Conductores");

            GeoFire geoFire = new GeoFire(ref);
            //estos serás los hijos de userid
            geoFire.setLocation("you", new GeoLocation( loc.getLatitude(), loc.getLongitude()));

            Map<String,Object> velo = new HashMap<>();

            velo.put("valor",speed);
            velo.put("latitudd", loc.getLatitude());
            velo.put("longitudd", loc.getLongitude());



            midatos.child("Conductores").child(userId).updateChildren(velo);
           // midatos.child("velocidad").updateChildren(velo);




        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            latitud.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            latitud.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }


        public double measureDistance(Double lat1,Double lat2,Double lon1,Double lon2){
            final int R = 6371; // Radio de la tierra
            double latDistance = Math.toRadians(lat2 - lat1);
            double lonDistance = Math.toRadians(lon2 - lon1);
            double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = R * c * 1000; // convierto a metros
            return distance;
        }


    }



}

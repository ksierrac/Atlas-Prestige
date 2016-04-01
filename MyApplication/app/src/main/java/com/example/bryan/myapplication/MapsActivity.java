package com.example.bryan.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.view.View.OnClickListener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback{

    public GoogleMap mMap;




    @Override
    /**
     * creating the main screen
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //needed for internet connection for some reason
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_maps);
        //addListenerOnbikeCheckbox(); //checkbox tests


        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * This serves as primary function for the interactions involving the 5 main buttons
     * @param v the View
     */

    public void onButtonClicked(View v) throws IOException{

        InputStream is = getAssets().open("buildingcoordinates.txt");
        switch (v.getId()) {

            case R.id.directionsButton: //direction button stuff goes here
                mMap.clear();
                Directions directions = new Directions(this,is);

                break;


            case R.id.bikesButton:
                // Code for button 2 click
                break;


            case R.id.busButton:

                Buses bus = new Buses(this);

                break;


            case R.id.foodButton:
                // Code for button 3 click
                break;


            case R.id.POIButton:

                POI poi = new POI(this,is);
                break;

        }
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

        // Add a marker in CIS and move the camera
        LatLng CIS = new LatLng(34.226107, -77.871775);
        LatLng leutzHall = new LatLng(34.227180, -77.871813);
        LatLng bearHall = new LatLng(34.228493, -77.872800);
        /**
        mMap.addMarker(new MarkerOptions().position(CIS).title("CIS BUILDING Marker WOOO").snippet("Home of Bryan, Sierra, and Victoria"));
        mMap.addMarker(new MarkerOptions().position(leutzHall).title("Bear Hall"));
        mMap.addMarker(new MarkerOptions().position(bearHall).title("Leutze Hall"));

        **/

        mMap.moveCamera(CameraUpdateFactory.newLatLng(CIS));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }




    private void drawPrimaryLinePath( ArrayList<LatLng> listLocsToDraw )
    {
        if ( mMap == null )
        {
            return;
        }

        if ( listLocsToDraw.size() < 2 )
        {
            return;
        }

        PolylineOptions options = new PolylineOptions();

        options.color(Color.GREEN);
        options.width(5);
        options.visible(true);

        for ( LatLng locRecorded : listLocsToDraw )
        {
            options.add( new LatLng( locRecorded.latitude,
                    locRecorded.longitude) );
        }

        mMap.addPolyline(options);

    }
        }
/**
 public void addListenerOnbikeCheckbox() {
 bikeCheckbox = (CheckBox) findViewById(R.id.checkBox);

 LatLng test = new LatLng(34.227524, -77.873301);
 LatLng test3 = new LatLng(34.227054, -77.872191);
 LatLng test2 = new LatLng(34.226107, -77.871775);



 places.add(test);
 places.add(test3);
 places.add(test2);

 bikeCheckbox.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View v) {
if (((CheckBox) v).isChecked()) {
Marker bikeMarker = mMap.addMarker(new MarkerOptions()
.position(new LatLng(34.227524, -77.873301))
.title("Bike Rack")
.snippet("you can put your bike here")
.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
Marker bikeMarker2 = mMap.addMarker(new MarkerOptions()
.position(new LatLng(34.227524, -77.871111))
.title("Bike Rack")
.snippet("you can put your bike here")
.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
drawPrimaryLinePath(places);
}
if (!(((CheckBox) v).isChecked())) {

}
try {
String fullString = "";
double latitude = 0;
double longitude = 0;
URL url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=702%20UNCW%20GREEN&iefix=36855");
BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
String line;
while ((line = reader.readLine()) != null) {

if (line.contains("<latitude>")) {
System.out.println(line.substring(10, line.indexOf("</")));
latitude = Double.parseDouble(line.substring(10, line.indexOf("</")));


}
if (line.contains("<longitude>")) {
System.out.println(line.substring(11, line.indexOf("</")));
longitude = Double.parseDouble(line.substring(11, line.indexOf("</")));
}
}
Marker bikeMarker = mMap.addMarker(new MarkerOptions()
.position(new LatLng(latitude, longitude))
.title("GREEN BUS")
.snippet("brings bryan to school some days")
.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
System.out.println(fullString);
reader.close();
} catch (MalformedURLException e) {
} catch (IOException e) {
}
try {
String fullString = "";
double latitude = 0;
double longitude = 0;
URL url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=712%20UNCW%20TEAL&iefix=36855");
BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
String line;
while ((line = reader.readLine()) != null) {

if (line.contains("<latitude>")) {
System.out.println(line.substring(10, line.indexOf("</")));
latitude = Double.parseDouble(line.substring(10, line.indexOf("</")));


}
if (line.contains("<longitude>")) {
System.out.println(line.substring(11, line.indexOf("</")));
longitude = Double.parseDouble(line.substring(11, line.indexOf("</")));
}
}
Marker bikeMarker = mMap.addMarker(new MarkerOptions()
.position(new LatLng(latitude, longitude))
.title("TEAL BUS")
.snippet("BRYAN IS THE GREATEST")
.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
System.out.println(fullString);
reader.close();
} catch (MalformedURLException e) {
} catch (IOException e) {
}

}
});

 }

 **/


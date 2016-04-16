package com.example.bryan.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Vika on 3/12/2016.
 */
public class POI {
    MapsActivity mapScreen;
    final PopupWindow popupWindow1;

    /**
     * initializes popup Window with functionality
     * @param map
     * @param buildings
     * @throws IOException
     */
    public POI(MapsActivity map, BuildingData buildings) throws IOException

    {

        mapScreen = map;
        final ImageButton poiButton = (ImageButton) mapScreen.findViewById(R.id.POIButton); // setup button function for directions
        LayoutInflater layoutInflater1 //popup behavior
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        View popupView1 = layoutInflater1.inflate(R.layout.poipopup, null);

        final Spinner busRouteSpinner = (Spinner) popupView1.findViewById(R.id.POISpinner); //initiate start spinner
        //String[] busRouteNames = new String[]{"Building1", "Building2", "Building3"}; //bus routes
        final BuildingData data= buildings;

        String[] buildingNames = data.buildingCoordinates.keySet().toArray(new String[data.buildingCoordinates.keySet().size()]);


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mapScreen.getApplicationContext(), R.layout.spinnerlayout, buildingNames); //adapter required for the spinner
        busRouteSpinner.setAdapter(adapter1); //set adapter


        popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                poiButton.setSelected(false);
                popupWindow1.dismiss();
                System.out.println("selected=false");
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                poiButton.setSelected(false);
                popupWindow1.dismiss();
                System.out.println("selected=false");
                LatLng location = data.buildingCoordinates.get(busRouteSpinner.getSelectedItem()).get(0);
                mapScreen.mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mapScreen.mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
//                mapScreen.mMap.addMarker(new MarkerOptions().position(location)
//                        .icon(BitmapDescriptorFactory.defaultMarker()).title(busRouteSpinner.getSelectedItem().toString()));

                mapScreen.mMap.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.poiicon))
                        .title(busRouteSpinner.getSelectedItem().toString()));

            }
        });

        popupWindow1.showAsDropDown(poiButton, 50, -30);

    }
}

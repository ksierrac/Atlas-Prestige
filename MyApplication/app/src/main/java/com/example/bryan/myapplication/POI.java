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
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Arrays;

/**
 * Point of Interest class - initializes a spinner and drops a marker on the chosen POI
 */
public class POI {

    MapsActivity mapScreen;
    final PopupWindow popupWindow1;

    /**
     * initializes popup Window with spinner functionality
     * @param map
     * @param buildings
     * @throws IOException
     */
    public POI(MapsActivity map, BuildingData buildings) throws IOException

    {
        //Will pass in main map screen as parameter
        mapScreen = map;

        //Assign to POI button in the GUI
        final ImageButton poiButton = (ImageButton) mapScreen.findViewById(R.id.POIButton);

        //Set up popup window view and behavior
        LayoutInflater layoutInflater1
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        View popupView1 = layoutInflater1.inflate(R.layout.poipopup, null);

        //Initialize spinner and building data
        final Spinner poiSpinner = (Spinner) popupView1.findViewById(R.id.POISpinner);
        final BuildingData data= buildings;

        //Pull building names from data and sort alphabetically
        String[] buildingNames = data.buildingCoordinates.keySet().toArray(new String[data.buildingCoordinates.keySet().size()]);
        Arrays.sort(buildingNames);

        //set adapter for spinner
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mapScreen.getApplicationContext(), R.layout.spinnerlayout, buildingNames); //adapter required for the spinner
        poiSpinner.setAdapter(adapter1);

        //Initialize popup window
        popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button

        //Dismiss popup window and add building markers back to map
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                poiButton.setSelected(false);
                popupWindow1.dismiss();
                mapScreen.addMarkersToMap(mapScreen.buildingCoords, R.drawable.mapsicon, 50, 50);
            }
        });

        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        //Set a marker and zoom in on the chosen point of interest
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                poiButton.setSelected(false);
                popupWindow1.dismiss();
                LatLng location = data.buildingCoordinates.get(poiSpinner.getSelectedItem()).get(0);
                mapScreen.mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                mapScreen.mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
                mapScreen.mMap.addMarker(new MarkerOptions().position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.poiicon))
                        .title(poiSpinner.getSelectedItem().toString()));
            }
        });

        popupWindow1.showAsDropDown(poiButton, 50, -30);

    }
}

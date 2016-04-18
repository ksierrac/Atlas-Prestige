package com.example.bryan.myapplication;


import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;


/**
 * Main Activity for application.
 * Displays the main screen of the application containing the map of the campus with building markers
 * and five buttons for directions:
 * Directions button - allows to get building-to-building directions on campus.
 * Bike racks button - displays bike racks.
 * Bus button - allows to select and display wave transit bus routes and track the corresponding buses.
 * Dinning button - displays the places to dine on campus.
 * Points 0f interest button - allows to find points of interest on campus (such as one card office, financial id office, etc.)
 */
public class MapsActivity extends FragmentActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback{

    public GoogleMap mMap;
    BuildingData buildings;
    BuildingData dining;
    BuildingData poiData;
    ArrayList <LatLng> poiCoords;
    ArrayList <LatLng> diningCoords;
    ArrayList <LatLng> buildingCoords;
    ArrayList <String> diningKeys;
    ArrayList<LatLng> bikeCoords;
    ArrayList <String> buildingNames;
    ArrayList <String> poiNames;
    BusRouteData busData;
    Boolean busButton = false;
    Buses bus;
    InputStream is;
    InputStream bikesIs;
    InputStream busesIs;
    InputStream foodIs;
    InputStream infoIs;
    InputStream poiIs;
    int [] buttons;
    Directions directions;
    POI poi;
    BuildingInfo info;

    @Override
    /**
     * creating the main screen
     * @param savedInstanceState the Bundle
     */
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //needed for internet connection for some reason
        StrictMode.setThreadPolicy(policy);

        //select the view for the class
        setContentView(R.layout.activity_maps);
        buttons = new int [] {R.id.bikesButton, R.id.directionsButton, R.id.busButton, R.id.foodButton, R.id.POIButton};

        // Obtain the SupportMapFragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }

    /**
     * creates markers for buildings given an arrayList of coordinates, marker image and the size it needs to be scaled to
     * @param latLngs the arrayList of marker coordinates
     * @param image Image for the markers
     * @param size1 Desired image width
     * @param size2 Desired image height
     */
    public void addMarkersToMap(ArrayList<LatLng> latLngs, int image, int size1, int size2) {
        //get the image for markers
        Drawable drawable = ResourcesCompat.getDrawable( getResources(),image, null);

        //turn the image into a bitmap to make it scalable and scale it to the desired size
        Bitmap b = ((BitmapDrawable)drawable).getBitmap();
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, size1, size2, false);

        //add building markers to the map with the snippet "info" to distinguish them from other markers.
        for (int i=0;i<latLngs.size();i++) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(buildingNames.get(i))
                    .snippet("info")
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmapResized)));
            marker.setVisible(true);
        }
    }

    /**
     * creates bike rack markers given an arrayList of coordinates uses bike markers
     * @param latLngs the ArrayList of marker coordinates
     */
    public void addBikeMarkersToMap(ArrayList<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.bikeiconsmall))
                    .snippet(""));
            marker.setVisible(true);
        }
    }

    /**
     * creates dinning markers given an arrayList of coordinates uses dining markers
     * @param latLngs the ArrayList of marker coordinates
     */
    public void addFoodMarkersToMap(ArrayList<LatLng> latLngs)  {

        for (int i=0;i<latLngs.size();i++) {
            Marker marker = mMap.addMarker(new MarkerOptions().position(latLngs.get(i))
                    .title(diningKeys.get(i))
                    .snippet("")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.food)));
            marker.setVisible(true);
        }
    }



    /** deselectes all of the buttons except the one that's been pressed
     *
                * @param exceptThisButton the button that was pressed
                */
                public void deselectButtons(int exceptThisButton){
                for(int button : buttons){
                    if (button != exceptThisButton) findViewById(button).setSelected(false);
                }
            }

            /**
             * Closes popups if any are open when a button is clicked
             */
        public void closePopUps(){
            if(directions!=null){directions.popupWindow1.dismiss(); }
            if(bus!=null)bus.popupWindow1.dismiss();
            if(poi!=null)poi.popupWindow1.dismiss();
        }

        /**
         * This serves as primary function for the interactions involving the 5 main buttons
         * @param v the View for this class
         * @throws IOException
         */

        public void onButtonClicked(View v) throws IOException{
            //clear the map when a button is clicked
            mMap.clear();
            switch (v.getId()) {
                //directions button
                case R.id.directionsButton: // set up directions
                    deselectButtons(R.id.directionsButton); //deselect other buttons
                    closePopUps(); // close popups
                    if (busButton) {bus.mainTimer.cancel();} //if busButton was selected, stop the timer that updates bus GPS

                    // if the button wasn't previously clicked
                if (!v.isSelected()) {
                    v.setSelected(true); //select the button to change its image
                    directions = new Directions(this,buildings); //start directions activity
                }
                else { //the button clicked for the second time in a row
                    v.setSelected(false);
                    directions.popupWindow1.dismiss(); //close the pop-up
                    addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50); //display building markers
                }

                break;

            // bike rack button
            case R.id.bikesButton:
                deselectButtons(R.id.bikesButton); //deselect other buttons
                closePopUps(); // close popups
                if (busButton) {bus.mainTimer.cancel();} // stop bus timer
                // if the button wasn't previously clicked
                if (!v.isSelected()) {
                    v.setSelected(true);
                    addBikeMarkersToMap(bikeCoords);
                }
                else { //the button clicked for the second time in a row
                    v.setSelected(false);
                    addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50);
                }
                break;

            // bus button
            case R.id.busButton:
                deselectButtons(R.id.busButton); //deselect other buttons
                closePopUps(); // close popups
                if (busButton) {bus.mainTimer.cancel();} // stop bus timer

                if (!v.isSelected()) {
                    v.setSelected(true);
                    bus = new Buses(this,busData); //set up bus routes and tracking
                    busButton = true; // starts the bus timer that updates bus markers
                }
                else {
                    v.setSelected(false);
                    busButton = false;
                    addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50);
                }

                break;

            // dinning button
            case R.id.foodButton:
                deselectButtons(R.id.foodButton);
                closePopUps();
                if (busButton) {bus.mainTimer.cancel();}

                if (!v.isSelected()) {
                    v.setSelected(true);
                    addFoodMarkersToMap(diningCoords); // add dinning markers to the map
                } else {
                    v.setSelected(false);
                    addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50);
                }

                break;

            // points of interest button
            case R.id.POIButton:
                deselectButtons(R.id.POIButton);
                closePopUps();
                if (busButton) {bus.mainTimer.cancel();} // stop bus timer

                if (!v.isSelected()) {
                    v.setSelected(true);
                    poi = new POI(this,poiData); // start points of interest activity
                } else {
                    v.setSelected(false);
                    poi.popupWindow1.dismiss();
                    addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50);
                }

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
     * @param googleMap the google map
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap; //campus map

        // get current location
        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
        };


        // Add a marker in CIS and move the camera
        LatLng CIS = new LatLng(34.226107, -77.871775);

        //set up hashmaps with building coordinates, building info, bus routes, dinning coordinates,
        //bike racks, and points of interest coordinates.
        try {
            is = getAssets().open("buildingcoordinates.txt");
            bikesIs = getAssets().open("bikeracks");
            busesIs = getAssets().open("busRoutes.txt");
            foodIs = getAssets().open("dininglocations.txt");
            infoIs = getAssets().open("buildinginfo");
            poiIs = getAssets().open("poi list.txt");

            // create a list with center coordinates of buildings and add building markers
            buildings = new BuildingData(is);
            buildingCoords= new ArrayList<>();
            buildingNames = new ArrayList<String>();
            for(String test: buildings.buildingCoordinates.keySet())
            {
                buildingCoords.add(buildings.buildingCoordinates.get(test).get(0));
                buildingNames.add(test);
            }
            //add building markers to the map by default
            addMarkersToMap(buildingCoords, R.drawable.mapsicon, 50, 50);


            // create a list with dining coordinates
            dining = new BuildingData(foodIs);
            diningCoords= new ArrayList<>();
            diningKeys = new ArrayList<String>();
            for(String test: dining.buildingCoordinates.keySet())
            {
                diningCoords.add(dining.buildingCoordinates.get(test).get(0));
                diningKeys.add(test);
            }

            // create a list with bike coordinates
            Scanner scan = new Scanner(bikesIs);
            bikeCoords = new ArrayList<LatLng>();
            while (scan.hasNext()) {
                LatLng coord = new LatLng(scan.nextDouble(),scan.nextDouble());
                bikeCoords.add(coord);
            }
            scan.close();

            //create bus routes hashmap
            busData = new BusRouteData(busesIs);
            info = new BuildingInfo(infoIs);

            //creates points of interest hashmap
            poiData = new BuildingData(poiIs);
            poiCoords= new ArrayList<>();
            poiNames = new ArrayList<String>();
            for(String test: poiData.buildingCoordinates.keySet())
            {
                poiCoords.add(poiData.buildingCoordinates.get(test).get(0));
                poiNames.add(test);
            }


        }
        catch(IOException e){

        };
        mMap.setOnMarkerClickListener(this); //add marker click listener
        mMap.moveCamera(CameraUpdateFactory.newLatLng(CIS));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //if a marker clicked is a building marker, create a window with the building picture and info
        if (marker.getSnippet()!= null && marker.getSnippet().equals("info")) {
            final Dialog d = new Dialog(MapsActivity.this);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE); //create a window

            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            d.setContentView(R.layout.content); //set window layout

            //remove spaces and convert marker title to lower case to get the name of the corresponding image
            String resourceName = marker.getTitle().replaceAll("\\s", "").toLowerCase().replaceAll("-", "");
            int resourceID = this.getResources().getIdentifier(resourceName, "drawable",this.getPackageName());

            //building image
            ImageView image = (ImageView)d.findViewById(R.id.image);
            image.setImageResource(resourceID);

            //building name
            TextView name = (TextView) d.findViewById(R.id.name);
            name.setText(marker.getTitle());

            //building info
            TextView content = (TextView)d.findViewById(R.id.info);

            //format info
            String test = info.buildingInfo.get(marker.getTitle()).replaceAll("\\\\n", "%");
            String newString = "";
            String ch;
            for(int i=0;i<test.length();i++){
                ch = ""+test.charAt(i);
                if("%".equals(ch)){newString += "\n";}
                else{newString += ch;}
            }

            content.setText(newString);

            d.show();
            return true;
        }

        return false;

    }
}



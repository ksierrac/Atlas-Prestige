package com.example.bryan.myapplication;

import android.graphics.Color;
import android.location.Location;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Directions popup and Functionality
 */
public class Directions {

    MapsActivity mapScreen;
    private List<Marker> markers = new ArrayList<Marker>();
    int currentPt;
    int ANIMATE_SPEED = 500;
    private String startBuilding = "";
    private String endBuilding = "";
    final PopupWindow popupWindow1;

    /**
     * Initiates PopWindow
     * @param map MapActivity screen
     * @param buildings hashmap for building coordinates
     * @throws IOException
     */
    public Directions(MapsActivity map, BuildingData buildings) throws IOException{

        mapScreen = map;

        final ImageButton dirButton = (ImageButton) mapScreen.findViewById(R.id.directionsButton); // setup button function for directions
        LayoutInflater layoutInflater1 //popup behavior
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        View popupView1 = layoutInflater1.inflate(R.layout.directionspopup, null);


        final Spinner startSpinner = (Spinner) popupView1.findViewById(R.id.startSpinner); //initiate start spinner

        final BuildingData data = buildings; //dictionary access


        String[] buildingNames = data.buildingCoordinates.keySet().toArray(new String[data.buildingCoordinates.keySet().size()]); //takes the keys from dictionary to populate spinner
        Arrays.sort(buildingNames); //sorts names alphabetically

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mapScreen.getApplicationContext(), R.layout.spinnerlayout, buildingNames); //adapter required for the spinner
        startSpinner.setAdapter(adapter1); //set adapter
        final Spinner endSpinner = (Spinner) popupView1.findViewById(R.id.destinationSpinner); //initiate start spinner
        endSpinner.setAdapter(adapter1);

        popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dirButton.setSelected(false);
                popupWindow1.dismiss();
                mapScreen.addMarkersToMap(mapScreen.buildingCoords, R.drawable.mapsicon, 50, 50);
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dirButton.setSelected(false);
                popupWindow1.dismiss();
                ArrayList <LatLng> chosenEntrances = new ArrayList<LatLng>(); //set array for final two entrances to get directions to

                //gets the two entrances between two building yields the shortest distance apart
                chosenEntrances = shortestEntrancePath(data.buildingCoordinates.get(startSpinner.getSelectedItem().toString()), data.buildingCoordinates.get(endSpinner.getSelectedItem().toString()));

                LatLng startDestination = chosenEntrances.get(0);
                LatLng endDestination = chosenEntrances.get(1);
                //System.out.println(startDestination);

                startBuilding = startSpinner.getSelectedItem().toString();
                endBuilding =  endSpinner.getSelectedItem().toString();


                try {


                    //  directions url request
                    URL url = new URL("https://maps.google.com/maps/api/directions/json?origin=" + startDestination.latitude + "," + startDestination.longitude + "&destination=" + endDestination.latitude + "," + endDestination.longitude +"&mode=walking&sensor=false&key=AIzaSyAU5Hq8LlAFjyFwBjEh__17CXR4bbsId40");
                    //System.out.println(url.toString());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())); //setup reader
                    String line;
                    String test;
                    String testString = "";

                    // while reader has stuff to read, concatenates a string
                    while ((line = reader.readLine()) != null) {

                        testString= testString + line;

                    }

                    test = testString.substring(testString.indexOf("overview_polyline"),testString.indexOf("summary")-1); //gets the full encoded string for the polyline


                    test = test.substring(test.indexOf("points")+11,test.lastIndexOf('"')); //gets the full encoded string for the polyline
                    //System.out.println(test);
                    String test2 = test.replaceAll("\\\\\\\\", "\\\\"); //need to remove half the \'s since the language automatically adds another one to try and do literal, this is already accounted for in google so it is not needed
                    //System.out.println(test2);

                    ArrayList<LatLng> encoded = decodePoly(test2); //gets the coordinates based off the encoded strings

                    encoded.add(endDestination); // makes final connection to destination
                    encoded.add(0,startDestination); //makes final connection to start
                    addMarkersToMap(encoded); // adds markers to set up animation
                    startAnimation(); //starts animation
                    //drawing the resulting ArrayList
                    new Routes(encoded, mapScreen, Color.GREEN);
                   // System.out.println(test);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //example of decoding the polyline overview string





            }
        });

        popupWindow1.showAsDropDown(dirButton, 50, -30);

    }

    /**
     * takes encoded google Maps string and
     * @param encoded encoded string to be decoded
     * @return ArrayList of coordinates
     */
    public static ArrayList decodePoly(String encoded) {
        ArrayList poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;
            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;
            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }

    /**
     * gets the shortest entrance distances
     * @param startList entrances for Start Location in ArrayList
     * @param endList entrances for Destination in ARrayList
     * @return ArrayList of possible entrance combinations sorted by distance
     */
    private ArrayList<LatLng> shortestEntrancePath (ArrayList<LatLng> startList, ArrayList<LatLng> endList)
    {
        ArrayList<LatLng> chosenEntrances= new ArrayList<LatLng>();
        ArrayList<LatLng> startListCopy = new ArrayList<LatLng>();
        ArrayList<LatLng> endListCopy = new ArrayList<LatLng>();

        for (int i= 1; i <startList.size(); i++)
        {
            startListCopy.add(startList.get(i));
        }
        for (int i= 1; i <endList.size(); i++)
        {
            endListCopy.add(endList.get(i));
        }
        float[] results = new float[1];
        results[0] = 999999999;
        float min = 999999999;

        for (LatLng locRecorded : startListCopy)
        {
            for (LatLng locRecorded2 : endListCopy)
            {
                Location.distanceBetween(locRecorded.latitude,locRecorded.longitude,locRecorded2.latitude,locRecorded2.longitude,results);
                if(results[0] < min)
                {
                    chosenEntrances.clear();
                    chosenEntrances.add(locRecorded);
                    chosenEntrances.add(locRecorded2);
                    min = results[0];



                }
            }
        }

        return chosenEntrances;
    }

    /**
     * Adds markers to the Map
     * @param latLngs ArrayList of Coordinates
     */
    public void addMarkersToMap(ArrayList<LatLng> latLngs) {
        for (LatLng latLng : latLngs) {
            Marker marker = mapScreen.mMap.addMarker(new MarkerOptions().position(latLng)
                    .title("title")
                    .snippet("snippet"));
            marker.setVisible(false);
            markers.add(marker);

        }
    }



    // able to control finish activities and cancel activities with this
    CancelableCallback MyCancelableCallback = new CancelableCallback(){

        @Override // if user opts out of animation
        public void onCancel() {
            for (Marker marker: markers)
            {
                //start marker information appears
                marker.setVisible(false);
                markers.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                markers.get(0).setVisible(true);
                markers.get(0).setTitle(startBuilding);
                markers.get(0).setSnippet("");

                //destination marker information appears
                markers.get(markers.size()-1).setVisible(true);
                markers.get(markers.size()-1).setTitle(endBuilding);
                markers.get(markers.size()-1).setSnippet("");
                markers.get(markers.size()-1).showInfoWindow();

            }
           // System.out.println("********** oncancel");
        }


        @Override //shows the movement
        public void onFinish() {

        try {
            //set all markers invisible
            if (++currentPt < markers.size()) {
                for (Marker marker : markers) {
                    marker.setVisible(false);
                }
                //sets marker instance visible before moving to next one
                LatLng targetLatLng = markers.get(currentPt).getPosition();
                markers.get(currentPt).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                markers.get(currentPt).setVisible(true);


                //gets camera to focus on route being walked
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(targetLatLng)
                                .tilt(0)
                                .bearing(0)
                                .zoom(mapScreen.mMap.getCameraPosition().zoom)
                                .build();

                mapScreen.mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        ANIMATE_SPEED,
                        currentPt == markers.size() - 1 ? FinalCancelableCallback : MyCancelableCallback);

//						googleMap.moveCamera(
//								CameraUpdateFactory.newCameraPosition(cameraPosition));


            }
        } catch (IllegalArgumentException e){};
        }

    };

    CancelableCallback FinalCancelableCallback = new CancelableCallback() {

        @Override //final motion if animation went all the way through
        public void onFinish() {
            mapScreen.mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

            markers.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            //markers.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.uncw));
            markers.get(0).setVisible(true);
            markers.get(0).setTitle(startBuilding);
            markers.get(0).setSnippet("");

            markers.get(markers.size() - 1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markers.get(markers.size()-1).setVisible(true);
            markers.get(markers.size()-1).setTitle(endBuilding);
            markers.get(markers.size()-1).setSnippet("");
            markers.get(markers.size()-1).showInfoWindow();

        }

        @Override
        public void onCancel() {

        }
    };

    public void startAnimation() { //starts animation
        mapScreen.mMap.animateCamera(
                CameraUpdateFactory.zoomTo(17),
                100,
                MyCancelableCallback);

        currentPt = 0-1;

    }

    
}

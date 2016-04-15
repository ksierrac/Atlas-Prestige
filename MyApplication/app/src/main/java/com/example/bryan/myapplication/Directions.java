package com.example.bryan.myapplication;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import android.view.animation.Interpolator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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

        //final BuildingData data= new BuildingData(is);
        final BuildingData data = buildings;

        String[] buildingNames = data.buildingCoordinates.keySet().toArray(new String[data.buildingCoordinates.keySet().size()]);


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
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                dirButton.setSelected(false);
                popupWindow1.dismiss();
                ArrayList <LatLng> chosenEntrances = new ArrayList<LatLng>();
                chosenEntrances = shortestEntrancePath(data.buildingCoordinates.get(startSpinner.getSelectedItem().toString()), data.buildingCoordinates.get(endSpinner.getSelectedItem().toString()));
                LatLng startDestination = chosenEntrances.get(0);
                LatLng endDestination = chosenEntrances.get(1);
                System.out.println(startDestination);
                startBuilding = startSpinner.getSelectedItem().toString();
                endBuilding =  endSpinner.getSelectedItem().toString();


                try {


                    // examples of directions url request
                    URL url = new URL("https://maps.google.com/maps/api/directions/json?origin=" + startDestination.latitude + "," + startDestination.longitude + "&destination=" + endDestination.latitude + "," + endDestination.longitude +"&mode=walking&sensor=false&key=AIzaSyAU5Hq8LlAFjyFwBjEh__17CXR4bbsId40");
                    System.out.println(url.toString());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String line;
                    String test;
                    String testString = "";

                    while ((line = reader.readLine()) != null) {

                        testString= testString + line;

                    }
                    test = testString.substring(testString.indexOf("overview_polyline"),testString.indexOf("summary")-1);
                    test = test.substring(test.indexOf("points")+11,test.lastIndexOf('"'));
                    System.out.println(test);
                    String test2 = test.replaceAll("\\\\\\\\", "\\\\");
                    System.out.println(test2);

                    // there is a problem with backslashes.....example kenan auditorium to depaulo hall.... damnit!!!!
                    ArrayList<LatLng> encoded = decodePoly(test2);

                    encoded.add(endDestination);
                    encoded.add(0,startDestination);
                    addMarkersToMap(encoded);
                    startAnimation();
                    //drawing the resulting ArrayList
                    new Routes(encoded, mapScreen, Color.GREEN);
                    System.out.println(test);
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
        float[] results = new float[1];
        results[0] = 999999999;
        float min = 999999999;
        startList.remove(0);
        endList.remove(0);
        for (LatLng locRecorded : startList)
        {
            for (LatLng locRecorded2 : endList)
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

    private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
        Location beginL= convertLatLngToLocation(begin);
        Location endL= convertLatLngToLocation(end);
        return beginL.bearingTo(endL);
    }

    private Location convertLatLngToLocation(LatLng latLng) {
        Location loc = new Location("someLoc");
        loc.setLatitude(latLng.latitude);
        loc.setLongitude(latLng.longitude);
        return loc;
    }

    CancelableCallback MyCancelableCallback = new CancelableCallback(){

        @Override
        public void onCancel() {
            for (Marker marker: markers)
            {
                marker.setVisible(false);
                markers.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                markers.get(0).setVisible(true);
                markers.get(0).setTitle(startBuilding);


                markers.get(markers.size()-1).setVisible(true);
                markers.get(markers.size()-1).setTitle(endBuilding);
                markers.get(markers.size()-1).showInfoWindow();

            }
            System.out.println("********** oncancel");
        }


        @Override
        public void onFinish() {

            if(++currentPt < markers.size()){
                float targetBearing = bearingBetweenLatLngs( mapScreen.mMap.getCameraPosition().target, markers.get(currentPt).getPosition());
                for (Marker marker: markers)
                {
                    marker.setVisible(false);
                }
                LatLng targetLatLng = markers.get(currentPt).getPosition();
                markers.get(currentPt).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                markers.get(currentPt).setTitle("FOLLOW MEEEEEE");

                markers.get(currentPt).setVisible(true);
                markers.get(currentPt).showInfoWindow();

                System.out.println(" ------- " + currentPt + " - " + markers.size() + " - " + targetBearing + " - " + targetLatLng);

                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                                .target(targetLatLng)
                                .tilt(0)
                                .bearing(0)
                                .zoom( mapScreen.mMap.getCameraPosition().zoom)
                                .build();

                mapScreen.mMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(cameraPosition),
                        ANIMATE_SPEED,
                        currentPt==markers.size()-1 ? FinalCancelableCallback : MyCancelableCallback);

//						googleMap.moveCamera(
//								CameraUpdateFactory.newCameraPosition(cameraPosition));



            }

        }

    };

    CancelableCallback FinalCancelableCallback = new CancelableCallback() {

        @Override
        public void onFinish() {
            mapScreen.mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

            markers.get(0).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
            //markers.get(0).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.uncw));
            markers.get(0).setVisible(true);
            markers.get(0).setTitle(startBuilding);

            markers.get(markers.size()-1).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            markers.get(markers.size()-1).setVisible(true);
            markers.get(markers.size()-1).setTitle(endBuilding);
            markers.get(markers.size()-1).showInfoWindow();

        }

        @Override
        public void onCancel() {

        }
    };

    public void startAnimation() {
        mapScreen.mMap.animateCamera(
                CameraUpdateFactory.zoomTo(17),
                100,
                MyCancelableCallback);

        currentPt = 0-1;

    }

    
}

package com.example.bryan.myapplication;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Vika on 3/12/2016.
 */
public class Directions {

    MapsActivity mapScreen;

    public Directions(MapsActivity map) {

        mapScreen = map;
        final Button busButton = (Button) mapScreen.findViewById(R.id.directionsButton); // setup button function for directions
        LayoutInflater layoutInflater1 //popup behavior
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        View popupView1 = layoutInflater1.inflate(R.layout.directionspopup, null);

        final Spinner busRouteSpinner = (Spinner) popupView1.findViewById(R.id.startSpinner); //initiate start spinner
        String[] busRouteNames = new String[]{"Building1", "Building2", "Building3"}; //bus routes
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mapScreen.getApplicationContext(), R.layout.spinnerlayout, busRouteNames); //adapter required for the spinner
        busRouteSpinner.setAdapter(adapter1); //set adapter
        Spinner endSpinner = (Spinner) popupView1.findViewById(R.id.destinationSpinner); //initiate start spinner
        endSpinner.setAdapter(adapter1);

        final PopupWindow popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow1.dismiss();
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    // examples of directions url request
                    URL url = new URL("https://maps.google.com/maps/api/directions/json?origin=34.227524,-77.873301&destination=34.227524,-77.873201&mode=walking&sensor=false&key=AIzaSyAU5Hq8LlAFjyFwBjEh__17CXR4bbsId40");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                //example of decoding the polyline overview string
                ArrayList<LatLng> encoded = decodePoly("id|oErohzMm@kAOpAAH");
                //drawing the resulting ArrayList
                new Routes(encoded, mapScreen);

                //the directions only show the sidewalk paths.
                ArrayList<LatLng> latlng1 = new ArrayList();
                latlng1.add(new LatLng(34.227895,-77.872604));
                latlng1.add(encoded.get(0));
                new Routes(latlng1, mapScreen);
            }
        });

        popupWindow1.showAsDropDown(busButton, 50, -30);

    }
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

    private void drawPrimaryLinePath( ArrayList<LatLng> listLocsToDraw )
    {
        if ( mapScreen.mMap == null )
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

        mapScreen.mMap.addPolyline(options);

    }
}

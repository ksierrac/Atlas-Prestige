package com.example.bryan.myapplication;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

/**
 * Created by Vika on 3/12/2016.
 */
public class Routes {

    MapsActivity mapScreen;

    public Routes(ArrayList<LatLng> listLocsToDraw, MapsActivity maps) {
        mapScreen = maps;
        {
            if (mapScreen.mMap == null) {
                return;
            }

            if (listLocsToDraw.size() < 2) {
                return;
            }

            PolylineOptions options = new PolylineOptions();

            options.color(Color.GREEN);
            options.width(5);
            options.visible(true);

            for (LatLng locRecorded : listLocsToDraw) {
                options.add(new LatLng(locRecorded.latitude,
                        locRecorded.longitude));
            }

            mapScreen.mMap.addPolyline(options);


        }

    }
}

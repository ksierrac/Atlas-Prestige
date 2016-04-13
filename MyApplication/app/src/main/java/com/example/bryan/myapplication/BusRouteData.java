package com.example.bryan.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * BusRoute class
 */
public class BusRouteData {

    public HashMap<String, ArrayList<LatLng>> busRoutes = new HashMap<String, ArrayList<LatLng>>();

    /**
     * creates Hashmap consisting of buses and their respective bus route Coords
     * @param is InputStream for access to text file
     * @throws IOException
     */
    public BusRouteData( InputStream is) throws IOException {

        Scanner scan = new Scanner(is);

        while (scan.hasNext()) {

            String key = "";
            String next = scan.next();
            while (!next.equals(":")) {
                key += next + " ";
                System.out.println(key);
                next = scan.next();
            }
            ArrayList<LatLng> values = new ArrayList<LatLng>();
            while (scan.hasNextDouble()) {
                LatLng coord = new LatLng(scan.nextDouble(),scan.nextDouble());
                values.add(coord);
            }
            key = key.trim();
            busRoutes.put(key, values);
        }
        scan.close();
    }
}

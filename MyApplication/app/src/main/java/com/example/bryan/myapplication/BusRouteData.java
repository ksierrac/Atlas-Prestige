package com.example.bryan.myapplication;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Creates a hashmap containg bus route names as keys and array lists with bus route coordinates as values
 */
public class BusRouteData {

    public HashMap<String, ArrayList<LatLng>> busRoutes = new HashMap<String, ArrayList<LatLng>>();

    /**
     * creates Hashmap consisting of buses and their respective bus route Coords
     * @param is InputStream for access to text file
     * @throws IOException
     */
    public BusRouteData( InputStream is) throws IOException {
        // process the file
        Scanner scan = new Scanner(is);
        while (scan.hasNext()) {
            //extract the bus route name to use as a key
            String key = "";
            String next = scan.next();
            while (!next.equals(":")) {
                key += next + " ";
                System.out.println(key);
                next = scan.next();
            }
            //create array list with bus route coordinates
            ArrayList<LatLng> values = new ArrayList<LatLng>();
            while (scan.hasNextDouble()) {
                LatLng coord = new LatLng(scan.nextDouble(),scan.nextDouble());
                values.add(coord);
            }
            key = key.trim(); // trim the key
            busRoutes.put(key, values); // add the key/value pair to the hashmap
        }
        scan.close();
    }
}

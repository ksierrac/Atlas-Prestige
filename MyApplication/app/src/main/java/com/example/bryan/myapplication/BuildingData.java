package com.example.bryan.myapplication;

import com.google.android.gms.maps.model.LatLng;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Created by Victoia Donelson on 3/12/2016.
 *
 * Creates a hashmap with building names as keys and array lists containing the coordinates
 * of the building centers and entrances as values.
 */


public class BuildingData {

    public HashMap<String, ArrayList<LatLng>> buildingCoordinates = new HashMap<String, ArrayList<LatLng>>();

    /**
     * Processes file with building data
     * @param is Input stream from a file containing building coordinates
     * @throws IOException
     */
    public BuildingData( InputStream is) throws IOException {
        //scan in info line by line
        Scanner scan = new Scanner(is);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Scanner in = new Scanner(line);
            String key = "";
            String next = in.next();
            //process each line to extract the name of the building to use as a key
            while (!next.equals(":")) {
                key += next + " ";
                next = in.next();
            }
            //read in coordinates to create LatLng objects and add them to array list
            ArrayList<LatLng> values = new ArrayList<LatLng>();
            while (in.hasNext()) {
                LatLng coord = new LatLng(in.nextDouble(),in.nextDouble());
                values.add(coord);
            }

            key = key.trim(); //get rid of spaces at the beginning and the end of the key
            buildingCoordinates.put(key, values); //add the key/value pair to the hashmap
        }
        scan.close();
    }

}

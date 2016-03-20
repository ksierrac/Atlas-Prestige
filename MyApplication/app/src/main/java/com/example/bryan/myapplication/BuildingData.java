package com.example.bryan.myapplication;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * Created by Vika on 3/12/2016.
 */


public class BuildingData {

    public HashMap<String, ArrayList<LatLng>> buildingCoordinates = new HashMap<String, ArrayList<LatLng>>();

    public BuildingData() throws FileNotFoundException {
        File f = new File("buildingcoordinates.txt");
        Scanner scan = new Scanner(f);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Scanner in = new Scanner(line);
            String key = "";
            String next = in.next();
            while (!next.equals(":")) {
                key += next + " ";
                //System.out.println(key);
                next = in.next();
            }
            ArrayList<LatLng> values = new ArrayList<LatLng>();
            while (in.hasNext()) {
                LatLng coord = new LatLng(in.nextDouble(),in.nextDouble());
                values.add(coord);
            }
            key = key.trim();
            buildingCoordinates.put(key, values);
        }
        scan.close();
    }

}

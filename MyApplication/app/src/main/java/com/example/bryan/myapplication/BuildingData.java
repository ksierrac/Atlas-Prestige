package com.example.bryan.myapplication;

import android.content.Context;
import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public BuildingData( InputStream is) throws IOException {

        Scanner scan = new Scanner(is);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            System.out.println(line);
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

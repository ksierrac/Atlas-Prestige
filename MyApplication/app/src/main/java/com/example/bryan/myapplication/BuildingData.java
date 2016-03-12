package com.example.bryan.myapplication;

import android.graphics.Point;

import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Scanner;
import java.io.FileNotFoundException;

/**
 * Created by Vika on 3/12/2016.
 */
public class BuildingData extends Dictionary{

    public BuildingData () throws FileNotFoundException
    {
        File f = new File("buildingcoordinates.txt");
        Scanner scan = new Scanner(f);
        while (scan.hasNextLine()){
            String line = scan.nextLine();
            Scanner in = new Scanner (line);
            String key = in.next();
            ArrayList <LatLng> values = new ArrayList <LatLng>();
            while (in.hasNext()){
                LatLng coord = new LatLng(in.nextDouble(), in.nextDouble());
                values.add(coord);
            }
            this.put(key, values);
        }
        scan.close();
    }
    public Enumeration elements() {
        return null;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Enumeration keys() {
        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

}

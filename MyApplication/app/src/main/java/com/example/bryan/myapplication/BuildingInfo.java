package com.example.bryan.myapplication;


import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by Vika on 4/16/2016.
 */
public class BuildingInfo {
    public HashMap<String, String> buildingInfo = new HashMap<String, String>();

    /**
     * creates Hashmap consisting of building names and corresponding building information
     * @param is InputStream for access to text file
     * @throws IOException
     */
    public BuildingInfo( InputStream is) throws IOException {

        Scanner scan = new Scanner(is);
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            //System.out.println(line);
            Scanner in = new Scanner(line);
            String key = "";
            String next = in.next();
            while (!next.equals(":")) {
                key += next + " ";
                //System.out.println(key);
                next = in.next();
            }
            String info = "";
            while (in.hasNext()) {
                info += in.next()+" ";
            }
            key = key.trim();

            buildingInfo.put(key, info);

        }
        scan.close();
    }
}



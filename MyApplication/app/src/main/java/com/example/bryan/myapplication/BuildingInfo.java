package com.example.bryan.myapplication;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Construct hashmap storing building names and data
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

        //Parse text file to assign building names as keys, using ":" as an endpoint
        while (scan.hasNextLine()) {
            String line = scan.nextLine();
            Scanner in = new Scanner(line);
            String key = "";
            String next = in.next();
            while (!next.equals(":")) {
                key += next + " ";
                next = in.next();
            }

        //All information after ":" is building information used as hashmap values
            String info = "";
            while (in.hasNext()) {
                info += in.next()+" ";
            }
            key = key.trim();

        //Enter building name/info pair into the hashmap
            buildingInfo.put(key, info);

        }
        scan.close();
    }
}



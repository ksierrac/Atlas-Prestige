package com.example.bryan.myapplication;

import android.graphics.Color;
import android.net.wifi.p2p.WifiP2pManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 *
 * Buses Popup Window and Functionality
 */
public class Buses {

    MapsActivity mapScreen;
    final BusRouteData data;
    final Spinner busRouteSpinner;
    public Timer mainTimer;
    URL url;
    URL url2;
    Marker busMarker;
    final PopupWindow popupWindow1;

    /**
     * Bus Constructor, automatically starts popupWindow upon intialization
     * @param map MapActivity
     * @param busData hashmap with bus route names and coordinates
     * @throws IOException
     */
    public Buses(MapsActivity map, BusRouteData busData) throws IOException

    {
        data= busData;

        mapScreen = map;

        final ImageButton busButton = (ImageButton) mapScreen.findViewById(R.id.busButton); // setup button function for directions
        LayoutInflater layoutInflater1 //popup behavior
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        final View popupView1 = layoutInflater1.inflate(R.layout.buspopup, null);

        mainTimer = new Timer();

        final TimerTask timerTask;
        timerTask = new TimerTask() {

            public void run() {
                mapScreen.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("AYEEE");
                        updateMap(mapScreen);

                    }
                });

            }
        };



         busRouteSpinner = (Spinner) popupView1.findViewById(R.id.busRouteSpinner); //initiate start spinner



        String[] busRouteNames = data.busRoutes.keySet().toArray(new String[data.busRoutes.keySet().size()]);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mapScreen.getApplicationContext(), R.layout.spinnerlayout, busRouteNames); //adapter required for the spinner
        busRouteSpinner.setAdapter(adapter1); //set adapter


        popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            /**
             * when exit button is clicked
             */
            public void onClick(View v) {
                // TODO Auto-generated method stub
                busButton.setSelected(false);
                popupWindow1.dismiss();
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            /**
             * when go Button is clicked
             */
            public void onClick(View v) {
                busButton.setSelected(false);
                popupWindow1.dismiss();

                System.out.println("test");
                ArrayList<LatLng> busCoords = data.busRoutes.get(busRouteSpinner.getSelectedItem());
                 busMarker = mapScreen.mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(0, 0))
                        .title(busRouteSpinner.getSelectedItem().toString() + " Bus")
                        .snippet("")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));

                int color = Color.BLACK;

                try {
                    //righthook.js -BRYAN
                    if (busRouteSpinner.getSelectedItem().equals("Green")) {
                        color = Color.GREEN;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=702%20UNCW%20GREEN&iefix=36855");
                    }
                    if (busRouteSpinner.getSelectedItem().equals("Red")){
                        color = Color.RED;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=703%20UNCW%20RED&iefix=10651");

                    }
                    if (busRouteSpinner.getSelectedItem().equals("Red Express")) {
                        color = Color.RED;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=7071%20UNCW%20RED%20EXP&iefix=79586");

                    }
                    if (busRouteSpinner.getSelectedItem().equals("Teal")) {
                        color = Color.argb(255, 0, 128, 128);
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=712%20UNCW%20TEAL&iefix=4705");

                    }
                    if (busRouteSpinner.getSelectedItem().equals("Blue")) {
                        color = Color.BLUE;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=7012%20UNCW%20BLUE&iefix=36855");
                        url2 = new URL ("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=7011%20UNCW%20BLUE&iefix=29513");

                    }
                    if (busRouteSpinner.getSelectedItem().equals("Yellow")) {
                        color = Color.YELLOW;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=704%20UNCW%20YELLOW&iefix=36855");
                    }
                    if (busRouteSpinner.getSelectedItem().equals("Grey")) {
                        color = Color.DKGRAY;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=711%20GREY&iefix=33074");
                    }
                    if (busRouteSpinner.getSelectedItem().equals("Loop")) {
                        color = Color.CYAN;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=705%20UNCW%20LOOP&iefix=36855");
                    }

                    if (busRouteSpinner.getSelectedItem().equals("Loop Express")) {
                        color = Color.CYAN;
                        url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=709%20UNCW%20LOOP%20EXPRESS&iefix=36855");
                    }
                }
                catch (MalformedURLException e){};



                Routes busRoute = new Routes(busCoords,mapScreen,color);
                    updateMap(mapScreen);
                    mainTimer.scheduleAtFixedRate(timerTask, 0, 30000);

            }
        });

        popupWindow1.showAsDropDown(busButton, 50, -30);

    }

    /**
     * timer accesses this function to continually update bus position
     * @param screen main MapActivity access
     */
    private  void updateMap(MapsActivity screen)
    {

        System.out.println("worked");

        try {

            String fullString = "";
            double latitude = 0;
            double longitude = 0;
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains("<latitude>")) {
                    System.out.println(line.substring(10, line.indexOf("</")));
                    latitude = Double.parseDouble(line.substring(10, line.indexOf("</")));


                }
                if (line.contains("<longitude>")) {
                    System.out.println(line.substring(11, line.indexOf("</")));
                    longitude = Double.parseDouble(line.substring(11, line.indexOf("</")));
                }
            }


            busMarker.setPosition(new LatLng(latitude, longitude));
            if (busMarker.getPosition().latitude != 0)
            {
                mapScreen.mMap.moveCamera(CameraUpdateFactory.newLatLng(busMarker.getPosition()));
            }
            System.out.println(fullString);

            reader.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        ;
    }

}
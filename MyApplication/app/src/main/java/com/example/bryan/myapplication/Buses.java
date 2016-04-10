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
 * Created by Vika on 3/9/2016.
 */
public class Buses {

    MapsActivity mapScreen;
    final BusRouteData data;
    final Spinner busRouteSpinner;

    public Buses(MapsActivity map, InputStream is) throws IOException

    {
        data= new BusRouteData(is);

        mapScreen = map;

        final ImageButton busButton = (ImageButton) mapScreen.findViewById(R.id.busButton); // setup button function for directions
        LayoutInflater layoutInflater1 //popup behavior
                = (LayoutInflater) mapScreen.getBaseContext()
                .getSystemService(mapScreen.LAYOUT_INFLATER_SERVICE);
        final View popupView1 = layoutInflater1.inflate(R.layout.buspopup, null);

        final Timer mainTimer = new Timer();

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


        final PopupWindow popupWindow1 = new PopupWindow(
                popupView1,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Button exitButton = (Button) popupView1.findViewById(R.id.dismiss); //exit button
        exitButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                busButton.setSelected(false);
                popupWindow1.dismiss();
            }
        });
        Button goButton = (Button) popupView1.findViewById(R.id.go); //go button
        goButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                busButton.setSelected(false);
                popupWindow1.dismiss();


                    updateMap(mapScreen);
                    mainTimer.scheduleAtFixedRate(timerTask, 0, 1000);

            }
        });

        popupWindow1.showAsDropDown(busButton, 50, -30);

    }
    private  void updateMap(MapsActivity screen)
    {

        System.out.println("YEP IT FUCKIN WORKED");
        int color = Color.BLACK;
        try {
            System.out.println("test");
            screen.mMap.clear();

            ArrayList<LatLng> busCoords = data.busRoutes.get(busRouteSpinner.getSelectedItem());

            if (busRouteSpinner.getSelectedItem().equals("Green")){
                color = Color.GREEN;
            }
            if (busRouteSpinner.getSelectedItem().equals("Red") || busRouteSpinner.getSelectedItem().equals("Red Express")){
                color = Color.RED;
            }
            if (busRouteSpinner.getSelectedItem().equals("Teal")){
                color = Color.argb(255,0,128,128);
            }
            if (busRouteSpinner.getSelectedItem().equals("Blue")){
                color = Color.BLUE;
            }
            if (busRouteSpinner.getSelectedItem().equals("Yellow")){
                color = Color.YELLOW;
            }
            if (busRouteSpinner.getSelectedItem().equals("Grey")){
                color = Color.DKGRAY;
            }
            if (busRouteSpinner.getSelectedItem().equals("Loop") || busRouteSpinner.getSelectedItem().equals("Loop Express")){
                color = Color.CYAN;
            }



            Routes busRoute = new Routes(busCoords,mapScreen,color);
            String fullString = "";
            double latitude = 0;
            double longitude = 0;
            URL url = new URL("http://text90947.com/bustracking/wavetransit/m/businfo.jsp?refine=702%20UNCW%20GREEN&iefix=36855");
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
            Marker busmarker = screen.mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title("GREEN BUS")
                    .snippet("brings bryan to school some days")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
            System.out.println(fullString);
            reader.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
        ;
    }

}
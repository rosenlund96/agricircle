package com.example.agricircle.project.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.agricircle.R;
import com.example.agricircle.project.Fragment.CreateFieldDetail;
import com.example.agricircle.project.Fragment.UserFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class DrawNewFieldActivity extends FragmentActivity implements OnMapReadyCallback {
    public Polygon polygon;
    private GoogleMap mMap;
    PlacesClient placesClient;
    Dialog myDialog;
    String apiKey = "AIzaSyAR8qicDS4pzB17PyiIs5Ms41HNHxrVsmw";
    TextView placeText;
    boolean drawmode;
    Button add, delete, next, crosshair;
    String placeString = "";
    private List<Marker> mapMarkers;
    private List<Polyline> mapLines;
    AutocompleteSupportFragment autocompleteFragment;
    private FragmentManager fragmentManager;
    private LinearLayout userinput;
    private static DrawNewFieldActivity sDrawNewFieldActivity;
    private static final String TAG = "MapsActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_draw_new_field);
        drawmode = false;
        mapMarkers = new ArrayList<>();
        mapLines = new ArrayList<>();
        myDialog = new Dialog(this);
        sDrawNewFieldActivity = this;
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fragmentManager = this.getSupportFragmentManager();
        placeText = findViewById(R.id.placetext);
        delete = findViewById(R.id.deleteButton);
        next = findViewById(R.id.nextButton);
        add = findViewById(R.id.addButton);
        crosshair = findViewById(R.id.crosshair);
        //delete.setText(R.string.Draw);
        userinput = findViewById(R.id.userinputhandle);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(polygon != null){
                    autocompleteFragment.getView().setVisibility(View.GONE);
                    mapFragment.getView().setVisibility(View.GONE);
                    userinput.setVisibility(View.GONE);
                    crosshair.setVisibility(View.GONE);

                    fragmentManager.beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.article_fragment
                                    , new CreateFieldDetail())
                            .commit();
                }
                else{
                    Button okay,cancel;
                    TextView overskrift, tekst;
                    myDialog.setContentView(R.layout.popup_modal);
                    overskrift = (TextView) myDialog.findViewById(R.id.modaloverskrift);
                    tekst = (TextView) myDialog.findViewById(R.id.modaltekst);
                    okay = (Button) myDialog.findViewById(R.id.alert_btn_okay);
                    cancel = (Button) myDialog.findViewById(R.id.alert_btn_cancel);
                    overskrift.setText(R.string.modalDrawTitle);
                    tekst.setText(R.string.modalDrawText);
                    okay.setText(R.string.modalDrawButton);
                    cancel.setText(R.string.modalDrawDelete);
                    okay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            myDialog.dismiss();
                        }
                    });
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            myDialog.dismiss();
                        }
                    });
                    myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    int width = dm.widthPixels;
                    int height = dm.heightPixels;
                    myDialog.getWindow().setLayout((int)(width ),(int)(height * 0.45 ));
                    myDialog.show();
                }





            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(mMap.getCameraPosition().target.latitude,
                                        mMap.getCameraPosition().target.longitude))
                        .draggable(true));
                mapMarkers.add(marker);
                DrawLines();
                if(mapMarkers.size()> 2){
                    Float distance = distance(mMap.getCameraPosition().target.latitude,
                            mMap.getCameraPosition().target.longitude,
                            mapMarkers.get(0).getPosition().latitude,mapMarkers.get(0).getPosition().longitude);
                    if( distance< 15f ){
                        drawPolygon();
                        for(int i = 0; i<mapMarkers.size();i++){
                            mapMarkers.get(i).remove();
                        }
                        for(int x = 0; x<mapLines.size();x++){
                            mapLines.get(x).remove();
                        }
                        mapMarkers.clear();
                        mapLines.clear();
                    }
                }
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapMarkers.size() > 0){
                    mapMarkers.get(mapMarkers.size()-1).remove();
                    mapMarkers.remove(mapMarkers.size()-1);
                }
                if(mapLines.size()> 0){
                    mapLines.get(mapLines.size()-1).remove();
                    mapLines.remove(mapLines.size()-1);
                }
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(this, apiKey);
        }

// Create a new Places client instance.
        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                placeString = place.getAddress();
                placeText.setText(placeString);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    public void DrawLines(){
        if(mapMarkers.size()> 1){
            Polyline polyline = mMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(Color.argb(100,255,164,0))
                    .add(mapMarkers.get(mapMarkers.size()-1).getPosition(), mapMarkers.get(mapMarkers.size()-2).getPosition()
                    ));
            mapLines.add(polyline);
        }
    }

    public float distance (double lat_a, double lng_a, double lat_b, double lng_b )
    {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b-lat_a);
        double lngDiff = Math.toRadians(lng_b-lng_a);
        double a = Math.sin(latDiff /2) * Math.sin(latDiff /2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff /2) * Math.sin(lngDiff /2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    public static DrawNewFieldActivity getInstance() {
        return sDrawNewFieldActivity;
    }

    public Polygon getPolygon(){
        return this.polygon;
    }


    public void removeMarkersFromMap(){

        for(int i = 0; i<mapMarkers.size(); i++){
            mapMarkers.get(i).remove();
        }
        mapMarkers.clear();

    }

    private void checkPoints(){

        if(mapMarkers.size()>2){
            Button okay,cancel;
            TextView overskrift, tekst;
            myDialog.setContentView(R.layout.popup_modal);
            overskrift = (TextView) myDialog.findViewById(R.id.modaloverskrift);
            tekst = (TextView) myDialog.findViewById(R.id.modaltekst);
            okay = (Button) myDialog.findViewById(R.id.alert_btn_okay);
            cancel = (Button) myDialog.findViewById(R.id.alert_btn_cancel);
            overskrift.setText(R.string.modalDrawTitle);
            tekst.setText(R.string.modalDrawText);
            okay.setText(R.string.modalDrawButton);
            cancel.setText(R.string.modalDrawDelete);
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawPolygon();
                    removeMarkersFromMap();

                    myDialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMarkersFromMap();
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            myDialog.getWindow().setLayout((int)(width ),(int)(height * 0.45 ));
            myDialog.show();
        }
        else{
            Button okay,cancel;
            TextView overskrift, tekst;
            myDialog.setContentView(R.layout.popup_modal);
            overskrift = (TextView) myDialog.findViewById(R.id.modaloverskrift);
            tekst = (TextView) myDialog.findViewById(R.id.modaltekst);
            okay = (Button) myDialog.findViewById(R.id.alert_btn_okay);
            okay.setEnabled(false);

            cancel = (Button) myDialog.findViewById(R.id.alert_btn_cancel);
            overskrift.setText(R.string.notEnoughMarkers);
            tekst.setText(R.string.notEnoughMarkersText);
            okay.setText(R.string.modalDrawButton);
            cancel.setText(R.string.modalDrawDelete);
            okay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawPolygon();
                    removeMarkersFromMap();

                    myDialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMarkersFromMap();
                    myDialog.dismiss();
                }
            });
            myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            DisplayMetrics dm = new DisplayMetrics();
            this.getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            myDialog.getWindow().setLayout((int)(width ),(int)(height * 0.45 ));
            myDialog.show();
        }

    }

    public void drawPolygon(){


        PolygonOptions rectOptions = new PolygonOptions();
        for(int i = 0; i<mapMarkers.size(); i++){
            rectOptions.add(mapMarkers.get(i).getPosition());

        }
        rectOptions.add(mapMarkers.get(0).getPosition());
        rectOptions.strokeColor(Color.argb(255,0,128,255));
        rectOptions.fillColor(Color.argb(160,0,128,255));

        Polygon polygon = mMap.addPolygon(rectOptions);
        polygon.setStrokeColor(Color.argb(255,0,128,255));
        polygon.setFillColor(Color.argb(160,0,128,255));
        polygon.setClickable(true);
        this.polygon = polygon;
    }




    private String LoadPreferences(String key) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String result = sharedPreferences.getString(key, "");
        return result;

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        String map = LoadPreferences("Map");
        if(map.equals("Normal")){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(map.equals("Sattelite")){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        else if(map.equals("Hybrid")){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(drawmode){
                    MarkerOptions markeroptions = new MarkerOptions()
                            .position(latLng);

                    Marker marker = mMap.addMarker(markeroptions);
                    mapMarkers.add(marker);
                }
            }
        });

        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);

        // Use the builder to create a FindCurrentPlaceRequest.
        final FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(new OnSuccessListener<FindCurrentPlaceResponse>() {
                @Override
                public void onSuccess(FindCurrentPlaceResponse findCurrentPlaceResponse) {
                    placeString = findCurrentPlaceResponse.getPlaceLikelihoods().get(0).getPlace().getAddress();
                    placeText.setText(placeString);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(findCurrentPlaceResponse.getPlaceLikelihoods().get(0).getPlace().getLatLng(), 15));
                }
            });
            placesClient.findCurrentPlace(request).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }
    }
}
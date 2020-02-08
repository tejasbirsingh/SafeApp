package com.example.shivam.project1;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class fragmentExplore extends Fragment implements OnMapReadyCallback{

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private boolean mLocationPermissionGranted = false;
    private static final int Location_Permission_Request_Code = 1234;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private String postalCode = "";
    private String currentPostalCode = "";
    private PlaceAutocompleteAdapter placeAutocompleteAdapter;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40,-168),new LatLng(71,136));

    //widgets
    private EditText msearchText;
    private ImageView mGps;
    private Button checkRatingsButton;
    private Button addRatingsButton;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragement_explore, container, false);

        addRatingsButton = (Button)v.findViewById(R.id.addRatingsButton);
        addRatingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(postalCode != "" || currentPostalCode != "") {
                    fragmentSafetyAudit safetyAudit = new fragmentSafetyAudit();
                    FragmentManager manager = getFragmentManager();
                    Bundle bundle = new Bundle();
                    if(postalCode != "") {
                        safetyAudit.setArguments(bundle);
                        Intent intent  = new Intent(getActivity(),fragmentAddSafetyRatings.class);
                        intent.putExtra("pincode",postalCode);
                        startActivity(intent);
                    }
                    else
                    {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                        builder1.setMessage("currently marking for current location. if you are searching for a place, kindly search a valid area with pincode");
                        builder1.setCancelable(true);

                        builder1.setPositiveButton(
                                "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        fragmentSafetyAudit safetyAudit = new fragmentSafetyAudit();
                                        FragmentManager manager = getFragmentManager();
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("pincode",currentPostalCode);
//                                        bundle.putBoolean("add_rating",true);
//                                        safetyAudit.setArguments(bundle);
//                                        manager.beginTransaction().replace(R.id.fragment_container, safetyAudit).commit();
                                        Intent intent = new Intent(getActivity(), fragmentAddSafetyRatings.class);
                                        intent.putExtra("pincode",currentPostalCode);
                                        startActivity(intent);
                                        dialog.cancel();
                                    }
                                });

                        builder1.setNegativeButton(
                                "Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alert11 = builder1.create();
                        alert11.show();


                    }

                }
                else
                {
                    Toast.makeText(getContext(),"please search for a detailed area having pincode",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
        Button checkRatingButton =(Button)v.findViewById(R.id.checkRatingsButton);
        checkRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentSafetyAudit safetyAudit = new fragmentSafetyAudit();
                FragmentManager manager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("pincode",postalCode);
                safetyAudit.setArguments(bundle);
                manager.beginTransaction().replace(R.id.fragment_container, safetyAudit).commit();


            }
        });

        return v;
    }

    @Override

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        msearchText = (EditText) getActivity().findViewById(R.id.input_search);
        mGps = (ImageView) getActivity().findViewById(R.id.ic_gps);
        getLocationPermission();
    }


    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();
        }

    }

    private void init(){


        msearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if(actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_GO
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER)
                {
                    geolocate();
                }
                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceLocation();
            }
        });
    }

    private void geolocate()
    {
        String searchString  = msearchText.getText().toString();
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> list = new ArrayList<>();
        try
        {
            list = geocoder.getFromLocationName(searchString,1);
        }
        catch(IOException e)
        {
            Log.d("geolocate",e.getMessage());
        }
        if(list.size() > 0)
        {
            Address address = list.get(0);
            if(address.getPostalCode() != null)
            {
                postalCode = address.getPostalCode().toString();
            }
            Log.d("geolocate",address.toString());
            moveCamera(new LatLng(address.getLatitude(),
                            address.getLongitude()),
                    15f,address.getAddressLine(0));

        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try{
            if(mLocationPermissionGranted)
            {
                Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful())
                        {
                            Location currentLocation = (Location) task.getResult();
                            Geocoder geocoder = new Geocoder(getActivity());
                            List<Address> list = new ArrayList<>();
                            try
                            {
                                list = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                            }
                            catch(IOException e)
                            {
                                Log.d("geolocate",e.getMessage());
                            }
                            currentPostalCode = list.get(0).getPostalCode().toString();
                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),
                                    15f,"My location");
                        }
                        else
                        {
                            Toast.makeText(getActivity(),"Unable to get device current location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.d("security exception",e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom,String title)
    {

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));

        MarkerOptions options = new MarkerOptions().position(latLng).title(title);
        mMap.addMarker(options);
    }


    private void getLocationPermission()
    {
        String [] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if(ContextCompat.checkSelfPermission(this.getContext(),FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this.getContext(),COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this.getActivity(),permissions,Location_Permission_Request_Code);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this.getActivity(),permissions,Location_Permission_Request_Code);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        Toast.makeText(getActivity(),"on request permission called",Toast.LENGTH_SHORT).show();
        switch (requestCode)
        {
            case Location_Permission_Request_Code : {
                if(grantResults.length > 0)
                {
                    for(int i = 0;i<grantResults.length;i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    initMap();
                }
            }
        }
    }


}
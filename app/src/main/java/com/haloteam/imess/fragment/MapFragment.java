//package com.haloteam.imess.fragment;
//
//import android.Manifest;
//import android.content.Context;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.OnMapReadyCallback;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.BitmapDescriptorFactory;
//import com.google.android.gms.maps.model.CameraPosition;
//import com.google.android.gms.maps.model.LatLng;
//import com.google.android.gms.maps.model.Marker;
//import com.google.android.gms.maps.model.MarkerOptions;
//import com.haloteam.imess.R;
//import com.haloteam.imess.ultil.GPSTracker;
//
///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MapFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MapFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
//
///**
// * handle it
// */
//public class MapFragment extends Fragment implements
//        OnMapReadyCallback {
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private GoogleMap mMap;
//    private GPSTracker mGPS;
//    private LatLng mCurrentLocation;
//    private Marker mCurrentMarker;
//
//    private OnFragmentInteractionListener mListener;
//
//    public MapFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MapFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MapFragment newInstance(String param1, String param2) {
//        MapFragment fragment = new MapFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//
//
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view = inflater.inflate(R.layout.fragment_map, container, false);
//
//        try {
//            initMap();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return view;
//    }
//
//    private void initMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
//
//        mGPS = new GPSTracker(getContext());
//    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        /**
//         * init the map and GPSTracker's instance again
//         */
//
//        initMap();
//    }
//
//    /**
//     * This callback is triggered when the map is ready to be used.
//     * @param googleMap
//     */
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//        mMap = googleMap;
//
//        if(!mGPS.canGetLocation()){
//            mGPS.showSettingsAlert();
//        }
//        else{
//
//            /**
//             * + when the map is opened, then user's location is updated.
//             *
//             * + mGPS instance of GPSTracker class will get Latlng continously
//             */
//            mCurrentLocation = new LatLng(mGPS.getLatitude(), mGPS.getLongitude());
//
//            MarkerOptions marker = new MarkerOptions();
//            marker.position(mCurrentLocation);
//            marker.title("You");
//            marker.icon(BitmapDescriptorFactory.defaultMarker());
//
//            mCurrentMarker = mMap.addMarker(marker);
//            mCurrentMarker.setTag(0);
//
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(mCurrentLocation).zoom(14).build();
//            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//
//            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                // TODO: Consider calling
//                //    ActivityCompat#requestPermissions
//                // here to request the missing permissions, and then overriding
//                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                //                                          int[] grantResults)
//                // to handle the case where the user grants the permission. See the documentation
//                // for ActivityCompat#requestPermissions for more details.
//                return;
//            }
//            mMap.setMyLocationEnabled(true);
//            mMap.getUiSettings().setCompassEnabled(true);
//
//        }
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
//}
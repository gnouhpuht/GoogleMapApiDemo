package com.phuong.googlemapapidemo;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.phuong.googlemapapidemo.map.DirectionFinder;
import com.phuong.googlemapapidemo.map.DirectionFinderListener;
import com.phuong.googlemapapidemo.map.Route;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;
    private Button btnFind;
    private Polyline line;
//    private EditText etOrigin;
//    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    private MyLocation myLocation = null;
    private MyLocation goLocation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);//khởi tạo map system

//        btnFind=findViewById(R.id.btnFindPath);
//        btnFind.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                    case R.id.btnFindPath:
//                        if (myLocation!=null & goLocation!=null) {
//                            String urlTopass = makeURL(
//                                    myLocation.getLat(),
//                                    myLocation.getLon(),
//                                    goLocation.getLat(),
//                                    goLocation.getLon());
//                            Log.d("1111111111111111", "onMapClick: " + urlTopass);
//                            new connectAsyncTask(urlTopass).execute();
//                        }
//
//                        break;
//
//                    default:
//                        break;
//                }
//
//            }
//        });

    }

    private class connectAsyncTask extends AsyncTask<Void, Void, String> {
        private ProgressDialog progressDialog;
        String url;

        connectAsyncTask(String urlPass) {
            url = urlPass;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressDialog = new ProgressDialog(MapsActivity.this);
            progressDialog.setMessage("Fetching route, Please wait...");
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            JSONParser jParser = new JSONParser();
            String json = jParser.getJSONFromUrl(url);
            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.hide();
            if (result != null) {
                drawPath(result);
            }
        }
    }
    public String makeURL(double sourcelat, double sourcelog, double destlat, double destlog) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));
        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
        urlString.append("&key=AIzaSyB0vID9H2rU_HcB-CUnnk9Z35r9nlD6mrA");

        return urlString.toString();

    }

//    private void sendRequest() {
////        String origin=etOrigin.getText().toString();
////        String destination=etDestination.getText().toString();
//
////        if (origin.isEmpty()){
////            Toast.makeText(this,"nhập điểm bắt đầu đi",Toast.LENGTH_SHORT).show();
////            return;
////        }
////        if (destination.isEmpty()){
////            Toast.makeText(this,"nhập điểm đến",Toast.LENGTH_LONG).show();
////            return;
////        }
//
////        try {
////            new DirectionFinder(this,origin,destination).execute();
////        }catch (UnsupportedEncodingException e){
////            e.printStackTrace();
////        }
//
//    }

    public class JSONParser {

        InputStream is = null;
        JSONObject jObj = null;
        String json = "";

        // constructor
        public JSONParser() {
        }

        public String getJSONFromUrl(String url) {

            // Making HTTP request
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();


            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                json = sb.toString();
                is.close();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            Log.d("1111111111111111", "onMapClick: " + json);
            return json;

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng dhcnhn = new LatLng(21.054459, 105.735024);//tạo một vị trí có vĩ độ v,v1


        //add một cái map maker trên bản đồ tại một vị trí cho trước
//        mMap.addMarker(new MarkerOptions().position(dhcnhn).title("Trường đại học công nghiệp hà nội")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher)));
//
//        // giúp di chuyển màn hình tới vị trí là sydey
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dhcnhn, 18));

        final LatLng cn=new LatLng(21.054930, 105.731155);
        //vẽ các đường trên bản đồ khi có vị trí cho trước
//        mMap.addPolyline(new PolylineOptions().add(dhcnhn,
//                new LatLng(21.053542, 105.735358),
//                new LatLng(21.055085, 105.732657),
//                cn
//        ).width(10).color(Color.RED));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);// lấy vị trí hiện tại của mình trên bản đồ
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
        //lấy vị trí click hiện tại
//        final Location[] mLocation = {mMap.getMyLocation()};
//        myLocation = myLocation;
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                myLocation = new MyLocation(location.getLatitude(),location.getLongitude());
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green)));
                Toast.makeText(MapsActivity.this, "Click event: " + latLng.latitude, Toast.LENGTH_SHORT).show();
                goLocation = new MyLocation(latLng.latitude,latLng.longitude);
                if (myLocation!=null & goLocation!=null) {
                    String urlTopass = makeURL(
                            myLocation.getLat(),
                            myLocation.getLon(),
                            goLocation.getLat(),
                            goLocation.getLon());
                    Log.d("1111111111111111", "onMapClick: " + urlTopass);
                    new connectAsyncTask(urlTopass).execute();
                }
            }
        });

    }

    public void drawPath(String result) {
        if (mMap != null) {
            mMap.clear();
        }

        mMap.addMarker(new MarkerOptions().
                position(new LatLng(goLocation.getLat(),goLocation.getLon()))
                .icon(
                BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));
        mMap.addMarker(new MarkerOptions().position(new LatLng(myLocation.getLat(),myLocation.getLon())).icon(
                BitmapDescriptorFactory.fromResource(R.drawable.end_green)));
        try {
            // Tranform the string into a json object
            Log.d("1111111111111111", "onMapClick: " + result);
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes
                    .getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);

            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
            for (int z = 0; z < list.size(); z++) {
                LatLng point = list.get(z);
                options.add(point);

                Log.d("1111111111111111", "onMapClick: " + point.latitude);
            }
            line = mMap.addPolyline(options);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    public class MyLocation{
        private double lat;
        private  double lon;

        public MyLocation(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }
}

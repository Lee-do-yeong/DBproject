package org.techtown.mycalendar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Intent intent;
    String value;
    FloatingActionButton fab;
    TMapView tmapview;
    TMapPoint tMapPointStart;
    TMapPoint tMapPointEnd = null;
    private GpsTracker gpsTracker;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 지도 띄우기
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xx9c10fb3dbac642078ebac04bd35fba5c");

        if (!checkLocationServicesStatus()) { showDialogForLocationServiceSetting(); }
        else {checkRunTimePermission(); }

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        gpsTracker = new GpsTracker(MainActivity.this);

        initialize(tmapview);
    }

    public void onClick(View v) { // 플로팅 버튼 클릭 시에 화장실 마커 표시(플로팅 버튼은 사라짐)
        if(v.getId() == R.id.fab_add){
            List<Data> toiletList=initLoadToiletDatabase();
            addToiletMarker(toiletList);
            //road(toiletList);
            fab.setVisibility(View.INVISIBLE); //마커 설정 후 버튼 사라짐
        }
    }

    private void road(List<Data>toiletList) {
        String point;

        double lat1 = 35.846964;
        double lon1 = 127.129436;

        //double lat1 = gpsTracker.getLatitude();
        //double lon1 = gpsTracker.getLongitude();

        //tmapview.setCenterPoint(lon1, lat1);
        //tmapview.setLocationPoint(lon1, lat1);
        setMultiMarkers2(lat1, lon1); //현재위치 마커표시*/

        point = toiletList.get(0).toString();
        Log.d("###", "POI: " + point);
        String lat = (String) point.subSequence(4,15);
        String lon = (String) point.subSequence(20,32);
        double lat2 = Double.parseDouble(lat);
        double lon2 = Double.parseDouble(lon);

        Log.d("###", "lat1: " + lat1);
        Log.d("###", "lon1: " + lon1);

        Log.d("###", "lat2: " + lat2);
        Log.d("###", "lon2: " + lon2);

        tMapPointStart = new TMapPoint(lat1, lon1);
        tMapPointEnd = new TMapPoint(lat2, lon2);

        TMapPolyLine polyLine = new TMapPolyLine();
        PathAsync pathAsync = new PathAsync();
        pathAsync.execute(polyLine);
    }

    class PathAsync extends AsyncTask<TMapPolyLine, Void, TMapPolyLine> {
        @Override
        protected TMapPolyLine doInBackground(TMapPolyLine... tMapPolyLines) {
            TMapPolyLine tMapPolyLine = tMapPolyLines[0];
            try {
                tMapPolyLine = new TMapData().findPathDataWithType(TMapData.TMapPathType.PEDESTRIAN_PATH, tMapPointStart, tMapPointEnd);
                tMapPolyLine.setOutLineColor(Color.RED);
                tMapPolyLine.setLineWidth(4);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("error", e.getMessage());
            }
            return tMapPolyLine;
        }

        @Override
        protected void onPostExecute(TMapPolyLine tMapPolyLine) {
            super.onPostExecute(tMapPolyLine);
            tmapview.addTMapPolyLine("Line", tMapPolyLine);
        }
    }

    private void initialize(TMapView tmapview) {
        LinearLayout linearLayoutTmap = (LinearLayout) findViewById(R.id.linearLayoutTmap);
        linearLayoutTmap.addView(tmapview);

        // 전북대로 설정
        tmapview.setOnClickListenerCallBack(mOnClickListenerCallback);
        tmapview.setZoomLevel(15);
        tmapview.setCenterPoint(127.129436, 35.846964);
        double lat1 = gpsTracker.getLatitude();
        double lon1 = gpsTracker.getLongitude();


        tmapview.setCenterPoint(lon1, lat1);
        tmapview.setLocationPoint(lon1, lat1);
        setMultiMarkers2(lat1, lon1); //현재위치 마커표시
    }

    // 주변 명칭 검색
    /*private void searchPOI(ArrayList<String> arrPOI) {
        final TMapData tMapData = new TMapData();
        final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
        final ArrayList<String> arrTitle = new ArrayList<>();
        final ArrayList<String> arrAddress = new ArrayList<>();

        for (int i = 0; i < arrPOI.size(); i++) {
            tMapData.findTitlePOI(arrPOI.get(i), new TMapData.FindTitlePOIListenerCallback() {
                @Override
                public void onFindTitlePOI(ArrayList<TMapPOIItem> arrayList) {
                    for (int j = 0; j < arrayList.size(); j++) {
                        TMapPOIItem tMapPOIItem = arrayList.get(j);
                        arrTMapPoint.add(tMapPOIItem.getPOIPoint());
                        arrTitle.add(tMapPOIItem.getPOIName());
                        arrAddress.add(tMapPOIItem.upperAddrName + " " +
                                tMapPOIItem.middleAddrName + " " + tMapPOIItem.lowerAddrName);
                    }
                    setMultiMarkers(arrTMapPoint, arrTitle, arrAddress);
                }
            });
        }
    }*/
    //화장실 마커 설정 및 풍선뷰
    public void addToiletMarker(List<Data>toiletList){
        Bitmap bitmapIcon = createMarkerIcon(R.drawable.poi_red);
        for(int i=0;i< toiletList.size();i++){
            String toiletName=toiletList.get(i).toilet;
            String address=toiletList.get(i).address;
            double lat=toiletList.get(i).latitude;
            double lon=toiletList.get(i).longitude;

            TMapPoint tMapPoint=new TMapPoint(lat,lon);
            //티맵 마커 초기 설정
            TMapMarkerItem tMapMarkerItem=new TMapMarkerItem();
            tMapMarkerItem.setIcon(bitmapIcon);
            tMapMarkerItem.setPosition(0.5f,1.0f);
            tMapMarkerItem.setTMapPoint(tMapPoint);
            tMapMarkerItem.setName(toiletName);
            //풍선뷰 초기설정
            tMapMarkerItem.setCanShowCallout(true);
            tMapMarkerItem.setCalloutTitle(toiletName);
            tMapMarkerItem.setCalloutSubTitle(address);
            tMapMarkerItem.setAutoCalloutVisible(false);

            tmapview.addMarkerItem("toiletLocation"+i,tMapMarkerItem);
        }
    }


    private void setMultiMarkers2(double lat, double lon){
        Bitmap bitmapIcon = createMarkerIcon(R.drawable.poi_dot);

        TMapPoint tMapPoint = new TMapPoint(lat, lon);

        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
        tMapMarkerItem.setIcon(bitmapIcon);
        tMapMarkerItem.setPosition(0.5f,1.0f);
        tMapMarkerItem.setTMapPoint(tMapPoint);

        tmapview.addMarkerItem("markerItem", tMapMarkerItem);
        tMapPointEnd = tMapMarkerItem.getTMapPoint();
    }


   //load database
    public List<Data> initLoadToiletDatabase(){
        DatabaseHelper databaseHelper=new DatabaseHelper(getApplicationContext());
        databaseHelper.OpenDatabaseFile();

        List<Data>toiletList=databaseHelper.getTableData();
        Log.e("text",String.valueOf(toiletList.size()));

        databaseHelper.close();
        return toiletList;
    }


    private Bitmap createMarkerIcon(int image) {
        Log.e("MapViewActivity", "(F)   createMarkerIcon()");

        Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), image);
        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);

        return bitmap;
    }

    TMapView.OnClickListenerCallback mOnClickListenerCallback = new TMapView.OnClickListenerCallback() {
        @Override
        public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
            return false;
        }
        @Override
        public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
            /*final ArrayList<TMapPoint> arrTMapPoint = new ArrayList<>();
            road(arrTMapPoint);*/ //지도 내 마커 클릭시 road 연결
            return false; 
        }
    };

    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grandResults) {
        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            boolean check_result = true;

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) { ; }
            else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    void checkRunTimePermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) { }
        else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
            else { ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    public String getCurrentAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 7);
        } catch (IOException ioException) {
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString() + "\n";
    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 진행하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}
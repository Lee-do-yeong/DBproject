package org.techtown.mycalendar;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
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

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Intent intent;
    String value;
    FloatingActionButton fab;
    TMapView tmapview;
    TMapPoint tMapPointStart;
    TMapPoint tMapPointEnd = null;
    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 지도 띄우기
        tmapview = new TMapView(this);
        tmapview.setSKTMapApiKey("l7xx9c10fb3dbac642078ebac04bd35fba5c");

        fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(this);

        gpsTracker = new GpsTracker(MainActivity.this);

        initialize(tmapview);
    }

    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            //풍선뷰 화면 설정..
            case R.id.fab_add:
                //intent = getIntent();
                //String location = intent.getExtras().getString("location");

                //ArrayList<String> arrBuilding = new ArrayList<>();
                //arrBuilding.add(location);
                //searchPOI(arrBuilding);
                fab.setVisibility(View.INVISIBLE); //마커 설정 후 버튼 사라짐
                break;
        }
    }

    private void road(ArrayList<TMapPoint> arrTPoint) {
        String point;

        double lat1 = gpsTracker.getLatitude();
        double lon1 = gpsTracker.getLongitude();

        /*tmapview.setCenterPoint(lon1, lat1);
        tmapview.setLocationPoint(lon1, lat1);
        setMultiMarkers2(lat1, lon1); //현재위치 마커표시*/

        point = arrTPoint.get(0).toString();
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

        /*tmapview.setCenterPoint(lon1, lat1);
        tmapview.setLocationPoint(lon1, lat1);
        setMultiMarkers2(lat1, lon1); //현재위치 마커표시*/
    }

    // 주변 명칭 검색
    private void searchPOI(ArrayList<String> arrPOI) {
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
    }
    // 마커 설정
    private void setMultiMarkers(ArrayList<TMapPoint> arrTPoint, ArrayList<String> arrTitle, ArrayList<String> arrAddress) {
        for (int i = 0; i < arrTPoint.size(); i++) {
            Bitmap bitmapIcon = createMarkerIcon(R.drawable.poi_red);

            TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
            tMapMarkerItem.setIcon(bitmapIcon);

            tMapMarkerItem.setTMapPoint(arrTPoint.get(i));

            tmapview.addMarkerItem("markerItem" + i, tMapMarkerItem);

            setBalloonView(tMapMarkerItem, arrTitle.get(i), arrAddress.get(i));
        }
    }

    private void setMultiMarkers2(double lat, double lon){
        Bitmap bitmapIcon = createMarkerIcon(R.drawable.poi_dot);

        TMapPoint tMapPoint = new TMapPoint(lat, lon);
        TMapMarkerItem tMapMarkerItem = new TMapMarkerItem();
        tMapMarkerItem.setIcon(bitmapIcon);

        tMapMarkerItem.setTMapPoint(tMapPoint);

        tmapview.addMarkerItem("markerItem", tMapMarkerItem);
        tMapPointEnd = tMapMarkerItem.getTMapPoint();
    }

    // 풍선뷰 통해서 내용 연결
    private void setBalloonView(TMapMarkerItem marker, String title, String address) {
        marker.setCanShowCallout(true);
        if (marker.getCanShowCallout()) {
            marker.setCalloutTitle(title);
            marker.setCalloutSubTitle(address);
        }
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
}
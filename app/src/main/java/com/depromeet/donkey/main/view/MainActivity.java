package com.depromeet.donkey.main.view;

import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.depromeet.donkey.R;
import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapMarkerItem;
import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPOIItem;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements TMapView.OnClickListenerCallback, LocationListener, TMapView.OnCalloutRightButtonClickCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.tmap_layout)
    LinearLayout tmapLayout;
    @BindView(R.id.gps_enable)
    LinearLayout gpsEnableLayout;

    private LocationManager locationManager;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ButterKnife.bind(this);
        TMapView tMapView = new TMapView(this);

        tMapView.setSKTMapApiKey(getString(R.string.tmap_key));
        tmapLayout.addView(tMapView);
        gpsInit();

        TMapMarkerItem mapMarkerItem = new TMapMarkerItem();
        mapMarkerItem.setTMapPoint(new TMapPoint(37.56263480184372, 126.98695421218872));
        mapMarkerItem.setName("Test");
        mapMarkerItem.setCanShowCallout(true);
        mapMarkerItem.setPosition((float) 0.5, (float) 1.0);
        mapMarkerItem.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.poi_dot));
        mapMarkerItem.setCalloutRightButtonImage(BitmapFactory.decodeResource(getResources(), R.drawable.poi_popup));
        tMapView.addMarkerItem("1", mapMarkerItem);
        tMapView.setOnCalloutRightButtonClickListener(new TMapView.OnCalloutRightButtonClickCallback() {
            @Override
            public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
                Log.d(TAG, tMapMarkerItem.getName());
                // add startActivity here.
            }
        });

    }

    private void gpsInit() {
        try {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            gpsEnableLayout.setVisibility(View.INVISIBLE);
        } else {
            gpsEnableLayout.setVisibility(View.VISIBLE);
        }
    }

    /***********************************TMap OnClickLinstenerCallback**********************************************/
    @Override
    public boolean onPressEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        Log.d(TAG, "lon : " + tMapPoint.getLongitude() + "/lat : " + tMapPoint.getLatitude());
        return false;
    }

    @Override
    public boolean onPressUpEvent(ArrayList<TMapMarkerItem> arrayList, ArrayList<TMapPOIItem> arrayList1, TMapPoint tMapPoint, PointF pointF) {
        return false;
    }

    /***********************************TMap LocationListener**********************************************/
    @Override
    public void onLocationChanged(final Location location) {
        Log.d(TAG, "lon : " + location.getLongitude() + "/lat : " + location.getLatitude());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TMapData tMapData = new TMapData();
                    String currentAddress = tMapData.convertGpsToAddress(location.getLatitude(), location.getLongitude());
                    if (address != null && !currentAddress.equals(address))
                        return;
                    /*
                    * 주소로 데이터 요청
                    */
                    Log.d(TAG, currentAddress);
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        Log.d(TAG, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(TAG, "onProviderEnabled");
        gpsEnableLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.d(TAG, "onProviderDisabled");
        gpsEnableLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCalloutRightButton(TMapMarkerItem tMapMarkerItem) {
        Log.d(TAG, "onCalloutRightButton" + tMapMarkerItem.getCalloutTitle());
    }
}

package com.example.reservjava_app.ui.b_where;

import android.content.Intent;
import android.graphics.PointF;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.example.reservjava_app.Common.CommonMethod;
import com.example.reservjava_app.DTO.BusinessDTO;
import com.example.reservjava_app.R;
import com.example.reservjava_app.reservation.Reservation;
import com.example.reservjava_app.reservation.Store;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.geometry.LatLngBounds;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.CompassView;
import com.naver.maps.map.widget.LocationButtonView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.example.reservjava_app.Common.CommonMethod.*;

import static com.example.reservjava_app.Common.CommonMethod.busiList;
public class SearchActivity extends AppCompatActivity implements NaverMap.OnMapClickListener, Overlay.OnClickListener, OnMapReadyCallback, NaverMap.OnCameraChangeListener {

  private static final String TAG = "main::SearchActivity";
  public static BusinessDTO busiSetItem = null;

  //????????? ????????? ????????? ?????? ?????????
  ArrayList<BusinessDTO> searchBusiList;
  Double lat = null, lng = null;
  double latitude=0.0, longitude=0.0;

  private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
  private FusedLocationSource mLocationSource;
  private NaverMap mNaverMap;
  private String searchText=null, address;

  // ?????? ?????? ???????????? ????????? ??????
  private ArrayList<Marker> markers = new ArrayList<>();
  Marker marker = new Marker();
  Address addr = null;
  LatLng markerPosition;
  private InfoWindow infoWindow;
  private Vector<Marker> activeMarkers;

  // ?????? Searchview??? ????????? EditText??? ????????? ???????????? ??????
  //Searchview ????????? ???????????????..
  EditText search_addrSearch;
  TextView tvAddr;
  String newAddr;
  Geocoder geocoder = new Geocoder(this);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);

    search_addrSearch = findViewById(R.id.search_addrSearch);
    tvAddr = findViewById(R.id.tvAddr);

    //(??????) ????????? ?????? ?????? ?????? ????????? ???????????????(???????)
    //???????????? ?????? ????????? ???????????? ???,, ?????? ?????? ?????????????????? ?????????//
    //?????? refresh ?????? ?????? ????????????.

    // ????????? ????????? ?????? ?????????????????? ????????????
    findViewById(R.id.setAddrBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(SearchActivity.this, AddrListActivity.class);
        startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
      }
    });

    // ????????? ???????????? ???????????? FusedLocationSource ??????
    mLocationSource =
        new FusedLocationSource(this, PERMISSIONS_REQUEST_CODE);

    //????????? - ????????????(whereList??? ??????)
    findViewById(R.id.search_searchBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      searchBusiList = new ArrayList<>();
      searchText = "";
      searchText = search_addrSearch.getText().toString();
      for(BusinessDTO dto : busiList) {
        if( dto.getBusiness_name().indexOf(searchText) >-1 || dto.getBusiness_hashtag().indexOf(searchText) >-1) {
          searchBusiList.add(dto);
        }
      }

      Toast.makeText(SearchActivity.this, searchText + " ????????? ???????????????", Toast.LENGTH_SHORT).show();

      Intent intent = new Intent(SearchActivity.this, WhereListActivity.class);
      intent.putExtra("searchBusiList",searchBusiList);
      startActivity(intent);
      search_addrSearch.setText(null);
      //finish();
      }
    });

    //????????? ???????????? ????????? ????????? ???????????? ??????
    //???????????? ?????? ?????? ???????????? ?????? ????????? ?????? ????????? ?????? ???????????? ????????????
    search_addrSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
      searchBusiList = new ArrayList<>();
      searchText = "";
      searchText = search_addrSearch.getText().toString();
      for(BusinessDTO dto : busiList) {
        if( dto.getBusiness_name().indexOf(searchText) >-1 || dto.getBusiness_hashtag().indexOf(searchText) >-1) {
          searchBusiList.add(dto);
        }
      }

      Toast.makeText(SearchActivity.this, searchText + " ????????? ???????????????", Toast.LENGTH_SHORT).show();
      Intent intent = new Intent(SearchActivity.this, WhereListActivity.class);
      intent.putExtra("searchBusiList",searchBusiList);
      startActivity(intent);
      search_addrSearch.setText(null);
      //finish();
      return false;
      }
    });

    //??? ??????
    findViewById(R.id.search_backBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      //Intent intent = new Intent(SearchActivity.this, MainActivity.class);
      //startActivity(intent);
      finish();
      }
    });
  }

  @UiThread
  @Override
  public void onMapReady(@NonNull NaverMap naverMap) {
    Log.d( TAG, "onMapReady");

    //????????? ????????? ?????? ????????? ??? ??????
    // NaverMap ?????? ????????? NaverMap ????????? ?????? ?????? ??????
    mNaverMap = naverMap;
    mNaverMap.setLocationSource(mLocationSource);

    UiSettings uiSettings = naverMap.getUiSettings();
    uiSettings.setCompassEnabled(false);
    uiSettings.setLocationButtonEnabled(false);

    //?????????, ?????? ?????? ?????? ?????? ?????????
    // ????????? ?????? ???????????? ???
    CompassView compassView = findViewById(R.id.compassBtn);
    compassView.setMap(mNaverMap);
    LocationButtonView locationButtonView = findViewById(R.id.locationBtn);
    locationButtonView.setMap(mNaverMap);

    /*Intent intent = getIntent();

    newAddr = intent.getStringExtra("newAddr");*/

    if(newAddr == null) {    // ????????????(???????????? ?????? ?????? ?????????)
      //?????? ???????????? ????????????
      address = currentAddress;
      address = address.substring(address.indexOf(" "));
      tvAddr.setText(address);

      // ????????? ????????? ??????
      CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(curAddr.latitude, curAddr.longitude), 16)
          .animate(CameraAnimation.Easing, 900);
      mNaverMap.setMinZoom(6.0);
      mNaverMap.setMaxZoom(18.0);
      mNaverMap.setExtent(new LatLngBounds(new LatLng(33.5, 126), new LatLng(39.35, 130)));
      mNaverMap.moveCamera(cameraUpdate);

    } else if(newAddr == "1") { //????????? ????????? ???????????? ??????
      //?????? ????????? ????????? ?????? ??? ???????????? ?????? ??????
      // ??????????????? ???????????? ????????? ?????? ????????? ??????
      List<Address> list = null;
      String str = tvAddr.getText().toString();
      try {
        list = geocoder.getFromLocationName(str, // ?????? ??????
                10); // ?????? ??????
      } catch (IOException e) {
        e.printStackTrace();
        Log.e("test","????????? ?????? - ???????????? ??????????????? ????????????");
      }

      if (list != null) {
        if (list.size() == 0) {
          tvAddr.setText("???????????? ?????? ????????? ????????????");
        } else {
          // ???????????? ????????? ????????? ?????????
          Address addr = list.get(0);
          latitude = addr.getLatitude();
          longitude = addr.getLongitude();

          // ????????? ????????? ??????
          CameraUpdate cameraUpdate = CameraUpdate.scrollAndZoomTo(new LatLng(latitude, longitude), 16)
              .animate(CameraAnimation.Easing, 900);
          mNaverMap.setMinZoom(6.0);
          mNaverMap.setMaxZoom(18.0);
          // ?????? ????????? ????????? ????????????
          mNaverMap.setExtent(new LatLngBounds(new LatLng(33.5, 126), new LatLng(39.35, 130)));
          mNaverMap.moveCamera(cameraUpdate);
          newAddr = null;
        }
      }
    }

    mNaverMap.addOnCameraChangeListener(SearchActivity.this);
    mNaverMap.setOnMapClickListener(SearchActivity.this);

    infoWindow = new InfoWindow();
    infoWindow.setAdapter(new InfoWindow.DefaultViewAdapter(this) {
      @NonNull
      @Override
      protected View getContentView(@NonNull InfoWindow infoWindow) {
        Marker marker = infoWindow.getMarker();
        BusinessDTO dto = (BusinessDTO) marker.getTag();

        String name = dto.getBusiness_name();
        String addr = dto.getBusiness_addr();
        String avg = String.valueOf((dto.getBusiness_star_avg())/20);
        String rNum = dto.getBusiness_tel();
        int category = dto.getBusiness_category_code();

        View view = View.inflate(SearchActivity.this, R.layout.business_view_map, null);
        ImageView logo = view.findViewById(R.id.search_bLogo);

        if("100".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
          logo.setImageResource(R.drawable.hosp_img);
        }else if("200".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
          logo.setImageResource(R.drawable.rest_img);
        }else if("300".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
          logo.setImageResource(R.drawable.culture_img);
        }else if("400".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
          logo.setImageResource(R.drawable.hotel_img);
        }else{

        }

        ((TextView) view.findViewById(R.id.search_bName)).setText(name);
        ((TextView) view.findViewById(R.id.search_bAddr)).setText(addr);
        ((TextView) view.findViewById(R.id.search_bSRateAvg)).setText(avg);
        ((TextView) view.findViewById(R.id.search_bReviewNum)).setText(rNum);
        return view;
      }
    });

    // ?????? ???????????? ?????? ????????? ??????
    infoWindow.setOnClickListener(new Overlay.OnClickListener() {
      @Override
      public boolean onClick(@NonNull Overlay overlay) {
        Marker marker = infoWindow.getMarker();
        BusinessDTO dto = (BusinessDTO) marker.getTag();

        //?????? ?????? ????????? ???????????? ????????? ????????? ?????? ?????? ???????????? ????????????
        //??? ????????? ???????????? ?????? ???????????? ???????????????..... ??????
        //?????? ??? ????????? ?????????.....
/*        busiSetItem = dto;
        Toast.makeText(SearchActivity.this, busiSetItem.getBusiness_name(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SearchActivity.this, Store.class);
        intent.putExtra("businessdto", busiSetItem);
        startActivity(intent);*/


        if(loginDTO == null) {
          CommonMethod.LoginPageCall(SearchActivity.this);
        }else {
          Intent intent = new Intent(SearchActivity.this, Reservation.class);
          intent.putExtra("business_code", dto.getBusiness_code());
          startActivity(intent);
          //finish();
        }

        return false;
      }
    });
  }

  //?????? ?????? ????????? ?????? ?????? ?????? - ?????? ?????? ?????? ???????????? ????????? ????????? ?????? ????????? ?????? ??? ????????? ????????? ????????? ??????.
  @Override
  public void onCameraChange(int i, boolean b) {
    freeActiveMarkers();
    // ????????? ?????????????????? ???????????? ????????????????????? ?????? ??????
    LatLng currentPosition = getCurrentPosition(mNaverMap);

    for (BusinessDTO dto: busiList) {
      markerPosition = new LatLng(dto.getBusiness_lat(), dto.getBusiness_lng());
      if (!withinSightMarker(currentPosition, markerPosition))
        continue;
      Marker marker = new Marker();
      marker.setTag(dto);
      marker.setWidth(200);
      marker.setHeight(200);
      marker.setPosition(markerPosition);
      // ?????? ????????? ??????
      if("100".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
        marker.setIcon(OverlayImage.fromResource(R.drawable.hosp_mark_img));
      }else if("200".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
        marker.setIcon(OverlayImage.fromResource(R.drawable.rest_mark_img));
      }else if("300".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
        marker.setIcon(OverlayImage.fromResource(R.drawable.culture_mark));
      }else if("400".equalsIgnoreCase(String.valueOf(dto.getBusiness_category_parent_code()))) {
        marker.setIcon(OverlayImage.fromResource(R.drawable.hotel_mark_img));
      }
      marker.setAnchor(new PointF(0.5f, 1.1f));
      marker.setHideCollidedMarkers(true);  //????????? ?????? ???????????? ??????
      marker.setMap(mNaverMap);
      marker.setOnClickListener(SearchActivity.this);
      activeMarkers.add(marker);
    }
  }

  //?????? ????????? ?????? ?????????
  @Override
  public boolean onClick(@NonNull Overlay overlay) {
    if(overlay instanceof Marker) {
      Marker marker = (Marker) overlay;
      if(marker.getInfoWindow() != null) {
        infoWindow.close();
      } else {
        infoWindow.open(marker);
      }
      return true;
    }
    //?????? ????????? ??????  ??????????????? ????????? ????????? ?????? ????????? ?????? ????????? ?????? ??????????????? ?????? ?????????.
    //CameraUpdate cameraUpdate = CameraUpdate.scrollTo(marker.getPosition());
    //    .animate(CameraAnimation.Easing, 500);
    //mNaverMap.moveCamera(cameraUpdate);
    return false;
  }

  //?????? ???????????? ??? ?????? ????????????
  @Override
  public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
    if(infoWindow.getMarker() != null) {
      infoWindow.close();
    }
  }

  // ?????? ???????????? ???????????? ??????
  public LatLng getCurrentPosition(NaverMap naverMap) {
    CameraPosition cameraPosition = naverMap.getCameraPosition();
    return new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
  }

  // ????????? ????????? ????????? ????????????(???????????? ???????????? ?????? ?????? 3km ???)??? ????????? ??????
  public final static double REFERANCE_LAT = 1 / 109.958489129649955;
  public final static double REFERANCE_LNG = 1 / 88.74;
  public final static double REFERANCE_LAT_X3 = 3 / 109.958489129649955;
  public final static double REFERANCE_LNG_X3 = 3 / 88.74;
  public boolean withinSightMarker(LatLng currentPosition, LatLng markerPosition) {
    boolean withinSightMarkerLat = Math.abs(currentPosition.latitude - markerPosition.latitude) <= REFERANCE_LAT_X3;
    boolean withinSightMarkerLng = Math.abs(currentPosition.longitude - markerPosition.longitude) <= REFERANCE_LNG_X3;
    return withinSightMarkerLat && withinSightMarkerLng;
  }

  // ???????????? ?????????????????? ????????? ???????????? ??????
  private void freeActiveMarkers() {
    if (activeMarkers == null) {
      activeMarkers = new Vector<Marker>();
      return;
    }
    for (Marker activeMarker: activeMarkers) {
      activeMarker.setMap(null);
    }
    activeMarkers = new Vector<Marker>();
  }

  //???????????? ??????
  public void onBackPressed() {
    super.onBackPressed();

/*    Intent intent = new Intent(SearchActivity.this, MainActivity.class);
    startActivity(intent);
    finish();*/
  }

  @Override
  protected void onResume() {
    super.onResume();

    FragmentManager fm = getSupportFragmentManager();
    MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
    if (mapFragment == null) {
      mapFragment = MapFragment.newInstance(new NaverMapOptions()
      );
      fm.beginTransaction().add(R.id.map, mapFragment).commit();
    }
    mapFragment.getMapAsync(this);
  }

  public void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);
    switch (requestCode) {
      case SEARCH_ADDRESS_ACTIVITY:
        if (resultCode == RESULT_OK) {
          String data = intent.getExtras().getString("data");
          if (data != null) {
            data = data.substring(7);
            tvAddr.setText(data);
            newAddr = "1";
          }
        }
        break;
    }
  }
}
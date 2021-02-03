package com.example.reservjava_app.ui.b_where;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.example.reservjava_app.ATask.SearchBusiness;
import com.example.reservjava_app.Common.GpsTracker;
import com.example.reservjava_app.DTO.BusinessDTO;
import com.example.reservjava_app.R;
import com.example.reservjava_app.ui.a_login_signup.LoginActivity;
import com.example.reservjava_app.ui.f_profile.ProfileActivity;
import com.example.reservjava_app.ui.f_profile.ReviewActivity;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.reservjava_app.ui.a_login_signup.LoginActivity.loginDTO;

public class SearchActivity extends AppCompatActivity implements OnMapReadyCallback  {

  private static final String TAG = "main::SearchActivity";
  public static BusinessDTO busiSetItem = null;

  private GpsTracker gpsTracker;
  private static final int GPS_ENABLE_REQUEST_CODE = 2001;
  private static final int PERMISSIONS_REQUEST_CODE = 100;
  private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
  String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
  private FusedLocationSource mLocationSource;
  private NaverMap mNaverMap, naverMap;
  private String searchText, address;

  // 일단 Searchview는 힘드니 EditText로 기능을 구현하고 나서
  //Searchview 사용을 고민해보자..
  EditText addrSearch;
  TextView tvAddr;
  int newAddr = 0;
  Geocoder geocoder = new Geocoder(this);
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_search);


    addrSearch = findViewById(R.id.addrSearch);
    tvAddr = findViewById(R.id.tvAddr);

    //네이버 맵 권한 체크(중복)
    if (!checkLocationServicesStatus()) {
      showDialogForLocationServiceSetting();
    } else {
      checkRunTimePermission();
    }

    //(임시) 누르면 현재 위치 찾는 것으로 구현해보자(종료?)
    //자동으로 위치 찾기가 안되었을 때,, 지금으로서는 다른 화면으로
    //이동했다가 다시 Search화면으로 와야 검색이 된다.
    //자동 refresh 되는 옵션 추가하자.
    // 누르면 새로운 지도 검색화면으로 연결하자
    findViewById(R.id.setAddrBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(SearchActivity.this, "11", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SearchActivity.this, AddrListActivity.class);
        startActivityForResult(intent, SEARCH_ADDRESS_ACTIVITY);
      }
    });

    //지도 객체 띄우기
    FragmentManager fm = getSupportFragmentManager();
    MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
    if (mapFragment == null) {
      mapFragment = MapFragment.newInstance(new NaverMapOptions()
          .camera(new CameraPosition(new LatLng(37.5116620, 127.0594274), 16, 0, 90))
          .locationButtonEnabled(true)
          .compassEnabled(true)  // 이동을 할 때?? 뜸,, 바로 안 뜬다//이건 나중에
          );

      fm.beginTransaction().add(R.id.map, mapFragment).commit();
    }

    // getMapAsync를 호출하여 비동기로 onMapReady 콜백 메서드 호출
    // onMapReady에서 NaverMap 객체를 받음
    mapFragment.getMapAsync(this);

    // 위치를 반환하는 구현체인 FusedLocationSource 생성
    mLocationSource =
        new FusedLocationSource(this, PERMISSIONS_REQUEST_CODE);


    //상단바 - 검색버튼(whereList로 이동)
    findViewById(R.id.searchBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        searchText = addrSearch.getText().toString();
        Toast.makeText(SearchActivity.this, searchText + "를 검색합니다", Toast.LENGTH_SHORT).show();
        //Log.d(TAG, "onClick searchText : " + searchText);

        Intent intent = new Intent(SearchActivity.this, WhereListActivity.class);
        intent.putExtra("searchText", searchText);
        startActivity(intent);
      }
    });

    //검색명 입력하고 엔터키 입력시 검색으로 연결
    addrSearch.setOnKeyListener(new View.OnKeyListener() {
      @Override
      public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
        if(keyCode == keyEvent.KEYCODE_ENTER) {
          switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
              searchText = "";
              searchText = addrSearch.getText().toString();
              Toast.makeText(SearchActivity.this, searchText + "를 검색합니다", Toast.LENGTH_SHORT).show();
              //Log.d(TAG, "onClick searchText : " + searchText);

              Intent intent = new Intent(SearchActivity.this, WhereListActivity.class);
              intent.putExtra("searchText", searchText);
              startActivity(intent);
              break;
          }
          return true;
        }
        return false;
      }
    });


    // (임시) 리뷰 등록 화면으로 이동
    findViewById(R.id.moveToReview).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ReviewActivity.class);
        startActivity(intent);
      }
    });

    // (임시) 프로필 화면으로 이동
    findViewById(R.id.moveToProfile).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if(loginDTO == null) {
          AlertDialog.Builder builder = new AlertDialog.Builder(SearchActivity.this);
          builder.setTitle("알림");
          builder.setMessage("로그인이 필요한 페이지 입니다");
          builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              dialogInterface.dismiss();
              Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
              startActivity(intent);
            }
          });
          builder.show();

        } else {
          Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
          startActivity(intent);
        }
      }
    });
  }

  public void setLayerGroupEnabled​(@NonNull String group, boolean enabled) {
  }

  @UiThread
  @Override
  public void onMapReady(@NonNull NaverMap naverMap) {
    Log.d( TAG, "onMapReady");
    double latitude =0, longitude = 0;

    UiSettings uiSettings = naverMap.getUiSettings();
    uiSettings.setCompassEnabled(true);
    uiSettings.setLocationButtonEnabled(true);

    // NaverMap 객체 받아서 NaverMap 객체에 위치 소스 지정
    mNaverMap = naverMap;
    mNaverMap.setLocationSource(mLocationSource);

    //새로운 주소를 설정했을 경우 주소를 자동으로 불러오는 기능을 멈춰야 한다

    if(newAddr == 0) {
      gpsTracker = new GpsTracker(this);

      latitude = gpsTracker.getLatitude();
      longitude = gpsTracker.getLongitude();

      //위 경도를 도로명 주소로 변경 // 주소 자동으로 입력하기
      address = getCurrentAddress(latitude, longitude);
      address = address.substring(address.indexOf(" "));
      tvAddr.setText(address);

    } else if(newAddr ==1) {
      // 지오코더를 이용하여 주소를 위도 경도로 변환

      List<Address> list = null;

      String str = tvAddr.getText().toString();
      try {
        list = geocoder.getFromLocationName
            (str, // 지역 이름
                10); // 읽을 개수
      } catch (IOException e) {
        e.printStackTrace();
        Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
      }

      if (list != null) {
        if (list.size() == 0) {
          tvAddr.setText("해당되는 주소 정보는 없습니다");
        } else {
          // 해당되는 주소로 인텐트 날리기
          Address addr = list.get(0);
          latitude = addr.getLatitude();
          longitude = addr.getLongitude();

          String sss = String.format("geo:%f,%f", latitude, longitude);
        }
      }
    }


    Log.d(TAG, "onMapReady: " + latitude +" : " +longitude );

    // 지도상에 마커 표시
    Marker marker = new Marker();
    marker.setPosition(new LatLng(latitude, longitude));
    marker.setMap(naverMap);

    // 지정된 위치로 이동
    CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(latitude, longitude))
        .animate(CameraAnimation.Easing, 2000);

    mNaverMap.moveCamera(cameraUpdate);

  }

  //네이버 맵 관련
  public String getCurrentAddress( double latitude, double longitude) {

    //지오코더... GPS를 주소로 변환
    geocoder = new Geocoder(this, Locale.getDefault());

    List<Address> addresses;

    try {
      addresses = geocoder.getFromLocation(latitude, longitude, 7);
    } catch (IOException ioException) {
      //네트워크 문제
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
    return address.getAddressLine(0) +"\n";
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    super.onActivityResult(requestCode, resultCode, intent);

    switch (requestCode) {
      //주소 검색한 뒤 결과를 입력
      case SEARCH_ADDRESS_ACTIVITY:
        if (resultCode == RESULT_OK) {
          String data = intent.getExtras().getString("data");
          if (data != null) {
            newAddr = 1;
            String addrText = data.substring(data.indexOf(" "));
            tvAddr.setText(addrText);
            Log.d(TAG, "onActivityResult: newAddr  " + data);

            //지도 객체 띄우기
            FragmentManager fm = getSupportFragmentManager();
            MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map);
            if (mapFragment == null) {
              mapFragment = MapFragment.newInstance(new NaverMapOptions()
                  .locationButtonEnabled(true)
                  .compassEnabled(true)  // 이동을 할 때?? 뜸,, 바로 안 뜬다//이건 나중에
              );

              fm.beginTransaction().add(R.id.map, mapFragment).commit();
            }

            mapFragment.getMapAsync(this);
          }
        }
        break;
      //GPS 활성화 여부 확인
      case GPS_ENABLE_REQUEST_CODE:
        //사용자가 GPS 활성 시켰는지 검사
        if (checkLocationServicesStatus()) {
          if (checkLocationServicesStatus()) {
            //@@@로 하면 이름이 나오는 건가;;
            Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
            //어짜피 어플 뜰 때 체크함
            //checkRunTimePermission();
            return;
          }
        }
        break;
    }
  }
















  //여기부터는 GPS 활성화를 위한 메소드들
  private void showDialogForLocationServiceSetting() {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("위치 서비스 비활성화");
    builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
        + "위치 설정을 수정하시겠습니까?");
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

/*  @Override  // 네이버 맵 권한 부여 중복
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    // request code와 권한획득 여부 확인
    if (requestCode == PERMISSION_REQUEST_CODE) {
      if (grantResults.length > 0
          && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        mNaverMap.setLocationTrackingMode(LocationTrackingMode.Follow);
      }
    }
  }*/

  //권한관련해서는 이미 작업해놓은 것이 있으므로 중복
  /* ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드. */
  @Override
  public void onRequestPermissionsResult(int permsRequestCode,
                                         @NonNull String[] permissions,
                                         @NonNull int[] grandResults) {

    if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
      // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
      boolean check_result = true;

      // 모든 퍼미션을 허용했는지 체크.
      for (int result : grandResults) {
        if (result != PackageManager.PERMISSION_GRANTED) {
          check_result = false;
          break;
        }
      }

      if ( check_result ) {

      }
      else {
        // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료.

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
            || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

          Toast.makeText(this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
          finish(); // 액티비티가 아니라 종료하면 안될 거 같은데..
        }else {
          Toast.makeText(this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
        }
      }
    }
  }

  void checkRunTimePermission(){

    //런타임 퍼미션 처리
    // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
    int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_FINE_LOCATION);
    int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
        Manifest.permission.ACCESS_COARSE_LOCATION);


    if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
        hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

      // 2. 이미 퍼미션을 가지고 있다면
      // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
      // 3.  위치 값을 가져올 수 있음

    } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

      // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
      if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
        // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
        Toast.makeText(this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
        // 3-3. 사용자에게 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
            PERMISSIONS_REQUEST_CODE);

      } else {
        // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
            PERMISSIONS_REQUEST_CODE);
      }
    }
  }


  public boolean checkLocationServicesStatus() {
    LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
  }
}
package com.example.reservjava_app.ui.d_bongsun;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.reservjava_app.ATask.BookingCancel;
import com.example.reservjava_app.ATask.BookingView;
import com.example.reservjava_app.DTO.BookingDTO;
import com.example.reservjava_app.ListActivity;
import com.example.reservjava_app.MainActivity;
import com.example.reservjava_app.R;
import com.example.reservjava_app.fragment.ListFragment;
import com.example.reservjava_app.fragment.d_bongsun.QnAFragment;
import com.example.reservjava_app.ui.a_login_signup.JoinActivity;
import com.example.reservjava_app.ui.a_login_signup.LoginActivity;
import com.example.reservjava_app.ui.a_login_signup.QnAMainActivity;
import com.example.reservjava_app.ui.b_where.SearchActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import static com.example.reservjava_app.Common.CommonMethod.loginDTO;

public class BookingViewActivity extends AppCompatActivity {

    //HomeFragment homeFragment;
    ListFragment listFragment;
    QnAFragment qnAFragment;
    Toolbar toolbar;

    String state;
    String member_code;
    String booking_code;

    public static BookingDTO bookingDTO = null;

    private static String TAG = "main:BookingViewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_view);

        EditText addSearch = (EditText) findViewById(R.id.addrSearch);
        addSearch.setText("activity_booking_view.xml");

        //1. ???????????? ????????? A, B, C ??? ???????????? ?????????
        //  ???????????? ????????? ????????? ????????? ?????????.

        toolbar = findViewById(R.id.backJoinBtn);
        setSupportActionBar(toolbar);   //?????????????????? ????????? ??????

        //?????????, ????????? ?????? ??????
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_dehaze_24);   //????????? ????????? ??????
        //getSupportActionBar().setTitle("??????");   //???????????????(default: app_name @res.values.strings.xml)

        //????????? ????????? Navigation Drawer( ???????????? ??????)??????
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Navigation Drawer(???????????? ??????) ????????? ?????? ????????? ??????
        NavigationView navigationView = findViewById(R.id.loginnavigation);
        if(loginDTO == null) {  //????????? ????????? ???
            navigationView.getMenu().findItem(R.id.nav_membershipbtn)
                    .setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout)
                    .setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_listchk)
                    .setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_loginbtn)
                    .setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_signupbtn)
                    .setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_qna)
                    .setVisible(true);
        } else {  //????????? ?????? ???
            navigationView.getMenu().findItem(R.id.nav_membershipbtn)
                    .setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout)
                    .setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_listchk)
                    .setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_loginbtn)
                    .setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_signupbtn)
                    .setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_qna)
                    .setVisible(true);
        }

        //Navigation Drawer(???????????? ??????) ????????? ?????? ????????? ?????? ??????
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                //???????????? ?????? ????????? ??????
                if(id == R.id.nav_loginbtn){
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                }else if(id == R.id.nav_signupbtn){
                    Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
                    startActivity(intent);
                }else if(id == R.id.nav_qna){
                    Intent intent = new Intent(getApplicationContext(), QnAMainActivity.class);
                    startActivity(intent);
                }
                return false;
            }



        });

        //????????? ??????
        //homeFragment = new HomeFragment();
        listFragment = new ListFragment();
        qnAFragment = new QnAFragment();

        //getSupportFragmentManager().beginTransaction()
        //        .replace(R.id.container, homeFragment).commit();    //?????? ????????? ?????????
        BottomNavigationView bottomNavigationView =
                findViewById(R.id.bottom_navigation);

        //????????? ????????? ?????? ????????? ??????
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent; //???????????? ?????? ?????? ???????????? ??????
                switch (item.getItemId()){
                    case R.id.homeItem:
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.searchItem:
                        intent = new Intent(getApplicationContext(), SearchActivity.class);
                        startActivity(intent);
                        return true;

                    case R.id.listItem:
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, listFragment).commit();
                        return true;

                }//switch
                return false;
            }//onNavigationItemSelected()
        });

        // id = ((EditText) findViewById(R.id.addrSearch)).getText().toString();
        member_code = "101";//?????? ????????????
        booking_code = "105";//?????? ????????????

        //???????????? ????????????
        BookingView bookingView = new BookingView(booking_code);
        try {
            //showBooking( bookingView.execute().get() );
            bookingDTO = bookingView.execute().get();
            Log.d(TAG, "readMessage: " + " : " + bookingDTO.getBooking_code() + " : " + bookingDTO.getBooking_kind() + " : " + bookingDTO.getBooking_member_code() + " : " + bookingDTO.getBooking_business_code() + " : " + bookingDTO.getBooking_product_code() + " : " + bookingDTO.getBooking_price() + " : " + bookingDTO.getBooking_price_deposit() + " : " + bookingDTO.getBooking_num() + " : " + bookingDTO.getBooking_date() + " : " + bookingDTO.getBooking_date_reservation() + " : " + bookingDTO.getBooking_etc() + " : " + bookingDTO.getBooking_appraisal_star() + " : " + bookingDTO.getBooking_appraisal());
            Log.d(TAG, "onCreate: bookingView.execute().get() ?????????.");

            showBooking(bookingDTO);
/*
            TextView business_name = (TextView) findViewById(R.id.business_name);
            business_name.setText( String.valueOf(bookingDTO.getBooking_business_code()) );
*/
        } catch (Exception e) {
            e.printStackTrace();
        }//try//catch

        //???????????? ??????
        findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookingCancel();
            }//onClick()
        });//cancelBtn.setOnClickListener()

    }//onCreat()

    // ??????????????? ?????? ?????????
    public void onFragmentChange(int state){
        Intent intent; //???????????? ?????? ?????? ???????????? ??????
        if (state == 1) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (state == 2) {
            intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        } else if (state == 3) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, listFragment).commit();
        } else if (state == 4) {    //????????? ?????????(??????) state = 4
            intent = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(intent);
        } else if (state == 7) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, qnAFragment).commit();
        }
    }
    
    private void showBooking(BookingDTO dto){
        ((TextView) findViewById(R.id.business_name))
                        .setText( String.valueOf(dto.getBooking_business_name()) );
        ((TextView) findViewById(R.id.booking_date))
                        .setText( String.valueOf(dto.getBooking_date_reservation()) );
        ((TextView) findViewById(R.id.booking_etc))
                        .setText( String.valueOf(dto.getBooking_price()) );
    }

    private void bookingCancel(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("??????");
        builder.setMessage("????????? ?????? ???????????????????");
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setNegativeButton("???", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = "???. ????????? ???????????????!";
                Log.d(TAG, "showMessage().onClick: " + message);

                BookingCancel bookingCancel = new BookingCancel(booking_code);
                try {
                    state = bookingCancel.execute().get();
                    Log.d(TAG, "cancelBtn:onClick: bookingCancel.execute().get() ?????????.");
                } catch (Exception e) {
                    e.printStackTrace();
                }//try//catch

                if(state.equals("1")){
                    Log.d(TAG, "cancelBtn:onClick: ???????????? ?????? !!!");

                    showAlert("???????????? ???????????????.");
                    //finish();
                }else{
                    Log.d(TAG, "cancelBtn:onClick: ???????????? ?????? !!!");

                    showAlert("??????????????? ???????????? ???????????????.");
                    //finish();
                }
            }
        });//builder.setNegativeButton()

        builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String message = "?????????. ????????? ???????????????!";
                Log.d(TAG, "showMessage().onClick: " + message);
                if(dialog != null){
                    dialog.dismiss();
                }
            }
        });//builder.setPositiveButton()

        AlertDialog dialog = builder.create();
        dialog.show();

    }//bookingCancel()

    private void showAlert(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("??????");
        builder.setMessage( msg );
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialog != null){
                    dialog.dismiss();
                }
                //finish();
            }
        });//builder.setPositiveButton()

        AlertDialog dialog = builder.create();
        dialog.show();
    }//showAlert()

}//class BookingViewActivity
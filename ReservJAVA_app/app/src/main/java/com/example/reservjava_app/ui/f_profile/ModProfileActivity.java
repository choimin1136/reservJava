package com.example.reservjava_app.ui.f_profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.reservjava_app.ATask.FileUploadUtils;
import com.example.reservjava_app.ATask.MemberUpdate;
import com.example.reservjava_app.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import static com.example.reservjava_app.Common.CommonMethod.ipConfig;
import static com.example.reservjava_app.Common.CommonMethod.member_imgPath;
import static com.example.reservjava_app.Common.CommonMethod.pServer;
import static com.example.reservjava_app.ui.a_login_signup.LoginActivity.loginDTO;

public class ModProfileActivity extends AppCompatActivity {
  private static final String TAG = "main:ModProfileActivity";

  EditText mod_editPW, mod_editPW2, mod_editNick, mod_editTel, mod_editEmail;
  TextView mod_tv_name, mod_tv_id;
  ImageView mod_faceImg;

  //사진 경로 지정을 위한 변수
  public String imagePath;
  //갤러리에서 사진을 넘겨오기 위한 변수
  private static final int REQUEST_CODE = 0;

  final int CAMERA_REQUEST = 1010;
  final int LOAD_IMAGE = 1011;

  File file = null;
  long fileSize = 0;
  java.text.SimpleDateFormat tmpDateFormat;

  // 여기까지 사진 관련


  String member_id, member_name, member_pw, member_pw2, member_nick, member_tel, member_email, member_image;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_mod_profile);

    //사진
    //만약 사진 필드가 비어 있다면... 디폴트 이미지

    imagePath = ipConfig + pServer + member_imgPath + loginDTO.getMember_image();
    mod_faceImg = findViewById(R.id.mod_faceImg);
    mod_faceImg.setVisibility(View.VISIBLE);
    //선택된 이미지 보여주기(움직이는 그림도 됨)
    Glide.with(ModProfileActivity.this).load(imagePath).into(mod_faceImg);
    //사진 저장시 파일명을 위한 시간포멧
    tmpDateFormat = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss");


    //loginDTO사용하여 정보 가지고 옴,,
    //비번은 가지고 오지 않는다.
    //단 값이 입력되어 있다면 두 값을 비교해서 같으면 저장, 아니면 다르다고 메시지 띄운다
    //수정 시 확인 메시지 띄움
    //폰이라 정보 수정에는 비번 필요 x,  결제 정보 변경에는 비번이 필요

    // ** 이미 저장 된 값을 불러와서 자동으로 표시해 줌
    //화면상에 표시할 곳을 지정해주고
    mod_tv_id = findViewById(R.id.mod_tv_id);
    mod_tv_name = findViewById(R.id.mod_tv_name);
    mod_editPW = findViewById(R.id.mod_editPW);
    mod_editPW2 = findViewById(R.id.mod_editPW2);
    mod_editNick = findViewById(R.id.mod_editNick);
    mod_editTel = findViewById(R.id.mod_editTel);
    mod_editEmail = findViewById(R.id.mod_editEmail);
    //값을 불러와서
    member_id = loginDTO.getMember_id();
    member_name = loginDTO.getMember_name();
    //member_pw = loginDTO.getMember_pw();
    member_nick = loginDTO.getMember_nick();
    member_tel = loginDTO.getMember_tel();
    member_email = loginDTO.getMember_email();
    //저장 된 값 화면에 표시
    mod_tv_id.setText(member_id);
    //mod_editPW.setText(member_pw);
    //mod_editPW2.setText(member_pw);
    mod_tv_name.setText("소중한 " + member_name + "님");
    mod_editNick.setText(member_nick);
    mod_editTel.setText(member_tel);
    mod_editEmail.setText(member_email);

    //이미지를 클릭하면 갤러리창을 띄워 사진을 변경할 수 있게 한다
    //카메라 띄워서 바로 저장하는 건 나중에~~
    //그리고 서버에 그림 파일을 저장할 때,, 그림 파일명이 겹치지 않게 해줘야 한다.
    //**
    mod_faceImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE);
      }
    });


        //이건 사진찍는 것
  /*  mod_faceImg.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        try{
          try{
            file = createFile();
            Log.d("ModProfile:FilePath ", file.getAbsolutePath());
          }catch(Exception e){
            Log.d("ModProfile:error1", "Something Wrong", e);
          }

          mod_faceImg.setVisibility(View.VISIBLE);

          Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
          if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // API24 이상 부터
            intent.putExtra(MediaStore.EXTRA_OUTPUT,
                FileProvider.getUriForFile(getApplicationContext(),
                    getApplicationContext().getPackageName() + ".fileprovider", file));
            Log.d("ModPro:appId", getApplicationContext().getPackageName());
          }else {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
          }
          if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAMERA_REQUEST);
          }
        }catch(Exception e){
          Log.d("ModProfile:error2", "Something Wrong", e);
        }
      }
    }); */


    //저장버튼(수정한 정보를 저장한다)
    findViewById(R.id.mod_saveBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        member_pw = mod_editPW.getText().toString();
        member_pw2 = mod_editPW2.getText().toString();
        //입력한 두 비밀번호가 맞는지 확인
        if(!(member_pw.equals(member_pw2))) {
          AlertDialog.Builder builder = new AlertDialog.Builder(ModProfileActivity.this);
          builder.setTitle("알림");
          builder.setMessage("비밀번호가 일치하지 않습니다");
          builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
              dialogInterface.dismiss();
            }
          });
          builder.show();
          mod_editPW.requestFocus();
        }
          Log.d(TAG, "onClick: 1" + member_pw );

        //파일 업로드
        String Path = "1";
        FileUploadUtils.send2Server(file, Path);


        // 이미지 변경이 없을 때와 있을 때~~

        Log.d(TAG, "onClick: 2" + file.getName());


        // ** 수정 된 값을 변수에 저장
        member_id = mod_tv_id.getText().toString();
        member_pw = mod_editPW.getText().toString();
        member_nick = mod_editNick.getText().toString();
        member_tel = mod_editTel.getText().toString();
        member_email = mod_editEmail.getText().toString();
        member_image = file.getName();

        Log.d(TAG, "onClick: 3" + member_id + member_pw + member_nick + member_tel + member_email + member_image);
        // 저장된 변수를 ATack로 넘긴다
        MemberUpdate memberUpdate = new MemberUpdate(member_id, member_pw, member_nick, member_tel, member_email, member_image);
        memberUpdate.execute();

        // 확인 메시지 띄우고 프로필 화면으로 이동
        Toast.makeText(ModProfileActivity.this, "수정한 내용이 저장되었습니다", Toast.LENGTH_SHORT).show();
/*        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        finish();*/
      }
    });

    //취소 버튼(프로필 화면으로 돌아간다)
    findViewById(R.id.mod_cancelBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Toast.makeText(ModProfileActivity.this, "취소되었습니다", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        finish();
      }
    });

    //뒤로가기 버튼(프로필 화면으로 돌아간다)
    findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        startActivity(intent);
        finish();
      }
    });
  }

/*  //서버에 저장할 이미지 파일 만들기
  //이렇게 하면 폰에서 위치 찾음(이게 맞긴한데.. 나중에 불러들이는 것을 바꿔야 할듯??)
  private File createFile() throws IOException {

    String imageFileName = "My" + tmpDateFormat.format(new Date()) + ".jpg";
    Log.d(TAG, "createFile: " + imageFileName);
    File storageDir = Environment.getExternalStorageDirectory();
    File curFile = new File(storageDir, imageFileName);
    Log.d(TAG, "createFile: " +storageDir);

    return curFile;
  }*/

  //갤러리
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE) {
      if (resultCode == RESULT_OK) {



        try {
          InputStream in = getContentResolver().openInputStream(data.getData());

          Bitmap img = BitmapFactory.decodeStream(in);
          mod_faceImg.setImageBitmap(img);
          in.close();


          String imgSavaFileName = loginDTO.getMember_id() + tmpDateFormat.format(new Date());
          file = new File(Environment.getExternalStorageDirectory()+"/Pictures/ResevJava/", imgSavaFileName + ".jpeg");
          Log.d(TAG, "onActivityResult: filepath" + file);
          OutputStream out = new FileOutputStream(file);
          img.compress(Bitmap.CompressFormat.JPEG, 100, out);
          Log.d(TAG, "onActivityResult: img" + img);
        } catch (Exception e) {

        }
      } else if (resultCode == RESULT_CANCELED) {
        Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
      }
    }
  }


/* //이미지 변경
  public void mod_saveBtnClicked(View view){
    if(isNetworkConnected(this) == true){

    // 파일 크기 제한은 나중에 시간이 될 때 다시 하자
      if(fileSize <= 5000000) {  // 파일크기가 5메가 보다 작아야 업로드 할수 있음

        finish();
      }else{
        // 알림창 띄움
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage("파일 크기가 5MB초과하는 파일은 업로드가 제한되어 있습니다.\n30MB이하 파일로 선택해 주십시요!!!");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.dismiss();
          }
        });
        builder.show();
      }

    }else {
      Toast.makeText(this, "인터넷이 연결되어 있지 않습니다.",
          Toast.LENGTH_SHORT).show();
    }
  }*/
}
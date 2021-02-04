package com.example.reservjava_app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.reservjava_app.R;
import com.naver.maps.map.overlay.InfoWindow;

public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
  private final Context mContext;
  private final ViewGroup mParent;

  public pointAdapter(@NonNull Context context, ViewGroup parent)
  {
    super(context);
    mContext = context;
    mParent = parent;
  }

  @NonNull
  @Override
  protected View getContentView(@NonNull InfoWindow infoWindow)
  {

    View view = (View) LayoutInflater.from(mContext).inflate(R.layout.item_point, mParent, false);

    TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
    ImageView imagePoint = (ImageView) view.findViewById(R.id.imagepoint);
    TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
    TextView txtTel = (TextView) view.findViewById(R.id.txttel);

    txtTitle.setText("한울직업전문학교");
    imagePoint.setImageResource(R.drawable.location);
    txtAddr.setText("제주 제주시 문연로 6\n(지번) 연동 312-1");
    txtTel.setText("064-710-2114");

    return view;
  }
}
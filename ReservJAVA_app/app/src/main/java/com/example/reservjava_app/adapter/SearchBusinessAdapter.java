package com.example.reservjava_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.reservjava_app.DTO.BusinessDTO;
import com.example.reservjava_app.R;
import com.example.reservjava_app.reservation.Store;
import com.example.reservjava_app.ui.b_where.WhereListActivity;

import java.util.ArrayList;
import static com.example.reservjava_app.ui.b_where.SearchActivity.busiSetItem;

public class SearchBusinessAdapter extends
    RecyclerView.Adapter<SearchBusinessAdapter.ItemViewHolder> {
  private static final String TAG = "main:SBAdater";

  Context mContext;
  ArrayList<BusinessDTO> busiList;
  LinearLayout parentLayout;

  public SearchBusinessAdapter(Context mContext, ArrayList<BusinessDTO> busiList){
    this.mContext = mContext;
    this.busiList = busiList;
  }

  //화면(xml) 연결
  @NonNull
  @Override
  public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    //String name1 = busiList.get(1).getBusiness_name();
    //Log.d(TAG, "onCreateViewHolder: " + name1);
    View itemView = inflater.inflate(R.layout.business_view, parent, false);

    return  new ItemViewHolder(itemView);
  }

  //데이터 연결
  @Override
  public void onBindViewHolder(@NonNull SearchBusinessAdapter.ItemViewHolder holder, final int position) {
    //Log.d(TAG, "onAdapter : "  + position);

    final BusinessDTO busiDTO = busiList.get(position);
    holder.setItem(busiDTO);

    holder.parentLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Log.d(TAG, "onClick: " + position);

        busiSetItem = busiList.get(position);

        Toast.makeText(mContext, "OnClick " + busiList.get(position).getBusiness_name(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(v.getContext(), Store.class);
        intent.putExtra("businessdto", busiSetItem);
        v.getContext().startActivity(intent);
      }
    });
  }

  @Override
  public int getItemCount() { return busiList.size(); }

  //어댑터에 메소드 만들기

  //내용 추가하기
  public void addItem(BusinessDTO businessDTO){
    busiList.add(businessDTO);
  }
  public void addItem(String toString, String toString1, double business_star_avg) {
  }

  //리사이클러뷰 내용 모두 지우기
  public void removeAllItem() { busiList.clear();}

  // 특정 인덱스 항목 가져오기
  public BusinessDTO getItem(int position) { return busiList.get(position);}

  //특정 인덱스 항목 세팅하기
  public void setItem(int position, BusinessDTO busiDTO) {busiList.set(position, busiDTO);}

  //ArrayList 통째로 세팅하기
  public void setItems(ArrayList<BusinessDTO> busiList) { this.busiList = busiList;}

  public static class ItemViewHolder extends RecyclerView.ViewHolder {

    public LinearLayout parentLayout;
    public ImageView search_bLogo;
    public TextView search_bName;
    public TextView search_bAddr;
    public TextView search_bSRateAvg;
    //이건 테이블 바꿔야 해서 우선은 보류
    // 이 부분은 따로 테이블 내에 필드가 없어도 카운트만으로도 가능할 것 같다.
    //public TextView search_bReviewNum;

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);

      parentLayout = itemView.findViewById(R.id.parentLay);
      search_bLogo = itemView.findViewById(R.id.search_bLogo);
      search_bName = itemView.findViewById(R.id.search_bName);
      search_bAddr = itemView.findViewById(R.id.search_bAddr);
      search_bSRateAvg = itemView.findViewById(R.id.search_bSRateAvg);
      //search_bReviewNum = itemView.findViewById(R.id.search_bReviewNum);
    }

    public void setItem(BusinessDTO busiDTO) {
      search_bName.setText(busiDTO.getBusiness_name());
      search_bAddr.setText(busiDTO.getBusiness_addr());
      String bRateAvg = String.format("%.2f", busiDTO.getBusiness_star_avg()/20);
      Log.d(TAG, "setItem: " + bRateAvg);
      search_bSRateAvg.setText(bRateAvg);

      //Glide.with(itemView).load(busiDTO.getBusiness_image()).into(search_bLogo);
    }
  }
}
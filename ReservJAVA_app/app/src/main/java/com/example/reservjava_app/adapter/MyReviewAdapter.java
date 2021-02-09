package com.example.reservjava_app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.reservjava_app.DTO.ReviewDTO;
import com.example.reservjava_app.R;

import java.util.ArrayList;

import static com.example.reservjava_app.ui.f_profile.ProfileActivity.reviewSetItem;

public class MyReviewAdapter extends
    RecyclerView.Adapter<MyReviewAdapter.ItemViewHolder> {
  private static final String TAG = "main:MyReviewAdapter";

  Context mContext;
  ArrayList<ReviewDTO> reviewDTOS;
  LinearLayout parentLayout;

  public MyReviewAdapter(Context mContext, ArrayList<ReviewDTO> reviewDTOS) {
    this.mContext = mContext;
    this.reviewDTOS = reviewDTOS;
  }

  //화면 연결
  @NonNull
  @Override
  public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View itemView = inflater.inflate(R.layout.review_view, parent, false);

    return new ItemViewHolder(itemView);
  }

  //데이터 연결
  @Override
  public void onBindViewHolder(@NonNull MyReviewAdapter.ItemViewHolder holder, final int position) {
    Log.d(TAG, "onBindViewHolder: " + position);

    final ReviewDTO reviewDTO = reviewDTOS.get(position);
    holder.setItem(reviewDTO);

    holder.parentLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //우선 데이터 가져와서 작업하자.
        Toast.makeText(mContext, reviewDTOS.get(position).getBooking_business_code() + "가 클릭되었습니다", Toast.LENGTH_SHORT).show();
      }
    });
  }

  @Override
  public int getItemCount() {
    return reviewDTOS.size();
  }

  //어댑터에 메소드 만들기
  //내용 추가하기
  public void addItem(ReviewDTO reviewDTO){
    reviewDTOS.add(reviewDTO);
  }

  //리사이클러뷰 내용 모두 지우기
  public void removeAllItem() { reviewDTOS.clear();}

  // 특정 인덱스 항목 가져오기
  public ReviewDTO getItem(int position) { return reviewDTOS.get(position);}

  //특정 인덱스 항목 세팅하기
  public void setItem(int position, ReviewDTO reviewDTO) {reviewDTOS.set(position, reviewDTO);}

  //ArrayList 통째로 세팅하기
  public void setItems(ArrayList<ReviewDTO> reviewDTOS) { this.reviewDTOS = reviewDTOS;}

  public static class  ItemViewHolder extends  RecyclerView.ViewHolder {

    public LinearLayout parentLayout;
    public ImageView review_category;
    public TextView review_name;
    public RatingBar review_ratingBar;
    public TextView review_contents;

    public ItemViewHolder(@NonNull View itemView) {
      super(itemView);

      parentLayout = itemView.findViewById(R.id.parentLay);
      //카테고리 로고 넣을 곳
      review_category = itemView.findViewById(R.id.review_category);
      review_name = itemView.findViewById(R.id.review_name);
      review_ratingBar = itemView.findViewById(R.id.review_ratingBar);
      review_contents = itemView.findViewById(R.id.review_contents);
    }

    public void setItem(ReviewDTO reviewDTO) {

      //임시
      review_category.setImageResource(R.drawable.fitness);
      review_name.setText(reviewDTO.getBusiness_name());
      review_ratingBar.setRating(reviewDTO.getBooking_appraisal_star());
      review_contents.setText(reviewDTO.getBooking_appraisal());
    }
  }
}

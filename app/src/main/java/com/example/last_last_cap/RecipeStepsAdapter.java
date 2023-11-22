//package com.example.last_last_cap;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//
//public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {
//    private List<String> recipeSteps;
//
//    public RecipeStepsAdapter(List<String> recipeSteps) {
//        this.recipeSteps = recipeSteps;
//    }
//
//    @NonNull
//    @Override
//    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecipeStepsAdapter.ViewHolder holder, int position) {
//        holder.textView.setText(recipeSteps.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return recipeSteps.size();
//    }
//
//    public static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView textView;
//
//        public ViewHolder(View view) {
//            super(view);
//            textView = view.findViewById(android.R.id.text1);
//        }
//    }
//}


package com.example.last_last_cap;

import static com.example.last_last_cap.datePickerActivity.context;

import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecipeStepsAdapter extends RecyclerView.Adapter<RecipeStepsAdapter.ViewHolder> {
    private List<RecipeStep> recipeSteps;

    public RecipeStepsAdapter(List<RecipeStep> recipeSteps) {
        this.recipeSteps = recipeSteps;
    }

    @NonNull
    @Override
    public RecipeStepsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 여기서는 레시피 단계를 표시할 레이아웃을 인플레이트합니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_step_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeStepsAdapter.ViewHolder holder, int position) {
        RecipeStep step = recipeSteps.get(position);
        holder.textView.setText(step.getDescription());
        Glide.with(holder.itemView.getContext()).load(step.getImageUrl()).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.stepTextView); // 수정된 레이아웃에 맞는 ID로 변경
            imageView = view.findViewById(R.id.stepImageView); // 추가된 이미지뷰
        }
    }
}

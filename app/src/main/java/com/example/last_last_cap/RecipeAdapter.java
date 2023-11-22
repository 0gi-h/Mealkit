package com.example.last_last_cap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<RecipeData> recipeList;
    private RecipeFragment recipeFragment;
    private RecipeClickListener listener;

    public RecipeAdapter(List<RecipeData> recipeList, RecipeClickListener listener) {
        this.recipeList = recipeList;
        this.listener = listener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        RecipeData recipe = recipeList.get(position);
        holder.recipeTitleTextView.setText(recipe.getTitle());
        // 이미지 로딩 라이브러리를 사용하여 이미지를 로드합니다. 예: Glide
        Glide.with(holder.itemView.getContext()).load(recipe.getImageUrl()).into(holder.recipeImageView);

        // 클릭 리스너 추가
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    String detailUrl = recipe.getDetailUrl();
                    listener.onRecipeClicked(detailUrl);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImageView;
        TextView recipeTitleTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            recipeImageView = itemView.findViewById(R.id.recipeImageView);
            recipeTitleTextView = itemView.findViewById(R.id.recipeTitleTextView);
        }
    }
}

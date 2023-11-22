package com.example.last_last_cap;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AllergyAdapter extends RecyclerView.Adapter<AllergyAdapter.ViewHolder> {

    private List<String> allergies;
    private OnItemClickListener listener;

    public AllergyAdapter(List<String> allergies, OnItemClickListener listener) {
        this.allergies = allergies;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allergy_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String allergy = allergies.get(position);
        holder.bind(allergy);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int adapterPosition = holder.getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        listener.onItemClick(adapterPosition);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return allergies.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView allergyTextView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            allergyTextView = itemView.findViewById(R.id.text_view_allergy);
            itemView.setOnClickListener(this);
            Typeface font = ResourcesCompat.getFont(itemView.getContext(), R.font.cookie_r); // your_font은 사용하려는 폰트 파일 이름
            allergyTextView.setTypeface(font);
        }

        void bind(String allergy) {
            allergyTextView.setText(allergy);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}

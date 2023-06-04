package com.example.last_last_cap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

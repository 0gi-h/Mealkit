package com.example.last_last_cap;

import static com.example.last_last_cap.datePickerActivity.context;

import android.app.NotificationManager;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;
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
        Glide.with(holder.itemView.getContext()).load(step.getImageUrl()).override(1500, 900).into(holder.imageView);
// 타이머 초기화
        setupTimer(holder, step.getCookTimeInSeconds());
    }


    private void setupTimer(RecipeStepsAdapter.ViewHolder holder, int cookTimeInSeconds) {

        class TimerState {
            long remainingTime;
            boolean isRunning;

            TimerState(long remainingTime, boolean isRunning) {
                this.remainingTime = remainingTime;
                this.isRunning = isRunning;
            }

            void updateRemainingTime(long newRemainingTime) {
                this.remainingTime = newRemainingTime;
            }

            void setIsRunning(boolean isRunning) {
                this.isRunning = isRunning;
            }
        }

        TimerState timerState = new TimerState(cookTimeInSeconds * 1000, false);
        AtomicReference<CountDownTimer> timerRef = new AtomicReference<>(null);

        CountDownTimer timer = new CountDownTimer(timerState.remainingTime, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                timerState.updateRemainingTime(millisUntilFinished);
                holder.textViewTimer.setText(String.format(Locale.getDefault(), "%d 초", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                holder.textViewTimer.setText("0 초");
                holder.startPauseButton.setText("시작");
                playAlarmSound(holder.itemView.getContext());

                timerState.setIsRunning(false);
            }
        };
        timerRef.set(timer);

        holder.startPauseButton.setOnClickListener(v -> {
            if (timerState.isRunning) {
                timerRef.get().cancel();
                holder.startPauseButton.setText("시작");
            } else {
                timerRef.set(new CountDownTimer(timerState.remainingTime, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timerState.updateRemainingTime(millisUntilFinished);
                        holder.textViewTimer.setText(String.format(Locale.getDefault(), "%d 초", millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        playAlarmSound(holder.itemView.getContext());
                        holder.textViewTimer.setText("0 초");
                        holder.startPauseButton.setText("시작");
                        timerState.setIsRunning(false);

                    }
                }.start());

                holder.startPauseButton.setText("일시정지");
            }
            timerState.setIsRunning(!timerState.isRunning);
        });

        holder.reset.setOnClickListener(v -> {
            // 타이머를 처음 받아온 시간 값으로 재설정
            if (!timerState.isRunning) {
                // 타이머를 처음 받아온 시간 값으로 재설정
                timerState.updateRemainingTime(cookTimeInSeconds * 1000);
                setupTimer(holder, cookTimeInSeconds);
                holder.textViewTimer.setText(String.format(Locale.getDefault(), "%d 초", cookTimeInSeconds));
            }
        });
        if (cookTimeInSeconds <= 0) {
            holder.textViewTimer.setVisibility(View.GONE);
            holder.startPauseButton.setVisibility(View.GONE);
            holder.reset.setVisibility(View.GONE);
        } else {
            // Enable the reset button only when the timer is stopped
            holder.reset.setVisibility(View.VISIBLE);
        }
    }
    private void playAlarmSound(Context context) {
        // MediaPlayer를 사용하여 알람 소리 재생
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.media);
        mediaPlayer.start();

        // MediaPlayer 리소스 해제
        mediaPlayer.setOnCompletionListener(mp -> {
            mp.release();
        });
    }

    @Override
    public int getItemCount() {
        return recipeSteps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        TextView textViewTimer; // 추가된 타이머 텍스트뷰
        Button startPauseButton; // 재생/일시정지 버튼
        Button reset;

        public ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.stepTextView);
            imageView = view.findViewById(R.id.stepImageView);
            textViewTimer = view.findViewById(R.id.textViewTimer); // 타이머 텍스트뷰 초기화
            startPauseButton = view.findViewById(R.id.startPauseButton); // 버튼 초기화
            reset = view.findViewById(R.id.reset);
        }
    }
}
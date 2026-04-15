package com.example.automation.overlay;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.automation.service.AutomationCommandDispatcher;
import com.example.automation.service.AutomationCommands;

public class FloatingControllerService extends Service {
    private WindowManager windowManager;
    private View floatingView;
    private LinearLayout controlsContainer;
    private boolean expanded = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        showOverlay();
    }

    private void showOverlay() {
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        floatingView = createFloatingLayout();

        int type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                : WindowManager.LayoutParams.TYPE_PHONE;

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                type,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 30;
        params.y = 220;

        floatingView.setOnTouchListener(new DragTouchListener(params));
        windowManager.addView(floatingView, params);
    }

    private View createFloatingLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
        root.setPadding(12, 12, 12, 12);
        root.setAlpha(0.93f);

        TextView header = new TextView(this);
        header.setText("⚙ Bot Control");
        header.setTextSize(14);
        header.setPadding(8, 4, 8, 10);

        controlsContainer = new LinearLayout(this);
        controlsContainer.setOrientation(LinearLayout.HORIZONTAL);

        ImageButton start = createIconButton(android.R.drawable.ic_media_play);
        ImageButton pause = createIconButton(android.R.drawable.ic_media_pause);
        ImageButton resume = createIconButton(android.R.drawable.ic_media_ff);
        ImageButton stop = createIconButton(android.R.drawable.ic_menu_close_clear_cancel);
        ImageButton record = createIconButton(android.R.drawable.presence_video_online);
        ImageButton hide = createIconButton(android.R.drawable.arrow_up_float);

        start.setOnClickListener(v -> sendAction(AutomationCommands.ACTION_START));
        pause.setOnClickListener(v -> sendAction(AutomationCommands.ACTION_PAUSE));
        resume.setOnClickListener(v -> sendAction(AutomationCommands.ACTION_RESUME));
        stop.setOnClickListener(v -> sendAction(AutomationCommands.ACTION_STOP));
        record.setOnClickListener(v -> toggleRecord(record));
        hide.setOnClickListener(v -> toggleExpanded());
        hide.setOnLongClickListener(v -> {
            stopSelf();
            return true;
        });

        controlsContainer.addView(start);
        controlsContainer.addView(pause);
        controlsContainer.addView(resume);
        controlsContainer.addView(stop);
        controlsContainer.addView(record);
        controlsContainer.addView(hide);

        root.addView(header);
        root.addView(controlsContainer);
        return root;
    }

    private ImageButton createIconButton(int resId) {
        ImageButton button = new ImageButton(this);
        button.setImageResource(resId);
        button.setBackgroundColor(0x00000000);
        return button;
    }

    private void toggleRecord(ImageButton record) {
        if (record.isSelected()) {
            sendAction(AutomationCommands.ACTION_RECORD_STOP);
            record.setSelected(false);
            record.setAlpha(1f);
        } else {
            sendAction(AutomationCommands.ACTION_RECORD_START);
            record.setSelected(true);
            record.setAlpha(0.5f);
        }
    }

    private void toggleExpanded() {
        expanded = !expanded;
        controlsContainer.setVisibility(expanded ? View.VISIBLE : View.GONE);
    }

    private void sendAction(String action) {
        AutomationCommandDispatcher.dispatch(this, action);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (floatingView != null && windowManager != null) {
            windowManager.removeView(floatingView);
        }
    }

    private class DragTouchListener implements View.OnTouchListener {
        private final WindowManager.LayoutParams params;
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;
        private long downTime;

        DragTouchListener(WindowManager.LayoutParams params) {
            this.params = params;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    downTime = System.currentTimeMillis();
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return false;
                case MotionEvent.ACTION_MOVE:
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(floatingView, params);
                    return true;
                case MotionEvent.ACTION_UP:
                    snapToEdge(params);
                    if (System.currentTimeMillis() - downTime > 700) {
                        toggleExpanded();
                        return true;
                    }
                    return false;
                default:
                    return false;
            }
        }

        private void snapToEdge(WindowManager.LayoutParams params) {
            int width = getResources().getDisplayMetrics().widthPixels;
            params.x = params.x < width / 2 ? 0 : width - floatingView.getWidth();
            windowManager.updateViewLayout(floatingView, params);
        }
    }
}

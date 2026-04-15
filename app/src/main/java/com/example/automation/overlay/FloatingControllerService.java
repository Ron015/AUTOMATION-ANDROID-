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

import androidx.annotation.Nullable;

import com.example.automation.service.AutomationAccessibilityService;

public class FloatingControllerService extends Service {
    private WindowManager windowManager;
    private View floatingView;

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
        params.x = 50;
        params.y = 200;

        floatingView.setOnTouchListener(new DragTouchListener(params));
        windowManager.addView(floatingView, params);

        ImageButton start = floatingView.findViewById(1001);
        ImageButton pause = floatingView.findViewById(1002);
        ImageButton stop = floatingView.findViewById(1003);

        start.setOnClickListener(v -> sendAction(AutomationAccessibilityService.ACTION_START));
        pause.setOnClickListener(v -> sendAction(AutomationAccessibilityService.ACTION_PAUSE));
        stop.setOnClickListener(v -> sendAction(AutomationAccessibilityService.ACTION_STOP));
    }

    private View createFloatingLayout() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);

        ImageButton start = new ImageButton(this);
        start.setId(1001);
        start.setImageResource(android.R.drawable.ic_media_play);

        ImageButton pause = new ImageButton(this);
        pause.setId(1002);
        pause.setImageResource(android.R.drawable.ic_media_pause);

        ImageButton stop = new ImageButton(this);
        stop.setId(1003);
        stop.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);

        root.addView(start);
        root.addView(pause);
        root.addView(stop);
        return root;
    }

    private void sendAction(String action) {
        Intent intent = new Intent(this, AutomationAccessibilityService.class);
        intent.setAction(action);
        startService(intent);
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

        DragTouchListener(WindowManager.LayoutParams params) {
            this.params = params;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
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
                default:
                    return false;
            }
        }
    }
}

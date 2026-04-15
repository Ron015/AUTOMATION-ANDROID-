package com.example.automation.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.automation.R;
import com.example.automation.overlay.FloatingControllerService;
import com.example.automation.service.AutomationAccessibilityService;
import com.example.automation.service.AutomationCommandDispatcher;
import com.example.automation.service.AutomationCommands;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class MainActivity extends AppCompatActivity {
    private TextView txtStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);

        findViewById(R.id.btnAccessibility).setOnClickListener(v -> openAccessibilitySettings());
        findViewById(R.id.btnEditor).setOnClickListener(v -> startActivity(new Intent(this, ScriptEditorActivity.class)));
        findViewById(R.id.btnOverlay).setOnClickListener(v -> startOverlay());

        findViewById(R.id.btnStart).setOnClickListener(v -> sendAction(AutomationCommands.ACTION_START));
        findViewById(R.id.btnPause).setOnClickListener(v -> sendAction(AutomationCommands.ACTION_PAUSE));
        findViewById(R.id.btnStop).setOnClickListener(v -> sendAction(AutomationCommands.ACTION_STOP));

        SwitchMaterial record = findViewById(R.id.switchRecord);
        record.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) {
                sendAction(AutomationCommands.ACTION_RECORD_START);
            } else {
                sendAction(AutomationCommands.ACTION_RECORD_STOP);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean enabled = AutomationAccessibilityService.getInstance() != null;
        txtStatus.setText(enabled ? "Status: Service connected" : "Status: Service not connected");
        txtStatus.setTextColor(enabled ? Color.parseColor("#69F0AE") : Color.parseColor("#FF8A80"));
    }

    private void openAccessibilitySettings() {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
        Toast.makeText(this, "Enable Automation Android accessibility service", Toast.LENGTH_LONG).show();
    }

    private void startOverlay() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return;
        }
        startService(new Intent(this, FloatingControllerService.class));
    }

    private void sendAction(String action) {
        AutomationCommandDispatcher.dispatch(this, action);
        Toast.makeText(this, "Action: " + action.substring(action.lastIndexOf('.') + 1), Toast.LENGTH_SHORT).show();
    }
}

package com.example.automation.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.automation.R;
import com.example.automation.overlay.FloatingControllerService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnAccessibility).setOnClickListener(v -> openAccessibilitySettings());
        findViewById(R.id.btnEditor).setOnClickListener(v -> startActivity(new Intent(this, ScriptEditorActivity.class)));
        findViewById(R.id.btnOverlay).setOnClickListener(v -> startOverlay());
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
}

package com.example.automation.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automation.R;
import com.example.automation.util.ScriptRepository;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ScriptEditorActivity extends AppCompatActivity {
    private final List<StepItem> stepItems = new ArrayList<>();
    private StepAdapter adapter;
    private TextInputEditText editJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_script_editor);

        editJson = findViewById(R.id.editJson);
        RecyclerView recyclerView = findViewById(R.id.recyclerSteps);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StepAdapter(stepItems);
        recyclerView.setAdapter(adapter);

        loadSavedScript();

        findViewById(R.id.btnAddStep).setOnClickListener(v -> {
            stepItems.add(new StepItem("click", "", ""));
            adapter.notifyItemInserted(stepItems.size() - 1);
            regenerateJson();
        });

        findViewById(R.id.btnSaveJson).setOnClickListener(v -> saveCurrentJson());
    }

    private void loadSavedScript() {
        String json = ScriptRepository.loadScript(this);
        editJson.setText(json);
        try {
            JSONObject root = new JSONObject(json);
            JSONArray steps = root.optJSONArray("steps");
            if (steps == null) {
                return;
            }
            for (int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                stepItems.add(new StepItem(
                        step.optString("action"),
                        step.optString("id", step.optString("text")),
                        step.optString("value")
                ));
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException ignored) {
        }
    }

    private void saveCurrentJson() {
        regenerateJson();
        String json = String.valueOf(editJson.getText());
        ScriptRepository.saveScript(this, json);
        Toast.makeText(this, "Script saved", Toast.LENGTH_SHORT).show();
    }

    private void regenerateJson() {
        JSONObject root = new JSONObject();
        JSONArray steps = new JSONArray();
        try {
            for (StepItem item : stepItems) {
                JSONObject step = new JSONObject();
                step.put("action", item.action);
                if (item.selector != null && !item.selector.isEmpty()) {
                    if (item.selector.contains(":id/")) {
                        step.put("id", item.selector);
                    } else {
                        step.put("text", item.selector);
                    }
                }
                if (item.value != null && !item.value.isEmpty()) {
                    step.put("value", item.value);
                }
                step.put("delay_before", 300);
                step.put("delay_after", 300);
                steps.put(step);
            }
            root.put("steps", steps);
            editJson.setText(root.toString(2));
        } catch (JSONException ignored) {
        }
    }
}

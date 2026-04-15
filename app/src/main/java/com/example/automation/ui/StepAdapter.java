package com.example.automation.ui;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.automation.R;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {
    private final List<StepItem> items;

    public StepAdapter(List<StepItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step, parent, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
        StepItem item = items.get(position);
        holder.action.setText(item.action);
        holder.selector.setText(item.selector);
        holder.value.setText(item.value);

        holder.bindWatcher(holder.action, text -> item.action = text);
        holder.bindWatcher(holder.selector, text -> item.selector = text);
        holder.bindWatcher(holder.value, text -> item.value = text);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StepViewHolder extends RecyclerView.ViewHolder {
        EditText action;
        EditText selector;
        EditText value;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            action = itemView.findViewById(R.id.editAction);
            selector = itemView.findViewById(R.id.editSelector);
            value = itemView.findViewById(R.id.editValue);
        }

        public void bindWatcher(EditText editText, OnTextChanged listener) {
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listener.onChanged(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }
    }

    interface OnTextChanged {
        void onChanged(String text);
    }
}

package com.example.automation.selector;

import android.graphics.Rect;
import android.text.TextUtils;
import android.view.accessibility.AccessibilityNodeInfo;

import com.example.automation.model.SelectorSpec;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Pattern;

public class SelectorEngine {

    public AccessibilityNodeInfo findNode(AccessibilityNodeInfo root, SelectorSpec primary, List<SelectorSpec> fallback, List<SelectorSpec> path) {
        if (root == null) {
            return null;
        }
        if (path != null && !path.isEmpty()) {
            AccessibilityNodeInfo pathNode = findByPath(root, path, 0);
            if (pathNode != null) {
                return pathNode;
            }
        }
        AccessibilityNodeInfo primaryNode = findFirst(root, primary);
        if (primaryNode != null) {
            return primaryNode;
        }
        if (fallback != null) {
            for (SelectorSpec spec : fallback) {
                AccessibilityNodeInfo node = findFirst(root, spec);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }

    public AccessibilityNodeInfo findFirst(AccessibilityNodeInfo root, SelectorSpec selector) {
        if (root == null || selector == null) {
            return null;
        }

        Queue<AccessibilityNodeInfo> queue = new ArrayDeque<>();
        queue.add(root);
        List<AccessibilityNodeInfo> matches = new ArrayList<>();

        while (!queue.isEmpty()) {
            AccessibilityNodeInfo node = queue.poll();
            if (node == null) {
                continue;
            }
            if (matches(node, selector)) {
                matches.add(node);
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                AccessibilityNodeInfo child = node.getChild(i);
                if (child != null) {
                    queue.offer(child);
                }
            }
        }

        if (matches.isEmpty()) {
            return null;
        }
        if (selector.index != null && selector.index >= 0 && selector.index < matches.size()) {
            return matches.get(selector.index);
        }
        return matches.get(0);
    }

    private AccessibilityNodeInfo findByPath(AccessibilityNodeInfo node, List<SelectorSpec> path, int depth) {
        if (node == null) {
            return null;
        }
        SelectorSpec current = path.get(depth);
        List<AccessibilityNodeInfo> children = new ArrayList<>();
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null && matches(child, current)) {
                children.add(child);
            }
        }
        if (children.isEmpty()) {
            return null;
        }
        int targetIndex = current.index != null ? current.index : 0;
        if (targetIndex >= children.size()) {
            return null;
        }
        AccessibilityNodeInfo next = children.get(targetIndex);
        if (depth == path.size() - 1) {
            return next;
        }
        return findByPath(next, path, depth + 1);
    }

    private boolean matches(AccessibilityNodeInfo node, SelectorSpec selector) {
        if (selector == null) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.id) && !selector.id.equals(node.getViewIdResourceName())) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.text) && !selector.text.contentEquals(node.getText())) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.desc) && !selector.desc.contentEquals(node.getContentDescription())) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.className) && !selector.className.contentEquals(node.getClassName())) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.packageName) && !selector.packageName.contentEquals(node.getPackageName())) {
            return false;
        }
        if (!TextUtils.isEmpty(selector.regex)) {
            String aggregate = String.format("%s|%s|%s",
                    node.getText(), node.getContentDescription(), node.getViewIdResourceName());
            if (!Pattern.compile(selector.regex).matcher(aggregate).find()) {
                return false;
            }
        }
        if (selector.bounds != null) {
            Rect rect = new Rect();
            node.getBoundsInScreen(rect);
            if (!rect.equals(selector.bounds)) {
                return false;
            }
        }
        return true;
    }
}

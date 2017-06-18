package com.sebatmedikal.activity;

import android.view.View;

import com.sebatmedikal.util.LogUtil;

import java.util.ArrayList;

/**
 * Created by orhan on 18.06.2017.
 */
public class ViewQueue {
    private int currentIndex = 0;
    private ArrayList<View> views = new ArrayList<>();

    public void addView(View view) {
        views.add(view);
        currentIndex++;
    }

    public View getPreviousView() {
        if (currentIndex <= 0) {
            return null;
        }

        currentIndex--;
        return views.get(currentIndex);
    }

    public View getNexView() {
        if (currentIndex >= (views.size() - 1)) {
            return null;
        }

        currentIndex++;
        return views.get(currentIndex);
    }
}

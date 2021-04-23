/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.permissioncontroller.permission.ui.handheld;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Encapsulates a {@link CompositeCircleView}, labeling each of its colored partial circles.
 */
public class CompositeCircleViewLabeler extends RelativeLayout {

    private int mCircleId;
    private TextView mCenterLabel;
    private TextView[] mLabels;
    private float mLabelRadiusScalar;

    public CompositeCircleViewLabeler(@NonNull Context context) {
        super(context);
    }

    public CompositeCircleViewLabeler(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CompositeCircleViewLabeler(@NonNull Context context, @Nullable AttributeSet attrs,
                                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CompositeCircleViewLabeler(@NonNull Context context, @Nullable AttributeSet attrs,
                                      int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Sets labels to surround the contained {@link CompositeCircleView} with, and the radius
     * scalar to place them at.
     *
     * @param circleId view ID of the circle being labeled
     * @param centerLabel the center label
     * @param labels labels labels to position next to each circle value segment
     * @param labelRadiusScalar scalar to multiply the contained circle radius by to get the
     *                          radius at which we want to show labels
     */
    public void configure(int circleId, TextView centerLabel, TextView[] labels,
                          float labelRadiusScalar) {
        // Remove previous text content first.
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i) instanceof TextView) {
                removeViewAt(i);
            }
        }
        mCircleId = circleId;
        mCenterLabel = centerLabel;
        addView(centerLabel);
        mLabels = labels;
        for (int i = 0; i < labels.length; i++) {
            addView(labels[i]);
        }
        mLabelRadiusScalar = labelRadiusScalar;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // Gather CCV geometry.
        CompositeCircleView ccv = findViewById(mCircleId);
        int ccvWidth = ccv.getWidth();
        int ccvHeight = ccv.getHeight();
        float ccvCenterX = ccv.getX() + (ccvWidth * 0.5f);
        float ccvCenterY = ccv.getY() + (ccvHeight * 0.5f);
        float ccvRadius = Math.max(ccvWidth, ccvHeight) * 0.5f;
        float labelRadius = ccvRadius * mLabelRadiusScalar;

        // Position center label.
        mCenterLabel.setX(ccvCenterX - (mCenterLabel.getWidth() * 0.5f));
        mCenterLabel.setY(ccvCenterY - (mCenterLabel.getHeight() * 0.5f));

        // For each provided label, determine position angle.
        for (int i = 0; i < mLabels.length; i++) {
            TextView label = mLabels[i];
            // For circle path, top angle is 270d. Convert to unit circle rads.
            double angle = Math.toRadians(360 - ccv.getPartialCircleCenterAngle(i));
            double x = ccvCenterX + (Math.cos(angle) * labelRadius);
            double y = ccvCenterY - (Math.sin(angle) * labelRadius);

            // Determine anchor corner for text, adjust accordingly.
            if (angle < (Math.PI * 0.5d)) {
                y -= label.getHeight();
            } else if (angle < Math.PI) {
                x -= label.getWidth();
                y -= label.getHeight();
            } else if (angle < (Math.PI * 1.5d)) {
                x -= label.getWidth();
            }
            label.setX((float) x);
            label.setY((float) y);
        }
    }
}


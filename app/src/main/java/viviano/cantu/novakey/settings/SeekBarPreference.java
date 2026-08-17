/*
 * NovaKey - An alternative touchscreen input method
 * Copyright (C) 2019  Viviano Cantu
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 *
 * Any questions about the program or source may be directed to <strellastudios@gmail.com>
 */

package viviano.cantu.novakey.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import viviano.cantu.novakey.R;

/**
 * A {@link DialogPreference} backed by a {@link SeekBar}.
 *
 * <p>Drop-in replacement for the former
 * {@code com.pavelsikun:material-seekbar-preference} dependency. Supports the
 * same attributes ({@code msbp_minValue}, {@code msbp_maxValue},
 * {@code msbp_interval}, {@code msbp_measurementUnit}) and persists the chosen
 * value as an integer under the preference key.</p>
 */
public class SeekBarPreference extends DialogPreference
        implements SeekBar.OnSeekBarChangeListener {

    private int mMinValue = 0;
    private int mMaxValue = 100;
    private int mInterval = 1;
    private String mMeasurementUnit = "";

    private int mValue = mMinValue;
    private TextView mValueView;
    private SeekBar mSeekBar;

    public SeekBarPreference(Context context) {
        this(context, null);
    }

    public SeekBarPreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekBarPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // DialogPreference does not default its button labels; without these the
        // dialog would be shown with no OK/Cancel buttons at all.
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekBarPreference);
        mMinValue = a.getInt(R.styleable.SeekBarPreference_msbp_minValue, mMinValue);
        mMaxValue = a.getInt(R.styleable.SeekBarPreference_msbp_maxValue, mMaxValue);
        mInterval = a.getInt(R.styleable.SeekBarPreference_msbp_interval, mInterval);
        String unit = a.getString(R.styleable.SeekBarPreference_msbp_measurementUnit);
        if (unit != null)
            mMeasurementUnit = unit;
        a.recycle();

        if (mInterval <= 0)
            mInterval = 1;
        if (mMaxValue < mMinValue)
            mMaxValue = mMinValue;
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            mValue = getPersistedInt(mMinValue);
        } else {
            int value = (defaultValue == null) ? mMinValue : (Integer) defaultValue;
            mValue = Math.max(mMinValue, Math.min(mMaxValue, value));
        }
        updateSummary();
    }

    @Override
    protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 20, 50, 20);

        mValueView = new TextView(getContext());
        mValueView.setGravity(Gravity.CENTER_HORIZONTAL);
        mValueView.setTextSize(24);
        mValueView.setText(String.valueOf(mValue));
        layout.addView(mValueView);

        mSeekBar = new SeekBar(getContext());
        mSeekBar.setMax((mMaxValue - mMinValue) / mInterval);
        mSeekBar.setProgress((mValue - mMinValue) / mInterval);
        mSeekBar.setOnSeekBarChangeListener(this);
        layout.addView(mSeekBar);

        return layout;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult && shouldPersist()) {
            persistInt(mValue);
            updateSummary();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
            int newValue = mMinValue + (progress * mInterval);
            mValue = Math.max(mMinValue, Math.min(mMaxValue, newValue));
            if (mValueView != null)
                mValueView.setText(String.valueOf(mValue));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void updateSummary() {
        String unit = (mMeasurementUnit == null || mMeasurementUnit.isEmpty())
                ? "" : " " + mMeasurementUnit;
        setSummary(String.valueOf(mValue) + unit);
    }
}
package org.moss.prefs;

import org.moss.Common;

import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.view.View;
import android.util.AttributeSet;

public class ColorPickerPreference extends DialogPreference {

    public ColorPickerPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    void init() {
        setWidgetLayoutResource(org.moss.R.layout.pref_color_block);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        widget = (View) view.findViewById(org.moss.R.id.color_box);
        if (null != widget) {
            int color = getPersistedInt(0xFF000000);
            widget.setBackgroundColor(color);
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        int color = getPersistedInt(0xFF000000);
        final ColorPickerDialog dialog = new ColorPickerDialog(getContext(), ccListener, color);
        dialog.setOnDismissListener(this);
        dialog.show();
    }

    private ColorPickerView.OnColorChangedListener ccListener = new ColorPickerView.OnColorChangedListener() {
        public void colorChanged(int color, boolean selected) {
            if (!selected) {
                return;
            }
            if (widget != null) {
                widget.setBackgroundColor(color);
            }
            persistInt(color);

            String hex = String.format("#%x", color);
            setSummary(hex);
        }
    };

    private View widget;
}

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
            String color = getPersistedString("FF000000");
            int defColor = Common.hexToInt(color, 0x000000);
            widget.setBackgroundColor(defColor);
        }
    }

    @Override
    protected void showDialog(Bundle state) {
        String color = getPersistedString("FF000000");
        int defColor = Common.hexToInt(color, 0x000000);
        final ColorPickerDialog dialog = new ColorPickerDialog(getContext(), ccListener, defColor);
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
            String hex = String.format("#%x", color);
            setSummary(hex);
            persistString(hex);
        }
    };

    private View widget;
}

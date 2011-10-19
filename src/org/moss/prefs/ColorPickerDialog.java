/*
 * Copyright (C) 2007 The Android Open Source Project
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
package org.moss.prefs;

import org.moss.R;

import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;

public class ColorPickerDialog extends Dialog {

    private ColorPickerView.OnColorChangedListener mListener;
    private int mInitialColor;

    public ColorPickerDialog(Context context,
                             ColorPickerView.OnColorChangedListener listener,
                             int initialColor) {
        super(context);
        mListener = listener;
        mInitialColor = initialColor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dia_color_pick);

        ColorPickerView.OnColorChangedListener listener = new ColorPickerView.OnColorChangedListener() {
            public void colorChanged(int color, boolean selected) {
                mListener.colorChanged(color, selected);
                if (selected) {
                    dismiss();
                }
            }
        };

        ColorPickerView colorView = (ColorPickerView) findViewById(R.id.colorpicker_view);
        if (null != colorView) {
            colorView.setListener(listener);
            colorView.setColor(mInitialColor);
        }
    }
}

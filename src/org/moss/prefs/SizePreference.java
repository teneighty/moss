/*
 * ConnectBot: simple, powerful, open-source SSH client for Android
 * Copyright 2007 Kenny Root, Jeffrey Sharkey
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.moss.prefs;

import org.moss.Config;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author kenny
 *
 */
public class SizePreference extends DialogPreference implements OnSeekBarChangeListener {

    private TextView mText;
    private int mProgress;
	/**
	 * @param context
	 * @param attrs
	 */
	public SizePreference(Context context, AttributeSet attrs) {
		super(context, attrs);

		setupLayout(context, attrs);
	}

	public SizePreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		setupLayout(context, attrs);
	}

	private void setupLayout(Context context, AttributeSet attrs) {
        setPersistent(true);
	}

	@Override
	protected View onCreateDialogView() {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setLayoutParams(
                new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        mText = new TextView(getContext());
		mText.setPadding(10, 10, 10, 10);
        mText.setTextAppearance(getContext(), android.R.attr.textAppearanceLarge);
        layout.addView(mText);

        mProgress = (int) (getPersistedFloat(Config.CONF_FONT_SIZE_VALUE));
		SeekBar sb = new SeekBar(getContext());
		sb.setMax(MAX - MIN);
		sb.setProgress(mProgress - MIN);
		sb.setPadding(10, 10, 10, 10);
		sb.setOnSeekBarChangeListener(this);
        layout.addView(sb);

        mText.setText(String.valueOf(mProgress));

		return layout;
	}

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            persistFloat(mProgress);
        }
    }

	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.mProgress = adjustProgress(progress);
        if (mText != null) {
            mText.setText(String.valueOf(mProgress));
        }
	}

    private int adjustProgress(int progress) {
        return progress + MIN;
    }

	public void onStartTrackingTouch(SeekBar seekBar) { }

	public void onStopTrackingTouch(SeekBar seekBar) { }

    private final int MIN = 8;
    private final int MAX = 30;
}

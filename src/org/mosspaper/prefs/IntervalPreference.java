package org.mosspaper.prefs;

import org.mosspaper.Config;
import org.mosspaper.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import android.widget.EditText;

public class IntervalPreference extends DialogPreference {

    static final String TAG = "IntervalPreference";
    private EditText mText;
    private Spinner mSpinner;
    private int[] typeValues;
    private int intervalType;

	public IntervalPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout(context, attrs);
	}

	public IntervalPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupLayout(context, attrs);
	}

	private void setupLayout(Context context, AttributeSet attrs) {
        Resources r = getContext().getResources();
        typeValues = r.getIntArray(R.array.interval_values);

        setPersistent(true);
        setDialogLayoutResource(R.layout.dia_interval);
	}

	@Override
    protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        float val = getPersistedFloat(1.0f);
        int idx = 0; // seconds
        if (val >= 3600) {
            idx = 2; // hours
            val /= 3600.0f;
        } else if (val >= 60) {
            idx = 1;  // minutes
            val /= 60.0f;
        }

        mText = (EditText) view.findViewById(R.id.interval_string);
        mText.setText(String.valueOf(val));

        mSpinner = (Spinner) view.findViewById(R.id.interval_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.interval_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        mSpinner.setSelection(idx);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                intervalType = typeValues[pos];
            }

            public void onNothingSelected(AdapterView parent) {
            }

        });
        return view;
	}

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            try {
                float interval = Float.parseFloat(mText.getText().toString());
                persistFloat(interval * intervalType);
            } catch (NumberFormatException e) {
                android.util.Log.e(TAG, "", e);
            }
        }
    }
}

package org.moss.prefs;

import org.moss.Config;
import org.moss.R;

import android.content.Context;
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
    private EditText iString;
    private Spinner iSpinner;
    private String spinnerValue;

	public IntervalPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setupLayout(context, attrs);
	}

	public IntervalPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setupLayout(context, attrs);
	}

	private void setupLayout(Context context, AttributeSet attrs) {
        setPersistent(true);
        setDialogLayoutResource(R.layout.dia_interval);
	}

	@Override
	protected View onCreateDialogView() {
        View view = super.onCreateDialogView();

        float val = getPersistedFloat(1.0f);

        iString = (EditText) view.findViewById(R.id.interval_string);
        if (null != iString) {
            iString.setText(String.valueOf(val));
        }

        iSpinner = (Spinner) view.findViewById(R.id.interval_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(), R.array.interval_types, android.R.layout.simple_spinner_item);
        iSpinner.setAdapter(adapter);
        iSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(AdapterView parent, View view, int pos, long id) {
                spinnerValue = parent.getItemAtPosition(pos).toString();
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
                float interval = Float.parseFloat(iString.getText().toString());
                float type = Float.parseFloat(spinnerValue);

                persistFloat(interval * type);
            } catch (NumberFormatException e) {
                android.util.Log.e(TAG, "", e);
            }
        }
    }
}

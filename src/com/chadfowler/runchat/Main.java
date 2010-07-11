package com.chadfowler.runchat;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;

public class Main extends Activity {
	private static final String PHRASE_KEY_PREFIX = "phrases";
	private SmsManager smsManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smsManager = SmsManager.getDefault();
		TableLayout layout = new TableLayout(this);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		ArrayList<String> phraseKeys = getPhraseKeys(prefs);
		createPhraseButtonsFromPreferences(layout, prefs, phraseKeys);
		setContentView(layout);
	}

	private void createPhraseButtonsFromPreferences(LinearLayout layout,
			SharedPreferences prefs, ArrayList<String> phraseKeys) {
		for(String key : phraseKeys) {
			Button b = addButtonForKey(prefs, key);
			layout.addView(b);
		}
	}

	private Button addButtonForKey(SharedPreferences prefs, String key) {
		Button b = new Button(this);
		b.setText(prefs.getString(key, ""));
		b.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		b.setGravity(Gravity.CENTER);     
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				smsManager.sendTextMessage(getRecipientAddress(), null, ((Button)view).getText().toString(), null, null);
			}

		});
		return b;
	}

	private ArrayList<String> getPhraseKeys(SharedPreferences prefs) {
		Map<String, ?> allPrefs = prefs.getAll();
		if(allPrefs.size() == 0) {
			setupDefaultPhraseKeys(prefs);
			allPrefs = prefs.getAll();
		}
		Set<String> keys = allPrefs.keySet();
		ArrayList<String> matchingKeys = new ArrayList<String>();
		for(String key : keys) {
			if(key.startsWith(PHRASE_KEY_PREFIX)) {
				matchingKeys.add(key);
			}
		}
		return matchingKeys;
	}

	private void setupDefaultPhraseKeys(SharedPreferences prefs) {
		Editor editor = prefs.edit();
		editor.putString("phrases1", "Yes");
		editor.putString("phrases2", "No");
		editor.putString("phrases3", "1/2 way");
		editor.putString("phrases4", "Will call soon");
		editor.commit();
	}

	private String getRecipientAddress() {
		return "5556";
	}
}

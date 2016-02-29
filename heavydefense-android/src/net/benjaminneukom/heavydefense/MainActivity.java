package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.system.AppVersion;
import net.benjaminneukom.heavydefense.system.Tracking;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

public class MainActivity extends AndroidApplication {

	private static final String LOG_TAG = "HeavyDefense";

	private AndroidBilling billing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
		cfg.useGL20 = true;
		cfg.hideStatusBar = true;
		cfg.useAccelerometer = false;
		cfg.useCompass = false;

		billing = new AndroidBilling(this);
		final Tracking tracking = new AndroidAppTracking(this);
		final AppVersion appVersion = new AndroidVersion(this);

		initialize(new HeavyDefenseGame(billing, tracking, appVersion), cfg);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(LOG_TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if (!billing.getIabHelper().handleActivityResult(requestCode, resultCode, data)) {
			super.onActivityResult(requestCode, resultCode, data);
		}
		else {
			Log.d(LOG_TAG, "onActivityResult handled by IABUtil.");
		}
	}
}
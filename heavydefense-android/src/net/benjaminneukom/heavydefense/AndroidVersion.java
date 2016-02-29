package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.system.AppVersion;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;

public class AndroidVersion implements AppVersion {

	private Activity activity;

	public AndroidVersion(Activity activity) {
		this.activity = activity;
	}

	@Override
	public String getVersion() {
		try {
			return activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return "0";
	}

}

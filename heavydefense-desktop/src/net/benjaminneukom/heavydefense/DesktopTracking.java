package net.benjaminneukom.heavydefense;

import net.benjaminneukom.heavydefense.system.Tracking;

public class DesktopTracking implements Tracking {

	@Override
	public void sendEvent(String category, String action, String label, Long value) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void initialize() {
	}

	@Override
	public void sendTiming(String category, Long intervalInMilliseconds, String name, String label) {
	}

	public void sendScreen(String screenName) {
	}

}

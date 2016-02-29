package net.benjaminneukom.heavydefense.system;

public interface Tracking {

	public abstract void sendEvent(String category, String action, String label, Long value);

	public abstract void dispose();

	public abstract void initialize();

	public abstract void sendTiming(String category, Long intervalInMilliseconds, String name, String label);

	public abstract void sendScreen(String screenName);

}

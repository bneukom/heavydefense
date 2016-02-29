package net.benjaminneukom.heavydefense;

import java.io.PrintWriter;
import java.io.StringWriter;

import net.benjaminneukom.heavydefense.system.Tracking;
import android.app.Activity;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.ExceptionParser;
import com.google.analytics.tracking.android.ExceptionReporter;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public class AndroidAppTracking implements Tracking {

	private Activity activity;
	private EasyTracker tracker;

	public AndroidAppTracking(Activity activity) {
		this.activity = activity;
		this.tracker = EasyTracker.getInstance(activity);

		// Change uncaught exception parser.
		Thread.UncaughtExceptionHandler uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		if (uncaughtExceptionHandler instanceof ExceptionReporter) {
			ExceptionReporter exceptionReporter = (ExceptionReporter) uncaughtExceptionHandler;
			exceptionReporter.setExceptionParser(new AnalyticsExceptionParser());
		}
	}

	@Override
	public void initialize() {
	}

	@Override
	public void dispose() {
	}

	@Override
	public void sendScreen(String screenName) {
		tracker.set(Fields.SCREEN_NAME, screenName);

		tracker.send(MapBuilder.createAppView().build());
	}

	@Override
	public void sendEvent(String category, String action, String label, Long value) {
		tracker.send(MapBuilder.createEvent(category, action, label, value).build());
	}

	@Override
	public void sendTiming(String category, Long intervalInMilliseconds, String name, String label) {
		tracker.send(MapBuilder.createTiming(category, intervalInMilliseconds, name, label).build());
	}

	// From: http://dandar3.blogspot.ch/2013/03/google-analytics-easytracker-detailed.html
	public static class AnalyticsExceptionParser implements ExceptionParser {
		public String getDescription(String thread, Throwable throwable) {
			return "Thread: " + thread + ", Exception: " + getStackTrace(throwable);
		}
	}

	// From: http://commons.apache.org/proper/commons-lang/apidocs/src-html/org/apache/commons/lang3/exception/ExceptionUtils.html
	public static String getStackTrace(final Throwable throwable) {
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		throwable.printStackTrace(pw);
		return sw.getBuffer().toString();
	}
}

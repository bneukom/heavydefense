package net.benjaminneukom.heavydefense.system;

public interface Billing {
	public static final String DONATE_SMALL = "donate2";
	public static final String DONATE_MEDIUM = "donate5";
	public static final String DONATE_BIG = "donate10";

	public abstract void dispose();

	public abstract void initiateDonate(String donateSku);

	public abstract void initlaize();

	public abstract boolean isAvailable();
}

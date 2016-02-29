package net.benjaminneukom.heavydefense.util;

import java.text.NumberFormat;

public class StringUtil {

	public static String numberSepeartor(float number) {
		return numberSepeartor((int) number);

	}

	public static String numberSepeartor(int number) {
		return NumberFormat.getInstance().format(number);
	}

	public static void main(String[] args) {
		System.out.println(numberSepeartor(5000));
		System.out.println(numberSepeartor(3.4235f));
		System.out.println(numberSepeartor(450302434));
	}
}

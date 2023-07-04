package ru.m210projects.Build.Pattern.Tools;

import static ru.m210projects.Build.Strhandler.*;

public class NaturalComparator {

	public static int compare(String sequence1, String sequence2) {
		int len1 = sequence1.length(), len2 = sequence2.length();
		int idx1 = 0, idx2 = 0;

		while (idx1 < len1 && idx2 < len2) {
			char c1 = sequence1.charAt(idx1++);
			char c2 = sequence2.charAt(idx2++);

			boolean isDigit1 = isdigit(c1);
			boolean isDigit2 = isdigit(c2);

			if (isDigit1 && !isDigit2) {
				return -1;
			} else if (!isDigit1 && isDigit2) {
				return 1;
			} else if (!isDigit1 && !isDigit2) {
				int c = c1 - c2;
				if (c != 0) 
					return c;
			} else {
				long num1 = parse(c1);
				while (idx1 < len1) {
					char digit = sequence1.charAt(idx1++);
					if (isdigit(digit)) {
						num1 = num1 * 10 + parse(digit);
					} else {
						idx1--;
						break;
					}
				}

				long num2 = parse(c2);
				while (idx2 < len2) {
					char digit = sequence2.charAt(idx2++);
					if (isdigit(digit)) {
						num2 = num2 * 10 + parse(digit);
					} else {
						idx2--;
						break;
					}
				}

				if (num1 != num2) {
					return compareUnsigned(num1, num2);
				}
			}
		}

		if (idx1 < len1) {
			return 1;
		} else if (idx2 < len2) {
			return -1;
		} else {
			return 0;
		}
	}

	private static int compareUnsigned(long num1, long num2) {
		return compare(num1 + Long.MIN_VALUE, num2 + Long.MIN_VALUE);
	}

	private static int compare(long x, long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	private static long parse(char c1) {
		return c1 - '0';
	}
}

package com.hebangdata;

import java.util.ArrayList;
import java.util.List;

public class Utils {
	public static final String SPLITER = "\n";
	public static final byte[] SPLITER_BYTES = SPLITER.getBytes();

	public static List<String> split(final String line) {
		if (null == line) return null;

		// 语句拆分
		final List<String> lines = new ArrayList<>();

		// 循环每一个字符，拆出每一个完整的句子
		final StringBuilder builder = new StringBuilder();

		for (final Character chr : line.toCharArray()) {
			if (isSpliter(chr)) {
				if (0 < builder.length()) {
					final String sentence = builder.toString();

					// 只有首字为汉字或者英文才被添加
					if (isSentence(sentence))
						lines.add(sentence);

					builder.delete(0, builder.length());
				}
			} else {
				builder.append(chr);
			}
		}

		return lines;
	}
	/**
	 * 判断一个字符是不是非句内字符
	 * @param chr
	 * @return
	 */
	private static boolean isSpliter(final Character chr) {
		final Character.UnicodeScript ub = Character.UnicodeScript.of(chr);

		if (Character.UnicodeScript.HAN == ub || Character.UnicodeScript.LATIN == ub) // 判断是不是汉字
			return false;
		else if (0 <= chr.compareTo('0') && 0 >= chr.compareTo('9')) // 判断是不是数字
			return false;
		else if (chr.equals(' ') || chr.equals('、')) // 判断是不是空格及特别的符号
			return false;
		else
			return true;
	}

	/**
	 * 过滤只有空格和标点符号的无效行
	 * @param line
	 * @return
	 */
	private static boolean isSentence(final String line) {
		for (final Character chr : line.toCharArray()) {
			final Character.UnicodeScript ub = Character.UnicodeScript.of(chr);

			if (Character.UnicodeScript.HAN == ub || Character.UnicodeScript.LATIN == ub)
				return true;
		}

		return false;
	}
}

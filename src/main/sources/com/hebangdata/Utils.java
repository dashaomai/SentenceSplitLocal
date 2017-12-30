package com.hebangdata;

import java.util.List;

public class Utils {
	public static final String NORMAL_EXT = ".txt";
	public static final String ORIGIN_EXT = ".origin.txt";
	public static final String GROUPED_EXT = ".grouped.txt";

	public static void split(final String line, final List<String> lines) {
		if (null == line) return;

		lines.clear();

		final StringBuilder builder = new StringBuilder();

		// 循环每一个字符，拆出每一个完整的句子
		for (final Character chr : line.toCharArray()) {
			if (isSpliter(chr)) {
				if (0 < builder.length()) {
					final String sentence = builder.toString();

					// 只有首字为汉字或者英文才被添加
					if (isSentence(sentence))
						lines.add(sentence.trim());

//					builder.delete(0, builder.length());
					builder.setLength(0);
				}
			} else {
				builder.append(chr);
			}
		}
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
		if (line.length() > 1)
			for (final Character chr : line.toCharArray()) {
				final Character.UnicodeScript ub = Character.UnicodeScript.of(chr);

				if (Character.UnicodeScript.HAN == ub || Character.UnicodeScript.LATIN == ub)
					return true;
			}

		return false;
	}
}

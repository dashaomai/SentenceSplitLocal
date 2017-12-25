package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * 进行第一次初分的功能类，先读出所有文本，在内存中处理完毕再写入磁盘。
 *
 * 问题：内存占用太大
 */
public class FirstSplit {
	private static final Logger log = LoggerFactory.getLogger("FirstSplit");

	public static void process(final String url) throws IOException {
		final String inputUrl = url + Utils.NORMAL_EXT;
		final String outputUrl = url + Utils.ORIGIN_EXT;
		final String groupedUrl = url + Utils.GROUPED_EXT;

		// 先读取文件内容，并整理成为行的集合
		final List<String> orderedSentences = parseSentences(inputUrl);

		// 把未去重的结果写入文件内
		 writeOrigin(outputUrl, orderedSentences);

		// 把去重后的结果写入文件内
		 writeGrouped(groupedUrl, orderedSentences);
	}

	private static void writeGrouped(String groupedUrl, List<String> orderedSentences) throws IOException {
		final Set<String> groupedSentences = new HashSet<>();
		groupedSentences.addAll(orderedSentences);
		orderedSentences.clear();

		final long groupedBegin = System.currentTimeMillis();

		Files.write(Paths.get(groupedUrl), groupedSentences);

		final long groupedEnd = System.currentTimeMillis();

		log.info("写入去重文件：{}，{} 行，耗时：{} 秒", groupedUrl, groupedSentences.size(), (groupedEnd - groupedBegin) / 1000L);

		groupedSentences.clear();
	}

	private static void writeOrigin(String outputUrl, List<String> orderedSentences) throws IOException {
		final long outputBegin = System.currentTimeMillis();

		Files.write(Paths.get(outputUrl), orderedSentences);

		final long outputEnd = System.currentTimeMillis();

		log.info("写入有序文件：{}，{} 行，耗时：{} 秒", outputUrl, orderedSentences.size(), (outputEnd - outputBegin) / 1000L);
	}

	private static List<String> parseSentences(final String inputUrl) throws IOException {
		final long begin = System.currentTimeMillis();

		final List<String> orderedSentences = Collections.synchronizedList(new ArrayList<>());

		final Stream<String> inputStream = Files.lines(Paths.get(inputUrl), Charset.forName("UTF-8")).parallel();

		inputStream.forEach(line -> {
			final List<String> lines = new ArrayList<>();

			Utils.split(line, lines);

			orderedSentences.addAll(lines);
		});

		inputStream.close();

		final long end = System.currentTimeMillis();

		log.info("读取：{} 耗时：{} 秒，获得：{} 行文本", inputUrl, (end - begin) / 1000L, orderedSentences.size());
		return orderedSentences;
	}
}

package com.hebangdata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 进行第二次合并的功能类，读取指定的几个去重文件，去重后的结果，到指定的文件名当中
 */
public class SecondCombine {
	private static final Logger log = LoggerFactory.getLogger("SecondCombine");

	public static void process(final String url, final String ...paths) throws IOException {
		// 读出指定的文件列表，并在内存中去重
		final Collection<String> it = read(paths);

		// 将去重结果写回文件中
		write(url, it);
	}

	private static Collection<String> read(final String... paths) throws IOException {
		final Set<String> groupedSentences = Collections.synchronizedSet(new HashSet<>());

		final long begin = System.currentTimeMillis();

		for (final String path : paths) {
			final Stream<String> inputStream = Files.lines(Paths.get(path + Utils.GROUPED_EXT), Charset.forName("UTF-8")).parallel();

			inputStream.forEach(groupedSentences::add);

			inputStream.close();
		}

		final long end = System.currentTimeMillis();

		log.info("读取所有文件：{}，耗时：{} 秒", paths, (end - begin) / 1000L);

		return groupedSentences;
	}

	private static void write(final String url, final Collection<String> sentences) throws IOException {
		final long begin = System.currentTimeMillis();

		Files.write(Paths.get(url + Utils.GROUPED_EXT), sentences);

		final long end = System.currentTimeMillis();

		log.info("向文件：{} 写入 {} 行，耗时：{} 秒", url, sentences.size(), (end - begin) / 1000L);

		sentences.clear();
	}
}

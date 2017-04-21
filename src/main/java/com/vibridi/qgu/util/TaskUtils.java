package com.vibridi.qgu.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.vibridi.qgu.Main;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.widget.api.TaskTreeWalkerCallback;

public class TaskUtils {

	/**
	 * Computes the size of the whole tree, including the root node.
	 * 
	 * @param root
	 * @return number of nodes in the tree
	 */
	public static int treeSize(GanttTask root) {
		AtomicInteger acc = new AtomicInteger(0);
		walkDepthFirst(root, node -> { 
			acc.incrementAndGet(); 
		});
		return acc.get();
	}
		
	public static GanttTask readTaskTree(String file) {
		try {
			List<String> lines = Files.readAllLines(Paths.get(Main.class.getClass().getResource("/" + file).toURI()));

			if(lines.size() == 0 || !lines.get(0).equals("root"))
				throw new IllegalArgumentException("File is not a task tree");

			GanttTask root = new GanttTask("root");

			lines.remove(0);

			for(String line : lines) {
				int tab = line.lastIndexOf('\t');
				GanttTask parent = root;
				for(int i = 0; i < tab; i++)
					parent = parent.getChild(parent.size() - 1);
				parent.addChild(new GanttTask(line.trim()));
			}
			return root;
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void walkDepthFirst(GanttTask node, TaskTreeWalkerCallback callback) {

		callback.processNode(node);
		
		if(node.size() == 0)
			return;	
		IntStream.range(0, node.size())
			.forEach(i -> walkDepthFirst(node.getChild(i), callback));
	}
	
	public static String pathToString(int[] path) {
		return IntStream.of(path).mapToObj(Integer::toString).collect(Collectors.joining("."));
	}

	public static void printPath(int[] path) {
		IntStream.of(path).forEach(i -> System.out.print(i + "\t"));
		System.out.println();
	}
	
}

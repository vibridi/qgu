package com.vibridi.qgu.util;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Function;

import com.vibridi.qgu.Main;
import com.vibridi.qgu.model.GanttTask;

public class TaskUtils {

	public static GanttTask readTaskTree() {
		try {
			List<String> lines = Files.readAllLines(Paths.get(Main.class.getClass().getResource("/tasktree.txt").toURI()));

			if(lines.size() == 0 || !lines.get(0).equals("root"))
				throw new IllegalArgumentException("File is not a task tree");

			GanttTask root = new GanttTask("root");

			lines.remove(0);

			for(String line : lines) {
				int tab = line.lastIndexOf('\t');
				GanttTask parent = root;
				for(int i = 0; i < tab; i++)
					parent = parent.getChildren().get(parent.getChildren().size() - 1);
				parent.addChild(new GanttTask(line));
			}
			return root;
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void traverseDepthFirst(GanttTask task, Function<GanttTask, Void> treeVisitor) {
		treeVisitor.apply(task);

		if(task.getChildren().size() == 0) 
			return;

		for(int i = 0; i < task.getChildren().size(); i++)
			traverseDepthFirst(task.getChildren().get(i), treeVisitor);
	}


}

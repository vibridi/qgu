package com.vibridi.qgu;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.vibridi.qgu.model.GanttData;
import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.storage.QGUStorageManager;
import com.vibridi.qgu.util.TaskUtils;
import com.vibridi.qgu.widget.GanttChart;

import javafx.application.Application;
import javafx.stage.Stage;

public class MainTest {

	public static class FakeApp extends Application {
		@Override public void start(Stage primaryStage) throws Exception { }
	}

	@BeforeClass
	public static void initJFX() {
		Thread t = new Thread("JavaFX Init Thread") {
			public void run() {
				Application.launch(FakeApp.class, new String[0]);
			}
		};
		t.setDaemon(true);
		t.start();
	}

	@Test
	public void testRootPath() {
		GanttTask root = new GanttTask("root");
		assertTrue(root.getPath().length == 0);
	}

	@Test
	public void testAddChildManual() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree().getRoot();

		GanttTask item = root.getChild(0,0);
		assertTrue(item.getName().trim().equals("item00"));
		assertTrue(item.getPath().length == 2);
		assertTrue(item.getPath()[0] == 0);
		assertTrue(item.getPath()[1] == 0);

		item = root.getChild(0,2,0);
		assertTrue(item.getName().trim().equals("item020"));
		assertTrue(item.getPath().length == 3); 
		assertTrue(item.getPath()[0] == 0);
		assertTrue(item.getPath()[1] == 2);
		assertTrue(item.getPath()[2] == 0);

		item = root.getChild(3,1,0);
		assertTrue(item.getName().trim().equals("item310"));
		assertTrue(item.getPath().length == 3);        
		assertTrue(item.getPath()[0] == 3);
		assertTrue(item.getPath()[1] == 1);
		assertTrue(item.getPath()[2] == 0);

	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetChildFail() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree().getRoot();
		root.getChild(1,0,0);
	}

//	@Test
//	public void testWalkTaskTree() {
//		TaskTableView view = new TaskTableView();
//		view.addTask(new GanttTask("A"));
//		view.addTask(new GanttTask("B"));
//		view.addTask(new GanttTask("A.1"), 0);
//		view.addTask(new GanttTask("A.1.1"), 0,0);
//		view.addTask(new GanttTask("B.1"), 1);
//
//		List<String> benchmark = Arrays.asList("root","A","A.1","A.1.1","B","B.1");
//		List<String> test = new ArrayList<>();
//
//		view.walkDepthFirst(task -> {
//			test.add(task.getName());
//		});
//
//		assertTrue(Arrays.equals(benchmark.toArray(), test.toArray()));	
//	}

	@Test
	public void testTreeSize() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree().getRoot();
		assertTrue(TaskUtils.treeSize(root) == 15);
	}

	@Test
	public void testToFlatList() throws IOException, URISyntaxException {
		List<String> benchmark = Files.readAllLines(Paths.get(this.getClass().getResource("/tasktree.txt").toURI())).stream()
				.map(line -> line.trim()).collect(Collectors.toList());
		List<String> test = readTaskTree().getRoot().toFlatList().stream().map(node -> node.getName()).collect(Collectors.toList());
		assertTrue(Arrays.equals(benchmark.toArray(), test.toArray()));
	}

	@Test
	public void testAddTaskTree() throws IOException, URISyntaxException {
		List<String> benchmark = Files.readAllLines(Paths.get(this.getClass().getResource("/tasktree.txt").toURI())).stream()
				.map(line -> line.trim()).collect(Collectors.toList());

		GanttChart gc = new GanttChart(readTaskTree().getRoot());
		List<String> test = gc.getGanttRoot().toFlatList().stream()
				.map(node -> node.getName()).collect(Collectors.toList());

		assertTrue(Arrays.equals(benchmark.toArray(), test.toArray()));
	}

	@Test
	public void testRemoveChild() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree().getRoot();
		GanttTask item1 = root.removeChild(1);

		assertTrue(item1.pathEquals(new int[] {1}));
		assertTrue(!root.getChild(1).equals(item1)); 
		assertTrue(root.getChild(1).pathEquals(new int[] {1}));
		assertTrue(root.getChild(2,1,0).getName().equals("item310"));

		GanttTask item01 = root.removeChild(0,1);
		assertTrue(item01.pathEquals(new int[] {0,1}));
		assertTrue(!root.getChild(0,1).equals(item01)); 
		assertTrue(root.getChild(0,1).pathEquals(new int[] {0,1}));
		assertTrue(root.getChild(0,1,0).getName().equals("item020"));
	}

	@Test
	public void testSerialize() throws IOException, URISyntaxException, ClassNotFoundException {
		GanttTask root = readTaskTree().getRoot();

		GanttTask item = root.getChild(3,1,0);
		assertTrue(item.getName().trim().equals("item310"));
		assertTrue(item.getPath().length == 3);        
		assertTrue(item.getPath()[0] == 3);
		assertTrue(item.getPath()[1] == 1);
		assertTrue(item.getPath()[2] == 0);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(root);

		ObjectInputStream ios = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));

		GanttTask deserialized = (GanttTask) ios.readObject();
		//TaskUtils.printTree(deserialized);
		// TODO more meaningful assertions
	}

	@Test
	public void testAbsoluteIndex() throws IOException, URISyntaxException {		
		GanttChart gc = new GanttChart(readTaskTree().getRoot());

		int index = gc.addTask(new GanttTask("New"));
		assertTrue(index == 14);
	}

	private GanttData readTaskTree() {
		try {
			File f = new File(this.getClass().getResource("/tasktree.gtt").toURI());
			return QGUStorageManager.instance.load(f);
		} catch(Throwable t) {
			throw new RuntimeException(t);
		}
	}

	//@Test
	public void testRemoveChildTEMP() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree().getRoot();

		TaskUtils.printTree(root);
		System.out.println("-----------------------------------");

		GanttTask item1 = root.removeChild(1);

		TaskUtils.printTree(root);
		System.out.println("-----------------------------------");


		assertTrue(item1.pathEquals(new int[] {1}));
		assertTrue(!root.getChild(1).equals(item1)); 
		assertTrue(root.getChild(1).pathEquals(new int[] {1}));
		assertTrue(root.getChild(2,1,0).getName().equals("item310"));

		GanttTask item01 = root.removeChild(0,1);

		TaskUtils.printTree(root);
		System.out.println("-----------------------------------");

		assertTrue(item01.pathEquals(new int[] {0,1}));
		assertTrue(!root.getChild(0,1).equals(item01)); 
		assertTrue(root.getChild(0,1).pathEquals(new int[] {0,1}));
		assertTrue(root.getChild(0,1,0).getName().equals("item020"));
	}

	//	public String getResource(String resource) {
	//		Files.readAllLines(Paths.get("/tasktree.txt"));
	//	}


}

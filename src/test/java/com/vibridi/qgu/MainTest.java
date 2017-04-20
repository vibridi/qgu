package com.vibridi.qgu;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;

import com.vibridi.qgu.model.GanttTask;
import com.vibridi.qgu.util.TaskUtils;

public class MainTest {

	@Test
	public void testRootPath() {
        GanttTask root = new GanttTask("root");
        assertTrue(root.getPath().length == 0);
	}
	
	@Test
    public void testAddChildManual() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree();
        
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
		GanttTask root = readTaskTree();
		root.getChild(1,0,0);
	}
	
	@Test
	public void testTraverse() throws IOException, URISyntaxException {
		GanttTask root = readTaskTree();
		
		TaskUtils.traverseDepthFirst(root, task -> {
			if(task.getLevel() != 0)
				System.out.println(task.getName());
			return null;
		});
	}
	
	
	private GanttTask readTaskTree() throws IOException, URISyntaxException {
		List<String> lines = Files.readAllLines(Paths.get(this.getClass().getResource("/tasktree.txt").toURI()));
		
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
	}
	
	
	
//	public String getResource(String resource) {
//		Files.readAllLines(Paths.get("/tasktree.txt"));
//	}
	
	
}

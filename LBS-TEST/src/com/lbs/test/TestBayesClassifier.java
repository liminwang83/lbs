package com.lbs.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.lbs.nbc.BayesClassifier;
import com.lbs.util.*;
import junit.framework.*;

public class TestBayesClassifier extends TestCase{
	
	File testingSet;
	BayesClassifier classifier;
	
	java.util.List<Category> classList = new ArrayList<Category>();
	
	static Logger logger = Logger.getLogger(TestBayesClassifier.class.getName());
	
	
	private class Category{
		public Category(String name){
			this.name = name;
		}
		//all package access
		String name;
		int docCount =0;
		int rightClassifiedCount =0;
		int avgDocLen =0;
		long startTime=0L;
		long endTime =0L;
	}
	
	
	protected void setUp() throws Exception
	{
		PropertyConfigurator.configure("bin\\Log4j.properties"); //configure log4j
		testingSet = new File(ConfigUtil.getProperty("testing.docs.dir")); 
		classifier = new BayesClassifier();
	}
	
	
	public void testClassify(){
		
		File[] Categories = testingSet.listFiles();
		
		for(File category : Categories){ //Loop 测试文本类别
			
			logger.info("Processing Category: "+ category.getName());
			
			if(!category.isDirectory()){
				logger.error("测试文本路径错误");
			}	
			
			//used for statistics
			Category c = new Category(category.getName());
			c.startTime = System.currentTimeMillis();
			classList.add(c);
			
			File textFiles[] = category.listFiles();
			for(File file : textFiles){ //Loop 类别中的文档
				
				logger.info("processing document: "+ file.getName());
				
				if(!file.isFile()){
					logger.error("must be file");
				}
				c.docCount++;
				/*
				String text = "";
				try {
					text = FileUtil.getText(file).trim(); //去除空格
					c.avgDocLen = (c.avgDocLen*c.docCount + text.length())/(++c.docCount);
					
				} catch (FileNotFoundException e) {
					logger.error("**********file not found*********", e);
				} catch (IOException e) {
					logger.error("*********IO exception************", e);
				}*/
				
				String result = classifier.classify(file);
				//assertEquals(result, category.getName());
				if(result.equals(category.getName())){
					c.rightClassifiedCount++;
				}
				
			}
			c.endTime = System.currentTimeMillis();
		}
		
	}
	
	
	public void tearDown(){
		//print result		
		for(Category c : classList){
			System.out.println("********测试文档 类别[" + c.name + "]的实验数据如下：");
			System.out.println("文档总量为：" + c.docCount);
			System.out.println("正确分类的文档数量为：" + c.rightClassifiedCount);
			System.out.println("准确率为：" + c.rightClassifiedCount*100f/c.docCount + "%");
			System.out.println("平均文档长度为：" + c.avgDocLen + "字");
			System.out.println("平均耗时为：" + (c.endTime-c.startTime)/c.docCount + "毫秒");
		}
	}

}

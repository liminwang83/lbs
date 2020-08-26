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
		
		for(File category : Categories){ //Loop �����ı����
			
			logger.info("Processing Category: "+ category.getName());
			
			if(!category.isDirectory()){
				logger.error("�����ı�·������");
			}	
			
			//used for statistics
			Category c = new Category(category.getName());
			c.startTime = System.currentTimeMillis();
			classList.add(c);
			
			File textFiles[] = category.listFiles();
			for(File file : textFiles){ //Loop ����е��ĵ�
				
				logger.info("processing document: "+ file.getName());
				
				if(!file.isFile()){
					logger.error("must be file");
				}
				c.docCount++;
				/*
				String text = "";
				try {
					text = FileUtil.getText(file).trim(); //ȥ���ո�
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
			System.out.println("********�����ĵ� ���[" + c.name + "]��ʵ���������£�");
			System.out.println("�ĵ�����Ϊ��" + c.docCount);
			System.out.println("��ȷ������ĵ�����Ϊ��" + c.rightClassifiedCount);
			System.out.println("׼ȷ��Ϊ��" + c.rightClassifiedCount*100f/c.docCount + "%");
			System.out.println("ƽ���ĵ�����Ϊ��" + c.avgDocLen + "��");
			System.out.println("ƽ����ʱΪ��" + (c.endTime-c.startTime)/c.docCount + "����");
		}
	}

}

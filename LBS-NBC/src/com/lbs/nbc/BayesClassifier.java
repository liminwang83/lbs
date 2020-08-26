package com.lbs.nbc;

import com.lbs.nbc.ChineseSpliter;
import com.lbs.nbc.ClassConditionalProbability;
import com.lbs.nbc.PriorProbability;
import com.lbs.nbc.StopWordsHandler;
import com.lbs.nbc.TrainingDataManager;
import com.lbs.util.FileUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
* ���ر�Ҷ˹������
*/
public class BayesClassifier 
{
	private TrainingDataManager tdm;//ѵ����������
	//private String trainnigDataPath;//ѵ����·��
	private static int zoomFactor = 10;
	static Logger logger = Logger.getLogger(BayesClassifier.class.getName());
	
	/**
	* Ĭ�ϵĹ���������ʼ��ѵ����
	*/
	public BayesClassifier() 
	{
		tdm =new TrainingDataManager();
	}
	
	public BayesClassifier(String trainingPath) 
	{
		tdm =new TrainingDataManager();
		tdm.setDefaultPath(trainingPath);
	}
	
	/**
	* ����������ı���������X�ڸ����ķ���Cj�е�����������
	* <code>ClassConditionalProbability</code>����ֵ
	* @param X �������ı���������
	* @param Cj ���������
	* @return ����������������ֵ����<br>
	*/
	float calcProd(String[] X, String Cj) 
	{
		float ret = 1.0F;
		// ��������������
		for (int i = 0; i <X.length; i++)
		{
			String Xi = X[i];
			//��Ϊ�����С�����������֮ǰ�Ŵ�10����������ս������Ӱ�죬��Ϊ����ֻ�ǱȽϸ��ʴ�С����
			ret *= ClassConditionalProbability.calculatePxc(Xi, Cj)*zoomFactor;
		}
		// �ٳ����������
		ret *= PriorProbability.calculatePc(Cj);
		return ret;
	}
	/**
	* ȥ��ͣ�ô�
	* @param text �������ı�
	* @return ȥͣ�ôʺ���
	*/
	public String[] DropStopWords(String[] oldWords)
	{
		Vector<String> v1 = new Vector<String>();
		for(int i=0;i<oldWords.length;++i)
		{
			if(StopWordsHandler.IsStopWord(oldWords[i])==false)
			{//����ͣ�ô�
				v1.add(oldWords[i]);
			}
		}
		String[] newWords = new String[v1.size()];
		v1.toArray(newWords);
		return newWords;
	}
	/**
	* �Ը������ı����з���
	* @param text �������ı�
	* @return ������
	*/
	@SuppressWarnings("unchecked")
	public String classify(String text) 
	{
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//���ķִʴ���(�ִʺ������ܻ�������ͣ�ôʣ�
		terms = DropStopWords(terms);//ȥ��ͣ�ôʣ�����Ӱ�����
		
		String[] Classes = tdm.getTraningClassifications();//����
		float probility = 0.0F;
		List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//������
		for (int i = 0; i <Classes.length; i++) 
		{
			logger.info("processing class: "+Classes[i]);
			String Ci = Classes[i];//��i������
			probility = calcProd(terms, Ci);//����������ı���������terms�ڸ����ķ���Ci�еķ�����������
			//���������
			ClassifyResult cr = new ClassifyResult();
			cr.classification = Ci;//����
			cr.probility = probility;//�ؼ����ڷ������������
			System.out.println("In process....");
			System.out.println("P(" + Ci + ")=" + probility);			
			crs.add(cr);
		}
		//�������ʽ����������
		java.util.Collections.sort(crs,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) 
				{
					return 1;
				} 
				else 
				{
					return -1;
				}
			}
		});
		//���ظ������ķ���
		return crs.get(0).classification;
	}
	
	
	/**
	* �Ը������ı����з���
	* @param doc �������ı��ĵ�
	* @return ������
	*/
	@SuppressWarnings("unchecked")
	public String classify(File doc) 
	{
		String text = "";
		try {
			text = FileUtil.getText(doc).trim(); //ȥ���ո�
			//c.avgDocLen = (c.avgDocLen*c.docCount + text.length())/(++c.docCount);
			
		} catch (FileNotFoundException e) {
			logger.error("**********File not found*********", e);
		} catch (IOException e) {
			logger.error("*********IO exception************", e);
		}
		
		String[] terms = null;
		terms= ChineseSpliter.split(text, " ").split(" ");//���ķִʴ���(�ִʺ������ܻ�������ͣ�ôʣ�
		terms = DropStopWords(terms);//ȥ��ͣ�ôʣ�����Ӱ�����
		
		String[] Classes = tdm.getTraningClassifications();//����
		float probility = 0.0F;
		List<ClassifyResult> crs = new ArrayList<ClassifyResult>();//������
		for (int i = 0; i <Classes.length; i++) 
		{
			//logger.info("processing class: "+Classes[i]);
			String Ci = Classes[i];//��i������
			probility = calcProd(terms, Ci);//����������ı���������terms�ڸ����ķ���Ci�еķ�����������
			//���������
			ClassifyResult cr = new ClassifyResult();
			cr.classification = Ci;//����
			cr.probility = probility;//�ؼ����ڷ������������
			//System.out.println("In process....");
			logger.info("P(" + Ci + "|" + doc.getName() + ")=" + probility);			
			crs.add(cr);
		}
		//�������ʽ����������
		java.util.Collections.sort(crs,new Comparator() 
		{
			public int compare(final Object o1,final Object o2) 
			{
				final ClassifyResult m1 = (ClassifyResult) o1;
				final ClassifyResult m2 = (ClassifyResult) o2;
				final double ret = m1.probility - m2.probility;
				if (ret < 0) 
				{
					return 1;
				} 
				else 
				{
					return -1;
				}
			}
		});
		//���ظ������ķ���
		return crs.get(0).classification;
	}
	
/*	
	public static void main(String[] args)
	{
		String text = "΢��˾�����446����Ԫ�ļ۸��չ��Ż��й���2��1�ձ��� ��������Ϣ��΢��˾�����446����Ԫ�ֽ�ӹ�Ʊ�ļ۸��չ�������վ�Ż���˾��΢�������ÿ��31��Ԫ�ļ۸��չ��Ż���΢����չ����۽��Ż�1��31�յ����̼�19.18��Ԫ���62%��΢��˾���Ż���˾�Ĺɶ�����ѡ�����ֽ���Ʊ���н��ס�΢����Ż���˾��2006��׺�2007�������Ѱ��˫���������������꣬�Ż�һֱ�����������г��ݶ��»�����Ӫҵ�����ѡ��ɼ۴���µ���������ͼ�ڻ������г�������Ϊ��΢����˵���չ��Ż�������һ���ݾ�����Ϊ˫�����зǳ�ǿ�Ļ����ԡ�(С��)";
		BayesClassifier classifier = new BayesClassifier();//����Bayes������
		String result = classifier.classify(text);//���з���
		System.out.println("��������["+result+"]");
	}*/
}
package com.lbs.swingworker;

import javax.swing.SwingWorker;

import org.apache.log4j.Logger;

import com.lbs.nbc.BayesClassifier;
import com.lbs.ui.JTextPaneWrapper;

public class TextClassifier extends SwingWorker<String, Void>{
	
	private String text = "";
	private JTextPaneWrapper statusPane; //to display classify result
	
	static Logger logger = Logger.getLogger(TextClassifier.class.getName());
	
	
	public TextClassifier(String text, JTextPaneWrapper statusPane){
		this.text = text;
		this.statusPane = statusPane;
	}

	@Override
	protected String doInBackground() throws Exception {
		logger.info("Swing Worker Thread started...");
		BayesClassifier classifier = new BayesClassifier();
		String result = classifier.classify(text); //time consuming process
		return result;
	}
	
	
	@Override
    protected void done() {  //called by Event Dispaching Thread
        String result = null;
        try {
            result = get();
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }

        statusPane.append("文本属于类别  ", JTextPaneWrapper.BLUE_HIGHLIGHT);
		statusPane.append(result, JTextPaneWrapper.RED_HIGHLIGHT);
    } 
	

}

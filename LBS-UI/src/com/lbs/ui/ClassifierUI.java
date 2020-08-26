package com.lbs.ui;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.lbs.swingworker.TextClassifier;
import com.lbs.util.FileUtil;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javax.swing.SwingWorker;

public class ClassifierUI extends JFrame{

	private JTextArea textArea;
	private JTextPaneWrapper statusPane;
	private JMenuBar menuBar;
	private JMenu menu, helpMenu;
	private JMenuItem menuItem1, menuItem2;
	//private JEditorPane statusPane;
	private String trainingFileDirectory;
	private JFileChooser fileChooser;
	private JTree tree;
	static Logger logger = Logger.getLogger(ClassifierUI.class.getName());
	
	public ClassifierUI(){
		redirectOut(); //redirect output to GUI
		PropertyConfigurator.configure("bin\\Log4j.properties"); //configure log4j
		
		initComponents();		
	}
	
	/*********************Listeners START*******************************************************/
	class ClassifyButtonListener implements ActionListener{

		public void actionPerformed(ActionEvent e) {
			String text= textArea.getText().trim();
			if(trainingFileDirectory==null){
				JOptionPane.showMessageDialog(ClassifierUI.this,
                        "请先指定Sogou训练文本位置！",
                        "NBC Warning",
                        JOptionPane.WARNING_MESSAGE);
				return;
			}else if(text==null || text.length()==0){
				JOptionPane.showMessageDialog(ClassifierUI.this,
                        "待分类文本不能为空！",
                        "NBC Warning",
                        JOptionPane.WARNING_MESSAGE);
				return;
			}
			statusPane.setText("");
			logger.info("Start classification...");
			
			SwingWorker<String, Void> worker = new TextClassifier(text, statusPane);
			worker.execute();
		}		
	}
	
	class MenuListerner implements ActionListener{
		
		File file;
		
		public void actionPerformed(ActionEvent e) {

			JMenuItem source = (JMenuItem)(e.getSource());			
			
			fileChooser= new JFileChooser();
	        if(source == menuItem1){
	        	fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	        } 
	        
	        int returnVal = fileChooser.showOpenDialog(ClassifierUI.this);
	        
            if (returnVal == JFileChooser.APPROVE_OPTION) {
            	
                file = fileChooser.getSelectedFile();
                
                if(source == menuItem1){
                	trainingFileDirectory=file.getPath();
                	DefaultMutableTreeNode root =(DefaultMutableTreeNode) tree.getModel().getRoot();
                	root.removeAllChildren(); //clean up?
                	tree.updateUI();
                	createNodes(root);
                	tree.fireTreeExpanded(new TreePath(root));
                	//tree.fireTreeExpanded(new TreePath(root.getFirstChild()));
                	tree.updateUI();
                	
                }else if(source == menuItem2){
                	
                }
                
            } 
		}
		
		private void createNodes(DefaultMutableTreeNode top){
			
			DefaultMutableTreeNode category = null;
            DefaultMutableTreeNode item = null;
            File[] Classes = file.listFiles();
            
            for (File classDir : Classes){  
            	
                if(!classDir.isDirectory()){
                	JOptionPane.showMessageDialog(ClassifierUI.this,
                            "训练文本非法，请重新指定！",
                            "错误信息",
                            JOptionPane.ERROR_MESSAGE);
    				return;
                }
            	category = new DefaultMutableTreeNode(classDir.getName());
                top.add(category);
                
    			File[] trainFiles = classDir.listFiles();	    			
    			for(File itemFile : trainFiles){

    				if(!itemFile.isFile()){
    					JOptionPane.showMessageDialog(ClassifierUI.this,
                                "训练文本非法，请重新指定！",
                                "错误信息",
                                JOptionPane.ERROR_MESSAGE);
        				return;
    				}
    				if(itemFile.getName().endsWith(".txt")){  //text files only
	    				item = new DefaultMutableTreeNode(new FileItemInfo(itemFile.getName(), 
	    						itemFile.getPath(), classDir.getName()));
	                    category.add(item);
    				}
    			}
            }
		}				
		
	}
	
	private class FileItemInfo{
		public String fileName;
		public String filePath;
		public String belongClass;
		
		public FileItemInfo(String fileName, String filePath, String belongClass){
			this.fileName=fileName;
			this.filePath=filePath;
			this.belongClass=belongClass; 
		}
		
		public String toString() {
            return fileName;
        }
	}
	
	class TreeListener implements TreeSelectionListener{
		
	    public void valueChanged(TreeSelectionEvent e) {
	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
	                           tree.getLastSelectedPathComponent();

	        if (node == null) return;

	        Object nodeInfo = node.getUserObject();
	        if (node.isLeaf() && !node.isRoot()) {
	        	FileItemInfo itemInfo = (FileItemInfo)nodeInfo;
	        	String text="";
				try {
					text = FileUtil.getText(itemInfo.filePath);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        	textArea.setText(text);
	        } 
	    }
	    
	}
	/*********************Listeners END*******************************************************/
	
	
	public void initComponents(){
		this.setTitle("贝叶斯分类器实验程序");
		this.setSize(600, 500);
		this.setLocationRelativeTo(null);
		
		/***********init Menu*********************/
		menuBar= new JMenuBar();
		this.setJMenuBar(menuBar);
		menu= new JMenu("文件");
		//menu.setShortcut(KeyEvent.VK_F);
		helpMenu = new JMenu("帮助");
		menuBar.add(menu);
		menuBar.add(helpMenu);
		menuItem1= new JMenuItem("定位训练文本");
		menuItem2= new JMenuItem("定位索引");
		menu.add(menuItem1);
		menu.add(menuItem2);
		MenuListerner menuListener= new MenuListerner();
		menuItem1.addActionListener(menuListener);
		menuItem2.addActionListener(menuListener);
		
		/*****************Components initializing*********************************/	
		textArea= new JTextArea(40,65);
		textArea.setLineWrap(true);
		ClassifyButtonListener classifyListener= new ClassifyButtonListener();
		JButton button=new JButton("Classify");
		button.addActionListener(classifyListener);
		//JTextPane tp= new JTextPane();
		//tp.setText(tp.getText()+"fuck");
		//statusArea= new JTextArea(20,65);
		//statusArea.setLineWrap(true);
		statusPane = new JTextPaneWrapper();
		statusPane.setEditable(false);
		statusPane.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
            	statusPane.setCaretPosition(statusPane.getDocument().getLength());
            }

            public void removeUpdate(DocumentEvent e) {
                //do nothing
            }

            public void changedUpdate(DocumentEvent e) {
                //do nothing
            }
        });
		//statusPane.setBackground(Color.cyan);
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setTopComponent(new JScrollPane(textArea));
        splitPane.setBottomComponent(new JScrollPane(statusPane));
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerLocation(300);
        
        /**************************init Tree**********************************/
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Sogou训练文本");
        tree = new JTree(root);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        TreeListener treeListener = new TreeListener();
        tree.addTreeSelectionListener(treeListener);
        
        /*************************Layout management**********************/
		Container cp=getContentPane();
		JScrollPane treeView = new JScrollPane(tree);
		//treeView.setMinimumSize(new Dimension(200,200));
		cp.add(BorderLayout.WEST, treeView);
		cp.add(BorderLayout.CENTER, splitPane);		
		cp.add(BorderLayout.SOUTH, button);
		
		String text = "微软公司提出以446亿美元的价格收购雅虎中国网2月1日报道 美联社消息，微软公司提出以446亿美元现金加股票的价格收购搜索网站雅虎公司。微软提出以每股31美元的价格收购雅虎。微软的收购报价较雅虎1月31日的收盘价19.18美元溢价62%。微软公司称雅虎公司的股东可以选择以现金或股票进行交易。微软和雅虎公司在2006年底和2007年初已在寻求双方合作。而近两年，雅虎一直处于困境：市场份额下滑、运营业绩不佳、股价大幅下跌。对于力图在互联网市场有所作为的微软来说，收购雅虎无疑是一条捷径，因为双方具有非常强的互补性。(小桥)";
		textArea.setText(text);
		//nameListener.actionPerformed(new ActionEvent("", 0, ""));
	}
	
	public void redirectOut(){
		PrintStream ps = new PrintStream(new OutputStream(){   
            
            final int LENGTH = 256;   
            byte[] bb = new byte[LENGTH];     
            int p = 0;   
            public void write(int b) throws IOException{   
                    bb[p++]= (byte)b;   
                      
                    if(p >= LENGTH){   
                       flush();   
                    }   
            }   
              
            public void flush()throws IOException{   
                    String str = new String(bb,0,p);                         
                    statusPane.append(str);                         
                    p = 0;   
            }   
		},true);   
      
		System.setOut(ps);         		
	}

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClassifierUI().setVisible(true);
            }
        });
    }
}

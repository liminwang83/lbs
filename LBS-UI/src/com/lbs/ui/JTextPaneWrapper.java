package com.lbs.ui;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class JTextPaneWrapper extends JTextPane {
	public static final String REGULAR = "regular";
	public static final String ITALIC = "italic";
	public static final String BOLD = "bold";
	public static final String LARGE = "large";
	public static final String SMALL = "small";
	public static final String ICON = "icon";
	public static final String BLUE_HIGHLIGHT = "blue_highlight";
	public static final String RED_HIGHLIGHT = "red_highlight";
	
	public JTextPaneWrapper(){
		//JTextPane textPane = new JTextPane();
        StyledDocument doc = this.getStyledDocument();
        addStylesToDocument(doc);
	}
	
	public void append(String str, String style){
		StyledDocument doc = this.getStyledDocument();
		
		try {
           doc.insertString(doc.getLength(), str,
                                 		doc.getStyle(style));              
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
	}
	
	public void append(String str){
		StyledDocument doc = this.getStyledDocument();
		
		try {
           doc.insertString(doc.getLength(), str,
                                 		doc.getStyle(REGULAR));              
        } catch (BadLocationException ble) {
            System.err.println("Couldn't insert initial text into text pane.");
        }
	}
	
	protected void addStylesToDocument(StyledDocument doc) {
        //Initialize some styles.
        Style def = StyleContext.getDefaultStyleContext().
                        getStyle(StyleContext.DEFAULT_STYLE);

        Style regular = doc.addStyle(REGULAR, def);
        StyleConstants.setFontFamily(def, "SansSerif");
        
        Style s = doc.addStyle(BLUE_HIGHLIGHT, regular);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.BLUE);
        
        s = doc.addStyle(RED_HIGHLIGHT, regular);
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setBold(s, true);
        StyleConstants.setForeground(s, Color.RED);
        
        s = doc.addStyle(ITALIC, regular);
        StyleConstants.setItalic(s, true);

        s = doc.addStyle(BOLD, regular);
        StyleConstants.setBold(s, true);

        s = doc.addStyle(SMALL, regular);
        StyleConstants.setFontSize(s, 10);

        s = doc.addStyle(LARGE, regular);
        StyleConstants.setFontSize(s, 16);
        StyleConstants.setForeground(s, Color.RED);
        /*
        s = doc.addStyle(ICON, regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon pigIcon = createImageIcon("images/Pig.gif",
                                            "a cute pig");
        if (pigIcon != null) {
            StyleConstants.setIcon(s, pigIcon);
        }
        
        s = doc.addStyle("button", regular);
        StyleConstants.setAlignment(s, StyleConstants.ALIGN_CENTER);
        ImageIcon soundIcon = createImageIcon("images/sound.gif",
                                              "sound icon");
        JButton button = new JButton();
        if (soundIcon != null) {
            button.setIcon(soundIcon);
        } else {
            button.setText("BEEP");
        }
        button.setCursor(Cursor.getDefaultCursor());
        button.setMargin(new Insets(0,0,0,0));
        button.setActionCommand(buttonString);
        button.addActionListener(this);
        StyleConstants.setComponent(s, button);*/
    }
	
	/** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = JTextPaneWrapper.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}

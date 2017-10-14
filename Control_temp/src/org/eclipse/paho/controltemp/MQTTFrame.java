/********************************************************************************
 * Copyright (c) 2002, 2013 IBM Corp.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution. 
 *
 * The Eclipse Public License is available at 
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 *   http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Description: Contains the main method for starting the MQTT swing user interface
 *
 * Contributors:
 *    Ian Harwood, Ian Craggs - initial API and implementation and/or initial documentation
 ********************************************************************************/

package org.eclipse.paho.controltemp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.border.EtchedBorder;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;


import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.GroupLayout.Alignment;

import java.awt.Rectangle;
import javax.swing.event.AncestorListener;
import javax.swing.event.AncestorEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.SystemColor;
import java.awt.Label;
import java.util.Date;
import java.util.Calendar;
import java.util.UUID;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.CardLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

@SuppressWarnings("serial")
class DrawGraph extends JPanel {
	   public static float MAX_SCORE = 0;
	   private static final int PREF_W = 800;
	   private static final int PREF_H = 650;
	   private static final int BORDER_GAP = 30;
	   private static final Color GRAPH_COLOR = Color.green;
	   private static final Color GRAPH_POINT_COLOR = new Color(150, 50, 50, 180);
	   private static final Stroke GRAPH_STROKE = new BasicStroke(3f);
	   private static final int GRAPH_POINT_WIDTH = 12;
	   private static final int Y_HATCH_CNT = 10;
	   private static List<Float> scores, y_array;
	   static boolean check_scroll, check_scroll2;
	   

	   public DrawGraph(List<Float> scores2) {
		   this.MAX_SCORE = MAX_SCORE;
		   this.y_array = y_array;
		   this.check_scroll = check_scroll;
		   this.check_scroll2 = check_scroll2;
		   this.scores = scores2;
		   JButton btnChange = new JButton("SCROLL");;
		  if (check_scroll) {btnChange.setBackground(Color.red); btnChange.setText("COMPACT");}
		  else {btnChange.setBackground(Color.yellow); btnChange.setText("SCROLL");}
		   btnChange.addMouseListener(new MouseAdapter() {
		    	@Override
		    	public void mouseReleased(MouseEvent e) {
		    		check_scroll = (check_scroll)?false:true;
		    		  if (check_scroll) {btnChange.setBackground(Color.red); btnChange.setText("COMPACT");}
		    		  else {btnChange.setBackground(Color.yellow); btnChange.setText("SCROLL");}
		  	      System.out.println(check_scroll);
		    	}
		    });
		    add(btnChange);
	   }

	   @Override
	   protected void paintComponent(Graphics g) {
	      super.paintComponent(g);
	      Graphics2D g2 = (Graphics2D)g;
	      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	      double xScale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
	      double yScale = ((double) getHeight() - 2 * BORDER_GAP) / (MAX_SCORE - 1);

	      List<Point> graphPoints = new ArrayList<Point>();
	      for (int i = 0; i < scores.size(); i++) {
	         int x1 = (int) (i * xScale + BORDER_GAP);
	         int y1 = (int) ((MAX_SCORE - scores.get(i)) * yScale + BORDER_GAP);
	         graphPoints.add(new Point(x1, y1));
	      }

	      // create x and y axes 
	      g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
	      g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, getHeight() - BORDER_GAP);

	      // create hatch marks for y axis. 
	      for (int i = 0; i < Y_HATCH_CNT; i++) {
	         int x0 = BORDER_GAP;
	         int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
	         int y0 = getHeight() - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
	         int y1 = y0;
	         g2.drawLine(x0, y0, x1, y1);
	      }

	      // and for x axis
	      for (int i = 0; i < scores.size() - 1; i++) {
	         int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
	         int x1 = x0;
	         int y0 = getHeight() - BORDER_GAP;
	         int y1 = y0 - GRAPH_POINT_WIDTH;
	         g2.drawLine(x0, y0, x1, y1);
	      }

	      Stroke oldStroke = g2.getStroke();
	      g2.setColor(GRAPH_COLOR);
	      g2.setStroke(GRAPH_STROKE);
	      for (int i = 0; i < graphPoints.size() - 1; i++) {
	         int x1 = graphPoints.get(i).x;
	         int y1 = graphPoints.get(i).y;
	         int x2 = graphPoints.get(i + 1).x;
	         int y2 = graphPoints.get(i + 1).y;
	         g2.drawLine(x1, y1, x2, y2);         
	      }

	      g2.setStroke(oldStroke);      
	      g2.setColor(GRAPH_POINT_COLOR);
	      for (int i = 0; i < graphPoints.size(); i++) {
	         int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
	         int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;;
	         int ovalW = GRAPH_POINT_WIDTH;
	         int ovalH = GRAPH_POINT_WIDTH;
	         //g2.fillOval(x, y, ovalW, ovalH);
	      }
	   }

	   @Override
	   public Dimension getPreferredSize() {
	      return new Dimension(PREF_W, PREF_H);
	   }
}

/**
 * This class is the controlling class for the application. It contains the main method
 * for launching the Swing GUI and itcontrols the MQTT connection.
 */
public class MQTTFrame implements ActionListener, MqttCallback, MqttCallbackExtended, Runnable  {
    private int status;
    int check_relay = 4;
    static boolean[] check_onoff_temp = {false,false,false,false};
	static boolean check_graph = false;
    static String[] list_topic= {"","","","",""};
    static String[] temp_des = {"0","0","0","0"};
    static String[] temp_real = {"0","0","0","0"};
    String[] hour1 = {"0","0","0","0","0","0","0","0"}, hour2 = {"0","0","0","0","0","0","0","0"};
    String[] hour3 = {"0","0","0","0","0","0","0","0"}, hour4 = {"0","0","0","0","0","0","0","0"};
    String[][] hour = {hour1, hour2, hour3, hour4};
    String[] min1 = {"0","0","0","0","0","0","0","0"}, min2 = {"0","0","0","0","0","0","0","0"};
    String[] min3 = {"0","0","0","0","0","0","0","0"}, min4 = {"0","0","0","0","0","0","0","0"};
    String[][] min = {min1, min2, min3, min4};
    String[] stat1 = {"0","0","0","0","0","0","0","0"}, stat2 = {"0","0","0","0","0","0","0","0"};
    String[] stat3 = {"0","0","0","0","0","0","0","0"}, stat4 = {"0","0","0","0","0","0","0","0"};
    String[][] stat = {stat1, stat2, stat3, stat4};
	private JPanel connPanel = new JPanel();
	private JPanel pubPanel = new JPanel();
	private JPanel subPanel = new JPanel();
	private SubPanel subPanelContr;
	
	// Main Components
	private JPanel     mqttComp = null;
	private JPanel led1_1, led1_2, led2_1, led2_2, led3_1, led3_2, led4_1, led4_2;
	JButton relay1_1, relay1_2, relay2_1, relay2_2, relay3_1, relay3_2, relay4_1, relay4_2;
	private JComboBox ipAddress;
	private JComboBox port;
	private JButton   connect;
	private JButton   disconnect;
	private static MqttClient mqtt = null;
    private MqttConnectOptions opts = null;
	private boolean connected = false;
	private Object    connLostWait = new Object(); // Object to coordinated ConnectionLost and disconnect threads if
                	                                // disconnect is hit during connectionLost
	private JFrame frame= null;
	static JFrame newFrame;
	
	
	/**
	 *  Constant controlling the display of JTextFields
	 */
    protected static final Dimension TEXT_FIELD_DIMENSION = new Dimension( 1000, 20 );
	/**
	 *  Constant controlling the display of JComboBoxes
	 */
    protected static final Dimension DROP_DOWN_DIMENSION = new Dimension(35, 20);
    protected static final Insets TEXT_MARGINS = new Insets(3,3,3,3);
    protected static final int FRAME_WIDTH = 375;
    protected static final int FRAME_HEIGHT = 450;

    // The name of the properties file
    private final static String PROP_FILE = "mqtt.properties";
    private final static String PROP_DELIM = ";";

    // Other constants
    private static final String DEFAULT_IP_ADDRESS = "tcp://iot.eclipse.org";
    private static final String DEFAULT_PORT_NUMBER = "1883";
    private JLabel lblDevice;
    private JLabel lblDevice_1;
    private JLabel lblDevice_2;
    private JLabel lblDevice_3;
    private JPanel device1;
    private JPanel device2;
    private JPanel device3;
    private JPanel device4;
    private JTable table;
    private JButton delete;
    private JButton update;
    private JPanel panel_1;
    private JPanel panel_21;
    private JTabbedPane tabbedPane_1;
    private JPanel panel_22;
    private JSplitPane splitPane_3;
    private JPanel panel_23;
    private JPanel panel_25;
    private JSplitPane splitPane_5;
    private JPanel panel_26;
    private JLabel label_1;
    private JComboBox comboBox_20;
    private JComboBox comboBox_21;
    private JPanel panel_27;
    private JLabel label_3;
    private JComboBox comboBox_22;
    private JComboBox comboBox_23;
    private JPanel panel_28;
    private JPanel panel_29;
    private JSplitPane splitPane_6;
    private JPanel panel_30;
    private JLabel label_5;
    private JComboBox comboBox_24;
    private JComboBox comboBox_25;
    private JPanel panel_31;
    private JLabel label_6;
    private JComboBox comboBox_26;
    private JComboBox comboBox_27;
    private JSplitPane splitPane_7;
    private JPanel panel_32;
    private JLabel label_7;
    private JComboBox comboBox_28;
    private JComboBox comboBox_29;
    private JPanel panel_33;
    private JLabel label_8;
    private JComboBox comboBox_30;
    private JComboBox comboBox_31;
    private JSplitPane splitPane;
    private JSplitPane splitPane_1;
    private JSplitPane splitPane_2;
    private JPanel panel_2;
    private JPanel panel_3;
    private JLabel label;
    private JLabel label_2;
    private JLabel lblAction;
    private JComboBox s1_1_1;
    private JSplitPane splitPane_4;
    private JPanel panel_4;
    private JPanel panel_5;
    private JLabel label_4;
    private JLabel label_9;
    private JLabel label_10;
    private JComboBox s1_1_2;
    private JLabel label_11;
    private JLabel label_12;
    private JLabel label_13;
    private JComboBox s1_1_3;
    private JLabel label_14;
    private JLabel label_15;
    private JLabel label_16;
    private JComboBox s1_1_4;
    private JSplitPane splitPane_8;
    private JPanel panel_6;
    private JLabel lblRelay_1;
    private JSplitPane splitPane_9;
    private JPanel panel_7;
    private JLabel label_18;
    private JLabel label_19;
    private JLabel label_20;
    private JComboBox s1_2_1;
    private JSplitPane splitPane_10;
    private JPanel panel_8;
    private JLabel label_21;
    private JLabel label_22;
    private JLabel label_23;
    private JComboBox s1_2_2;
    private JSplitPane splitPane_11;
    private JPanel panel_9;
    private JLabel label_24;
    private JLabel label_25;
    private JLabel label_26;
    private JComboBox s1_2_3;
    private JPanel panel_10;
    private JLabel label_27;
    private JLabel label_28;
    private JLabel label_29;
    private JComboBox s1_2_4;
    private JButton save1_2;
    private JPanel panel;
    private JLabel lblRelay;
    private JButton save1_1;
    private JSpinner h1_1_1;
    private JSpinner h1_1_2;
    private JSpinner h1_1_3;
    private JSpinner h1_1_4;
    private JSpinner h1_2_1;
    private JSpinner h1_2_2;
    private JSpinner h1_2_3;
    private JSpinner h1_2_4;
    private JSpinner m1_1_1;
    private JSpinner m1_1_2;
    private JSpinner m1_1_3;
    private JSpinner m1_1_4;
    private JSpinner m1_2_1;
    private JSpinner m1_2_2;
    private JSpinner m1_2_3;
    private JSpinner m1_2_4;
    private JPanel panel_11;
    private JTable table_1;
    private JButton btnTest;
    private JPanel panel_12;
    private JLabel label_17;
    private JLabel lblListDevice;
	/**
	 * Constructor for MQTTFrame
	 */
	public MQTTFrame() {		
		super();
	}


    /**
     * The main method for launching the GUI. The main method takes no arguments.
     * <BR>
     * This method attaches a WindowListener to the window to detect windowClosing events. When
     * such an event is detected then various parameters are gathered from the GUI and written
     * to a properties file before the application exits.
     * @param args No arguments are required.
     */
	public static void main(String[] args) {
		JFrame theFrame = null;
		final MQTTFrame view = new MQTTFrame();

		newFrame = new JFrame("DrawGraph");
		//newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		newFrame.setLocationByPlatform(true);
		
		theFrame = view.getJFrame();
        theFrame.setSize( 413, 498 );
        theFrame.setLocation(150, 50);
        theFrame.setResizable(true);
             		
		view.init( theFrame.getContentPane() );

		theFrame.setVisible(true);

      
		theFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				
				System.exit(0);
			}
		});
		
		  List<Float> scores = new ArrayList<Float>(), y_array = new ArrayList<Float>();
		  int max_array = 10;
		  Float temp_real_value;
	      int count=0;
	      while(true) {
	    	  try {
		          Thread.sleep(15000);
		        } catch (InterruptedException ex) {
		          System.out.println("Oh no!");
		        }
	    	  
			//System.out.println("15000");
			for(int i=0;i<4;i++) {
				if(check_onoff_temp[i]) {
					on_off_temp(temp_des[i],temp_real[i],"server/"+list_topic[i].split("/")[1]);
					check_onoff_temp[i] = false;
				}
			}
			
	    	  DrawGraph mainPanel2 = new DrawGraph(scores);
		      count++;
		      temp_real_value = Float.parseFloat(temp_real[0])/100;
		      y_array.add(temp_real_value);
		      if(temp_real_value <= 15) System.out.println("Not yet");
		      else {
		          System.out.println(temp_real_value+" & "+y_array.size());
			      if (temp_real_value>mainPanel2.MAX_SCORE) mainPanel2.MAX_SCORE = temp_real_value;
			      if ((count > max_array)) {
			    	  if (mainPanel2.check_scroll) {
			    		  if (mainPanel2.check_scroll2)	{
			    			  for (int i=0; i<max_array-1; i++) scores.set(i,y_array.get(i));
			    			  for (int i=max_array-1; i<y_array.size(); i++) scores.add(y_array.get(i));
			    			  mainPanel2.check_scroll2 = false;
			    		  }
			    		  scores.add(temp_real_value);
			    		  //scores = y_array;//for(int i=0; i<y_array.size();i++) scores.set(i,y_array.get(i));
			    	  }
			    	  else	{
			    		  scores.clear();
			    		  for(int i=0; i<max_array;i++) scores.add(y_array.get(count - max_array+i));
			    		  mainPanel2.check_scroll2 = true;
			    	  }
			      }
			      else	scores.add(temp_real_value);
			      DrawGraph mainPanel = new DrawGraph(scores);
			      newFrame.getContentPane().add(mainPanel);
			      newFrame.pack();
			      newFrame.setVisible(check_graph);
		      }
	      }
		
	}

	/**
	 * @return This MQTTFrame's JFrame object reference.
	 */
	JFrame getJFrame() {
		if ( frame == null ) {
			frame = new JFrame();
		}
		return frame;
	}
    /**
     * This method builds all the components to add to the
     * content pane of the JFrame window. It builds the connect panel itself and delegates building
     * of the publish and subscribe panels to the PubPanel and SubPanel objects respectively.
     * It also loads the properties file and gives the window a Windows look and feel.
     * @param contentPane - The content pane of the JFrame window.
     */
	
	protected void init( Container contentPane ) {
		
        FileInputStream propFile = null;
        Properties      props = new Properties();
        
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch ( Exception ex) {
        	// Don't worry if we can't set the look and feel
        }	

        // Does a properties file exist from which the GUI can be populated?
        try {
            propFile = new FileInputStream( PROP_FILE );
            props.load( propFile );
        } catch(Exception fe) {
        	// If we can't find a properties file then don't worry
        	propFile = null;
        }	  
        
        
        // Now build the GUI components
        setTitleText("");        

		connPanel.setLayout(new GridLayout(2, 1));
        connPanel.setBorder( new EtchedBorder() );

        // Build the main components to add to the tabbed pane.
        mqttComp = new JPanel( new BorderLayout() );
      
        // Add the panel which handles connecting and disconnecting from the broker
        mqttComp.add(connPanel, BorderLayout.NORTH);
		
        // Add the panels for publish and subscribe to a JSplitPane
        // The JSplitPane allows the panels to be resized evenly, allows the user to resize the 
        // panels manually and provides a good layout for two similar panels
		JSplitPane pubsub = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true,
		subPanel, pubPanel );
        table_1 = new JTable();
        table_1.setBorder(new LineBorder(new Color(0, 0, 0)));
        table_1.setModel(new DefaultTableModel(
        	new Object[][] {
        		{"Device", "Name"},
        		{"1", null},
        		{"2", null},
        		{"3", null},
        		{"4", null},
        	},
        	new String[] {
        		"New column", "New column"
        	}
        ) {
        	Class[] columnTypes = new Class[] {
        		String.class, String.class
        	};
        	public Class getColumnClass(int columnIndex) {
        		return columnTypes[columnIndex];
        	}
        });
        pubPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        pubPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        lblListDevice = new JLabel(" List Device                                                                             ");
        pubPanel.add(lblListDevice);
        lblListDevice.setFont(new Font("Tahoma", Font.BOLD, 13));
        pubPanel.add(table_1);
        table_1.getColumnModel().getColumn(0).setPreferredWidth(50);
        table_1.getColumnModel().getColumn(1).setPreferredWidth(130);
		pubsub.setOneTouchExpandable(true); // Allow either panel to be expanded to full size easily
		pubsub.setDividerSize(10);

        // Add the pubsub JSplitPane to the outer frame
		mqttComp.add( pubsub, BorderLayout.CENTER );
		
		        
        ipAddress = new JComboBox();
        ipAddress.setPreferredSize( new Dimension(125,20) );
        ipAddress.setEditable( true );
        // Load any TCP/IP address info from the config file
        getProperties( props, ipAddress, "IPAddressList", DEFAULT_IP_ADDRESS );
        
        port = new JComboBox();
        port.setPreferredSize( new Dimension(65,20) );
        port.setEditable( true );
        // Load any TCP/IP port info from the config file
        getProperties( props, port, "IPPortList", DEFAULT_PORT_NUMBER );
        
        
        connect = new JButton( "Connect" );
        disconnect = new JButton( "Disconnect" );
        disconnect.setEnabled(false);
                
        connect.addActionListener(this);        
        disconnect.addActionListener(this);
        
        JPanel text = new JPanel();
        text.add( new JLabel("Broker TCP/IP address: ") );
        text.add( ipAddress );
        text.add( port );
        
        
        JPanel buttons = new JPanel();
        buttons.setLayout( new BoxLayout( buttons, BoxLayout.X_AXIS ) );
        buttons.add( new JSeparator( SwingConstants.VERTICAL ) );
        buttons.add( connect );
        buttons.add( new JLabel("  ") );
        buttons.add( disconnect );
        buttons.add( new JSeparator( SwingConstants.VERTICAL ) );
                
        connPanel.add( text );
        connPanel.add( buttons );

        subPanelContr = new SubPanel( subPanel, this );

        if ( propFile != null ) {
        	try {
            	propFile.close();
        	} catch(Exception e) {
        		// Don't worry if we can't close the properties file
        	}		
	    }

        // Now construct the tabbed pane
        JTabbedPane tabbedGui = new JTabbedPane();
        tabbedGui.addTab( "Config", mqttComp );
        
        contentPane.add( tabbedGui );
        
 JPanel Control = new JPanel();
 Control.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		lblDevice.setBorder(BorderFactory.createEmptyBorder());
 		lblDevice_1.setBorder(BorderFactory.createEmptyBorder());
 		lblDevice_2.setBorder(BorderFactory.createEmptyBorder());
 		lblDevice_3.setBorder(BorderFactory.createEmptyBorder());
 		check_relay = 4;
 	}
 });
 tabbedGui.addTab("Control", null, Control, null);
 tabbedGui.setEnabledAt(1, true);
 
 panel_11 = new JPanel();
 Control.add(panel_11);
 
 panel_1 = new JPanel();
 panel_11.add(panel_1);
 
 delete = new JButton("Delete");
 delete.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		switch(check_relay) {
 			case 0:  lblDevice.setEnabled(false); led1_1.setBackground(Color.lightGray); led1_2.setBackground(Color.lightGray); relay1_1.setEnabled(false); relay1_2.setEnabled(false); device1.setBackground(Color.lightGray); break;
 			case 1:  lblDevice_1.setEnabled(false); led2_1.setBackground(Color.lightGray); led2_2.setBackground(Color.lightGray); relay2_1.setEnabled(false); relay2_2.setEnabled(false); device2.setBackground(Color.lightGray); break;
 			case 2:  lblDevice_2.setEnabled(false); led3_1.setBackground(Color.lightGray); led3_2.setBackground(Color.lightGray); relay3_1.setEnabled(false); relay3_2.setEnabled(false); device3.setBackground(Color.lightGray); break;
 			case 3:  lblDevice_3.setEnabled(false); led4_1.setBackground(Color.lightGray); led4_2.setBackground(Color.lightGray); relay4_1.setEnabled(false); relay4_2.setEnabled(false); device4.setBackground(Color.lightGray); break;
 		}
 		table.getModel().setValueAt("", check_relay+1, 1); table_1.getModel().setValueAt("", check_relay+1, 1);
 		table.getModel().setValueAt("", check_relay+1, 2); 
 		table.getModel().setValueAt("", check_relay+1, 3); 
		try {
			switch(check_relay) {
				case 0: mqtt.unsubscribe("device/esp000021"); break;
				case 1: mqtt.unsubscribe("device/esp000022"); break;
				case 2: mqtt.unsubscribe("device/esp000023"); break;
				case 3: mqtt.unsubscribe("device/esp000024"); break;
			}					
		} catch (MqttException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}; 
		list_topic[check_relay] = "";
		check_relay = 4;
 	}
 });
 panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
 panel_1.add(delete);
 
 update = new JButton("Update");
 update.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		for(int i=0; i<4; i++) {
 			if(list_topic[i]=="") ;
 			else {
 				boolean pubSuccess = false;
             	String topicName = "server/"+list_topic[i].split("/")[1];
             	String pubText =  "{\"func\":\"6\",\"addr\":\"0\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"0\",\"user\":\"admin\"}";
             	//String pubText1 = "{\"func\":\"1\",\"addr\":\"4\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"0\",\"user\":\"admin\"}";
             	String pubText2 = "{\"func\":\"1\",\"addr\":\"5\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"0\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
             	publish(topicName,pubText2);
 			}
 		}

 	}
 });
 panel_1.add(update);
 
 device1 = new JPanel();
 device1.setBackground(Color.CYAN);
 Control.add(device1);
 
 lblDevice = new JLabel("Device 1:");
 lblDevice.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		if (lblDevice.isEnabled()==true) {
	        		lblDevice.setBorder(BorderFactory.createLineBorder(Color.black));
	        		lblDevice_1.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_2.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_3.setBorder(BorderFactory.createEmptyBorder());
	        		check_relay = 0;
 		}
 	}
 });
 lblDevice.setForeground(SystemColor.desktop);
 lblDevice.setOpaque(true);
 lblDevice.setBackground(SystemColor.inactiveCaptionBorder);
 device1.add(lblDevice);
 relay1_1 = new JButton("Relay 1"); 
 device1.add(relay1_1);
 
 relay1_1.setBounds(10,10,80,20);
 
 led1_1 = new JPanel();
 device1.add(led1_1);
 relay1_2 = new JButton("Relay 2"); 
 device1.add(relay1_2);
 relay1_2.setBounds(10,30,80,20);
 
 led1_2 = new JPanel();
 device1.add(led1_2);
 
 device2 = new JPanel();
 device2.setBackground(Color.CYAN);
 Control.add(device2);
 
 lblDevice_1 = new JLabel("Device 2:");
 lblDevice_1.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		if (lblDevice_1.isEnabled()==true) {
	        		lblDevice.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_1.setBorder(BorderFactory.createLineBorder(Color.black));
	        		lblDevice_2.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_3.setBorder(BorderFactory.createEmptyBorder());
	        		check_relay = 1;
 		}
 	}
 });
 lblDevice_1.setBackground(SystemColor.inactiveCaptionBorder);
 lblDevice_1.setOpaque(true);
 device2.add(lblDevice_1);
 relay2_1 = new JButton("Relay 1"); 
 device2.add(relay2_1);
 
 led2_1 = new JPanel();
 device2.add(led2_1);
 relay2_2 = new JButton("Relay 2");
 device2.add(relay2_2);
 
 led2_2 = new JPanel();
 device2.add(led2_2);
 
 device3 = new JPanel();
 device3.setBackground(Color.CYAN);
 Control.add(device3);
 
 lblDevice_2 = new JLabel("Device 3:");
 lblDevice_2.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		if (lblDevice_2.isEnabled()==true) {
	        		lblDevice.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_1.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_2.setBorder(BorderFactory.createLineBorder(Color.black));
	        		lblDevice_3.setBorder(BorderFactory.createEmptyBorder());
	        		check_relay = 2;
 		}
 	}
 });
 lblDevice_2.setBackground(SystemColor.inactiveCaptionBorder);
 lblDevice_2.setOpaque(true);
 device3.add(lblDevice_2);
 relay3_1 = new JButton("Relay 1"); 
 device3.add(relay3_1);
 relay3_1.setBounds(10,90,80,20);
 
 led3_1 = new JPanel();
 device3.add(led3_1);
 relay3_2 = new JButton("Relay 2"); 
 device3.add(relay3_2);
 relay3_2.setBounds(10,110,80,20);
 
 led3_2 = new JPanel();
 device3.add(led3_2);
 
 device4 = new JPanel();
 device4.setBackground(Color.CYAN);
 Control.add(device4);
 
 lblDevice_3 = new JLabel("Device 4:");
 lblDevice_3.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {  
 		if (lblDevice_3.isEnabled()==true) {
	        		lblDevice.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_1.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_2.setBorder(BorderFactory.createEmptyBorder());
	        		lblDevice_3.setBorder(BorderFactory.createLineBorder(Color.black));  
	        		check_relay = 3;  	
 		}
 	}
 });
 lblDevice_3.setBackground(SystemColor.inactiveCaptionBorder);
 lblDevice_3.setOpaque(true);
 device4.add(lblDevice_3);
 relay4_1 = new JButton("Relay 1");
 device4.add(relay4_1);
 relay4_1.setBounds(10,130,80,20);
 
 led4_1 = new JPanel();
 device4.add(led4_1);
 relay4_2 = new JButton("Relay 2");
 device4.add(relay4_2);
 relay4_2.setBounds(10,150,80,20);
 
 led4_2 = new JPanel();
 device4.add(led4_2);
 
 table = new JTable();
 table.setBorder(new LineBorder(new Color(0, 0, 0)));
 table.setToolTipText("");
 table.setModel(new DefaultTableModel(
 	new Object[][] {
 		{"Device", "Name", "Des Temp", "Des Humid", "Actual Temp", "Actual Humid"},
 		{"1", null, null, null, null, null},
 		{"2", null, null, null, null, null},
 		{"3", null, null, null, null, null},
 		{"4", null, null, null, null, null},
 	},
 	new String[] {
 		"Device", "Name", "Temperature", "Humidity", "Temperature", "Humidity"
 	}
 ) {
 	Class[] columnTypes = new Class[] {
 		String.class, String.class, String.class, Object.class, Object.class, String.class
 	};
 	public Class getColumnClass(int columnIndex) {
 		return columnTypes[columnIndex];
 	}
 });
 table.getColumnModel().getColumn(0).setResizable(false);
 table.getColumnModel().getColumn(0).setPreferredWidth(39);
 table.getColumnModel().getColumn(1).setPreferredWidth(80);
 table.getColumnModel().getColumn(2).setPreferredWidth(61);
 table.getColumnModel().getColumn(3).setPreferredWidth(62);
 table.getColumnModel().getColumn(4).setPreferredWidth(70);
 table.getColumnModel().getColumn(5).setPreferredWidth(70);

 // Align CENTER
 DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
 centerRenderer.setHorizontalAlignment( JLabel.CENTER );
 for (int columnIndex = 0; columnIndex < table.getColumnCount(); columnIndex++)	table.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
 for (int columnIndex = 0; columnIndex < table_1.getColumnCount(); columnIndex++)	table_1.getColumnModel().getColumn(columnIndex).setCellRenderer(centerRenderer);
 Control.add(table);
 
		 relay1_1.addMouseListener(new MouseAdapter() {
		 	@Override
		 	public void mouseReleased(MouseEvent e) {
		 		status = (led1_1.getBackground().getRGB()==Color.red.getRGB())?100:0;
		       			boolean pubSuccess = false;
		     	String topicName = "server/"+list_topic[0].split("/")[1];	
		     	String pubText =  "{\"func\":\"2\",\"addr\":\"1\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
		     	
		     	publish(topicName,pubText);
		 	}
		 });
 
		 relay1_2.addMouseListener(new MouseAdapter() {
		 	@Override
		 	public void mouseReleased(MouseEvent e) {
		 		status = (led1_2.getBackground().getRGB()==Color.red.getRGB())?100:0;
		     	String topicName = "server/"+list_topic[0].split("/")[1];	
		     	String pubText =  "{\"func\":\"2\",\"addr\":\"2\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
		     	publish(topicName,pubText);
		 	}
		 });
 
         relay2_1.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led2_1.getBackground().getRGB()==Color.red.getRGB())?100:0;
         		System.out.println(status);
             	String topicName = "server/"+list_topic[1].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"1\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
         	}
         });
         
         relay2_2.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led2_2.getBackground().getRGB()==Color.red.getRGB())?100:0;
             	String topicName = "server/"+list_topic[1].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"2\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
         	}
         });
         
         relay3_1.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led3_1.getBackground().getRGB()==Color.red.getRGB())?100:0;
             	String topicName = "server/"+list_topic[2].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"1\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
         	}
         });
         
         relay3_2.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led3_2.getBackground().getRGB()==Color.red.getRGB())?100:0;
             	String topicName = "server/"+list_topic[2].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"2\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
         	}
         });
                 
         relay4_1.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led3_1.getBackground().getRGB()==Color.red.getRGB())?100:0;
             	String topicName = "server/"+list_topic[3].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"1\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText);
         	}
         });
                         
         relay4_2.addMouseListener(new MouseAdapter() {
         	@Override
         	public void mouseReleased(MouseEvent e) {
         		status = (led3_2.getBackground().getRGB()==Color.red.getRGB())?100:0;
             	String topicName = "server/"+list_topic[3].split("/")[1];	
             	String pubText =  "{\"func\":\"2\",\"addr\":\"2\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\"" + status + "\",\"user\":\"admin\"}";
             	publish(topicName,pubText); 
         	}
         });
                         lblDevice.setEnabled(false); 
                         led1_1.setBackground(Color.lightGray); 
                         led1_2.setBackground(Color.lightGray); 
                         relay1_1.setEnabled(false); 
                         relay1_2.setEnabled(false); 
                         device1.setBackground(Color.lightGray); 
                         lblDevice_1.setEnabled(false); 
                         led2_1.setBackground(Color.lightGray); 
                         led2_2.setBackground(Color.lightGray); 
                         relay2_1.setEnabled(false); 
                         relay2_2.setEnabled(false); 
                         device2.setBackground(Color.lightGray); 
                         lblDevice_2.setEnabled(false); 
                         led3_1.setBackground(Color.lightGray); 
                         led3_2.setBackground(Color.lightGray); 
                         relay3_1.setEnabled(false); 
                         relay3_2.setEnabled(false); 
                         device3.setBackground(Color.lightGray); 
                         lblDevice_3.setEnabled(false); 
                         led4_1.setBackground(Color.lightGray); 
                         led4_2.setBackground(Color.lightGray); 
                         relay4_1.setEnabled(false); 
                         relay4_2.setEnabled(false); 
                         device4.setBackground(Color.lightGray);
                         
                         panel_12 = new JPanel();
                         FlowLayout flowLayout = (FlowLayout) panel_12.getLayout();
                         flowLayout.setAlignment(FlowLayout.RIGHT);
                         Control.add(panel_12);
                         
                         label_17 = new JLabel("                                                                                                        ");
                         panel_12.add(label_17);
                         
                         btnTest = new JButton("Graph");
                         panel_12.add(btnTest);
                         btnTest.addActionListener(new ActionListener() {
                         	public void actionPerformed(ActionEvent e) {
                         		check_graph = (check_graph)?false:true;
                         		newFrame.pack();
                         		newFrame.setVisible(check_graph);
                         	}
                         });
 
 panel_21 = new JPanel();
 tabbedGui.addTab("Timer", null, panel_21, null);
 
 tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
 panel_21.add(tabbedPane_1);
 
 panel_22 = new JPanel();
 tabbedPane_1.addTab("Device 1", null, panel_22, null);
 
 splitPane_3 = new JSplitPane();
 splitPane_3.setOrientation(JSplitPane.VERTICAL_SPLIT);
 panel_22.add(splitPane_3);
 
 panel_23 = new JPanel();
 splitPane_3.setLeftComponent(panel_23);
 
 splitPane = new JSplitPane();
 splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
 panel_23.add(splitPane);
 
 splitPane_1 = new JSplitPane();
 splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane.setRightComponent(splitPane_1);
 
 splitPane_2 = new JSplitPane();
 splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_1.setRightComponent(splitPane_2);
 
 panel_3 = new JPanel();
 splitPane_2.setLeftComponent(panel_3);
 
 label_4 = new JLabel("Hour:");
 panel_3.add(label_4);
 
 h1_1_2 = new JSpinner();
 h1_1_2.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_3.add(h1_1_2);
 
 label_9 = new JLabel("Minute: ");
 panel_3.add(label_9);
 
 m1_1_2 = new JSpinner();
 m1_1_2.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_3.add(m1_1_2);
 
 label_10 = new JLabel("Action");
 panel_3.add(label_10);
 
 s1_1_2 = new JComboBox();
 s1_1_2.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_1_2.setMaximumRowCount(2);
 panel_3.add(s1_1_2);
 
 splitPane_4 = new JSplitPane();
 splitPane_4.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_2.setRightComponent(splitPane_4);
 
 panel_4 = new JPanel();
 splitPane_4.setLeftComponent(panel_4);
 
 label_11 = new JLabel("Hour:");
 panel_4.add(label_11);
 
 h1_1_3 = new JSpinner();
 h1_1_3.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_4.add(h1_1_3);
 
 label_12 = new JLabel("Minute: ");
 panel_4.add(label_12);
 
 m1_1_3 = new JSpinner();
 m1_1_3.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_4.add(m1_1_3);
 
 label_13 = new JLabel("Action");
 panel_4.add(label_13);
 
 s1_1_3 = new JComboBox();
 s1_1_3.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_1_3.setMaximumRowCount(2);
 panel_4.add(s1_1_3);
 
 panel_5 = new JPanel();
 splitPane_4.setRightComponent(panel_5);
 
 label_14 = new JLabel("Hour:");
 panel_5.add(label_14);
 
 h1_1_4 = new JSpinner();
 h1_1_4.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_5.add(h1_1_4);
 
 label_15 = new JLabel("Minute: ");
 panel_5.add(label_15);
 
 m1_1_4 = new JSpinner();
 m1_1_4.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_5.add(m1_1_4);
 
 label_16 = new JLabel("Action");
 panel_5.add(label_16);
 
 s1_1_4 = new JComboBox();
 s1_1_4.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_1_4.setMaximumRowCount(2);
 panel_5.add(s1_1_4);
 
 panel_2 = new JPanel();
 splitPane_1.setLeftComponent(panel_2);
 
 label = new JLabel("Hour:");
 panel_2.add(label);
 
 h1_1_1 = new JSpinner();
 h1_1_1.setModel(new SpinnerNumberModel(new Byte((byte) 0), null, new Byte((byte) 23), new Byte((byte) 1)));
 panel_2.add(h1_1_1);
 
 label_2 = new JLabel("Minute: ");
 panel_2.add(label_2);
 
 m1_1_1 = new JSpinner();
 m1_1_1.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_2.add(m1_1_1);
 
 lblAction = new JLabel("Action");
 panel_2.add(lblAction);
 
 s1_1_1 = new JComboBox();
 s1_1_1.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_1_1.setMaximumRowCount(2);
 panel_2.add(s1_1_1);
 
 panel = new JPanel();
 splitPane.setLeftComponent(panel);
 
 lblRelay = new JLabel("Relay 1");
 panel.add(lblRelay);
 
 save1_1 = new JButton("Save");
 save1_1.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent arg0) {
 		int a=0, b=0;
 		String[] h1_1 = {h1_1_1.getValue().toString(),h1_1_2.getValue().toString(),h1_1_3.getValue().toString(),h1_1_4.getValue().toString()};
 		String[] m1_1 = {m1_1_1.getValue().toString(),m1_1_2.getValue().toString(),m1_1_3.getValue().toString(),m1_1_4.getValue().toString()};
 		String[] s1_1 = {s1_1_1.getSelectedItem().toString(),s1_1_2.getSelectedItem().toString(),s1_1_3.getSelectedItem().toString(),s1_1_4.getSelectedItem().toString()};
 		for (int i=0;i<4;i++)	{
 			hour[0][i] = h1_1[i];
 			min[0][i] = m1_1[i];
 			stat[0][i] = s1_1[i];
 		}
 		publish("Timeralarm","{\"func\":\"1\",\"addr\":\"1\",\"hour1\":\""+hour[a][0+b]+"\",\"min\":\""+min[a][0+b]+"\",\"status\":\""+stat[a][0+b]+"\",\"hour1\":\""+hour[a][1+b]+"\",\"min\":\""+min[a][1+b]+"\",\"status\":\""+stat[a][1+b]+"\",\"hour1\":\""+hour[a][2+b]+"\",\"min\":\""+min[a][2+b]+"\",\"status\":\""+stat[a][2+b]+"\",\"hour1\":\""+hour[a][3+b]+"\",\"min\":\""+min[a][3+b]+"\",\"status\":\""+stat[a][3+b]+"\",\"user\":\"admin\"}");
 	}
 });
 panel.add(save1_1);
 
 splitPane_8 = new JSplitPane();
 splitPane_8.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_3.setRightComponent(splitPane_8);
 
 panel_6 = new JPanel();
 splitPane_8.setLeftComponent(panel_6);
 
 lblRelay_1 = new JLabel("Relay 2");
 panel_6.add(lblRelay_1);
 
 save1_2 = new JButton("Save");
 save1_2.addMouseListener(new MouseAdapter() {
 	@Override
 	public void mouseClicked(MouseEvent e) {
 		int a = 0, b = 4;
 		String[] h1_2 = {h1_2_1.getValue().toString(),h1_2_2.getValue().toString(),h1_2_3.getValue().toString(),h1_2_4.getValue().toString()};
 		String[] m1_2 = {m1_2_1.getValue().toString(),m1_2_2.getValue().toString(),m1_2_3.getValue().toString(),m1_2_4.getValue().toString()};
 		String[] s1_2 = {s1_2_1.getSelectedItem().toString(),s1_2_2.getSelectedItem().toString(),s1_2_3.getSelectedItem().toString(),s1_2_4.getSelectedItem().toString()};
 		for (int i=0;i<4;i++)	{
 			hour[0][i+4] = h1_2[i];
 			min[0][i+4] = m1_2[i];
 			stat[0][i+4] = s1_2[i];
 		}
 		publish("Timeralarm","{\"func\":\"1\",\"addr\":\"1\",\"hour1\":\""+hour[a][0+b]+"\",\"min\":\""+min[a][0+b]+"\",\"status\":\""+stat[a][0+b]+"\",\"hour1\":\""+hour[a][1+b]+"\",\"min\":\""+min[a][1+b]+"\",\"status\":\""+stat[a][1+b]+"\",\"hour1\":\""+hour[a][2+b]+"\",\"min\":\""+min[a][2+b]+"\",\"status\":\""+stat[a][2+b]+"\",\"hour1\":\""+hour[a][3+b]+"\",\"min\":\""+min[a][3+b]+"\",\"status\":\""+stat[a][3+b]+"\",\"user\":\"admin\"}");
 	}
 });
 panel_6.add(save1_2);
 
 splitPane_9 = new JSplitPane();
 splitPane_9.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_8.setRightComponent(splitPane_9);
 
 panel_7 = new JPanel();
 splitPane_9.setLeftComponent(panel_7);
 
 label_18 = new JLabel("Hour:");
 panel_7.add(label_18);
 
 h1_2_1 = new JSpinner();
 h1_2_1.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_7.add(h1_2_1);
 
 label_19 = new JLabel("Minute: ");
 panel_7.add(label_19);
 
 m1_2_1 = new JSpinner();
 m1_2_1.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_7.add(m1_2_1);
 
 label_20 = new JLabel("Action");
 panel_7.add(label_20);
 
 s1_2_1 = new JComboBox();
 s1_2_1.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_2_1.setMaximumRowCount(2);
 panel_7.add(s1_2_1);
 
 splitPane_10 = new JSplitPane();
 splitPane_10.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_9.setRightComponent(splitPane_10);
 
 panel_8 = new JPanel();
 splitPane_10.setLeftComponent(panel_8);
 
 label_21 = new JLabel("Hour:");
 panel_8.add(label_21);
 
 h1_2_2 = new JSpinner();
 h1_2_2.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_8.add(h1_2_2);
 
 label_22 = new JLabel("Minute: ");
 panel_8.add(label_22);
 
 m1_2_2 = new JSpinner();
 m1_2_2.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_8.add(m1_2_2);
 
 label_23 = new JLabel("Action");
 panel_8.add(label_23);
 
 s1_2_2 = new JComboBox();
 s1_2_2.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_2_2.setMaximumRowCount(2);
 panel_8.add(s1_2_2);
 
 splitPane_11 = new JSplitPane();
 splitPane_11.setOrientation(JSplitPane.VERTICAL_SPLIT);
 splitPane_10.setRightComponent(splitPane_11);
 
 panel_9 = new JPanel();
 splitPane_11.setLeftComponent(panel_9);
 
 label_24 = new JLabel("Hour:");
 panel_9.add(label_24);
 
 h1_2_3 = new JSpinner();
 h1_2_3.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_9.add(h1_2_3);
 
 label_25 = new JLabel("Minute: ");
 panel_9.add(label_25);
 
 m1_2_3 = new JSpinner();
 m1_2_3.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_9.add(m1_2_3);
 
 label_26 = new JLabel("Action");
 panel_9.add(label_26);
 
 s1_2_3 = new JComboBox();
 s1_2_3.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_2_3.setMaximumRowCount(2);
 panel_9.add(s1_2_3);
 
 panel_10 = new JPanel();
 splitPane_11.setRightComponent(panel_10);
 
 label_27 = new JLabel("Hour:");
 panel_10.add(label_27);
 
 h1_2_4 = new JSpinner();
 h1_2_4.setModel(new SpinnerNumberModel(0, 0, 23, 1));
 panel_10.add(h1_2_4);
 
 label_28 = new JLabel("Minute: ");
 panel_10.add(label_28);
 
 m1_2_4 = new JSpinner();
 m1_2_4.setModel(new SpinnerNumberModel(0, 0, 59, 1));
 panel_10.add(m1_2_4);
 
 label_29 = new JLabel("Action");
 panel_10.add(label_29);
 
 s1_2_4 = new JComboBox();
 s1_2_4.setModel(new DefaultComboBoxModel(new String[] {"ON", "OFF"}));
 s1_2_4.setMaximumRowCount(2);
 panel_10.add(s1_2_4);
 
 panel_25 = new JPanel();
 tabbedPane_1.addTab("Device 2", null, panel_25, null);
 
 splitPane_5 = new JSplitPane();
 panel_25.add(splitPane_5);
 
 panel_26 = new JPanel();
 splitPane_5.setLeftComponent(panel_26);
 
 label_1 = new JLabel("Relay 1");
 panel_26.add(label_1);
 
 comboBox_20 = new JComboBox();
 panel_26.add(comboBox_20);
 
 comboBox_21 = new JComboBox();
 panel_26.add(comboBox_21);
 
 panel_27 = new JPanel();
 splitPane_5.setRightComponent(panel_27);
 
 label_3 = new JLabel("Relay 2");
 panel_27.add(label_3);
 
 comboBox_22 = new JComboBox();
 panel_27.add(comboBox_22);
 
 comboBox_23 = new JComboBox();
 panel_27.add(comboBox_23);
 
 panel_28 = new JPanel();
 tabbedPane_1.addTab("Device 3", null, panel_28, null);
 
 splitPane_6 = new JSplitPane();
 panel_28.add(splitPane_6);
 
 panel_30 = new JPanel();
 splitPane_6.setLeftComponent(panel_30);
 
 label_5 = new JLabel("Relay 1");
 panel_30.add(label_5);
 
 comboBox_24 = new JComboBox();
 panel_30.add(comboBox_24);
 
 comboBox_25 = new JComboBox();
 panel_30.add(comboBox_25);
 
 panel_31 = new JPanel();
 splitPane_6.setRightComponent(panel_31);
 
 label_6 = new JLabel("Relay 2");
 panel_31.add(label_6);
 
 comboBox_26 = new JComboBox();
 panel_31.add(comboBox_26);
 
 comboBox_27 = new JComboBox();
 panel_31.add(comboBox_27);
 
 panel_29 = new JPanel();
 tabbedPane_1.addTab("Device 4", null, panel_29, null);
 
 splitPane_7 = new JSplitPane();
 panel_29.add(splitPane_7);
 
 panel_32 = new JPanel();
 splitPane_7.setLeftComponent(panel_32);
 
 label_7 = new JLabel("Relay 1");
 panel_32.add(label_7);
 
 comboBox_28 = new JComboBox();
 panel_32.add(comboBox_28);
 
 comboBox_29 = new JComboBox();
 panel_32.add(comboBox_29);
 
 panel_33 = new JPanel();
 splitPane_7.setRightComponent(panel_33);
 
 label_8 = new JLabel("Relay 2");
 panel_33.add(label_8);
 
 comboBox_30 = new JComboBox();
 panel_33.add(comboBox_30);
 
 comboBox_31 = new JComboBox();
 panel_33.add(comboBox_31);
		
}


	
	
	/**
     * A simple wrapper for the MQTT publish method. This method is invoked as a result of the
     * publish button being pressed. If a problem is detected then an exception is thrown and an error
     * message is displayed in the window title bar or in a separate dialog box.
     * @param topic The topic on which the data will be published.
     * @param message The data to be published
     * @param qos The Quality of Service at which the publication should be delivered.
     * @param retained Is this a retained publication or not?
     * @throws Exception on error
     */
    public void publish( String topic, byte[] message, int qos, boolean retained ) throws Exception {
		setTitleText( "" );

    	if ( connected ) {
    		try {
    		    mqtt.getTopic(topic).publish( message, qos, retained );
    		} catch ( MqttException ex ) {
        		setTitleText( "MQTT publish exception !" );
        		JOptionPane.showMessageDialog( frame, ex.getClass().getName() + "\n" + ex.getMessage(), "MQTT Publish Exception", JOptionPane.ERROR_MESSAGE );
        		throw ex;
    		}	
    	} else {
    		setTitleText( "MQTT client not connected !" );
    		throw new Exception( "MQTT client not connected" );
    	}		
    }	
    
    /**
     * A wrapper for the MQTT disconnect method.
     * As well as disconnecting the protocol this method enables / disables buttons
     * as appropriate when in disconnected state. It also sets the correct colour of the indicator LED.
     */
    public void disconnect() {
		connected = false;

        // Notify connectionLost to give up. It may be running..
		synchronized(connLostWait) {
   			connLostWait.notify();
		}

		// Disconnect from the broker
		if ( mqtt != null ) {
  			try {
  				mqtt.disconnect();
   			} catch ( Exception ex ) {
    			setTitleText( "MQTT disconnect error !" );
   				ex.printStackTrace();
   				System.exit(1);
   			}	 
   		}		

        // Set the LED state correctly
		// If the led is flashing then turn it off

        setConnected( false );
    }	
    
    /**
     * A wrapper for the MQTT connect method. If the ip address, port number or persistence flag
     * has changed since the last time then a new MqttClient object is required. If these values haven't changed then
     * any previously created object can be used.
     * Check whether Last Will and Testament is required and call the appropriate connect method.
     * The only persistence implementation supported at the moment is MqttFilePersistence.
     * @param connStr Connection string
     * @param usePersistence Is persistence required?
     * @throws MqttException on error
     */
    public void connect( String connStr, boolean usePersistence ) throws MqttException {
		// Connect to the broker
    	// If we have a MqttClient object and the new ip address
		// or port number is not equal to the previous, or the persistence flag changes between
		// off and on then we need a new object.
		if ( (mqtt != null) &&
		     (!connStr.equals(mqtt.getServerURI()) /*||
		      (usePersistence != (mqtt.getPersistence() != null) )*/ ) ) {
		    //mqtt.terminate();
			mqtt = null;
		}	
		if ( mqtt == null ) {
			MqttClientPersistence persistence = null;
			if ( usePersistence ) {
				persistence = new MqttDefaultFilePersistence( "" );
			}	
			String uniqueID = UUID.randomUUID().toString();
			System.out.println(uniqueID);
			mqtt = new MqttClient( connStr, uniqueID, persistence );
    	    mqtt.setCallback( this );
    	    
		}	
        
        // Set the retry interval for the connection
        //mqtt.setRetry( optionsComp.getRetryInterval() );
		opts = new MqttConnectOptions();
		opts.setCleanSession(true);
		opts.setKeepAliveInterval(30);
        
	mqtt.connect(opts);
    }

    /**
     * This method is called when either the subscribe or unsubscribe buttons are pressed. It performs the 
     * MQTT subscribe or unsubscribe and writes an entry in the history log if the history window is open.<BR>
     * Any exceptions are caught and displayed in a dialog box.
     * @param topic The topic to subscribe to
     * @param qos The maximum Quality of Service at which to receive publications
     * @param sub Is this a subscribe or unsubscribe operation? (true if subscribe).
     */     			
    public void subscription( String topic, int qos, boolean sub ) {
		setTitleText( "" );

    	if ( connected ) {
    		try {
       		    String[] theseTopics = new String[1];
    		    int[] theseQoS = new int[1];
    		  
    		    theseTopics[0] = topic;
    		    theseQoS[0] = qos;

        	      
                if ( sub ) {
        		    mqtt.subscribe( theseTopics, theseQoS );
                } else {
                    mqtt.unsubscribe( theseTopics );
                }	  
    		  
    		} catch ( Exception ex ) {
        		setTitleText( "MQTT subscription exception caught !" );
        		JOptionPane.showMessageDialog( frame, ex.getMessage(), "MQTT Subscription Exception", JOptionPane.ERROR_MESSAGE );
    		}	

		    for (int i=0; i<4; i++) {
		    	if(list_topic[i]=="") {
		    		list_topic[i] = topic; 
		    		table.getModel().setValueAt(topic.split("/")[1], i+1, 1);
		    		table_1.getModel().setValueAt(topic.split("/")[1], i+1, 1);
		    		switch(i) {
            			case 0:  lblDevice.setEnabled(true); relay1_1.setEnabled(true); relay1_2.setEnabled(true); device1.setBackground(Color.CYAN);; break;
            			case 1:  lblDevice_1.setEnabled(true); relay2_1.setEnabled(true); relay2_2.setEnabled(true); device2.setBackground(Color.CYAN); break;
            			case 2:  lblDevice_2.setEnabled(true); relay3_1.setEnabled(true); relay3_2.setEnabled(true); device3.setBackground(Color.CYAN); break;
            			case 3:  lblDevice_3.setEnabled(true); relay4_1.setEnabled(true); relay4_2.setEnabled(true); device4.setBackground(Color.CYAN); break;
            		}
		    		i=4;
		    	}
		    }
    	} else {
    		setTitleText( "MQTT client not connected !" );
    	}		
    }	
    
    /** Invoked by actionPerformed when connect is pressed. 
     *  This allows actionPerformed to return and paint the window. This thread
     *  then does the MQTT connect to the broker.<BR>
     *  This method also ensures that the LED colour is set correctly and writes
     *  an entry to the history dialog if it is open.
     */
    public void run() {
    	int rc = -1;
    	
		// Connect to the broker
		String ipAddr = (String)ipAddress.getSelectedItem();
		String portNum = (String)port.getSelectedItem();
		String connStr = "";
   	 	try {
			// If the entry in the IP Address drop down list contains '://' then assume
			// the connection has been explicitly entered as tcp://ip_address:port or local://broker_name.
			// Otherwise read the ip address and port number from their respective drop downs.
			if ( ipAddr.indexOf("://") < 0 ) {
    			connStr = "tcp://" + ipAddr + ":" + portNum;
   	 		} else {
   	 			connStr = ipAddr;
   	 		}
	
    		connect( connStr, false );
			
			// Successful connect(no exception). Remember the ipAddress and port in the drop downs
   			updateComboBoxList( ipAddress, ipAddr );
    		updateComboBoxList( port, portNum );
   	    	connected = true;
	    	setConnected( true );
			
		} catch( NumberFormatException nfe ) {
       		JOptionPane.showMessageDialog( frame, "Invalid port number !", "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
		} catch ( MqttException mqe ) {
       		setTitleText( "MQTT connect failed !" );
       		Throwable e = mqe.getCause();
       		String msg = "";
       		if ( e == null ) {
       			e = mqe;
       		} else if ( mqe.getMessage() != null ) {
       			msg += mqe.getMessage() + "\n";
       		}
       		msg += e;
       		JOptionPane.showMessageDialog( frame, msg, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
       		e.printStackTrace();
		} catch ( Exception ex ) {
       		setTitleText( "MQTT connect failed !" );
       		JOptionPane.showMessageDialog( frame, ex, "MQTT Connect Exception", JOptionPane.ERROR_MESSAGE );
       		ex.printStackTrace();
  		}
    		
    	if ( !connected ) {
    		setConnected( false );
    	}

    }
    		
    /**
     *  Implement the ActionListener interface and catch user interface events. Button pressed events handled are as follows:
     * <UL><LI>Connect - Check the client is not already connected, set the LED colour to amber to indicate connect is in progress and
     * start a thread to do the connect.
     * <LI>Disconnect - Check the client is connected and then disconnect the MQTT protocol
     * <LI>Otions - If the options dialog is opened then reset it's size and position to default values before making it visible
     * </UL>
     * @param e The action event to process.
     */
   	public void actionPerformed( ActionEvent e) {
   		
		setTitleText( "" );

   		if ( e.getActionCommand().equals("Connect") ) {
   			// When the connect button is pressed we are either connected or not connected
   			// If we are connected then say so.
   			// If we are not connected then
   			//      1. Set the LED to Amber, the state to connecting and start a thread to do the actual connect.
   			//         This allows the GUI thread to return and paint the window correctly
   			if ( connected ) {
   				    // Already connected
            		setTitleText( "MQTT session already active !" );
   			} else {
   				// Initialise the GUI prior to connecting by setting the LED to amber.
   				// Start a thread to do the connect.
   				connect.setEnabled(false);
   				connected = false;
   				new Thread(this).start();
   			}	
   		} else if ( e.getActionCommand().equals("Disconnect") ) {
   			if ( connected ) {
    			// Disconnect from the broker
    			disconnect();
   			}	else {
    			setTitleText( "MQTT client not connected !" );
   			}	
   		} 
        		
   	}	
   	
   	/**
   	 * This method accepts a string on text and displays it in the window's title bar.
   	 * @param extraText The text to be appended to some default words and displayed.
   	 */
   	// Synchronized as this may also be called on the connectionLost thread, which is
   	// created by the Java MQTT Client
   	public synchronized void setTitleText( String extraText ) {
   		if ( extraText.equals("") ) {
     		frame.setTitle( "Paho MQTT Utility" );
   		} else {
    		frame.setTitle( "Paho MQTT Utility - " + extraText );
   		}		
   	}	

    /**
     * The method is part of the MqttSimpleCallback interface
     * <BR>In the event of the MQTT connection being broken the LED is set to colour amber and made to flash.
     * The code then keeps trying to reconnect until either a successful
     * reconnect occurs or the disconnect button is pressed. Finally the LED is stopped flashing and set to 
     * green or red depending upon whether the connect was successful or not.
     */
    public void connectionLost(java.lang.Throwable cause) {
    	int rc = -1;
    	
    	
    	setTitleText( "Connection Lost!....Reconnecting" );

    	try {
    		// While we have failed to reconnect and disconnect hasn't
    		// been called by another thread retry to connect
    		while ( (rc == -1) &&  connected ) {
    			
           		try {
           			synchronized(connLostWait) {
               			connLostWait.wait(10000);
           			}
           		} catch (InterruptedException iex) {
       	    		// Don't care if we are interrupted
       		    }		

        	    synchronized(this) { // Grab the log synchronisation lock
        	    	if ( connected ) {
        	    		try {
				    connect( mqtt.getServerURI(), true);
        	    			rc = 0;
        	    		} catch (MqttException mqte) {
        	    			// Catch any MQTT exceptions, set rc to -1 and retry
        	    			rc = -1;
        	    		}		
        	    	}
        	    }
    		}	
    		// Remove title text once we have reconnected
        	setTitleText( "" );
    	} catch (Exception ex) {
    		setTitleText( "MQTT connection broken !" );
    		ex.printStackTrace();
    		disconnect();
    		//throw ex;    	
    	}	
    	
    	// If we get here and we are connected then set the led to green
    	if ( connected ) {
    		setConnected( true );
    	} else {
    		setConnected( false );
    	}	
    }	
    
    /**
     * The method is part of the MqttCallback interface<BR>
     * Pass the message as is to the SubPanel object which will display it.
     */
    public void messageArrived( String topic, MqttMessage message) {
	try {
		String msg = new String(message.getPayload());
		String[] list = msg.split("\"");
		//{"user":"ESP8266","func":"1","addr":"6","year":"0","mon":"0","day":"0","hour":"0","min":"0","data":"100"}
		//func:7; addr:11; data:35
		String func = list[7], addr = list[11], data = list[35];
		System.out.println("topic= "+topic+" func= "+func+" addr= "+addr+" data= "+data);
		if (func.equals("1")||func.equals("6")) {
			if(topic.equals(list_topic[0])) {
				if(addr.equals("1")) {
					if(data.equals("0"))	led1_1.setBackground(Color.red);
					else led1_1.setBackground(Color.green);
				}
				else if(addr.equals("2")) {
					if(data.equals("0"))	led1_2.setBackground(Color.red);
					else led1_2.setBackground(Color.green);
				}
				else if(addr.equals("4")) {
				}
				else if(addr.equals("5")) {
					temp_real[0] = data;
					table.getModel().setValueAt(data, 1, 4);	//row, column
					temp_des[0] = table.getModel().getValueAt(1, 2).toString();	//row, column
					//System.out.println("temp_des[0]= "+temp_des[0]);
					check_onoff_temp[0]=true;
				}
				else if(addr.equals("6")) {
					table.getModel().setValueAt(data, 1, 5);
				}
			}
			else if(topic.equals(list_topic[1])) {
				if(addr.equals("1")) {
					if(data.equals("0"))	led2_1.setBackground(Color.red);
					else led2_1.setBackground(Color.green);
				}
				else if(addr.equals("2")) {
					if(data.equals("0"))	led2_2.setBackground(Color.red);
					else led2_2.setBackground(Color.green);
				}
				else if(addr.equals("4")) {
				}
				else if(addr.equals("5")) {
					temp_real[1] = data;
					table.getModel().setValueAt(data, 2, 4);	
					temp_des[0] = table.getModel().getValueAt(2, 2).toString();	
					check_onoff_temp[1]=true;
				}
				else if(addr.equals("6")) {
					table.getModel().setValueAt(data, 2, 5);
				}
			}
			else if(topic.equals(list_topic[2])) {
				if(addr.equals("1")) {
					if(data.equals("0"))	led3_1.setBackground(Color.red);
					else led3_1.setBackground(Color.green);
				}
				else if(addr.equals("2")) {
					if(data.equals("0"))	led3_2.setBackground(Color.red);
					else led3_2.setBackground(Color.green);
				}
				else if(addr.equals("4")) {
				}
				else if(addr.equals("5")) {
					temp_real[2] = data;
					table.getModel().setValueAt(data, 3, 4);
					temp_des[0] = table.getModel().getValueAt(3, 2).toString();	
					check_onoff_temp[2]=true;
				}
				else if(addr.equals("6")) {
					table.getModel().setValueAt(data, 3, 5);
				}
			}
			else if(topic.equals(list_topic[3])) {
				if(addr.equals("1")) {
					if(data.equals("0"))	led4_1.setBackground(Color.red);
					else led4_1.setBackground(Color.green);
				}
				else if(addr.equals("2")) {
					if(data.equals("0"))	led4_2.setBackground(Color.red);
					else led4_2.setBackground(Color.green);
				}
				else if(addr.equals("4")) {
				}
				else if(addr.equals("5")) {
					temp_real[3] = data;
					table.getModel().setValueAt(data, 4, 4);
					temp_des[0] = table.getModel().getValueAt(4, 2).toString();	
					check_onoff_temp[3]=true;
				}
				else if(addr.equals("6")) {
					table.getModel().setValueAt(data, 4, 5);
				}
			}
		}
		else if (func.equals("254")) {
			switch (data) {
				case "1": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": ESP parse không được chuỗi Json"); break;
				case "2": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Time out (Không thấy frame phản hồi của STM (F4))"); break;
				case "3": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Đếm đủ ký tự nhưng ký tự kết thúc không phải là ‘#’ hoặc kí tự bắt đầu ko phải là ‘*’"); break;
				case "4": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Buffer full, gửi lại sau!"); break;
				case "5": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": ESP nhận không đủ Frame"); break;
				case "17": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Wrong frame!"); break;
				case "18": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Wrong function!"); break;
				case "19": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Wrong data!"); break;
				case "20": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": RTC module disconnected"); break;
				case "21": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": Sensor disconnected"); break;
				case "22": JOptionPane.showMessageDialog(null, topic.split("/")[1] + ": STM Reset (watchdog timer)"); break;
			}
		}
	}
	catch (Exception e)
	    {
	    }
    }	

    private static void on_off_temp(String des, String real, String topic) {
    	float des_value = Float.parseFloat(des);
    	if (des == "") des_value = 0;
    	float real_value = Float.parseFloat(real);
//    	System.out.println("1530: "+real_value);
//     	//String topicName = "server/"+topic.split("/")[1];	
//    	//System.out.println("1527:"+topicName);
     	String data_comp = "100";
     	data_comp = (des_value>real_value)?"100":"0";
     	publish("server/esp000021", "{\"func\":\"2\",\"addr\":\"2\", \"year\":\"0\",\"mon\":\"0\",\"day\":\"0\",\"hour\":\"0\",\"min\":\"0\",\"data\":\""+data_comp+"\",\"user\":\"admin\"}");
		//if (des_value>real_value)	System.out.println("1536: ON");
		//else	System.out.println("1537: OFF");
    }
    
    private static void publish(String topic, String pubText) {
		boolean pubSuccess = false;;
		System.out.println(topic+" + "+pubText);
	 	Exception pubExp = null;
	 	try {
	         mqtt.publish( topic, pubText.getBytes(), 0, false);                  
	         pubSuccess = true;
	 	} catch( Exception ex) {
	 		// Publish failed
	 		System.out.println("error 1549");
	 		pubSuccess = false;
	 		pubExp = ex;
	 	}  
    }


	public void deliveryComplete( IMqttDeliveryToken token ) {
	
    }
    
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
    	System.out.println("53");
  		System.out.println("Connection ok");
  		if(reconnect) {
//  			try {
//  				client.subscribe("test1/esp", 1);
//  			} catch (MqttException e) {
//  				// TODO Auto-generated catch block
//  				e.printStackTrace();
//  			}
  		}
  	}

    /**
     * If a new topic is typed into the publish topic box then the
     * subscribe topic box needs to be updated.
     * @param topicName The topic name to add to the subscribe topic list
     */
    public void updateSubscribeTopicList( String topicName ) {
    	subPanelContr.updateTopicList( topicName );
    }	

    
    /**
     * This method handles string items in JComboBox drop downs. It checks
     * to see if an item already exists in the list. If it doesn't, then it updates the list.
     * @param list The JComboBox object to update
     * @param itemName The value to add into the list if it doesn't already exist
     * @return True if the item was added to the list
     */
    public boolean updateComboBoxList( JComboBox list, String itemName ) {
    	int listCount = list.getItemCount();
    	boolean found = false;
    	
    	if ( itemName == null ) {
    		return false;
    	}
    		
    	for( int i=0; i<listCount; i++ ) {
    		if ( ((String)list.getItemAt(i)).equals(itemName) ) {
    			// This topic already exists in the list, so don't add it again
    			found = true;
    			break;
    		}	
    	}	
    	
    	if ( !found ) {
    		list.addItem( itemName );
    		return true;
    	}	
    	
    	return false;
    }
        
    
    /** Add properties from a Properties object to a JComboBox. The properties to add
     *  are selected using the specified key. Each key returns a single string, which may be
     *  further broken down into substrings delimited by the PROP_DELIM character.
     *  If no properties are found then the default value is used.
     * @param props The properties object from which to get the property value.
     * @param comp The drop down box to which the tokens within the key must be added.
     * @param key The key to identify the property to access.
     * @param defValue The default value for the key if no value is found.
     */
    public void getProperties( Properties props, JComboBox comp, String key, String defValue ) {
         	String parms = props.getProperty( key, defValue );
    	
    	StringTokenizer st = new StringTokenizer( parms, PROP_DELIM );
    	
       	while ( st.hasMoreTokens() ) {
       		comp.addItem( st.nextToken() );
       	}	
    }	

    /**
     *  For a given property key construct a value to associate with the key
     *  The value can then be inserted in a properties object
     * @param prop The property to construct a string value for.
     * @return A string to write into the properties file.
     */
    public String constructPropertyValue( String prop ) {
    	String retString = null;

        // For IPADDRESS and IPPORT there may be mulitple values, so
        // delimit the values with the PROP_DELIM character     	
    	if ( prop.equals("IPAddressList") ) {
            int numAddrs = ipAddress.getItemCount();
            if (numAddrs > 0) {
            	retString = "";
            } else {
            	retString = DEFAULT_IP_ADDRESS;
            }	
            for( int i=0; i < numAddrs; i++ ) {
            	retString += ipAddress.getItemAt(i);
            	// Don't add a delimiter after the last token
            	if ( i != numAddrs - 1 ) {
                	retString += PROP_DELIM;
            	}
            }	
    	} else if ( prop.equals("IPPortList") ) {
            int numPorts = port.getItemCount();
            if (numPorts > 0) {
            	retString = "";
            } else {
            	retString = DEFAULT_PORT_NUMBER;
            }	
            for( int i=0; i < numPorts; i++ ) {
            	retString += port.getItemAt(i);
            	// Don't add a delimiter after the last token
            	if ( i != numPorts - 1 ) {
                	retString += PROP_DELIM;
            	}
            }	
    	} else if ( prop.equals("ClientId") ) {
    		retString = "HuyBeo";
    	} else if ( prop.equals("Persistence") ) {
    		retString = String.valueOf( false );
    	} else if ( prop.equals("PersistenceDir") ) {
    		retString = "";
    	}			
    	
    	return retString;
    }	

    /**
     *  If we are not connected then disable the disconnect, publish,
     *  subscribe, and unsubscribe buttons. Enable connect.
     *  If we are connected then do the inverse.
     * @param b True if connected, false otherwise.
     */
    private void setConnected( boolean b ) {
   		subPanelContr.enableButtons( b );
   		disconnect.setEnabled( b );
   		connect.setEnabled( !b );
    }	
    	
    	
}


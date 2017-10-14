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
 * Description: Controls and lays out the subscription panel on the main window
 *
 * Contributors:
 *    Ian Harwood, Ian Craggs - initial API and implementation and/or initial documentation
 ********************************************************************************/

package org.eclipse.paho.controltemp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

/**
 * This class creates a panel that contains all the components to do with
 * subscribing for and receiving publications.
 */
public class SubPanel implements ActionListener {
	private JPanel subPanel;
	private JComboBox topic;
	private MQTTFrame mqttMgr = null;
    private JLabel subLabel = null;
    private JButton subButton;
    private static final String PANEL_TITLE = " Config Device";
    
    /**
     * The constructor for the subscription panel
     * @param theSubPanel The JPanel component into which all other components are placed
     * @param aMqttMgr The MQIsdpFrame object through which WMQtt communication is done.
     */        
	public SubPanel( JPanel theSubPanel, MQTTFrame aMqttMgr ) {
		subPanel = theSubPanel;
        subPanel.setBorder( new EtchedBorder() );
        mqttMgr = aMqttMgr;
        init();
	}	
	
    /**
     * The init method builds all the required components and adds them to the
     * subscription panel. Different layout managers are used throughout to ensure that the components
     * resize correctly as the window resizes.
     */	
	public void init() {
		subPanel.setLayout( new BorderLayout() );

        // Create field to specify topics to subscribe to / unsubscribe from
        // Set a maximum size for it to stop it sizing vertically
		topic = new JComboBox();
		topic.setEditable( true );
        topic.setMaximumSize(MQTTFrame.TEXT_FIELD_DIMENSION);
        topic.setMaximumRowCount(5);



        // Create the components to go in the NORTH panel
        // Create a horizontal box layout in which to display the topic sent and its label
        JPanel sendTopicPanel = new JPanel();
        sendTopicPanel.setLayout( new BoxLayout( sendTopicPanel, BoxLayout.X_AXIS) );
        sendTopicPanel.add( new JLabel(" Subscribe Topic:") );
        sendTopicPanel.add( topic );
        // Keep the qoslist from being right up against the edge of the window by adding some spaces
        sendTopicPanel.add( new JLabel("  ") );

        
        // Add some white space in to stop the receivedTopic field being sized to the whole width of the GUI
        // The white space occupies the same space as the Request QoS JLabel, so the sent and rcvd topic fields line up reasonably well
        // rcvdTopicPanel.add( new JLabel("                                       ") );

        // Now to incorporate a title into the panel we place the two topic fields in a vertical
        // layout with the title "Subscribe To Topics"
        JPanel titleAndTopics = new JPanel();
        titleAndTopics.setLayout( new GridLayout( 3, 1 ) );
        
        // Add a title in BOLD        
        subLabel = new JLabel( PANEL_TITLE);
        Font f = subLabel.getFont();
        subLabel.setFont( new Font( f.getName(), Font.BOLD, f.getSize() + 1 ) );
        
        // Add all the fields that will make up the NORTH panel
        titleAndTopics.add( subLabel );
        titleAndTopics.add( sendTopicPanel );
        
        // Create the components to go in the EAST panel
        // Add the buttons to a FlowLayout to stop them resizing
		JPanel subButtonsLayout = new JPanel();
        JPanel buttons = new JPanel();
        buttons.setLayout( new GridLayout(4,1) );

    	subButton = new JButton( "Subscribe" );
    	subButton.setEnabled(false);
		subButton.addActionListener( this );
        
        buttons.add( subButton );

		subButtonsLayout.add( buttons );

        // Add the Title and Topic fields to the NORTH panel
        // Add the received publications text area to the CENTER
        // Add the Subscribe and Unsubscribe buttons to the EAST       
        subPanel.add( titleAndTopics, BorderLayout.NORTH );
        subPanel.add( subButtonsLayout, BorderLayout.EAST );

   	}	
		
    /**
     * For any requests to add a topic to the publish drop down box
     * use the updateComboBoxList method in class MQIsdpFrame to do the job.
     */
    public boolean updateTopicList( String topicName ) {
        return mqttMgr.updateComboBoxList( topic, topicName );
    }

    /**
     * ActionListener interface<BR>
     * Listen out for the Subscribe button, Unsubscribe button, Save button or the Hex/Text button being pressed
     * <BR>Subscribing / Unsubscribing for data involves:<BR>
     * <UL><LI>Updating the drop down boxes with the topic if necessary
     * <LI>Subscribing / Unsubscribing for the data
     * </UL>
     * <BR>Processing the Save button involves poping up a file dialog, opening the file, writing the data and closing the file.
     * <BR>Processing the Hex/Text button presses involves converting the display between a text character representation
     * and hexadecimal character representation.
     */	    		  
    public void actionPerformed( ActionEvent e ) {
    	String topicName = (String)topic.getSelectedItem();
    	
    	if ( e.getActionCommand().equals( "Subscribe" ) ) {
            // Subscribe
            mqttMgr.subscription( topicName, 1, true );
            
        } else if ( e.getActionCommand().equals("Unsubscribe") ){
            // Unsubscribe
            //mqttMgr.subscription( topicName, 0, false );
    	}  
    }    
    
    /**
     * This enables or disables the subscribe and unsubscribe buttons depending on the value of the boolean.
     * @param b Button enabled if true, otherwise disabled.
     */
    public void enableButtons( boolean b ) {
   		subButton.setEnabled( b );
    }	
    
    
}

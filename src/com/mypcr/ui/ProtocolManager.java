package com.mypcr.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.mypcr.beans.Action;
import com.mypcr.constant.ProtocolConstants;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.Functions;
import com.mypcr.handler.Handler;
import com.mypcr.ui.custom.ProtocolViewer;

public class ProtocolManager extends JDialog implements WindowListener, ActionListener, Handler {
	private static final long serialVersionUID = 1L;

	private Handler handler = null;
	
	private ProtocolViewer viewer = null;
	
	private JPanel uiPanel, viewerPanel, buttonPanel, listPanel, selectPanel;
	private JLabel totalTimeLabel = null, selectProtocolLabel = null;
	private JButton buttonCreate, buttonSelect, buttonEdit, buttonRemove;
	private JComboBox<String> comboProtocol;
	
	private ArrayList<Action[]> actions = null;
	
	public static final int EVENT_PROTOCOL_LIST_REFRESH = 0x01;
	
	public ProtocolManager(Handler handler){
		this.handler = handler;
		
		setSize(ProtocolViewer.PREFERENCE_WIDTH + 45, 585);
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());
		setTitle("Protocol Manager");
		
		initComponents();
		loadProtocolList();
	}
	
	private void initComponents(){
		// for entire ui
		uiPanel = new JPanel();
		uiPanel.setLayout(null);
		uiPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		
		// for button menu
		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		buttonPanel.setBorder(BorderFactory.createTitledBorder("Protocol Management"));
		buttonPanel.setBounds(10, 10, ProtocolViewer.PREFERENCE_WIDTH+20, 70);
		
		buttonCreate = new JButton("Create New");
		buttonCreate.setBounds(15, 25, 100, 30);
		buttonCreate.addActionListener(this);
		
		JLabel labelNotice = new JLabel("* 모든 프로토콜은 공유되어 사용됩니다.");
		labelNotice.setBounds(450, 20, 250, 30);
		
		// For combobox border
		listPanel = new JPanel();
		listPanel.setLayout(null);
		listPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		listPanel.setBorder(BorderFactory.createTitledBorder("Existing Protocols"));
		listPanel.setBounds(ProtocolViewer.PREFERENCE_WIDTH-400, 10, 410, 50);
		
		comboProtocol = new JComboBox<String>();
		comboProtocol.setBounds(15, 17, 380, 25);
		comboProtocol.addActionListener(this);
		
		listPanel.add(comboProtocol);
		
		buttonPanel.add(buttonCreate);
		buttonPanel.add(labelNotice);
		buttonPanel.add(listPanel);
		
		// for protocol manipulation menu
		selectPanel = new JPanel();
		selectPanel.setLayout(null);
		selectPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		selectPanel.setBorder(BorderFactory.createTitledBorder("Selected Protocol"));
		selectPanel.setBounds(10, 90, ProtocolViewer.PREFERENCE_WIDTH+20, 70);
		
		selectProtocolLabel = new JLabel("Default Protocol");
		selectProtocolLabel.setBounds(20, 30, 200, 20);
		
		buttonSelect = new JButton("Use Selected...");
		buttonSelect.setBounds(ProtocolViewer.PREFERENCE_WIDTH-345, 25, 100, 30);
		buttonSelect.setBorder(BorderFactory.createLineBorder(Color.RED));
		buttonSelect.addActionListener(this);
		
		buttonEdit = new JButton("Edit Selected...");
		buttonEdit.setBounds(ProtocolViewer.PREFERENCE_WIDTH-235, 25, 100, 30);
		buttonEdit.addActionListener(this);
		
		buttonRemove = new JButton("Remove Selected...");
		buttonRemove.setBounds(ProtocolViewer.PREFERENCE_WIDTH-125, 25, 120, 30);
		buttonRemove.addActionListener(this);
		
		selectPanel.add(selectProtocolLabel);
		selectPanel.add(buttonSelect);
		selectPanel.add(buttonEdit);
		selectPanel.add(buttonRemove);
		
		// for bordering viewer
		viewerPanel = new JPanel();
		viewerPanel.setLayout(null);
		viewerPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		viewerPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		viewerPanel.setBounds(10, 170, ProtocolViewer.PREFERENCE_WIDTH+20, ProtocolViewer.PREFERENCE_HEIGHT+60);
		
		viewer = new ProtocolViewer();
		
		JLabel leftTimeStatic = new JLabel("Total Run Time: ");
		leftTimeStatic.setBounds(20, 23, 90, 20);
		
		totalTimeLabel = new JLabel("00:00:00");
		totalTimeLabel.setBounds(115, 23, 100, 20);
		
		JScrollPane scrollPane = new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 50, ProtocolViewer.PREFERENCE_WIDTH, ProtocolViewer.PREFERENCE_HEIGHT);
		
		viewerPanel.add(leftTimeStatic);
		viewerPanel.add(totalTimeLabel);
		viewerPanel.add(scrollPane);
		
		uiPanel.add(buttonPanel);
		uiPanel.add(selectPanel);
		uiPanel.add(viewerPanel);
		add(uiPanel);
	}
	
	public void showDialog(){
		setVisible(true);
	}
	
	private void loadProtocolList(){
		actions = Functions.enumProtocols();
		
		if( actions.isEmpty() ){
			// 저장된 protocol 이 없는 경우, 저장된 protocol 들을 불러 오도록 한다.
			actions = ProtocolConstants.getBuiltProtocols();
			
			for(int i=0; i<actions.size(); ++i)
				Functions.saveProtocol(actions.get(i), actions.get(i)[0].getProtocolName());
			
			JOptionPane.showMessageDialog(null, "저장된 Custom protocol 이 존재하지 않아, 프로그램 내에 내장된 Protocol 이 생성되었습니다.");
		}
		
		comboProtocol.removeAllItems();
		
		// action List 에 있는 protocol 들을 모두 불러온다.
		for(int i=0; i<actions.size(); ++i){
			Action[] tempActions = actions.get(i);
			
			comboProtocol.addItem(tempActions[0].getProtocolName());
		}
	}
	
	private void loadProtocol(int selectedIndex){
		if( selectedIndex == -1 )
			return;
		
		if( selectedIndex >= actions.size() )
			throw new RuntimeException("software program has some problems.");
		
		viewer.clearProtocols();
		
		Action[] action = actions.get(selectedIndex);
		
		// Setting the protocol names
		selectProtocolLabel.setText(action[0].getProtocolName());
		totalTimeLabel.setText(Functions.calcTotalTime(action));
		
		int prevTemp = 25, gotoCount = 0;
		for(int i=0; i<action.length; ++i){
			int param1 = Integer.parseInt(action[i].getTemp());
			int param2 = Integer.parseInt(action[i].getTime());
			
			if( action[i].getLabel().equalsIgnoreCase("GOTO") ){
				viewer.appendGotoProtocol(i+1, param1+gotoCount, param2);
				gotoCount++;
			}
			else{
				viewer.appendProtocol(i+1, prevTemp, param1, param2);
				prevTemp = param1;
			}
		}
		
		viewer.appendEnd();
		viewer.updateProtocol();
	}
	
	@Override	
	public void windowClosed(WindowEvent e){
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object s = e.getSource();
		
		if( s == buttonCreate ){
			ProtocolEditor editor = new ProtocolEditor(this);
			editor.showDialog();
		}
		else if( s == buttonSelect ){
			String selectedProtocol = (String)comboProtocol.getItemAt( comboProtocol.getSelectedIndex() );
			Functions.Save_RecentProtocol(selectedProtocol);
			if( handler != null )
				handler.OnHandleMessage(ButtonUI.MESSAGE_PROTOCOL_SELECTED, actions.get(comboProtocol.getSelectedIndex()));
			
			dispose();
		}
		else if( s == buttonEdit ){
			int selectedIndex = comboProtocol.getSelectedIndex();
			
			ProtocolEditor editor = new ProtocolEditor(this, actions.get(selectedIndex), comboProtocol.getItemAt(selectedIndex));
			editor.showDialog();
		}
		else if( s == buttonRemove ){
			String selectedProtocol = (String)comboProtocol.getItemAt( comboProtocol.getSelectedIndex() );
			Functions.removeProtocol(selectedProtocol);
			
			JOptionPane.showMessageDialog(null, selectedProtocol + " 이 삭제되었습니다.");
			
			loadProtocolList();
		}
		else if( s == comboProtocol ){
			loadProtocol(comboProtocol.getSelectedIndex());
		}
	}
	
	@Override
	public void OnHandleMessage(int MessageType, Object data) {
		switch(MessageType){
			case EVENT_PROTOCOL_LIST_REFRESH:
				loadProtocolList();
				
				String selectedProtocol = (String)data;
				for(int i=0; i<comboProtocol.getItemCount(); ++i){
					if( comboProtocol.getItemAt(i).equals(selectedProtocol) ){
						comboProtocol.setSelectedIndex(i);
						break;
					}
				}
				break;
		}
	}

	// Not used
	@Override	public void windowOpened(WindowEvent e) {}
	@Override	public void windowClosing(WindowEvent e) {}
	@Override	public void windowIconified(WindowEvent e) {}
	@Override	public void windowDeiconified(WindowEvent e) {}
	@Override	public void windowActivated(WindowEvent e) {}
	@Override	public void windowDeactivated(WindowEvent e) {}
}

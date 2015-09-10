package com.mypcr.ui;

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
import javax.swing.JSeparator;

import com.mypcr.beans.Action;
import com.mypcr.constant.ProtocolConstants;
import com.mypcr.constant.UIConstant;
import com.mypcr.function.Functions;
import com.mypcr.handler.Handler;
import com.mypcr.ui.custom.Protocol;
import com.mypcr.ui.custom.ProtocolEvent;
import com.mypcr.ui.custom.ProtocolViewer;

public class ProtocolEditor extends JDialog implements WindowListener, ActionListener, Handler {
	private static final long serialVersionUID = 1L;
	
	private Handler handler = null;
	
	private ProtocolViewer viewer = null;
	
	private JScrollPane scrollPane = null;
	
	private JPanel uiPanel, viewerPanel, informationPanel, buttonPanel;
	private JLabel totalTimeLabel = null;
	private JComboBox<String> insertTypeCombo = null;
	private JButton buttonInsertStep, buttonInsertGoto, buttonDeleteStep, buttonOk, buttonCancel;
	
	private Action[] action = null;
	private String protocolName = null;
	
	public ProtocolEditor(Handler handler){
		this.handler = handler;
		
		initComponents();
		updateProtocolFromViewer();
	}
	
	public ProtocolEditor(Handler handler, Action[] action, String protocolName){
		this.handler = handler;
		this.action = action;
		this.protocolName = protocolName;
		
		initComponents();
		updateProtocolFromViewer();
	}
	
	private void initComponents(){
		setSize(ProtocolViewer.PREFERENCE_WIDTH+45, 565) ;
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setModal(true);
		setLocationRelativeTo((JDialog)handler);
		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());
		setTitle("Protocol Editor - " + (protocolName==null ? "New" : protocolName));
		
		// for entire ui
		uiPanel = new JPanel();
		uiPanel.setLayout(null);
		uiPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		
		// for information ui
		informationPanel = new JPanel();
		informationPanel.setLayout(null);
		informationPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		informationPanel.setBorder(BorderFactory.createTitledBorder("Protocol Infomation"));
		informationPanel.setBounds(10, 10, ProtocolViewer.PREFERENCE_WIDTH+20, 70);
		
		JLabel staticInsertType = new JLabel("Insert Step");
		staticInsertType.setBounds(20, 28, 60, 20);
		
		insertTypeCombo = new JComboBox<String>();
		insertTypeCombo.setBounds(90, 26, 100, 30);
		insertTypeCombo.addItem("  Before");
		insertTypeCombo.addItem("  After");
		insertTypeCombo.setAlignmentX(JComboBox.CENTER_ALIGNMENT);
		insertTypeCombo.setSelectedIndex(1);
		insertTypeCombo.addActionListener(this);
		
		JSeparator separator = new JSeparator(JSeparator.VERTICAL);
		separator.setBounds(200, 20, 5, 40);
		
		JLabel staticTotalTime = new JLabel("Total Run Time: ");
		staticTotalTime.setBounds(210, 28, 90, 20);
		
		totalTimeLabel = new JLabel(Functions.calcTotalTime(action));
		totalTimeLabel.setBounds(305, 28, 100, 20);
		
		informationPanel.add(staticInsertType);
		informationPanel.add(insertTypeCombo);
		informationPanel.add(separator);
		informationPanel.add(staticTotalTime);
		informationPanel.add(totalTimeLabel);
		
		// For viewer ui
		viewerPanel = new JPanel();
		viewerPanel.setLayout(null);
		viewerPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		viewerPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
		viewerPanel.setBounds(10, 90, ProtocolViewer.PREFERENCE_WIDTH+20, ProtocolViewer.PREFERENCE_HEIGHT+40);
		
		viewer = new ProtocolViewer();
		viewer.setEditable(true);
		viewer.setHandler(this);
		
		if( action == null )
			viewer.setDefaultProtocol();
		else{
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
		
		scrollPane = new JScrollPane(viewer, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setBounds(10, 25, ProtocolViewer.PREFERENCE_WIDTH, ProtocolViewer.PREFERENCE_HEIGHT);
		
		viewerPanel.add(scrollPane);
		
		// For button ui
		buttonPanel = new JPanel();
		buttonPanel.setLayout(null);
		buttonPanel.setBackground(UIConstant.BACKGROUND_COLOR);
		buttonPanel.setBorder(BorderFactory.createTitledBorder("Menu"));
		buttonPanel.setBounds(10, ProtocolViewer.PREFERENCE_HEIGHT+140, ProtocolViewer.PREFERENCE_WIDTH+20, 70);
		
		buttonInsertStep = new JButton("Insert Step");
		buttonInsertStep.setBounds(20, 25, 170, 30);
		buttonInsertStep.addActionListener(this);
		
		buttonInsertGoto = new JButton("Insert Goto");
		buttonInsertGoto.setBounds(200, 25, 170, 30);
		buttonInsertGoto.addActionListener(this);
		
		buttonDeleteStep = new JButton("Delete Step");
		buttonDeleteStep.setBounds(380, 25, 170, 30);
		buttonDeleteStep.addActionListener(this);
		
		JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);
		separator2.setBounds(ProtocolViewer.PREFERENCE_WIDTH-450, 20, 5, 40);
		
		buttonOk = new JButton("Save Protocol");
		buttonOk.setBounds(ProtocolViewer.PREFERENCE_WIDTH-350, 25, 170, 30);
		buttonOk.addActionListener(this);
		
		buttonCancel = new JButton("Cancel");
		buttonCancel.setBounds(ProtocolViewer.PREFERENCE_WIDTH-170, 25, 170, 30);
		buttonCancel.addActionListener(this);
		
		buttonPanel.add(buttonInsertStep);
		buttonPanel.add(buttonInsertGoto);
		buttonPanel.add(buttonDeleteStep);
		buttonPanel.add(separator2);
		buttonPanel.add(buttonOk);
		buttonPanel.add(buttonCancel);
		
		// For all ui
		uiPanel.add(informationPanel);
		uiPanel.add(viewerPanel);
		uiPanel.add(buttonPanel);
		
		add(uiPanel);
	}
	
	public void showDialog(){
		setVisible(true);
	}
	
	private void updateProtocolFromViewer(){
		ArrayList<Protocol> protocols = viewer.getProtocols();
		ArrayList<Action> ourProtocols = new ArrayList<Action>();
		int label = 1, gotoCount = 0;
		
		for(int i=0; i<protocols.size(); ++i){
			Protocol p = protocols.get(i);
			Action action = new Action(protocolName);
			
			if( !p.isGoto() && !p.isEnd() ){
				action.set(label+"", p.getTargetTemp() + "", p.getTime() + "");
				label++;
				ourProtocols.add(action);
			}
			else if( p.isGoto() ){
				action.set("GOTO", (p.getGoto()-gotoCount) + "", p.getRepeat() + "");
				ourProtocols.add(action);
				gotoCount++;
			}
		}
		
		action = new Action[ourProtocols.size()];
		for(int i=0; i<action.length; ++i)
			action[i] = ourProtocols.get(i);
		
		totalTimeLabel.setText(Functions.calcTotalTime(action));
		
		// auto scrolling
		scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMaximum());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if( o == buttonInsertStep || o == buttonInsertGoto || o == buttonDeleteStep ){
			boolean isAfter = insertTypeCombo.getSelectedIndex() == 1;
			int offset = (isAfter ? 1 : 0);
			int selectedProtocol = viewer.getSelectedProtocol();
			
			if( selectedProtocol == -1 ){
				JOptionPane.showMessageDialog(null, "Step 을 선택해 주세요.");
				return;
			}
			
			ArrayList<Protocol> protocols = viewer.getProtocols();
			
			if( o == buttonInsertStep ){
				// update new goto line when the selected protocol is gotoline.
				if( protocols.get(selectedProtocol).isGotoLine() && 
					protocols.get(selectedProtocol+1).isGotoLine() &&
					isAfter ){
					for(int i=selectedProtocol; i<protocols.size(); ++i){
						if( protocols.get(i).isGoto() ){
							protocols.get(i).setGotoFlag();
							break;
						}
					}
				}
				else if( !isAfter && (selectedProtocol-1 >= 0) ){
					if( protocols.get(selectedProtocol).isGotoLine() &&  
						protocols.get(selectedProtocol-1).isGotoLine() ){
						for(int i=selectedProtocol; i<protocols.size(); ++i){
							if( protocols.get(i).isGoto() ){
								protocols.get(i).setGotoFlag();
								break;
							}
						}
					}
				}
				
				viewer.addProtocol(selectedProtocol + offset);
				
				// 이전에 선택한 protocol 을 선택할 수 있도록 after, before 따라 index 를 재설정 후, focus 설정해준다.
				if( isAfter )	selectedProtocol++;
				
				viewer.clearAllSelection();
				viewer.setSelection(selectedProtocol, true);
				
				updateProtocolFromViewer();
			}
			else if( o == buttonInsertGoto ){
				int targetLabel = viewer.getProtocols().get(selectedProtocol).getActionNumber();
				int offset2 = isAfter ? 0 : -1;
				
				viewer.addGotoProtocol(selectedProtocol + offset, targetLabel + offset2);
				
				// 이전에 선택한 protocol 을 선택할 수 있도록 after, before 따라 index 를 재설정 후, focus 설정해준다.
				if( isAfter )	selectedProtocol++;
				
				viewer.clearAllSelection();
				viewer.setSelection(selectedProtocol, true);
				
				updateProtocolFromViewer();
				enableGotoButton();
			}
			else{
				if( protocols.size() == 2 ){
					JOptionPane.showMessageDialog(null, "마지막 Protocol 은 삭제할 수 없습니다.");
					return;
				}
				else if( protocols.size() == 3 && protocols.get(1).isGoto() && selectedProtocol == 0){	
					// protocol 이 3개이고(end 포함), goto 문과 일반 step 이 하나 있는 경우
					// 첫번째 프로토콜을 지울 경우, goto 도 같이 지워져야 하는게 기본 동작 원리인데, 그렇게 될 경우, 마지막 Protocol 까지 다 삭제되므로
					// 삭제가 되지 않도록 한다.
					JOptionPane.showMessageDialog(null, "마지막 Protocol 은 삭제할 수 없습니다.");
					return;
				}
				
				viewer.removeProtocol(selectedProtocol);
				viewer.setSelectedIndex(-1);
				
				updateProtocolFromViewer();
				enableGotoButton();
			}
		}
		else if( o == buttonOk ){
			if( protocolName == null ){
				String res = JOptionPane.showInputDialog(null, "Please input your protocol name", getTitle(), JOptionPane.YES_NO_CANCEL_OPTION);
			
				if( res != null ){
					if( res.isEmpty() ){
						JOptionPane.showMessageDialog(null, "Protocol Name 을 입력해주세요.");
						return;
					}
					
					// To prevent the duplication protocol name.
					ArrayList<String> savedProtocols = Functions.enumProtocolNames();
					if( savedProtocols.contains(res + ProtocolConstants.ext) ){
						JOptionPane.showMessageDialog(null, "이미 존재하는 프로토콜 이름입니다. 다시 시도해 주세요.");
						return;
					}

					protocolName = res + ProtocolConstants.ext;
					
					// Setting the protocol Name in actions
					for(int i=0; i<action.length; ++i)
						action[i].setProtocolName(protocolName);
				}
			}
			
			updateProtocolFromViewer();
			Functions.saveProtocol(action, protocolName);
			
			// event to parent and dispose.
			if( handler != null )
				handler.OnHandleMessage(ProtocolManager.EVENT_PROTOCOL_LIST_REFRESH, protocolName);
			dispose();
		}
		else if( o == buttonCancel ){
			dispose();
		}
		else if( o == insertTypeCombo ){
			enableGotoButton();
		}
	}
	
	private void enableGotoButton(){
		int selectedIndex = viewer.getSelectedProtocol();
		boolean isAfter = insertTypeCombo.getSelectedIndex() == 1;
		
		if( selectedIndex == -1 ){
			buttonInsertGoto.setEnabled(true);
			return;
		}
		
		// if the protocol contains goto, the insertgoto Button disabled.
		if( isAfter )
			buttonInsertGoto.setEnabled(!viewer.getProtocols().get(selectedIndex).isGotoLine());
		else{
			if( selectedIndex == 0 )
				buttonInsertGoto.setEnabled(false);
			else
				buttonInsertGoto.setEnabled(!viewer.getProtocols().get(selectedIndex-1).isGotoLine());
		}
	}
	
	@Override	
	public void windowClosed(WindowEvent e) {
		
	}
	
	// 1. selection 이벤트 시, insert goto 버튼 disable 처리
	// 2. remove 에 대한 처리...     (2)
	// 3. insert goto 에 대한 처리.. (1)
	
	@Override
	public void OnHandleMessage(int MessageType, Object data) {
		switch( MessageType ){
			case ProtocolEvent.EVENT_PROTOCOL_FOCUS_CHANGED:
				enableGotoButton();
				break;
			case ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED:
				updateProtocolFromViewer();
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

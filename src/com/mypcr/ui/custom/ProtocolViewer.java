package com.mypcr.ui.custom;

import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.mypcr.handler.Handler;


public class ProtocolViewer extends JPanel implements Handler{
	private static final long serialVersionUID = 1L;
	
	public static final int PREFERENCE_WIDTH = 1103;
	public static final int PREFERENCE_HEIGHT = 317;
	
	private static final int MINIMUM_WIDTH = 90;
	
	private static final int DEFAULT_TEMP = 25;
	private static final int DEFAULT_TARGET_TEMP = 50;
	private static final int DEFAULT_TIME = 30;
	
	private static final int DEFAULT_REPEAT = 10;
	
	public static final String LABEL_GOTO = "GOTO";
	public static final String LABEL_END = "END";
	
	private ArrayList<Protocol> protocolList = new ArrayList<Protocol>();
	private int selectedProtocol = -1;
	
	private boolean isEditable = false;
	
	private Handler handler = null;
	
	public ProtocolViewer(){
		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(true);
		setBackground(Color.white);
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	private void resizeProtocol(){
		// Counting the left protocol width
		int protocolWidth = PREFERENCE_WIDTH;
		int protocolCount = 0;
		int etcCount = 0;
		
		for(int i=0; i<protocolList.size(); ++i){
			if( protocolList.get(i).isGoto() || protocolList.get(i).isEnd() ){
				protocolWidth -= Protocol.fixedWidth;
				etcCount++;
			}
			else
				protocolCount++;
		}
		
		int labelWidth = protocolWidth/protocolCount;
		labelWidth /= 3;
		
		int leftWidth = (PREFERENCE_WIDTH-3) - ((labelWidth*3*protocolCount)+(Protocol.fixedWidth*etcCount));
		int leftCount = protocolCount;
		
		if( labelWidth*3 < MINIMUM_WIDTH ){
			labelWidth = 30;
			leftWidth = 0;
		}
		
		for(int i=0; i<protocolList.size(); ++i){
			if( !(protocolList.get(i).isGoto() || protocolList.get(i).isEnd()) ){
				leftCount--;
				if( leftCount == 0 ){
					protocolList.get(i).setColumnSpec(i+1, (labelWidth+(leftWidth/3)) + "px");
				}else{
					protocolList.get(i).setColumnSpec(i+1, labelWidth + "px");
				}
			}
			
			protocolList.get(i).getProtocol().repaint();
			protocolList.get(i).getProtocol().invalidate();
			protocolList.get(i).getProtocol().validate();
		}
		
		repaint();
		invalidate();
		validate();
	}
	
	public void updateProtocol(){
		removeAll();
		
		// renumbering for new protocols
		reNumberingProtocol();
		
		// for removing previous goto line
		Set<Integer> gotoLines = new HashSet<Integer>();
		
		int prevTemp = DEFAULT_TEMP;
		
		// update goto target labeling
		for(int i=0; i<protocolList.size(); ++i){
			Protocol p = protocolList.get(i);
			
			if( p.isGoto() ){
				int label = p.getActionNumber();
				int targetLabel = p.getGoto();
				int prevGotoLength = p.getGotoLength();
				int currentGotoLength = label-targetLabel;
				
				if( prevGotoLength != currentGotoLength ){
					p.setGoto(label-prevGotoLength);
					p.restoreGoto(true);
				}
			}
		}
		
		// update with goto drawing
		for(int i=0; i<protocolList.size(); ++i){
			Protocol p = protocolList.get(i);
			
			if( p.isGoto() ){
				int target = p.getGoto();
				int label = p.getActionNumber();
				
				for(int j=0; j<=i; ++j){
					Protocol p2 = protocolList.get(j);
					int label2 = p2.getActionNumber();
					
					// 이 범위 안에 있는 경우, goto 구문
					if( !p2.isEnd() && label2 >= target ){
						if( label2 >= target && label2 <= label ){
							// 	그 중, target 인 경우, 화살표로 그려야함
							if( label2 == target )
								p2.setGotoLine(true);
							else
								p2.setGotoLine(false);
							
							gotoLines.add(label2);
						}
					}
				}
			}
			else if( !p.isGoto() && !p.isEnd() ){
				p.setTemperature(prevTemp, p.getTargetTemp());
				prevTemp = p.getTargetTemp();
			}
		}
		
		// remove previous goto drawing
		for(int i=0; i<protocolList.size(); ++i){
			int label = protocolList.get(i).getActionNumber();
			
			if( label != -1 && !gotoLines.contains(label) )
				protocolList.get(i).removeGotoLine();
		}
		
		// for adding components
		for(int i=0; i<protocolList.size(); ++i){
			Protocol p = protocolList.get(i);
			
			add(p.getProtocol());
		}
		
		// Setting the editable
		for(int i=0; i<protocolList.size(); ++i)
			protocolList.get(i).setEditable(isEditable);
		
		resizeProtocol();
	}
	
	public void setEditable(boolean isEditable){
		this.isEditable = isEditable;
	}
	
	public void setDefaultProtocol(){
		protocolList.clear();
		
		// default protocol
		// TODO: default protocol 을 어떻게 설정해줬으면 하는지도 필요
		addProtocol(0);
		addProtocol(0);
		appendEnd();
		
		updateProtocol();
	}
	
	public int getSelectedProtocol(){
		return selectedProtocol;
	}
	
	public void addProtocol(int index){
		int label = 1;
		
		if( index < 0 || index > protocolList.size() )
			throw new RuntimeException("Software has some problems");
		
		for(int i=0; i<index; ++i){
			Protocol p = protocolList.get(i);
			
			if( (!p.isEnd() && !p.isGoto()) || p.isGoto() )
				label++;
		}
		
		Protocol newProtocol = new Protocol(label, DEFAULT_TEMP, DEFAULT_TARGET_TEMP, DEFAULT_TIME);
		newProtocol.setHandler(this);
		protocolList.add(index, newProtocol);
		
		updateProtocol();
	}
	
	public void addGotoProtocol(int index, int targetLabel){
		int label = 1;
		
		if( index < 0 || index > protocolList.size() )
			throw new RuntimeException("Software has some problems");
		
		for(int i=0; i<index; ++i){
			Protocol p = protocolList.get(i);
			
			if( (!p.isEnd() && !p.isGoto()) || p.isGoto())
				label++;
		}
		
		Protocol newProtocol = new Protocol(label, targetLabel, DEFAULT_REPEAT);
		newProtocol.setHandler(this);
		protocolList.add(index, newProtocol);
		
		updateProtocol();
	}
	
	public void removeProtocol(int index){
		if( index < 0  || (protocolList.size() <= 2 && index < 2) || index >= protocolList.size() )
			throw new RuntimeException("Software has some problems");
		
		boolean isGotoLine = false;
		// Checking the goto
		for(int i=index+1; i<protocolList.size(); ++i){
			Protocol p = protocolList.get(i);
			if( p.isGoto() ){
				int currentAction = protocolList.get(index).getActionNumber();
				int target = p.getGoto();
				int label = p.getActionNumber();
				
				if( currentAction >= target && currentAction <= label ){
					isGotoLine = true;
					break;
				}
			}
		}
		
		if( isGotoLine ){
			for(int i=index+1; i<protocolList.size(); ++i){
				Protocol p = protocolList.get(i);
				if( p.isGoto() ){
					int currentAction = protocolList.get(index).getActionNumber();
					int target = p.getGoto();
					int label = p.getActionNumber();
					
					if( currentAction >= target && currentAction <= label ){
						int gotoLength = p.getGotoLength();
						
						// goto Length 가 1 인 경우, goto 부분도 지울 수 있도록 두번 호출한다.
						if( gotoLength == 1 )
							protocolList.remove(index);
						else
							p.setGotoLength(p.getGotoLength()-1);
					}
					break;
				}
			}
		}
		
		protocolList.remove(index);
		
		updateProtocol();
	}
	
	public void appendProtocol(int label, int prevTemp, int targetTemp, int time){
		Protocol newProtocol = new Protocol(label, prevTemp, targetTemp, time);
		newProtocol.setHandler(this);
		
		protocolList.add(newProtocol);
	}
	
	public void appendGotoProtocol(int label, int targetLabel, int repeat){
		Protocol newProtocol = new Protocol(label, targetLabel, repeat);
		newProtocol.setHandler(this);
		
		protocolList.add(newProtocol);
	}
	
	public void appendEnd(){
		Protocol newProtocol = new Protocol();
		newProtocol.setHandler(this);
		
		protocolList.add(newProtocol);
	}
	
	public void clearProtocols(){
		protocolList.clear();
	}
	
	private void reNumberingProtocol(){
		int label = 1;
		
		for(int i=0; i<protocolList.size(); ++i){
			Protocol p = protocolList.get(i);
			
			if( (!p.isEnd() && !p.isGoto()) || p.isGoto() ){
				p.setActionNumber(label);
				label++;
			}
		}
	}
	
	public void setSelectedIndex(int index){
		selectedProtocol = index;
	}
	
	public void setFocus(int index, boolean isFocus){
		if( index < 0 || index >= protocolList.size() )
			throw new RuntimeException("Software have some problems");
		
		selectedProtocol = index;
		protocolList.get(index).setFocus(isFocus);
	}
	
	public void setSelection(int index, boolean isSelection){
		if( index < 0 || index >= protocolList.size() )
			throw new RuntimeException("Software have some problems");
		
		selectedProtocol = index;
		protocolList.get(index).setSelection(isSelection);
	}
	
	public void clearAllFocus(){
		for(int i=0; i<protocolList.size(); ++i)
			protocolList.get(i).setFocus(false);
	}
	
	public void clearAllSelection(){
		for(int i=0; i<protocolList.size(); ++i)
			protocolList.get(i).setSelection(false);
	}
	
	public ArrayList<Protocol> getProtocols(){
		return protocolList;
	}

	@Override
	public void OnHandleMessage(int type, Object data) {
		String changeTag = null;
		
		switch( type ){
			case ProtocolEvent.EVENT_PROTOCOL_FOCUS_CHANGED:
				clearAllSelection();
				selectedProtocol = (Integer)data - 1;
				protocolList.get(selectedProtocol).setFocus(true);
				break;
			case ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED:
				changeTag = (String)data;
				if( changeTag.equals(Protocol.tempTag) ){
					int prevTemp = 25;
					
					for(int i=0; i<protocolList.size(); ++i){
						Protocol p = protocolList.get(i);
						if( !p.isGoto() && !p.isEnd() ){
							int targetTemp = p.getTargetTemp();
							p.setTemperature(prevTemp, targetTemp);
							prevTemp = targetTemp;
						}
					}
					updateProtocol();
				}
				else if( changeTag.equals(Protocol.gotoTag) ){
					for(int i=0; i<protocolList.size(); ++i){
						Protocol p = protocolList.get(i);
						if( p.isGoto() ){
							int target = p.getGoto();
							
							if( target >= p.getActionNumber() ){
								JOptionPane.showMessageDialog(null, "target action number 은 현재 action number 보다 작아야 합니다.");
								p.restoreGoto(true);
								return;
							}
							
							for(int j=0; j<i; ++j){
								Protocol p2 = protocolList.get(j);
								
								if( p2.isGoto() ){
									int target2 = p2.getGoto();
									int label = p2.getActionNumber();
									
									if( ( target <= label ) || (target >= target2 && target <= label) ){
										JOptionPane.showMessageDialog(null, "target action number 은 다른 goto 에 포함될 수 없습니다.");
										p.restoreGoto(true);
										return;
									}
								}
							}
							
							p.restoreGoto(false);
						}
					}
					updateProtocol();
				}
				break;
		}
		
		// bypass to upper ui with this message
		if( handler != null )
			handler.OnHandleMessage(type, data);
	}
}

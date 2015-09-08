package com.mypcr.ui.custom;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.mypcr.handler.Handler;

public class Protocol implements FocusListener, KeyListener {
	private PanelBuilder builder;
	
	private JLabel text_header;
	
	private JTextField text_temperature, text_time, text_goto, text_repeat;
	private JTextField text_const1, text_const2, text_const3, text_const4;
	
	private ValueHolder value_temperature, value_time, value_goto, value_repeat;
	
	private Handler handler = null;
	
	private int actionNumber = -1;
	private int prevTemp = -1;
	private int targetTemp = -1;
	private int time = -1;
	private int gotoLabel = -1;
	private int repeat = -1;
	private int gotoLength = -1;
	private boolean isGoto = false;
	private boolean isEnd = false;
	private boolean isFocus = false;
	private boolean isGotoLine = false;
	
	private boolean gotoLengthFlag = false;
	
	private static final int rowPadding = 8;
	
	private static final String columnSpecs_common = "50px, 50px, 50px";
	private static final String columnSpecs_goto = "15px, 20px, 15px";
	private static final String rowSpecs = "7dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, "
											+ "1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 1dlu, 7dlu, 10dlu ";	// for scrolling, 10dlu
	
	// 0~3 index is not used
	private static final int[] rowTables = new int[105];
	
	private static final int rowSpecLength = 140;
	
	public static final int fixedWidth = 50;
	
	private static final String[] gotoLabeling = { "G", "O", "T", "O" };
	private static final String[] endLabeling = { "E", "N", "D", " " };
	
	private static final Color colorHeader = new Color(218, 165, 32);
	private static final Color colorEnd = new Color(96, 98, 0);
	private static final Color colorSelection = new Color(135, 206, 250);
	private static final Color colorSelection2 = new Color(51, 153, 255);
	
	private static final int graphRowPadding = rowPadding+1;
	
	public static final String tempTag = "temp";
	public static final String gotoTag = "goto";
	public static final String timeTag = "time";
	
	private class PlainLine extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private boolean arrayFlag = false;
		
		public PlainLine(){
			setOpaque(true);
			setBackground(Color.white);
		}
		
		public void setArray(){
			arrayFlag = true;
		}

		@Override
		 public void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        	
	        Graphics2D g2d = (Graphics2D)g;
	        g2d.setStroke(new BasicStroke(3));
	        g2d.setColor(Color.red);
	        if( !arrayFlag )
	        	g2d.drawLine(1, 1, ProtocolViewer.PREFERENCE_WIDTH, 1);
	        else{
	        	g2d.drawLine(3, 19, ProtocolViewer.PREFERENCE_WIDTH, 20);
	        	g2d.drawLine(3, 19, 10, 27);
	        	g2d.drawLine(3, 19, 10, 13);
	        }
	    }
	}
	
	private class CurvedLine extends JPanel
	{
		private static final long serialVersionUID = 1L;
		private static final int yPosPadding = 36;
		private static final int defaultWidth = 150;
		
		private int fromTemp = 0;
		private int toTemp = 0;
		private int width = defaultWidth;
		
		public CurvedLine(int fromTemp, int toTemp){
			setOpaque(true);
			setBackground(Color.WHITE);
			
			this.fromTemp = fromTemp;
			this.toTemp = toTemp;
		}
		
		private int getValue2Pos(int value){
			return (104-value) * 2 + yPosPadding + 1;
		}
		
		public void setTemperature(int fromTemp, int toTemp){
			this.fromTemp = fromTemp;
			this.toTemp = toTemp;
		}
		
		public void setWidth(int width){
			this.width = width;
		}

		@Override
		 public void paintComponent(Graphics g) {
	        super.paintComponent(g);

	        Graphics2D g2d = (Graphics2D)g;
	        g2d.setStroke(new BasicStroke(2));

	        g2d.setColor(Color.red);
	        g2d.drawLine(1, getValue2Pos(fromTemp), width, getValue2Pos(toTemp));
	    }
	}
	
	private PlainLine plainLine = null;
	private PlainLine gotoLine = null;
	private PlainLine arrayLine = null;
	private CurvedLine curvedLine = null;
	
	// For END
	public Protocol(){
		// For END Labeling
		isEnd = true;
		
		initComponents();
	}
	
	// For common protocol
	public Protocol(int actionNumber, int prevTemp, int targetTemp, int time){
		this.actionNumber = actionNumber;
		this.prevTemp = prevTemp;
		this.targetTemp = targetTemp;
		this.time = time;
		
		int min = time/60;
		int sec = time%60;
		
		value_temperature = new ValueHolder(targetTemp + "");
		value_time = new ValueHolder(String.format("%02d:%02d", min, sec));
		
		plainLine = new PlainLine();
		curvedLine = new CurvedLine(prevTemp, targetTemp);

		gotoLine = new PlainLine();
		arrayLine = new PlainLine();
		arrayLine.setArray();
		
		initComponents();
	}
	
	// For goto Protocol
	public Protocol(int actionNumber, int gotoLabel, int repeat){
		this.actionNumber = actionNumber;
		this.gotoLabel = gotoLabel;
		this.repeat = repeat;
		gotoLength = actionNumber - gotoLabel;
		isGoto = true;
		
		value_goto = new ValueHolder(gotoLabel + "");
		value_repeat = new ValueHolder(repeat + "");
		
		gotoLine = new PlainLine();
		
		initComponents();
	}
	
	private void initComponents(){
		// for indexing
		for(int i=4; i<rowTables.length; ++i)
			rowTables[i] = rowTables.length-(i-4);
		
		text_header = new JLabel(actionNumber + "", JLabel.CENTER);
		text_header.setFont(new Font("Arial", Font.PLAIN, 12));
		text_header.setOpaque(true);
		text_header.setBackground(colorHeader);
		
		if( isGoto || isEnd ){
			text_const1 = new JTextField();
			text_const2 = new JTextField();
			text_const3 = new JTextField();
			text_const4 = new JTextField();
			
			JTextField[] temp = { text_const1, text_const2, text_const3, text_const4 };
			for(int i=0; i<temp.length; ++i){
				if( isGoto ){
					temp[i].setText(gotoLabeling[i]);
					temp[i].setBackground(Color.WHITE);
					temp[i].addFocusListener(this);
					temp[i].setSelectedTextColor(Color.BLACK);
					temp[i].setSelectionColor(colorSelection);
				}
				else if( isEnd ){
					temp[i].setText(endLabeling[i]);
					temp[i].setBackground(colorEnd);
					temp[i].setForeground(Color.WHITE);
					temp[i].setFocusable(false);
					temp[i].setSelectedTextColor(Color.WHITE);
					temp[i].setSelectionColor(colorEnd);
				}
				
				temp[i].setOpaque(true);
				temp[i].setHorizontalAlignment(JTextField.CENTER);
				temp[i].setBorder(null);
				temp[i].setEditable(false);
			}
			
			if( isEnd ){
				text_header.setBackground(colorEnd);
				text_header.setText(" ");
			}
			else if( isGoto ){
				text_goto = BasicComponentFactory.createTextField(value_goto);
				text_goto.addKeyListener(this);
				settingTextProperties(text_goto);
				
				text_repeat = BasicComponentFactory.createTextField(value_repeat);
				text_repeat.addKeyListener(this);
				settingTextProperties(text_repeat);
				
				// Change to align
				text_goto.setHorizontalAlignment(JTextField.CENTER);
				text_repeat.setHorizontalAlignment(JTextField.LEFT);
			}
		}
		else{
			text_temperature = BasicComponentFactory.createTextField(value_temperature);
			text_temperature.addKeyListener(this);
			settingTextProperties(text_temperature);
			
			text_time = BasicComponentFactory.createTextField(value_time);
			text_time.addKeyListener(this);
			settingTextProperties(text_time);
			
			if(text_time.getText().equals("00:00"))
				text_time.setText("Forever");
		}
		
		buildUI();
	}
	
	private void settingTextProperties(JTextField component){
		component.setHorizontalAlignment(JTextField.CENTER);
		component.setBorder(null);
		component.setOpaque(true);
		component.addFocusListener(this);
		component.setSelectedTextColor(Color.WHITE);
		component.setSelectionColor(colorSelection2);
	}
	
	private void buildUI(){
		FormLayout layout = null;
		
		if( isGoto || isEnd )
			layout = new FormLayout(columnSpecs_goto, rowSpecs);
		else
			layout = new FormLayout(columnSpecs_common, rowSpecs);
		
		CellConstraints cc = new CellConstraints();
		
		builder = new PanelBuilder(layout);
		builder.getPanel().setOpaque(true);
		builder.getPanel().setBackground(Color.WHITE);
		
		builder.add(new JSeparator(SwingConstants.VERTICAL), cc.xywh(1, 1, 3, rowSpecLength+1));
		builder.add(text_header, cc.xyw(1, 1, 3));
		
		if( isGoto || isEnd ){
			JTextField[] temp = { text_const1, text_const2, text_const3, text_const4 };
			for(int i=0; i<temp.length; ++i)
				builder.add(temp[i], cc.xywh(2, 60 + (rowPadding*i), 1, rowPadding));
			
			if( isEnd )
				builder.getPanel().setBackground(colorEnd);
			else if( isGoto ){
				builder.add(text_goto, cc.xywh(2, 123, 1, rowPadding));
				builder.add(text_repeat, cc.xywh(2, 127 + rowPadding, 1, rowPadding-2));
				builder.addLabel("x", cc.xywh(3, 127 + rowPadding, 1, rowPadding-2, CellConstraints.CENTER, CellConstraints.CENTER));
			}
		}
		else{
			builder.add(plainLine, cc.xyw(2, rowTables[targetTemp]+graphRowPadding, 2));
			builder.add(curvedLine, cc.xywh(1, 1, 1, rowSpecLength-26));
			
			builder.add(text_temperature, cc.xywh(2, rowTables[targetTemp], 1, rowPadding));
			builder.add(text_time, cc.xywh(2, rowTables[targetTemp]+rowPadding+4, 1, rowPadding));
		}
		
		builder.getPanel().addFocusListener(this);
		
		// For selection function
		builder.getPanel().addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				if( !isEnd )
					builder.getPanel().requestFocus();
			}
			@Override	public void mouseReleased(MouseEvent e) {}
			@Override	public void mouseExited(MouseEvent e) {}
			@Override	public void mouseEntered(MouseEvent e) {}
			@Override	public void mouseClicked(MouseEvent e) {}
		});
	}
	
	private void setBackgroundColor(Color color){
		if( builder == null )
			throw new RuntimeException("builder is null!");
		
		builder.getPanel().setBackground(color);
		
		if( !(isGoto || isEnd) ){
			text_temperature.setBackground(color);
			text_time.setBackground(color);
			plainLine.setBackground(color);
			curvedLine.setBackground(color);
			gotoLine.setBackground(color);
			arrayLine.setBackground(color);
		}
		else if( isGoto ){
			JTextField[] temp = { text_const1, text_const2, text_const3, text_const4 };
			for(int i=0; i<temp.length; ++i)
				temp[i].setBackground(color);
			
			text_goto.setBackground(color);
			text_repeat.setBackground(color);
			gotoLine.setBackground(color);
		}
		else if( isEnd ){
			builder.getPanel().setBackground(colorEnd);
		}
	}
	
	private String getTimeWithFormat(int sec){
		int m = sec/60;
		int s = sec%60;
		
		return String.format("%02d:%02d", m, s);
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public boolean isFocus(){
		return isFocus;
	}
	
	public boolean isEnd(){
		return isEnd;
	}
	
	public boolean isGoto(){
		return isGoto;
	}
	
	public void setColumnSpec(int index, String spec){
		builder.getLayout().setColumnSpec(1, ColumnSpec.decode(spec));
		builder.getLayout().setColumnSpec(2, ColumnSpec.decode(spec));
		builder.getLayout().setColumnSpec(3, ColumnSpec.decode(spec));
		
		if( !isGoto && !isEnd ){
			int width = Integer.parseInt(spec.substring(0, spec.length()-2));
			curvedLine.setWidth(width);
		}
	}
	
	public void setTemperature(int prevTemp, int targetTemp){
		this.prevTemp = prevTemp;
		this.targetTemp = targetTemp;
		
		if( !isGoto && !isEnd ){
			CellConstraints cc = new CellConstraints();
			
			curvedLine.setTemperature(prevTemp, targetTemp);
			
			builder.getContainer().remove(plainLine);
			builder.getContainer().remove(curvedLine);
			builder.getContainer().remove(text_temperature);
			builder.getContainer().remove(text_time);
			
			builder.add(plainLine, cc.xyw(2, rowTables[targetTemp]+graphRowPadding, 2));
			builder.add(curvedLine, cc.xywh(1, 1, 1, rowSpecLength-26));
			
			builder.add(text_temperature, cc.xywh(2, rowTables[targetTemp], 1, rowPadding));
			builder.add(text_time, cc.xywh(2, rowTables[targetTemp]+rowPadding+4, 1, rowPadding));
		}
	}
	
	// Gotoline 의 경우, 외부에서 설정해주는 것이 필요하다.
	public void setGotoLine(boolean targetGoto){
		isGotoLine = true;
		
		CellConstraints cc = new CellConstraints();
		
		builder.getContainer().remove(gotoLine);
		
		if( !isGoto && !isEnd )
			builder.getContainer().remove(arrayLine);
		
		if( !targetGoto )
			builder.add(gotoLine, cc.xywh(1, 125+rowPadding, 3, 4));
		else{
			builder.add(arrayLine, cc.xywh(1, 124, 1, 15));
			builder.add(gotoLine, cc.xywh(2, 125+rowPadding, 2, 4));
		}
	}
	
	public void setGoto(int gotoLabel){
		if( !gotoLengthFlag )
			this.gotoLabel = gotoLabel;
		else{
			gotoLength++;
			gotoLengthFlag = false;
		}
	}
	
	public void setGotoFlag(){
		gotoLengthFlag = true;
	}
	
	public void restoreGoto(boolean previousValue){
		if( !previousValue ){
			gotoLabel = Integer.parseInt(text_goto.getText());
			gotoLength = actionNumber-gotoLabel;
		}
		
		text_goto.setText(gotoLabel + "");
	}
	
	public void setEditable(boolean isEditable){
		getProtocol().setFocusable(isEditable);
		
		if( !isGoto && !isEnd ){
			text_temperature.setFocusable(isEditable);
			text_time.setFocusable(isEditable);
		}
		else if( isGoto ){
			text_const1.setFocusable(isEditable);
			text_const2.setFocusable(isEditable);
			text_const3.setFocusable(isEditable);
			text_const4.setFocusable(isEditable);
			
			text_goto.setFocusable(isEditable);
			text_repeat.setFocusable(isEditable);
		}
	}
	
	public void setActionNumber(int actionNumber){
		this.actionNumber = actionNumber;
		
		text_header.setText(actionNumber + "");
	}
	
	public void setFocus(boolean isFocus){
		this.isFocus = isFocus;
		
		if( isFocus ){
			setBackgroundColor(colorSelection);
			if( isGoto ){
				text_const1.setFocusable(false);
				text_const2.setFocusable(false);
				text_const3.setFocusable(false);
				text_const4.setFocusable(false);
			}
		}
		else{
			setBackgroundColor(Color.WHITE);
			if( isGoto ){
				text_const1.setFocusable(true);
				text_const2.setFocusable(true);
				text_const3.setFocusable(true);
				text_const4.setFocusable(true);
			}
		}
	}
	
	public void setSelection(boolean isSelection){
		setBackgroundColor( (isSelection ? colorSelection : Color.WHITE) );
	}
	
	public void setGotoLength(int gotoLength){
		this.gotoLength = gotoLength;
	}
	
	public void removeGotoLine(){
		isGotoLine = false;
		builder.getContainer().remove(gotoLine);
		if( !isGoto && !isEnd )
			builder.getContainer().remove(arrayLine);
	}
	
	public int getPrevTemp(){
		return prevTemp;
	}
	
	public int getTargetTemp(){
		return targetTemp;
	}
	
	// 여기서 goto 값은 현재 text 에 설정된 값을 얻어오도록 한다.
	// 왜냐하면, 이 함수는 현재 값에 대한 validation 을 위한 함수이기 때문
	public int getGoto(){
		return Integer.parseInt(text_goto.getText());
	}
	
	public int getGotoLength(){
		return gotoLength;
	}
	
	public int getActionNumber(){
		return actionNumber;
	}
	
	public int getTime(){
		return time;
	}
	
	public int getRepeat(){
		return repeat;
	}
	
	public boolean isGotoLine(){
		return isGotoLine;
	}
	
	@Override
	public void focusGained(FocusEvent e) {
		if( !isEnd ){
			isFocus = true;
			setBackgroundColor(colorSelection);
			
			if( handler != null )
				handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_FOCUS_CHANGED, actionNumber);
			
			// if the focused object is "time object", checking the time and convert to forever if the time is 0.
			if( e.getSource() == text_time ){
				String currentTime = text_time.getText().trim();
				if( currentTime.equals("Forever") )
					text_time.setText("00:00");
			}
		}
	}

	@Override
	public void focusLost(FocusEvent e) {
		if( !isEnd ){
			/*
			focus 처리를 viewer 에서 하도록 변경.
			기존 방법으로는 button 클릭 시, selectedProtocol 을 알 수 없음
			isFocus = false;
			setBackgroundColor(Color.WHITE);
			
			if( handler != null )
				handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_FOCUS_CHANGED, null);
			*/
			
			// validation check the textfield
			// 숫자 이외의 값이 들어오는 경우에 대해서는 처리할 필요가 없다.
			// 이미 키보드 입력에서 막아두었기 때문
			// 하지만, time 의 경우, 가운데에 있는 : 값을 처리하는 부분이 필요하다.
			validationCheck(e.getSource());
			
			if( e.getSource() == text_time ){
				String currentTime = text_time.getText().trim();
				if( currentTime.equals("00:00") )
					text_time.setText("Forever");
			}
		}
	}
	
	private void validationCheck(Object source){
		if( !isGoto ){
			if( source == text_temperature ){
				String tempStr = text_temperature.getText();
				if( !(tempStr.length() > 0 && tempStr.length() <= 3) ){
					JOptionPane.showMessageDialog(null, "Temperature range : 4~104");
					text_temperature.setText(targetTemp + "");
					return;
				}
				
				int temp = Integer.parseInt(tempStr);
				
				if( !(temp >= 4 && temp <= 104) ){
					JOptionPane.showMessageDialog(null, "Temperature range : 4~104");
					text_temperature.setText(targetTemp + "");
					return;
				}
				
				targetTemp = temp;
				if( handler != null )
					handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED, tempTag);
			}
			else if( source == text_time ){
				String tempTime = text_time.getText();
				if( tempTime.isEmpty() ){
					JOptionPane.showMessageDialog(null, "Time range : 0s~");
					text_time.setText(getTimeWithFormat(time));
					return;
				}
				
				if( tempTime.contains(":") ){
					String[] times = tempTime.split(":");
					if( times[0].isEmpty() ){
						JOptionPane.showMessageDialog(null, "Time range : 0s~");
						text_time.setText(getTimeWithFormat(time));
						return;
					}else if( times[1].isEmpty() ){
						JOptionPane.showMessageDialog(null, "Time range : 0s~");
						text_time.setText(getTimeWithFormat(time));
						return;
					}
					
					time = Integer.parseInt(times[0])*60 + Integer.parseInt(times[1]);
				}else{
					time = Integer.parseInt(tempTime);
					text_time.setText(getTimeWithFormat(time));
				}
				
				if( handler != null )
					handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED, timeTag);
			}
		}
		else{
			if( source == text_goto ){
				String gotoStr = text_goto.getText();
				if( !(gotoStr.length() > 0 && gotoStr.length() <= 3) ){
					JOptionPane.showMessageDialog(null, "Goto range : 1~999");
					text_goto.setText(gotoLabel + "");
					return;
				}
				
				int targetLabel = Integer.parseInt(gotoStr);
				
				if( !(targetLabel >= 1 && targetLabel <= 999) ){
					JOptionPane.showMessageDialog(null, "Goto range : 1~999");
					text_goto.setText(gotoLabel + "");
					return;
				}
				
				if( handler != null )
					handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED, gotoTag);
				
				// TODO: goto 에 대한 예외 처리는 모든 label 의 정보를 가지고 있는 상위 viewer 클래스에서 처리하여,
				// 하위 Protocol 클래스에 알려주는 방식으로 구현하는 것이 아키텍처가 맞을 듯하여 handler 로 메시지만 보내주도록 한다.
			}
			else if( source == text_repeat ){
				String repeatStr = text_repeat.getText();
				if( !(repeatStr.length() > 0 && repeatStr.length() <= 3) ){
					JOptionPane.showMessageDialog(null, "Repeat range : 1~999");
					text_repeat.setText(repeat + "");
					return;
				}
				
				repeat = Integer.parseInt(repeatStr);
				
				if( handler != null )
					handler.OnHandleMessage(ProtocolEvent.EVENT_PROTOCOL_VALUE_CHANGED, gotoTag);
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();

		if( !Character.isDigit(c) ){
			if( ((int)e.getKeyChar()) == 10 ){
				e.consume();
				// for activating focus lost event
				JTextField text = (JTextField)e.getSource();
				text.setFocusable(false);
				text.setFocusable(true);	
				return;
			}
			
			e.consume();
			return;
		}
	}
	
	public JComponent getProtocol(){
		return builder.getPanel();
	}

	// Not used
	@Override	public void keyPressed(KeyEvent e) {}
	@Override	public void keyReleased(KeyEvent e) {}
}

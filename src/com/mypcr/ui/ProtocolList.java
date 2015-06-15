package com.mypcr.ui;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.mypcr.beans.Action;
import com.mypcr.constant.UIConstant;

public class ProtocolList
{
	private static ProtocolList instance = null;
	private static JTable m_Table = null;
	private static JScrollPane m_ScrollPane = null;
	private MyPCRTableModel m_TableModel = MyPCRTableModel.getInstance();

	private boolean toggle = true;

	private ProtocolList()
	{
		m_Table = new JTable( m_TableModel );
		m_Table.setRowHeight(25);
		// Single Select Mode
		m_Table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		m_ScrollPane = new JScrollPane(m_Table);
		m_ScrollPane.setBounds(10, 100, UIConstant.MYPCR_WIDTH - 25, 230);
	}

	public static ProtocolList getInstance()
	{
		if( instance == null )
			instance = new ProtocolList();

		return instance;
	}

	public JScrollPane getPane()
	{
		return m_ScrollPane;
	}

	public void InsertData(Action action)
	{
		m_TableModel.InsertData(action);
		setAlignCenter();
	}

	public void ChangeRemainTime(String time, int row)
	{
		ChangeData(time, row, 3);
	}

	public void ChangeData(String data, int row, int col)
	{
		m_TableModel.setValueAt((Object)data, row, col);
	}

	public void ResetContent()
	{
		m_TableModel.ResetContent();
	}

	public void setAlignCenter()
	{
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		Enumeration<TableColumn> columns = m_Table.getColumnModel().getColumns();

		while( columns.hasMoreElements() )
		{
			TableColumn column = columns.nextElement();
			column.setCellRenderer(centerRenderer);
		}
	}

	public void clearSelection()
	{
		ListSelectionModel selectionModel = m_Table.getSelectionModel();

		selectionModel.setSelectionInterval(m_TableModel.getRowCount(), m_TableModel.getRowCount());
	}

	public void setSelection(int row)
	{
		ListSelectionModel selectionModel = m_Table.getSelectionModel();

		toggle = !toggle;

		if( toggle )
			selectionModel.setSelectionInterval(row, row);
		else
			selectionModel.setSelectionInterval(m_TableModel.getRowCount(), m_TableModel.getRowCount());
	}
}

class MyPCRTableModel extends AbstractTableModel
{
	private static MyPCRTableModel instance = null;

	private ArrayList<Action> m_ActionList = null;

	private MyPCRTableModel()
	{
		m_ActionList = new ArrayList<Action>();
	}

	public static MyPCRTableModel getInstance()
	{
		if( instance == null )
			instance = new MyPCRTableModel();
		return instance;
	}

	public void ResetContent()
	{
		m_ActionList.clear();
	}

	@Override
	public int getColumnCount()
	{
		return UIConstant.TABLE_HEADER.length;
	}

	public String getColumnName(int col)
	{
		 return UIConstant.TABLE_HEADER[col];
	}

	@Override
	public Class<?> getColumnClass(int col)
	{
		return getValueAt(0 ,col).getClass();
	}

	@Override
	public int getRowCount()
	{
		return m_ActionList.size();
	}

	@Override
	public void setValueAt(Object aValue, int row, int col)
	{
		m_ActionList.get(row).set(col, (String)aValue);
		fireTableCellUpdated(row ,col);
	}

	@Override
	public Object getValueAt(int row, int col)
	{
		return m_ActionList.get(row).get(col);
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
	{
		return false;
	}

	public void InsertData(Action action)
	{
		m_ActionList.add(action);
		fireTableDataChanged();
	}
}


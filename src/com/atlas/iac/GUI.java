package com.atlas.iac;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

public class GUI {
	@SuppressWarnings("FieldCanBeLocal")
	private final boolean DEBUG = false;
	@SuppressWarnings("FieldCanBeLocal")
	private final Color COBALT = new Color(0x4285f4);
	private final String unfocusedText = "Enter command...";
	@SuppressWarnings("FieldCanBeLocal")
	private final Font font = new Font("Inconsolata", Font.PLAIN, 12);
	
	private JTextField consoleInputField; // For listener
	private JTextArea console; // For printing
	private JList<String> cmdList; // For listener
	private JTable table; // Potentially for listener
	private TableRowSorter<MyTableModel> sorter;
	private JTextField filterText;
	
	GUI(String title, int width, int height) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Create frame
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setResizable(false); //TODO
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				// https://docs.oracle.com/javase/tutorial/sound/sampled-overview.html
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				Client.quit();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
			
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
			
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
			
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				consoleInputField.requestFocusInWindow();
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
			
			}
		});
		frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				//System.out.println("Frame size: " + (int)frame.getSize().getWidth() + " x " + (int)frame.getSize().getHeight());
			}
		});
		
		// Create clientPanel
		JPanel clientPanel = new JPanel();
		clientPanel.setLayout(new BorderLayout());
		
		// Create table with sorter
		MyTableModel model = new MyTableModel();
		sorter = new TableRowSorter<>(model);
		table = new JTable(model);
		table.setRowSorter(sorter);
		table.setAutoCreateRowSorter(true);
		table.getTableHeader().setToolTipText("Click to sort; Shift-Click to sort in reverse order.");
//		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
//		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		table.setRowSelectionAllowed(true);
		table.setColumnSelectionAllowed(false);
		JScrollPane clientTableScroller = new JScrollPane(table);
		
		//Create a separate form for filterText and statusText
		JPanel form = new JPanel(new SpringLayout());
		JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
		form.add(l1);
		filterText = new JTextField();
		//Whenever filterText changes, invoke newFilter.
		filterText.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}
			
			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
			
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		l1.setLabelFor(filterText);
		form.add(filterText);
		
		// Create rightPanel
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setBackground(Color.MAGENTA);
		
		
		// Create consolePanel
		JPanel consolePanel = new JPanel();
		consolePanel.setLayout(new BorderLayout());
		consolePanel.setBackground(Color.ORANGE);
		
		// Create console
		console = new JTextArea();
		console.setEditable(false);
		console.setLineWrap(false);
		console.setBackground(Color.CYAN);
		console.setFont(font);
		console.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {
				consoleInputField.requestFocusInWindow();
			}
			
			public void keyPressed(KeyEvent e) {}
			
			public void keyReleased(KeyEvent e) {}
		});
		console.setSelectionColor(COBALT);
		console.setCaretColor(COBALT);
		JScrollPane consoleScroller = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		consoleScroller.getVerticalScrollBar().setBlockIncrement(console.getFont().getSize());
		consoleScroller.setPreferredSize(new Dimension(width, height));
		
		// Create consoleInputField
		consoleInputField = new JTextField(unfocusedText);
		consoleInputField.setCaretColor(COBALT);
		consoleInputField.setSelectionColor(COBALT);
		consoleInputField.setFont(font);
		consoleInputField.addFocusListener(new FocusListener() {
			
			public void focusGained(FocusEvent e) {
				if (consoleInputField.getText().equals(unfocusedText)) {
					consoleInputField.setText("");
				}
			}
			
			public void focusLost(FocusEvent e) {
				if (consoleInputField.getText().equals("")) {
					consoleInputField.setText(unfocusedText);
				}
			}
		});
		consoleInputField.setSelectionColor(COBALT);
		consoleInputField.setCaretColor(COBALT);
		
		// Create cmdPanel
		JPanel cmdPanel = new JPanel();
		cmdPanel.setLayout(new BorderLayout());
		
		// Create cmdList
		DefaultListModel<String> cmdListModel = new DefaultListModel<>();
		cmdListModel.addElement("status");
		cmdListModel.addElement("help");
		cmdList = new JList<>(cmdListModel);
		cmdList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		cmdList.setLayoutOrientation(JList.VERTICAL);
		cmdList.setSelectedIndex(0); // possibly disable
		cmdList.setVisibleRowCount(-1);
		cmdList.setSelectionBackground(COBALT);
		cmdList.setFont(font);
		JScrollPane cmdListScroller = new JScrollPane(cmdList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		cmdListScroller.getVerticalScrollBar().setBlockIncrement(console.getFont().getSize());
		
		// Create splitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, consoleScroller, clientPanel);
		splitPane.setResizeWeight(0.5);
		splitPane.setOneTouchExpandable(true);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(0.5);
		
		// Create menuBar
		JMenuBar menuBar = new JMenuBar();
		menuBar.setFont(font);
		menuBar.setBackground(Color.BLACK);
		//menuBar.setBorderPainted(false);
		JMenu connection = new JMenu("Connection");
		connection.setMnemonic(KeyEvent.VK_C);
		connection.getAccessibleContext().setAccessibleDescription("This menu has menu items");
		JMenuItem disconnect;
		disconnect = new JMenuItem("Disconnect", KeyEvent.VK_D);
		disconnect.getAccessibleContext().setAccessibleDescription("Disconnect from the server");
		
		// Add everything
		
		clientPanel.add(clientTableScroller, BorderLayout.CENTER);
		clientPanel.add(form, BorderLayout.SOUTH);
//		frame.add(clientPanel, BorderLayout.WEST);
		
		splitPane.setTopComponent(clientPanel);
		consolePanel.add(consoleScroller, BorderLayout.CENTER);
		consolePanel.add(consoleInputField, BorderLayout.SOUTH);
		splitPane.setBottomComponent(consolePanel);
		
		frame.add(splitPane, BorderLayout.CENTER);

//		consolePanel.add(consoleScroller, BorderLayout.CENTER);
//		consolePanel.add(consoleInputField, BorderLayout.SOUTH);
//		splitPane.setTopComponent(consolePanel);
//		cmdPanel.add(cmdListScroller, BorderLayout.CENTER);
//		splitPane.setBottomComponent(cmdPanel);
//		frame.add(splitPane, BorderLayout.EAST);
		
		connection.add(disconnect);
		menuBar.add(connection);
		frame.setJMenuBar(menuBar);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
		frame.setAlwaysOnTop(false);
		
	}
	
	private void newFilter() {
		RowFilter<MyTableModel, Object> rf;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter(filterText.getText(), 0);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}
	
	public JList<String> getCmdList() {
		return cmdList;
	}
	
	public JTextArea getConsole() {
		return console;
	}
	
	public JTextField getConsoleInputField() {
		return consoleInputField;
	}
	
	class MyTableModel extends AbstractTableModel {
		String[] colNames = {
				"Name",
				"UserID",
				"UniqueID",
				"Connected",
				"Ping, Loss",
				"State",
				"Rate",
				"Address"
		};
		
		private Object[][] data = {
				{"Kathy", "Smith", "Snowboarding", 5, false},
				{"John", "Doe", "Rowing", 3, true},
				{"Sue", "Black", "Knitting", 2, false},
				{"Jane", "White", "Speed reading", 20, true},
				{"Joe", "Brown", "Pool", 10, false}
		};
		
		@Override
		public int getRowCount() {
			return data.length;
		}
		
		@Override
		public int getColumnCount() {
			return colNames.length;
		}
		
		@Override
		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
		
		/*
		 * JTable uses this method to determine the default renderer/
		 * editor for each cell. If we didn't implement this method,
		 * then the last column would contain text ("true"/"false"),
		 * rather than a check box.
		 */
//		@Override
//		public Class getColumnClass(int c) {
//			return getValueAt(0, c).getClass();
//		}
		
		/*
		 * Don't need to implement this method unless your table's
		 * data can change.
		 */
		@Override
		public void setValueAt(Object value, int row, int col) {
			if (DEBUG) {
				System.out.println("Setting value at (" + row + "," + col + ") to " + value + " (an instance of " + value.getClass() + ")");
			}
			
			data[row][col] = value;
			fireTableCellUpdated(row, col);
			
			if (DEBUG) {
				System.out.println("New value of data:");
				printDebugData();
			}
		}
		
		private void printDebugData() {
			int numRows = getRowCount();
			int numCols = getColumnCount();
			
			for (int i = 0; i < numRows; i++) {
				System.out.print("    row " + i + ":");
				for (int j = 0; j < numCols; j++) {
					System.out.print("  " + data[i][j]);
				}
				System.out.println();
			}
			System.out.println("--------------------------");
		}
	}
}

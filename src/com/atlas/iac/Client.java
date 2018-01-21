package com.atlas.iac;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Map;

import javax.swing.*;

import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

public class Client {
	private static String ip;
	private static short port;
	private static byte[] password;
	private static GUI gui;
	private static Rcon rcon;
	@SuppressWarnings("FieldCanBeLocal")
	private static Map<String, User> users;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		String[] ips = new String[]{
				"74.91.120.44:27097",
				"74.91.120.44:27098",
				"74.91.120.44:27099"
		};
		JComboBox<String> ipField = new JComboBox<>(ips);
		ipField.setEditable(true);
		JPasswordField passwordField = new JPasswordField();
//		passwordField.setText("");
		final Object[] inputs = new JComponent[]{
				new JLabel("Server IP"),
				ipField,
				new JLabel("Password"),
				passwordField
		};
		int dialog = JOptionPane.showConfirmDialog(null, inputs, "Connect to Server", JOptionPane.OK_CANCEL_OPTION);
		if (dialog == JOptionPane.OK_OPTION) {
			String selected = ((String) ipField.getSelectedItem());
			assert selected != null;
			ip = selected.substring(0, selected.indexOf(':'));
			port = Short.parseShort(selected.substring(selected.indexOf(':') + 1));
			password = toBytes(passwordField.getPassword());
		} else {
			System.exit(0);
		}
		
		gui = new GUI("Insurgency Administration", 420, 630);
		
		gui.getConsoleInputField().setBackground(Color.RED);
		
		try {
			rcon = new Rcon(ip, port, password);
			gui.getConsoleInputField().setBackground(Color.WHITE);
		} catch (IOException | AuthenticationException e) {
			e.printStackTrace();
		}
		
		updateUsers();
		
		// Add action listener for consoleInputField
		gui.getConsoleInputField().addActionListener(e -> {
			String text = gui.getConsoleInputField().getText().trim(); //TODO
			if (!text.isEmpty()) {
				try {
					print(rcon.command(text));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				gui.getConsoleInputField().setText("");
			}
		});
		
		gui.getCmdList().addListSelectionListener(e -> System.out.println(gui.getCmdList().getSelectedValue()));
	}
	
	/**
	 * Prints formatted text to both the terminal and the GUI console
	 *
	 * @param format Format-ready string to be printed
	 * @param args   Optional arguments to format
	 */
	private static void print(String format, Object... args) {
		System.out.print(String.format(format, args) + "\n");
		gui.getConsole().append(String.format(format, args) + "\n");
	}
	
	/**
	 * Send char[] to byte[]
	 * Used for password conversion
	 *
	 * @param chars Characters to be converted
	 *
	 * @return Byte[]
	 */
	private static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}
	
	/**
	 * Updates the list of connected users, as well as their respective data
	 */
	private static void updateUsers() {
		// Send "status" and save the result
		String status = null;
		try {
			status = rcon.command("status");
			status = rcon.command("status");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String userListHeader = "# userid name uniqueid connected ping loss state rate adr\n";
		assert status != null;
		if (!status.substring(status.indexOf(userListHeader) + userListHeader.length(), status.indexOf("#end")).isEmpty()) {
			
			// Isolate the list of connected users
			String userList = status.substring(status.indexOf(userListHeader) + userListHeader.length(), status.indexOf("#end") - 1); // -1 accounts for trailing '\n'
			System.out.println(userList);
			
			// Parse the list
			System.out.println("---");
			String[] entries = userList.split("\\n");
			for (String entry : entries) {
				User user = new User();
				
				// Assign the user's values
				String[] values = entry.split("\\s+");
				int i = 0; // values[0] = "#"
				user.setUserID(Integer.parseInt(values[++i]));
				++i; // values[2] = position in server
				StringBuilder name = new StringBuilder(values[++i]);
				while (values[i].charAt(values[i].length() - 1) != '"') {
					i++;
					//noinspection StringConcatenationInsideStringBufferAppend
					name.append(" " + values[i]);
				}
				user.setName(name.toString().replaceAll("\"", ""));
				user.setUniqueID(values[++i]);
				user.setConnected(values[++i]);
				user.setPing(Short.parseShort(values[++i]));
				user.setLoss(Integer.parseInt(values[++i]));
				user.setState(values[++i]);
				user.setRate(Integer.parseInt(values[++i]));
				user.setAddress(values[++i]);
				
				if (!users.containsValue(user)) {
					users.put(user.getName(), user);
				}
				System.out.println(user);
			}
		}
	}
	
	/**
	 * Handles disconnecting from the server and quits the application
	 */
	static void quit() {
		try {
			rcon.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
}
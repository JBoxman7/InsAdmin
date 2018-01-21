package com.atlas.iac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Scanner;

import net.kronos.rkon.core.Rcon;
import net.kronos.rkon.core.ex.AuthenticationException;

public class ConsoleClient {
	private static Rcon rcon;
	
	public static void main(String[] args) {
		String[] servers = new String[]{
				"74.91.120.44:27097", // CO-OP Server
				"74.91.120.120:27015", // Scrim/testing Server
				"74.91.120.44:27099"  // PVP Server
		};
		
		System.out.println("What server would you like to connect to?");
		for (int i = 0; i < servers.length; i++) {
			System.out.println(i + ".\t" + servers[i]);
		}
		
		Scanner scanner = new Scanner(System.in);
		
		String[] serverData;
		while (true) {
			int option = Integer.parseInt(scanner.nextLine()); // Can't just do nextInt() because the reader caret doesn't move past the \n. nextLine() includes the \n.
			if (option >= 0 && option < servers.length) {
				serverData = servers[option].split(":");
				break;
			} else {
				System.out.println("Invalid input!");
			}
		}
		
		System.out.println("Password: ");
		String pass = scanner.nextLine();
		byte[] password = ConsoleClient.toBytes(pass.toCharArray());
		
		try {
			rcon = new Rcon(serverData[0], Integer.parseInt(serverData[1]), password);
		} catch (IOException | AuthenticationException e) {
			e.printStackTrace();
		}
		
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			//noinspection InfiniteLoopStatement
			while (true) {
				String response = rcon.command(reader.readLine());
				System.out.println(response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static byte[] toBytes(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tak;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chaitu
 */
public class TakServer extends Thread{
	public static int port;
	public static int portws;

	@Override
	public void run () {
		ServerSocket ssocket;
		ServerSocket wsocket;
		try {
			ssocket = new ServerSocket(port);
			System.out.println("Server running at " + port);
			ssocket.setSoTimeout(70);
			wsocket = new ServerSocket(portws);
			System.out.println("WebSocket Server running at " + portws);
			wsocket.setSoTimeout(70);
			while(true) {
				try{
					Date date = new Date();
					if((date.getTime()&128)==128){
						Socket socket = ssocket.accept();
						TakServer.Log("New Telnet client");
						Client cc = new Client(new Telnet(socket));
						cc.start();
					}
					else{
						Socket socket = wsocket.accept();
						TakServer.Log("New Websocket client");
						Client cc = new Client(new Websocket(socket));
						cc.start();
					}
				}
				catch(SocketTimeoutException e){
					
				}
			}
		} catch (IOException ex) {
			Logger.getLogger(TakServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	static void Log(Object obj) {
		System.out.println(new Date()+"		"+obj);
	}
}

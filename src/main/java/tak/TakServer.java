/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tak;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import tak.httpHandlers.AddSeekHandler;

/**
 *
 * @author chaitu
 */
public class TakServer extends Thread{
	public static int port;
	public static int portws;
	public static int portHttp;

	@Override
	public void run () {
		ServerSocket ssocket;
		ServerSocket wsocket;
		HttpServer httpServer;
		try {
			ssocket = new ServerSocket(port);
			System.out.println("Server running at " + port);
			ssocket.setSoTimeout(70);
			wsocket = new ServerSocket(portws);
			System.out.println("WebSocket Server running at " + portws);
			wsocket.setSoTimeout(70);

			// HTTP Server example from https://stackoverflow.com/questions/3732109/simple-http-server-in-java-using-only-java-se-api
			httpServer = HttpServer.create(new InetSocketAddress(portHttp), 0);
			System.out.println("HTTPServer running at " + portHttp);
			 // TODO: this matches `/api/v1/seeks*` but should only match exactly `/api/v1/seeks`
			httpServer.createContext("/api/v1/seeks", new AddSeekHandler());
			// Enables parallel processing of requests, probably not necessary
			httpServer.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
			httpServer.start();

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

package es.um.redes.nanoFiles.tcp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class NFServerSimple {

	//private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private static final String STOP_SERVER_COMMAND = "fgstop";
	private static final int PORT = 10000;
	private static volatile boolean stopServer=false;
	private static ServerSocket serverSocket = null;

	public NFServerSimple() throws IOException {
				
		boolean binded=false;
		int increase=0;
		while(!binded) {
			try {
			/*
			 * TODO: Crear una direción de socket a partir del puerto especificado
			 */
			InetSocketAddress serverAddress = new InetSocketAddress(PORT+increase);
			/*
			 * TODO: Crear un socket servidor y ligarlo a la dirección de socket anterior
			 */
			serverSocket = new ServerSocket();
			serverSocket.bind(serverAddress);
			serverSocket.setReuseAddress(true);
			binded=true;
			} catch (BindException e) { /*Si no puede hacer bind incrementar el puerto*/
				System.out.println("Port " + (PORT + increase) + " is already in use, trying next port...");
				increase++;
			}
		}
		stopServer=false;
	}

	/**
	 * Método para ejecutar el servidor de ficheros en primer plano. Sólo es capaz
	 * de atender una conexión de un cliente. Una vez se lanza, ya no es posible
	 * interactuar con la aplicación a menos que se implemente la funcionalidad de
	 * detectar el comando STOP_SERVER_COMMAND (opcional)
	 * 
	 */
	public void run() {
		/* Se separa en dos hilos la lectura por teclado y el servidor. El hilo teclado
		 * se encargará de comprobar si lo que se ha introducido por teclado es un fgstop,
		 * en cuyo caso el servidor parará sin tener que salir del accept, y el hilo servidor
		 * se encargará de servir los ficheros en primer plano.   		  		
		 */
		Thread teclado=new Thread(() -> leerTeclado());
		teclado.start();
		Thread servidor=new Thread(() ->servidor());
		servidor.start();
		while(!stopServer) {
			
		}	
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("NFServerSimple stopped. Returning to the nanoFiles shell...");
		
	}
	 private static void servidor() {
		 if (serverSocket == null) {
				System.err.println("* Error: Failed to run the file server. ");
				return;
			}else {
				System.out.println("NFServerSimple server running on: "+serverSocket.getLocalSocketAddress());
				System.out.println("Enter fgstop to stop the server. ");
			}
			Socket socket = null;
			while(!stopServer) {
				try {
					
					socket=serverSocket.accept();
				} catch (IOException e) {
					if (!stopServer) {
						System.err.println("* Error: Problem accepting a connection.");
					}
					
					socket=null;
				}
			/*
			 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
			 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
			 * hay que pasarle el socket devuelto por accept
			 */
			if (socket!=null) {
				NFServerComm.serveFilesToClient(socket);
			}
		}	
	 }
	private static void leerTeclado() {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	    try {
	        while (true) {
	            String input = reader.readLine();
	            if (input.equals(STOP_SERVER_COMMAND)) {
	                stopServer = true;
	                break; 
	            }
	        }
	    } catch (IOException e) {
	        System.err.println("* Error al leer la entrada del teclado.");
	    }
	}
}


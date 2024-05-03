package es.um.redes.nanoFiles.tcp.server;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.Enumeration;
import java.util.Random;

/**
 * Servidor que se ejecuta en un hilo propio. Creará objetos
 * {@link NFServerThread} cada vez que se conecte un cliente.
 */
public class NFServer implements Runnable {

	private static final int PORT=10000; 	
	private static ServerSocket serverSocket = null;
	private static volatile boolean stopServer = false;
	//private static final int SERVERSOCKET_ACCEPT_TIMEOUT_MILISECS = 1000;
	private Random random = new Random();

	public NFServer() throws IOException {
		/*
		 * TODO: Crear un socket servidor y ligarlo a cualquier puerto disponible
		 */
		boolean binded=false;
		stopServer=false;
		while(!binded) {
			try {
			InetSocketAddress serverAddress = new InetSocketAddress(random.nextInt(PORT, PORT+40000)); //puerto efímero
			serverSocket = new ServerSocket();	
			serverSocket.bind(serverAddress);
			binded=true;
			} catch (BindException e ) {
				
			}
		}
		
	}

	/**
	 * Método que crea un socket servidor y ejecuta el hilo principal del servidor,
	 * esperando conexiones de clientes.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		/*
		 * TODO: Usar el socket servidor para esperar conexiones de otros peers que
		 * soliciten descargar ficheros
		 */
		Thread servidor=new Thread(() ->servidor()); /*Se ejecuta el servidor como hilo para que en caso de recibir un stopserver
													  *no haya que esperar a salir del accept para que el servidor pare*/												
		servidor.start();
		while(!stopServer) {	//hasta que no se reciba el stopserver no se cierra el socket.
			
		}
		try {
			serverSocket.close();
		}catch(IOException e) {
			System.err.println("Failed to stop the server.");
		}
		System.out.println("Stopped the local file server.");
				
		}
		
	private static void servidor() {
		if (serverSocket==null || !serverSocket.isBound()) {
			System.err.println("* Error: Failed to run the file server.");
			return;
		}
		Socket socket=null;
		while(!stopServer) {
			try {
				socket=serverSocket.accept();
				System.out.println("\nNew client connected from: "+socket.getInetAddress().toString()+":"+socket.getPort());
			}catch(IOException e) {
				socket=null;
				if(!stopServer) {
					System.err.println("There was a problem with the local file server.");
				}
				
			}
			if(socket!=null) {
				/*
				 * TODO: Al establecerse la conexión con un peer, la comunicación con dicho
				 * cliente se hace en el método NFServerComm.serveFilesToClient(socket), al cual
				 * hay que pasarle el socket devuelto por accept
				 */
				/*
				 * TODO: (Opcional) Crear un hilo nuevo de la clase NFServerThread, que llevará
				 * a cabo la comunicación con el cliente que se acaba de conectar, mientras este
				 * hilo vuelve a quedar a la escucha de conexiones de nuevos clientes (para
				 * soportar múltiples clientes). Si este hilo es el que se encarga de atender al
				 * cliente conectado, no podremos tener más de un cliente conectado a este
				 * servidor.
				 */
				NFServerThread connectionThread =new NFServerThread(socket); 
				connectionThread.start();
			}
			
		}
	}
	
	
	
	
	/**
	 * TODO: Añadir métodos a esta clase para: 1) Arrancar el servidor en un hilo
	 * nuevo que se ejecutará en segundo plano 2) Detener el servidor (stopserver)
	 * 3) Obtener el puerto de escucha del servidor etc.
	 */

	public void startServer() {
		new Thread(this).start();
	}
	public int getPort(){
		if (serverSocket!=null) {
			return serverSocket.getLocalPort();
		}
		else {
		return 0;
		}
	}
	public String getAddress(){
		if (serverSocket!=null) {
			return serverSocket.getLocalSocketAddress().toString();
		}
		else {
		return null;
		}
	}
	/* Este método se encarga de obtener la ip de nuestra red local. Para ello hace un enumerado con las interfaces de red 
	 * 	de la máquina, y para cada interfaz va obteniendo las direcciones de la misma. Por último, comprueba que la direccion
	 * no sea de loopback, que no sea IPv6 (la dirección contiene ":"), y que pertenezca a una interfaz que empiece por en, wl, o eth
	 * pues así nos aseguramos que la dirección ip va a ser de una interfaz WiFi o Ethernet.    
	 */
	public String getIP(){
		String addr=null;
		try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (!address.isLoopbackAddress() && !address.getHostAddress().contains(":") &&(networkInterface.getName().contains("en")||networkInterface.getName().contains("wl")||networkInterface.getName().contains("eth"))) {
                       addr=address.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		return addr;	
	}
	public void stopServer() {
		stopServer=true;
	}


}

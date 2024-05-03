package es.um.redes.nanoFiles.udp.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Random;
import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;

public class NFDirectoryServer {
	/**
	 * Número de puerto UDP en el que escucha el directorio
	 */
	public static final int DIRECTORY_PORT = 6868;

	/**
	 * Socket de comunicación UDP con el cliente UDP (DirectoryConnector)
	 */
	private DatagramSocket socket = null;
	/**
	 * Estructura para guardar los nicks de usuarios registrados, y clave de sesión
	 * 
	 */
	private HashMap<String, Integer> nicks;
	/**
	 * Estructura para guardar las claves de sesión y sus nicks de usuario asociados
	 * 
	 */
	private HashMap<Integer, String> sessionKeys;
	/*
	 * TODO: Añadir aquí como atributos las estructuras de datos que sean necesarias
	 * para mantener en el directorio cualquier información necesaria para la
	 * funcionalidad del sistema nanoFilesP2P: ficheros publicados, servidores
	 * registrados, etc.
	 */
	private HashMap<Integer, Integer> servers;
	
	private HashMap<Integer, String> IPs;



	/**
	 * Generador de claves de sesión aleatorias (sessionKeys)
	 */
	Random random = new Random();
	/**
	 * Probabilidad de descartar un mensaje recibido en el directorio (para simular
	 * enlace no confiable y testear el código de retransmisión)
	 */
	private double messageDiscardProbability;

	public NFDirectoryServer(double corruptionProbability) throws SocketException {
		/*
		 * Guardar la probabilidad de pérdida de datagramas (simular enlace no
		 * confiable)
		 */
		messageDiscardProbability = corruptionProbability;
		/*
		 * TODO: (Boletín UDP) Inicializar el atributo socket: Crear un socket UDP
		 * ligado al puerto especificado por el argumento directoryPort en la máquina
		 * local,
		 */
		socket = new DatagramSocket(DIRECTORY_PORT);
		/*
		 * TODO: (Boletín UDP) Inicializar el resto de atributos de esta clase
		 * (estructuras de datos que mantiene el servidor: nicks, sessionKeys, etc.)
		 */
		
		nicks = new HashMap<String, Integer>();
		sessionKeys = new HashMap<Integer, String>();
		servers = new HashMap<Integer, Integer>(); 
		IPs = new HashMap<Integer, String>();


		if (NanoFiles.testMode) {
			if (socket == null || nicks == null || sessionKeys == null) {
				System.err.println("[testMode] NFDirectoryServer: code not yet fully functional.\n"
						+ "Check that all TODOs in its constructor and 'run' methods have been correctly addressed!");
				System.exit(-1);
			}
		}
	}

	public void run() throws IOException {
		byte[] receptionBuffer = null;
		InetSocketAddress clientAddr = null;
		int dataLength = -1;
		/*
		 * TODO: (Boletín UDP) Crear un búfer para recibir datagramas y un datagrama
		 * asociado al búfer
		 */
		receptionBuffer = new byte[DirMessage.PACKET_MAX_SIZE];
		
		System.out.println("Directory starting on port "+DIRECTORY_PORT+" . . .");

		while (true) { // Bucle principal del servidor de directorio

			// TODO: (Boletín UDP) Recibimos a través del socket un datagrama
			DatagramPacket requestPacket = new DatagramPacket(receptionBuffer, receptionBuffer.length);
			socket.receive(requestPacket);
			// TODO: (Boletín UDP) Establecemos dataLength con longitud del datagrama
			// recibido
			dataLength=requestPacket.getLength();
			// TODO: (Boletín UDP) Establecemos 'clientAddr' con la dirección del cliente,
			// obtenida del
			// datagrama recibido
			clientAddr= (InetSocketAddress) requestPacket.getSocketAddress();

			if (NanoFiles.testMode) {
				if (receptionBuffer == null || clientAddr == null || dataLength < 0) {
					System.err.println("NFDirectoryServer.run: code not yet fully functional.\n"
							+ "Check that all TODOs have been correctly addressed!");
					System.exit(-1);
				}
			}
			System.out.println("Directory received datagram from " + clientAddr + " of size " + dataLength + " bytes");

			// Analizamos la solicitud y la procesamos
			if (dataLength > 0) {
				String messageFromClient = null;
				/*
				 * TODO: (Boletín UDP) Construir una cadena a partir de los datos recibidos en
				 * el buffer de recepción
				 */
				messageFromClient=new String (receptionBuffer,0,dataLength);



				if (NanoFiles.testMode) { // En modo de prueba (mensajes en "crudo", boletín UDP)
					System.out.println("[testMode] Contents interpreted as " + dataLength + "-byte String: \""
							+ messageFromClient + "\"");
					/*
					 * TODO: (Boletín UDP) Comprobar que se ha recibido un datagrama con la cadena
					 * "login" y en ese caso enviar como respuesta un mensaje al cliente con la
					 * cadena "loginok". Si el mensaje recibido no es "login", se informa del error
					 * y no se envía ninguna respuesta.
					 */
					if (messageFromClient.equals("login")){
						String messageToClient = new String("loginok");
						byte[] dataToClient = messageToClient.getBytes();
						DatagramPacket packetToClient = new DatagramPacket(dataToClient, dataToClient.length, clientAddr);
						socket.send(packetToClient);
					}else {
						System.out.println("*No login. ");
					}


				} else { // Servidor funcionando en modo producción (mensajes bien formados)

					// Vemos si el mensaje debe ser ignorado por la probabilidad de descarte
					double rand = Math.random();
					if (rand < messageDiscardProbability) {
						System.err.println("Directory DISCARDED datagram from " + clientAddr);
						continue;
					}

					/*
					 * TODO: Construir String partir de los datos recibidos en el datagrama. A
					 * continuación, imprimir por pantalla dicha cadena a modo de depuración.
					 * Después, usar la cadena para construir un objeto DirMessage que contenga en
					 * sus atributos los valores del mensaje (fromString).
					 */
					System.out.println("Message received: "+messageFromClient);
					DirMessage requestMessage = DirMessage.fromString(messageFromClient);
					/*
					 * TODO: Llamar a buildResponseFromRequest para construir, a partir del objeto
					 * DirMessage con los valores del mensaje de petición recibido, un nuevo objeto
					 * DirMessage con el mensaje de respuesta a enviar. Los atributos del objeto
					 * DirMessage de respuesta deben haber sido establecidos con los valores
					 * adecuados para los diferentes campos del mensaje (operation, etc.)
					 */
					DirMessage responseMessage = buildResponseFromRequest(requestMessage, clientAddr);
					/*
					 * TODO: Convertir en string el objeto DirMessage con el mensaje de respuesta a
					 * enviar, extraer los bytes en que se codifica el string (getBytes), y
					 * finalmente enviarlos en un datagrama
					 */
					if (responseMessage != null) {
					String responseString = responseMessage.toString();
					byte[] responseBytes = responseString.getBytes();
					DatagramPacket packetResponse= new DatagramPacket(responseBytes, responseBytes.length,clientAddr);
					socket.send(packetResponse);
					 System.out.println("Sent response: " + responseString);
					} else {
						System.out.println("No response made from request.");
					}

				}
			} else {
				System.err.println("Directory ignores EMPTY datagram from " + clientAddr);
			}

		}
	}

	private DirMessage buildResponseFromRequest(DirMessage msg, InetSocketAddress clientAddr) {
		/*
		 * TODO: Construir un DirMessage con la respuesta en función del tipo de mensaje
		 * recibido, leyendo/modificando según sea necesario los atributos de esta clase
		 * (el "estado" guardado en el directorio: nicks, sessionKeys, servers,
		 * files...)
		 */
		String operation = msg.getOperation();

		DirMessage response = null;

		switch (operation) {
		case DirMessageOps.OPERATION_LOGIN: {
			String username = msg.getNickname();

			/*
			 * TODO: Comprobamos si tenemos dicho usuario registrado (atributo "nicks"). Si
			 * no está, generamos su sessionKey (número aleatorio entre 0 y 1000) y añadimos
			 * el nick y su sessionKey asociada. NOTA: Puedes usar random.nextInt(10000)
			 * para generar la session key
			 */
			if(!nicks.containsKey(username)) {
				int sessionKey = random.nextInt(10000); // Generar una session key aleatoria
				while (sessionKeys.containsKey(sessionKey)) {
					sessionKey = random.nextInt(10000); //Asegurarse de que la clave no ha sido ya generada
				}
                nicks.put(username, sessionKey);
                sessionKeys.put(sessionKey, username);
                servers.put(sessionKey, -1);
                IPs.put(sessionKey, null);
			
			/*
			 * TODO: Construimos un mensaje de respuesta que indique el éxito/fracaso del
			 * login y contenga la sessionKey en caso de éxito, y lo devolvemos como
			 * resultado del método.
			 */
            	/*
    			 * TODO: Imprimimos por pantalla el resultado de procesar la petición recibida
    			 * (éxito o fracaso) con los datos relevantes, a modo de depuración en el
    			 * servidor
    			 */
			 DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_LOGIN_OK);
             responseMessage.setSessionKey(Integer.toString(sessionKey));
             System.out.println("Login successful for: " + username + " with session key: " + sessionKey);
             response=responseMessage;
			}
			else {
                // Usuario ya registrado
                System.out.println("User already logged in: " + username);
                int existingSessionKey = nicks.get(username);
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_LOGIN_FAIL);
                responseMessage2.setSessionKey(Integer.toString(existingSessionKey));
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_LOGOUT: {
			Integer sessionKey = Integer.parseInt(msg.getSessionKey());
		
			if(sessionKeys.containsKey(sessionKey)) { //comprobar que está loggeado
				String username = sessionKeys.get(sessionKey);
                nicks.remove(username);
                sessionKeys.remove(sessionKey);
			
			 DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_LOGOUT_OK);
             System.out.println("Logout successful for: " + username + " with session key: " + sessionKey);
             response=responseMessage;
			}
			else {
                // Usuario no registrado
                System.err.println("User was not logged in. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_LOGOUT_FAIL);
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_USERLIST: {
			

			if(!nicks.isEmpty()) { //comprobar que hay usuarios
				String[] names=new String[nicks.size()];
				nicks.keySet().toArray(names); //transformar las claves del diccionario a array.
				String[] Servers=new String[nicks.size()]; 
				int i=0;
				for(Object sessionKey : sessionKeys.keySet().toArray()) {
					if(servers.get(sessionKey)!=-1) { //si el puerto no es -1, significa que es servidor
						String name=sessionKeys.get(sessionKey);
						Servers[i]=name;
						i++;
					}
				}
				DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_USERLIST_OK);
				 responseMessage.setUsers(names);
				 responseMessage.setServers(Servers);
	             System.out.println("Userlist sent.");
	             response=responseMessage;
			}
			else {
                // Usuario no registrado
                System.err.println("Cannot get the userlist. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_USERLIST_FAIL);
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_REGISTER_SERVER: {
			
			int port=msg.getPort();
			int sessionKey=Integer.parseInt(msg.getSessionKey());
			if (sessionKeys.containsKey(sessionKey)) { //que el usuario esté loggeado
				servers.put(sessionKey,port); 
				DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_REGISTER_SERVER_OK);
				 System.out.println("Registered the server using port: " +port);
	             response=responseMessage;
			}
			 
			else {
                // Usuario no registrado
                System.err.println("Cannot register the server. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_REGISTER_SERVER_FAIL);
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_UNREGISTER_SERVER: {
			
			int sessionKey=Integer.parseInt(msg.getSessionKey());
			if (servers.get(sessionKey)!=-1) { //que sea ya un servidor
				servers.put(sessionKey,-1); 
				DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_SERVER_OK);
				 System.out.println("Deleted the server. ");
	             response=responseMessage;
			}
			 
			else {
                // Usuario no registrado
                System.err.println("Cannot delete the server. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_SERVER_FAIL);
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_REGISTER_IP: {
			
			String ip=msg.getIP();
			int sessionKey=Integer.parseInt(msg.getSessionKey());
			if (sessionKeys.containsKey(sessionKey)) { //que el usuario esté ya loggeado
				IPs.put(sessionKey,ip); 
				DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_REGISTER_IP_OK);
				 System.out.println("Registered the server using IP: " +ip);
	             response=responseMessage;
			}
			 
			else {
                // Usuario no registrado
                System.err.println("Cannot register the server. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_REGISTER_IP_FAIL);
                response=responseMessage2;
			}

			break;
		}
		case DirMessageOps.OPERATION_REQUEST_ADDRESS: {
			
				if (nicks.containsKey(msg.getNickname())) { //usuario esté loggeado
				int sesionKey=nicks.get(msg.getNickname());	
				String addr=IPs.get(sesionKey);
				int port=servers.get(sesionKey); 
				if(port!=-1) { //si es un servidor
					DirMessage responseMessage = new DirMessage(DirMessageOps.OPERATION_REQUEST_ADDRESS_OK);
					responseMessage.setIP(addr);
					responseMessage.setPort(port);
					System.out.println("Answered with the following address for user: "+msg.getNickname()+ "= "+addr+":"+port); 
		            response=responseMessage;
				}
				else {
					 System.err.println("Nickname's address is not a server. ");
		             DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_REQUEST_ADDRESS_FAIL);
		             response=responseMessage2;
				}
			}
			 
			else {
                // Usuario no registrado
                System.err.println("Cannot get the nickname's address. ");
                DirMessage responseMessage2 = new DirMessage(DirMessageOps.OPERATION_REQUEST_ADDRESS_FAIL);
                response=responseMessage2;
			}
			break;
		}
		default:
			System.out.println("Unexpected message operation: \"" + operation + "\"");
		}
		return response;

	}
	
}

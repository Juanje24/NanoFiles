package es.um.redes.nanoFiles.udp.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import es.um.redes.nanoFiles.udp.message.DirMessage;
import es.um.redes.nanoFiles.udp.message.DirMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

/**
 * Cliente con métodos de consulta y actualización específicos del directorio
 */
public class DirectoryConnector {
	/**
	 * Puerto en el que atienden los servidores de directorio
	 */
	private static final int DIRECTORY_PORT = 6868;
	/**
	 * Tiempo máximo en milisegundos que se esperará a recibir una respuesta por el
	 * socket antes de que se deba lanzar una excepción SocketTimeoutException para
	 * recuperar el control
	 */
	private static final int TIMEOUT = 1000;
	/**
	 * Número de intentos máximos para obtener del directorio una respuesta a una
	 * solicitud enviada. Cada vez que expira el timeout sin recibir respuesta se
	 * cuenta como un intento.
	 */
	private static final int MAX_NUMBER_OF_ATTEMPTS = 5;

	/**
	 * Valor inválido de la clave de sesión, antes de ser obtenida del directorio al
	 * loguearse
	 */
	public static final int INVALID_SESSION_KEY = -1;

	/**
	 * Socket UDP usado para la comunicación con el directorio
	 */
	private DatagramSocket socket;
	/**
	 * Dirección de socket del directorio (IP:puertoUDP)
	 */
	private InetSocketAddress directoryAddress;

	private int sessionKey = INVALID_SESSION_KEY;
	//private boolean successfulResponseStatus;
	//private String errorDescription;

	public DirectoryConnector(String address) throws IOException {
		/*
		 * TODO: Convertir el nombre de host 'address' a InetAddress y guardar la
		 * dirección de socket (address:DIRECTORY_PORT) del directorio en el atributo
		 * directoryAddress, para poder enviar datagramas a dicho destino.
		 */
		InetAddress addressAsInetAdress = InetAddress.getByName(address);
		directoryAddress=new InetSocketAddress(addressAsInetAdress,DIRECTORY_PORT);
		/*
		 * TODO: Crea el socket UDP en cualquier puerto para enviar datagramas al
		 * directorio
		 */
		socket = new DatagramSocket();

	}

	/**
	 * Método para enviar y recibir datagramas al/del directorio
	 * 
	 * @param requestData los datos a enviar al directorio (mensaje de solicitud)
	 * @return los datos recibidos del directorio (mensaje de respuesta)
	 */
	private byte[] sendAndReceiveDatagrams(byte[] requestData) {
		byte responseData[] = new byte[DirMessage.PACKET_MAX_SIZE];
		byte response[] = null;
		if (directoryAddress == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP server destination address is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"directoryAddress\"");
			System.exit(-1);

		}
		if (socket == null) {
			System.err.println("DirectoryConnector.sendAndReceiveDatagrams: UDP socket is null!");
			System.err.println(
					"DirectoryConnector.sendAndReceiveDatagrams: make sure constructor initializes field \"socket\"");
			System.exit(-1);
		}
				
		int numTry=0;
		boolean received=false;
		/*
		 * TODO: Enviar datos en un datagrama al directorio y recibir una respuesta. El
		 * array devuelto debe contener únicamente los datos recibidos, *NO* el búfer de
		 * recepción al completo.
		 */
		DatagramPacket packetToServer = new DatagramPacket(requestData, requestData.length,directoryAddress);
		DatagramPacket packetFromServer = new DatagramPacket(responseData, responseData.length);
		while(numTry<MAX_NUMBER_OF_ATTEMPTS && !received) {
			try {
				socket.send(packetToServer);
				/*
				 * TODO: Una vez el envío y recepción asumiendo un canal confiable (sin
				 * pérdidas) esté terminado y probado, debe implementarse un mecanismo de
				 * retransmisión usando temporizador, en caso de que no se reciba respuesta en
				 * el plazo de TIMEOUT. En caso de salte el timeout, se debe reintentar como
				 * máximo en MAX_NUMBER_OF_ATTEMPTS ocasiones. 
				 */
				socket.setSoTimeout(TIMEOUT);
				socket.receive(packetFromServer);
				received=true;
				/*
				 * TODO: Las excepciones que puedan lanzarse al leer/escribir en el socket deben
				 * ser capturadas y tratadas en este método. Si se produce una excepción de
				 * entrada/salida (error del que no es posible recuperarse), se debe informar y
				 * terminar el programa.
				 */
				/*
				 * NOTA: Las excepciones deben tratarse de la más concreta a la más genérica.
				 * SocketTimeoutException es más concreta que IOException.
				 */
			} catch (SocketTimeoutException e) {
				System.out.println("* Timeout exception. Trying again...");
				numTry++;
			}catch (IOException e) {
				System.err.println("Error receiving the datagram.");
				System.exit(-1);
			}
		}
		if(numTry==MAX_NUMBER_OF_ATTEMPTS && !received) {
			System.err.println("Could not receive packet.");
			System.exit(-1);
		}
		int respDataLen = packetFromServer.getLength();
		response = new byte[respDataLen];
		System.arraycopy(responseData, 0, response, 0, respDataLen);
		
		if (response != null && response.length == responseData.length) {
			System.err.println("Your response is as large as the datagram reception buffer!!\n"
					+ "You must extract from the buffer only the bytes that belong to the datagram!");
		}
		return response;
	}

	/**
	 * Método para probar la comunicación con el directorio mediante el envío y
	 * recepción de mensajes sin formatear ("en crudo")
	 * 
	 * @return verdadero si se ha enviado un datagrama y recibido una respuesta
	 */
	public boolean testSendAndReceive() {
		/*
		 * TODO: Probar el correcto funcionamiento de sendAndReceiveDatagrams. Se debe
		 * enviar un datagrama con la cadena "login" y comprobar que la respuesta
		 * recibida es "loginok". En tal caso, devuelve verdadero, falso si la respuesta
		 * no contiene los datos esperados.
		 */
		boolean success = false;

		String strToSend = new String("login");
		byte[] dataToSend = strToSend.getBytes();
		byte[] receivedData = null;
		boolean communicationOk = true;
		
		try {
		receivedData= sendAndReceiveDatagrams(dataToSend);
		}
		catch(Exception e) {
			communicationOk=false;
			System.err.println("*Error in testSendAndReceive. ");
		}
		if (communicationOk && receivedData != null) {
			int receivedDataLen = receivedData.length;
			if (receivedDataLen>0) {
				String strFromServer = new String(receivedData,0,receivedDataLen);
				if (strFromServer.equals("loginok")) {
					success=true;
				}
			}
		}
		return success;
	}

	public InetSocketAddress getDirectoryAddress() {
		return directoryAddress;
	}

	public int getSessionKey() {
		return sessionKey;
	}

	/**
	 * Método para "iniciar sesión" en el directorio, comprobar que está operativo y
	 * obtener la clave de sesión asociada a este usuario.
	 * 
	 * @param nickname El nickname del usuario a registrar
	 * @return La clave de sesión asignada al usuario que acaba de loguearse, o -1
	 *         en caso de error
	 */
	public boolean logIntoDirectory(String nickname) {
		assert (sessionKey == INVALID_SESSION_KEY);
		boolean success = false;
		// DONE: 1.Crear el mensaje a enviar (objeto DirMessage) con atributos adecuados
		// (operation, etc.) NOTA: Usar como operaciones las constantes definidas en la clase
		// DirMessageOps
		DirMessage loginMessage = new DirMessage(DirMessageOps.OPERATION_LOGIN);
	    loginMessage.setNickname(nickname);
		// DONE: 2.Convertir el objeto DirMessage a enviar a un string (método toString)
	    String messageStr = loginMessage.toString();
		// DONE: 3.Crear un datagrama con los bytes en que se codifica la cadena
	    byte[] messageBytes = messageStr.getBytes();

		// DONE: 4.Enviar datagrama y recibir una respuesta (sendAndReceiveDatagrams).
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return false;
	    }
		// DONE: 5.Convertir respuesta recibida en un objeto DirMessage (método
		// DirMessage.fromString)
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		// DONE: 6.Extraer datos del objeto DirMessage y procesarlos (p.ej., sessionKey)
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_LOGIN_OK)) {
	        sessionKey = Integer.parseInt(responseMessage.getSessionKey()); 
	        if (sessionKey<0 || sessionKey>10000) {
	        	sessionKey=INVALID_SESSION_KEY;
	        	return false;
	        }
	        success = true;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_LOGIN_FAIL)) {
	        System.err.println("Login failed.");
	        return false;
	    }
		// DONE: 7.Devolver éxito/fracaso de la operación

	    //System.out.println("Login successful with session key: "+sessionKey);

		return success;
	}

	/**
	 * Método para obtener la lista de "nicknames" registrados en el directorio.
	 * Opcionalmente, la respuesta puede indicar para cada nickname si dicho peer
	 * está sirviendo ficheros en este instante.
	 * 
	 * @return La lista de nombres de usuario registrados, o null si el directorio
	 *         no pudo satisfacer nuestra solicitud
	 */
	public String[][] getUserList() {
		String[] userlist = null;
		String[] servers=null; 
		String[][] answer=new String[2][100];
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);

		DirMessage userlistMessage = new DirMessage(DirMessageOps.OPERATION_USERLIST);
		
	    String messageStr = userlistMessage.toString();
		
	    byte[] messageBytes = messageStr.getBytes();
		
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return null;
	    }
		
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_USERLIST_OK)) {
	    	userlist=responseMessage.getUsers();
	    	answer[0]=userlist;
	    	servers=responseMessage.getServers();
	    	answer[1]=servers;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_USERLIST_FAIL)) {
	        System.err.println("Userlist failed.");
	        return null;
	    }

		return answer;
	}

	/**
	 * Método para "cerrar sesión" en el directorio
	 * 
	 * @return Verdadero si el directorio eliminó a este usuario exitosamente
	 */
	public boolean logoutFromDirectory() {
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);
		boolean success = false;
		
		DirMessage logoutMessage = new DirMessage(DirMessageOps.OPERATION_LOGOUT);
	    logoutMessage.setSessionKey(Integer.toString(sessionKey));

	    String messageStr = logoutMessage.toString();

	    byte[] messageBytes = messageStr.getBytes();

	
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return false;
	    }
	
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_LOGOUT_OK)) {
	    	sessionKey=INVALID_SESSION_KEY;
	        success = true;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_LOGOUT_FAIL)) {
	        System.err.println("Logout failed.");
	        return false;
	    }
		

		return success;


	}

	/**
	 * Método para dar de alta como servidor de ficheros en el puerto indicado a
	 * este peer.
	 * 
	 * @param serverPort El puerto TCP en el que este peer sirve ficheros a otros
	 * @return Verdadero si el directorio acepta que este peer se convierta en
	 *         servidor.
	 */
	public boolean registerServerPort(int serverPort) {
		assert (sessionKey != INVALID_SESSION_KEY);
		boolean success = false;
		
		DirMessage registerMessage = new DirMessage(DirMessageOps.OPERATION_REGISTER_SERVER);
		registerMessage.setSessionKey(Integer.toString(sessionKey));
		registerMessage.setPort(serverPort);

	    String messageStr = registerMessage.toString();

	    byte[] messageBytes = messageStr.getBytes();

	
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return false;
	    }
	
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REGISTER_SERVER_OK)) {
	    	
	        success = true;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REGISTER_SERVER_FAIL)) {
	        System.err.println("Failed to register the server.");
	        return false;
	    }
		

		return success;
	}
	public boolean unregisterServerPort(int sessionKey) {
		assert (sessionKey != INVALID_SESSION_KEY);
		boolean success = false;
		
		DirMessage unregisterMessage = new DirMessage(DirMessageOps.OPERATION_UNREGISTER_SERVER);
		unregisterMessage.setSessionKey(Integer.toString(sessionKey));

	    String messageStr = unregisterMessage.toString();

	    byte[] messageBytes = messageStr.getBytes();

	
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return false;
	    }
	
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_UNREGISTER_SERVER_OK)) {
	    	
	        success = true;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_UNREGISTER_SERVER_FAIL)) {
	        System.err.println("Failed to delete the server.");
	        return false;
	    }
		

		return success;
	}
	public boolean registerServerIP(String serverIP) {
		assert (sessionKey != INVALID_SESSION_KEY);
		boolean success = false;
		
		DirMessage registerMessage = new DirMessage(DirMessageOps.OPERATION_REGISTER_IP);
		registerMessage.setSessionKey(Integer.toString(sessionKey));
		registerMessage.setIP(serverIP);

	    String messageStr = registerMessage.toString();

	    byte[] messageBytes = messageStr.getBytes();

	
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return false;
	    }
	
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REGISTER_IP_OK)) {
	    	
	        success = true;
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REGISTER_IP_FAIL)) {
	        System.err.println("Failed to register the server's IP.");
	        return false;
	    }
		

		return success;
	}
	/**
	 * Método para obtener del directorio la dirección de socket (IP:puerto)
	 * asociada a un determinado nickname.
	 * 
	 * @param nick El nickname del servidor de ficheros por el que se pregunta
	 * @return La dirección de socket del servidor en caso de que haya algún
	 *         servidor dado de alta en el directorio con ese nick, o null en caso
	 *         contrario.
	 */
	public InetSocketAddress lookupServerAddrByUsername(String nick) {
		
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar
		assert (sessionKey != INVALID_SESSION_KEY);
		InetSocketAddress serverAddr = null;
		
		DirMessage registerMessage = new DirMessage(DirMessageOps.OPERATION_REQUEST_ADDRESS);
		registerMessage.setNickname(nick);

	    String messageStr = registerMessage.toString();

	    byte[] messageBytes = messageStr.getBytes();

	
	    byte[] responseBytes = null;
	    responseBytes=sendAndReceiveDatagrams(messageBytes);
	    if (responseBytes == null) {
	        System.err.println("No response from directory server.");
	        return null;
	    }
	
	    String responseStr = new String(responseBytes);
	    DirMessage responseMessage = DirMessage.fromString(responseStr);
		
	    if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REQUEST_ADDRESS_OK)) {
	    	int port=responseMessage.getPort();
	    	String addr=responseMessage.getIP();
	        serverAddr = new InetSocketAddress(addr, port);
	    } else if (responseMessage.getOperation().equals(DirMessageOps.OPERATION_REGISTER_IP_FAIL)) {
	        System.err.println("Failed to obtain the nickname's address.");
	        return null;
	    }


		return serverAddr;
	}

	/**
	 * Método para publicar ficheros que este peer servidor de ficheros están
	 * compartiendo.
	 * 
	 * @param files La lista de ficheros que este peer está sirviendo.
	 * @return Verdadero si el directorio tiene registrado a este peer como servidor
	 *         y acepta la lista de ficheros, falso en caso contrario.
	 */
	public boolean publishLocalFiles(FileInfo[] files) {
		boolean success = false;

		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar



		return success;
	}

	/**
	 * Método para obtener la lista de ficheros que los peers servidores han
	 * publicado al directorio. Para cada fichero se debe obtener un objeto FileInfo
	 * con nombre, tamaño y hash. Opcionalmente, puede incluirse para cada fichero,
	 * su lista de peers servidores que lo están compartiendo.
	 * 
	 * @return Los ficheros publicados al directorio, o null si el directorio no
	 *         pudo satisfacer nuestra solicitud
	 */
	public FileInfo[] getFileList() {
		FileInfo[] filelist = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar



		return filelist;
	}

	/**
	 * Método para obtener la lista de nicknames de los peers servidores que tienen
	 * un fichero identificado por su hash. Opcionalmente, puede aceptar también
	 * buscar por una subcadena del hash, en vez de por el hash completo.
	 * 
	 * @return La lista de nicknames de los servidores que han publicado al
	 *         directorio el fichero indicado. Si no hay ningún servidor, devuelve
	 *         una lista vacía.
	 */
	public String[] getServerNicknamesSharingThisFile(String fileHash) {
		String[] nicklist = null;
		// TODO: Ver TODOs en logIntoDirectory y seguir esquema similar



		return nicklist;
	}

	




}

package es.um.redes.nanoFiles.logic;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.client.NFConnector;
import es.um.redes.nanoFiles.tcp.server.NFServer;
import es.um.redes.nanoFiles.tcp.server.NFServerSimple;




public class NFControllerLogicP2P {
	/*
	 * TODO: Para bgserve, se necesita un atributo NFServer que actuará como
	 * servidor de ficheros en segundo plano de este peer
	 */
	private NFServer bgFileServer =null;



	protected NFControllerLogicP2P() {
		bgFileServer=null;
	}

	/**
	 * Método para arrancar un servidor de ficheros en primer plano.
	 * 
	 */
	protected void foregroundServeFiles() {
		/*
		 * TODO: Crear objeto servidor NFServerSimple y ejecutarlo en primer plano.
		 */
		NFServerSimple serverSimple = null;
		try {
			serverSimple = new NFServerSimple();
		} catch (IOException e) {
			System.err.println("* Error. Unable to start the server.");
			return;
		}
		serverSimple.run();
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */

	}

	/**
	 * Método para ejecutar un servidor de ficheros en segundo plano. Debe arrancar
	 * el servidor en un nuevo hilo creado a tal efecto.
	 * 
	 * @return Verdadero si se ha arrancado en un nuevo hilo con el servidor de
	 *         ficheros, y está a la escucha en un puerto, falso en caso contrario.
	 * 
	 */
	protected boolean backgroundServeFiles() {
		/*
		 * TODO: Comprobar que no existe ya un objeto NFServer previamente creado, en
		 * cuyo caso el servidor ya está en marcha. Si no lo está, crear objeto servidor
		 * NFServer y arrancarlo en segundo plano creando un nuevo hilo. Finalmente,
		 * comprobar que el servidor está escuchando en un puerto válido (>0) e imprimir
		 * mensaje informando sobre el puerto, y devolver verdadero.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */
		if (bgFileServer!=null) {
			System.err.println("* Server already running");
			return false;
		}
		boolean result=false;
		try {
			bgFileServer=new NFServer();
		} catch (IOException e) {
			bgFileServer=null;
			System.err.println("Unable to start the server");
			return false;
		}
		bgFileServer.startServer();
		int listeningPort = bgFileServer.getPort();
		if (listeningPort>0) {
			result=true;
			System.out.println("NFServer running on "+bgFileServer.getAddress());
		}
		else {
			System.err.println("* Error. Unable to start the server.");
			return false;
		}
		return result;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param fserverAddr    La dirección del servidor al que se conectará
	 * @param targetFileHash El hash del fichero a descargar
	 * @param localFileName  El nombre con el que se guardará el fichero descargado
	 */
	protected boolean downloadFileFromSingleServer(InetSocketAddress fserverAddr, String targetFileHash,
			String localFileName) {
		boolean result = false;
		if (fserverAddr == null) {
			System.err.println("* Cannot start download - No server address provided");
			return false;
		}
		/*
		 * TODO: Crear un objeto NFConnector para establecer la conexión con el peer
		 * servidor de ficheros, y usarlo para descargar el fichero mediante su método
		 * "downloadFile". Se debe comprobar previamente si ya existe un fichero con el
		 * mismo nombre en esta máquina, en cuyo caso se informa y no se realiza la
		 * descarga. Si todo va bien, imprimir mensaje informando de que se ha
		 * completado la descarga.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */
		NFConnector nfConnector = null;
		File f = new File(NanoFiles.sharedDirname+"/"+localFileName);
		if (f.exists()) {
			System.out.println("File already exists. ");
			return false;
		}

		try {
			nfConnector = new NFConnector(fserverAddr);
		} catch (UnknownHostException e) {
			nfConnector = null;
			System.err.println();
		} catch (IOException e) {
			nfConnector = null;
		}
		if (nfConnector==null)
		{
			System.err.println("* Error: Unable to connect to the server. ");
			return false;
		}
		try {
			result=nfConnector.downloadFile(targetFileHash, f);
		} catch (IOException e) {
			result=false;
		}
		if (result) {
			System.out.println("The file has been downloaded successfully. ");
		}
		else {
			System.err.println("* Error downloading the file");
		}
		nfConnector.disconnect();
		return result;
	}

	/**
	 * Método para descargar un fichero del peer servidor de ficheros
	 * 
	 * @param serverAddressList La lista de direcciones de los servidores a los que
	 *                          se conectará
	 * @param targetFileHash    Hash completo del fichero a descargar
	 * @param localFileName     Nombre con el que se guardará el fichero descargado
	 */
	public boolean downloadFileFromMultipleServers(LinkedList<InetSocketAddress> serverAddressList,
			String targetFileHash, String localFileName) {
		boolean downloaded = false;

		if (serverAddressList == null) {
			System.err.println("* Cannot start download - No list of server addresses provided");
			return false;
		}
		/*
		 * TODO: Crear un objeto NFConnector para establecer la conexión con cada
		 * servidor de ficheros, y usarlo para descargar un trozo (chunk) del fichero
		 * mediante su método "downloadFileChunk". Se debe comprobar previamente si ya
		 * existe un fichero con el mismo nombre en esta máquina, en cuyo caso se
		 * informa y no se realiza la descarga. Si todo va bien, imprimir mensaje
		 * informando de que se ha completado la descarga.
		 */
		/*
		 * TODO: Las excepciones que puedan lanzarse deben ser capturadas y tratadas en
		 * este método. Si se produce una excepción de entrada/salida (error del que no
		 * es posible recuperarse), se debe informar sin abortar el programa
		 */



		return downloaded;
	}

	/**
	 * Método para obtener el puerto de escucha de nuestro servidor de ficheros en
	 * segundo plano
	 * 
	 * @return El puerto en el que escucha el servidor, o 0 en caso de error.
	 */
	public int getServerPort() {
		int port = 0;
		/*
		 * TODO: Devolver el puerto de escucha de nuestro servidor de ficheros en
		 * segundo plano
		 */
		port=bgFileServer.getPort();


		return port;
	}
	public String getServerIP() {
		return bgFileServer.getIP();
	}

	/**
	 * Método para detener nuestro servidor de ficheros en segundo plano
	 * 
	 */
	public void stopBackgroundFileServer() {
		/*
		 * TODO: Enviar señal para detener nuestro servidor de ficheros en segundo plano
		 */
		bgFileServer.stopServer();
		bgFileServer=null;
	}

}

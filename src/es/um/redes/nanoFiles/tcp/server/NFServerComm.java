package es.um.redes.nanoFiles.tcp.server;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

import es.um.redes.nanoFiles.application.NanoFiles;
import es.um.redes.nanoFiles.tcp.message.PeerMessage;
import es.um.redes.nanoFiles.tcp.message.PeerMessageOps;
import es.um.redes.nanoFiles.util.FileInfo;

public class NFServerComm {

	public static void serveFilesToClient(Socket socket) {			
		
		DataOutputStream dos=null;
		DataInputStream dis=null;
		try {
			/*
			 * TODO: Crear dis/dos a partir del socket
			 */
			dos=new DataOutputStream(socket.getOutputStream());
			dis=new DataInputStream(socket.getInputStream());
			/*
			 * TODO: Mientras el cliente esté conectado, leer mensajes de socket,
			 * convertirlo a un objeto PeerMessage y luego actuar en función del tipo de
			 * mensaje recibido, enviando los correspondientes mensajes de respuesta.
			 */
			PeerMessage msjReceived= PeerMessage.readMessageFromInputStream(dis);
			byte op=msjReceived.getOpcode();
			switch(op) {
				case PeerMessageOps.OPCODE_DOWNLOAD:{
					PeerMessage answer;
					/*
					 * TODO: Para servir un fichero, hay que localizarlo a partir de su hash (o
					 * subcadena) en nuestra base de datos de ficheros compartidos. Los ficheros
					 * compartidos se pueden obtener con NanoFiles.db.getFiles(). El método
					 * FileInfo.lookupHashSubstring es útil para buscar coincidencias de una
					 * subcadena del hash. El método NanoFiles.db.lookupFilePath(targethash)
					 * devuelve la ruta al fichero a partir de su hash completo.
					 */
					FileInfo[] files=FileInfo.lookupHashSubstring(NanoFiles.db.getFiles(), msjReceived.getFileHash());;
					if (files.length>1) {
						System.err.println("Hash is ambiguous, try again");
						answer=new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FAIL);
					}
					else if(files.length==0) {
						System.err.println("Hash does not exist, try again");
						answer=new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FAIL);
					}
					else {
						String path=NanoFiles.db.lookupFilePath(files[0].fileHash);
						File f = new File(path);
						long fLength= f.length();
						byte data[]= new byte[(int) fLength];
						FileInputStream fis=new FileInputStream(f); 
						fis.read(data);
						fis.close();
						
						if (data.length>0 && f!=null ) {
							byte hashLength=(byte) files[0].fileHash.length();
							answer= new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_OK, fLength, data, hashLength ,files[0].fileHash);
							System.out.println("Served file: "+files[0].fileName+" succesfully.");
						}
						else {
							answer=new PeerMessage(PeerMessageOps.OPCODE_DOWNLOAD_FAIL);
							System.out.println("Failed to serve file: "+files[0].fileName+" .");
						}
					}
					answer.writeMessageToOutputStream(dos);
					break;
				}				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

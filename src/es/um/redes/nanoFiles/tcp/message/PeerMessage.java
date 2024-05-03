package es.um.redes.nanoFiles.tcp.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;


public class PeerMessage {


	private byte opcode;

	/*
	 * TODO: Añadir atributos y crear otros constructores específicos para crear
	 * mensajes con otros campos (tipos de datos)
	 * 
	 */
	private byte HashLength =0;
	private String fileHash=null;
	private long fileLength=0;
	private byte[] data=null;


	public PeerMessage() {
		opcode = PeerMessageOps.OPCODE_INVALID_CODE;
	}

	public PeerMessage(byte op) {
		opcode = op;
	}
	public PeerMessage(byte op, byte Length, String hash) {
		opcode = op;
		HashLength=Length;
		fileHash=new String(hash);
	}

	public PeerMessage(byte op, long Length, byte[] data, byte hashLgth, String hash ) {
		opcode=op;
		fileLength=Length;
		this.data=data;
		HashLength=hashLgth;
		fileHash=new String(hash);
	}
	
	/*
	 * TODO: Crear métodos getter y setter para obtener valores de nuevos atributos,
	 * comprobando previamente que dichos atributos han sido establecidos por el
	 * constructor (sanity checks)
	 */
	public byte getOpcode() {
		return opcode;
	}
	public byte getHashLength() {
		return HashLength;
	}

	public void setHashLength(byte hashLength) {
		assert (opcode== PeerMessageOps.OPCODE_DOWNLOAD);
		HashLength = hashLength;
	}

	public String getFileHash() {
		if (fileHash==null) {
			return null;
		}
		return new String(fileHash);
	}

	public void setFileHash(String fileHash) {
		assert (opcode == PeerMessageOps.OPCODE_DOWNLOAD);
		this.fileHash = new String(fileHash);
	}
	
	public long getFileLength() {
		return fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * Método de clase para parsear los campos de un mensaje y construir el objeto
	 * DirMessage que contiene los datos del mensaje recibido
	 * 
	 * @param data El array de bytes recibido
	 * @return Un objeto de esta clase cuyos atributos contienen los datos del
	 *         mensaje recibido.
	 * @throws IOException
	 */
	public static PeerMessage readMessageFromInputStream(DataInputStream dis) throws IOException {
		/*
		 * TODO: En función del tipo de mensaje, leer del socket a través del "dis" el
		 * resto de campos para ir extrayendo con los valores y establecer los atributos
		 * del un objeto DirMessage que contendrá toda la información del mensaje, y que
		 * será devuelto como resultado. NOTA: Usar dis.readFully para leer un array de
		 * bytes, dis.readInt para leer un entero, etc.
		 */
		PeerMessage message = new PeerMessage();
		byte opcode = dis.readByte();
		switch (opcode) {

		case PeerMessageOps.OPCODE_DOWNLOAD:{
			byte hashLength=dis.readByte();
			byte[] hashValue= new byte[hashLength];
			dis.readFully(hashValue);
			message= new PeerMessage(opcode, hashLength, new String(hashValue));
			break;
		}
		
		case PeerMessageOps.OPCODE_DOWNLOAD_OK:{
			long fileLength=dis.readLong();
			byte[] data= new byte[(int)fileLength];
			dis.readFully(data);
			byte hashLength=dis.readByte();
			byte[] hashValue= new byte[hashLength];
			dis.readFully(hashValue);
			message= new PeerMessage(opcode, fileLength,data,hashLength,new String(hashValue));
			break;
		}
		case PeerMessageOps.OPCODE_DOWNLOAD_FAIL:{
			message= new PeerMessage(opcode);
			break;
		}

		default:
			System.err.println("PeerMessage.readMessageFromInputStream doesn't know how to parse this message opcode: "
					+ PeerMessageOps.opcodeToOperation(opcode));
			System.exit(-1);
		}
		return message;
	}

	public void writeMessageToOutputStream(DataOutputStream dos) throws IOException {
		/*
		 * TODO: Escribir los bytes en los que se codifica el mensaje en el socket a
		 * través del "dos", teniendo en cuenta opcode del mensaje del que se trata y
		 * los campos relevantes en cada caso. NOTA: Usar dos.write para leer un array
		 * de bytes, dos.writeInt para escribir un entero, etc.
		 */

		dos.writeByte(opcode);
		switch (opcode) {

		case PeerMessageOps.OPCODE_DOWNLOAD:{
			assert (HashLength>0 && fileHash.length()==HashLength);
			dos.writeByte(HashLength);
			byte[] hashValue= fileHash.getBytes();
			dos.write(hashValue);
			break;
		}
		
		case PeerMessageOps.OPCODE_DOWNLOAD_OK:{
			assert (fileLength>0 && data!=null && HashLength>0 && fileHash.length()==HashLength);
			dos.writeLong(fileLength);
			dos.write(data);
			dos.writeByte(HashLength);
			byte[] hashValue= fileHash.getBytes();
			dos.write(hashValue);
			break;
		}
		case PeerMessageOps.OPCODE_DOWNLOAD_FAIL:{
			break;
		}


		default:
			System.err.println("PeerMessage.writeMessageToOutputStream found unexpected message opcode " + opcode + "("
					+ PeerMessageOps.opcodeToOperation(opcode) + ")");
		}
	}





}

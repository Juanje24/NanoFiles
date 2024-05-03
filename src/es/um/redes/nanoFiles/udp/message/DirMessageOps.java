package es.um.redes.nanoFiles.udp.message;

public class DirMessageOps {

	/*
	 * TODO: Añadir aquí todas las constantes que definen los diferentes tipos de
	 * mensajes del protocolo de comunicación con el directorio.
	 */
	
	public static final String OPERATION_INVALID = "invalid_operation";
	public static final String OPERATION_LOGIN = "login";
	public static final String OPERATION_LOGIN_FAIL = "loginFail";
	public static final String OPERATION_LOGIN_OK = "loginOk";
	public static final String OPERATION_LOGOUT = "logout";
	public static final String OPERATION_LOGOUT_OK = "logoutOk";
	public static final String OPERATION_LOGOUT_FAIL = "logoutFail";
	public static final String OPERATION_USERLIST = "userlist";
	public static final String OPERATION_USERLIST_OK = "userlistOk";
	public static final String OPERATION_USERLIST_FAIL = "userlistFail";
	public static final String OPERATION_REGISTER_SERVER = "registerServer";
	public static final String OPERATION_REGISTER_SERVER_OK= "registerServerOk";
	public static final String OPERATION_REGISTER_SERVER_FAIL= "registerServerFail";
	public static final String OPERATION_UNREGISTER_SERVER = "unregisterServer";
	public static final String OPERATION_UNREGISTER_SERVER_OK= "unregisterServerOk";
	public static final String OPERATION_UNREGISTER_SERVER_FAIL= "unregisterServerFail";
	public static final String OPERATION_REGISTER_IP = "registerIP";
	public static final String OPERATION_REGISTER_IP_OK= "registerIPOk";
	public static final String OPERATION_REGISTER_IP_FAIL= "registerIPFail";
	public static final String OPERATION_REQUEST_ADDRESS = "requestAddress";
	public static final String OPERATION_REQUEST_ADDRESS_OK= "requestAddressOk";
	public static final String OPERATION_REQUEST_ADDRESS_FAIL= "requestAddressFail";

}

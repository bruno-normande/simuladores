package diffserv;

public class Package {
	// id da pessoa que enviou
	private int sender;
	// id do destinatário
	private int rcv;
	// id do protocolo
	private int idProtocol;
	// classe desse package
	private int ds;
	// corpo do pacote
	private String msg; 
	
	public Package(int sender, int rcv) {
		this.sender = sender;
		this.rcv = rcv;
	}

}

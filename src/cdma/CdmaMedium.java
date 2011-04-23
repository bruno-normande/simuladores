package cdma;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

/**
 *	Essa classe representa o meio no qual o servidor e os clientes
 * estarão imersos.
 * Elas será a responsável por "somar" as mensagens dos clientes e entrega-las ao 
 * servidor 
 * @author bruno
 */
public class CdmaMedium extends Thread {
	private final static int PORTA = 5060;
	// trheads dos clientes
	private ArrayList<ClienteThread> clientes;
	// contador de mensagem de cada cliente
	private int[] clientMsgCounter; //conta qnts mensagens cada cliente enviou
	// servidor
	private Server theServer;
	// socket de envio de dados apra o servidor
	private Socket clientSocket;
	// simula um buffer
	private static ArrayList<int[]> myBuffer;
	
	/**
	 * Implementação de singleton
	 */
	private static CdmaMedium theMedium;
	
	/**
	 * Implementação de singleton
	 */
	private CdmaMedium(){
		clientes = new ArrayList<ClienteThread>();
		myBuffer = new ArrayList<int[]>();
	}
	
	/**
	 * Implementação do singleton
	 * @return
	 */
	 public static CdmaMedium getInstance() {
		if(theMedium == null){
			theMedium = new CdmaMedium();
		}
		
		return theMedium;
	}
	 
	 @Override
	public void run() {
//		 Timer timer = new Timer(); // funciona assim?
		 final int FIRST = 0;
		 try {
			DataOutputStream output = new DataOutputStream(  clientSocket.getOutputStream());
			
			while(true){
				Thread.sleep(500);
				// TODO botar para enviar a mensagem
//				if(myBuffer.length() != 0){
//					System.out.println("sending ");
//					output.writeBytes(myBuffer + "\n\n");
//				}
				
				if(myBuffer.size() > 0){
//					System.out.println("Enviando ao servidor: ");
					for(int i = 0; i < myBuffer.get(FIRST).length; i++){
//						System.out.println(myBuffer.get(FIRST)[i] + " ");
						// enviando cada um dos elementos
						output.writeBytes( myBuffer.get(FIRST)[i] + "\n\n"); 
					}
//					System.out.println();
					myBuffer.remove(FIRST);
				}else{
//					System.out.println("não há nada para enviar");
				}
			}
				
				
		 } catch (Exception e1) {
			 e1.printStackTrace();
		 };
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
//	public static void main(String[] args) throws IOException {
//		Server server = new Server(PORTA);
//		Socket clientSocket;
//		Character b;
//		server.start();
//		Timer timer = new Timer();
//		
//		clientSocket = new Socket("localhost", PORTA);
//		
//		DataOutputStream output = new DataOutputStream(  clientSocket.getOutputStream());
//		while(true){
//			try {
//				Thread.sleep(2000);
//				
//				if(myBuffer.length() > 0){
//					System.out.println("sending " + myBuffer );
//					output.writeBytes(myBuffer + "\n\n");
//					
//				}else{
//					System.out.println("Nada a ser enviado.");
//				}
//				
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//
//	}

	
	/**
	 * Store the senders message into the buffer
	 * @param msg
	 */
//	public void sendMessage(ClienteThread sender ,String msg) {
//		myBuffer = msg; // TODO soma a mensagem
//		
//	}

	/** inicia o servidor */
	public void startServer() {
		try {
			theServer = new Server(PORTA);
			theServer.start();
			
			clientSocket = new Socket("localhost", PORTA);
			clientMsgCounter = new int[Server.CLIENT_MAX];
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public void startNewClient(String fileIn) {
		int[] chip;
		String[] fileName = fileIn.split("\\.");
		String extencao = fileName[fileName.length - 1];
		
		if(extencao.equals("txt")){ //binário
			chip = theServer.connectNewBinClient();
		}else{
			chip = theServer.connectNewTextClient();
		}
		
		if(chip != null){
			clientes.add( new ClienteThread(this, fileIn, chip));
		}else{
			System.out.println("Cannot connect more clients!");
		}
		
		
	}

	/**
	 * soma a mensagem chipped às outras do buffer
	 * @param chipedMsg
	 * @param id
	 */
	public void sendMessage(int[] chipedMsg, int id) {
		int counter = clientMsgCounter[id];
		clientMsgCounter[id]++;
		if (counter >= myBuffer.size()) {
			myBuffer.add( new int[Server.CLIENT_MAX]);
		}
		
		for(int i = 0; i < chipedMsg.length; i++){
			myBuffer.get(counter)[i] += chipedMsg[i];
		}
		
	}

}

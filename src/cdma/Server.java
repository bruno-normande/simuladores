package cdma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JTextArea;

import cdma.CdmaMedium;

/**
 * Esse será o servidor ele receberá a conexão e atribuirá um codigo para ela
 * sempre que requisitado.
 * ele receberá mensagens embaralhados por esses vários ccódigos e decodificará usando CDMA
 * @author bruno
 *
 */
public class Server extends Thread {
	//socket que vai ficar escutando as conexões
	private ServerSocket welcomeSocket;
	
	private static final int CHIP_TAM = 4;
	
	public static final int CLIENT_MAX = 4;
	public static final int BYTE_SIZE = 8;
	// num de clientes conectados
	private int clientCount;
	// code chip de cada usuario
	private ArrayList<int[]> codeChips;
	// bits decodificados dos usuarios
	private ArrayList<ArrayList<Integer>> bitsDecoded; 
	
	/**
	 * inicia um server que escuta na porta passada como parametro
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		welcomeSocket = new ServerSocket(port);
		codeChips = new ArrayList<int[]>();
		bitsDecoded = new ArrayList<ArrayList<Integer>>();
		
		clientCount = 0;
		
		for(int i = 0; i < CLIENT_MAX ; i++ ){
			codeChips.add( new int[CHIP_TAM] );
		}
		codeChips.get(0)[0] = 1; codeChips.get(0)[1] = 1; codeChips.get(0)[2] = -1; codeChips.get(0)[3] = -1;
		codeChips.get(1)[0] = -1; codeChips.get(1)[1] = -1; codeChips.get(1)[2] = -1; codeChips.get(1)[3] = -1;
		codeChips.get(2)[0] = 1; codeChips.get(2)[1] = -1; codeChips.get(2)[2] = 1; codeChips.get(2)[3] = -1;
		codeChips.get(3)[0] = -1; codeChips.get(3)[1] = 1; codeChips.get(3)[2] = 1; codeChips.get(3)[3] = -1;
		
	}
	
	/** loop infinito */
	@Override
	public void run() {
		int bitCoded[] = new int[CHIP_TAM];
		int bit;
		
		try {
			System.out.println("iniciou server");
			Socket connectionSocket = welcomeSocket.accept();
			Scanner scanner = new Scanner( new BufferedReader( new InputStreamReader( connectionSocket.getInputStream() ) ) );
			while(true){
				
				// recebe um bit do Medium
				for(int i = 0; i < CHIP_TAM; i++){
					bitCoded[i] = scanner.nextInt();
				}
				for(int i = 0; i < clientCount; i++){
					bit = 0;
					for(int j = 0; j < CHIP_TAM; j++){
						bit += bitCoded[j]*codeChips.get(i)[j];
					}
					bit /= CHIP_TAM;
					bit = bit < 0 ? 0 : 1;
					bitsDecoded.get(i).add(bit);
					if(bitsDecoded.get(i).size() == BYTE_SIZE){
						System.out.print("Cliete " + i + " :");
						for(int j = 0; j < BYTE_SIZE; j++){
							System.out.print( bitsDecoded.get(i).remove(0) );
						}
						System.out.println();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * if the number of clients isn't the max, return a code chip
	 * else, return null
	 * @return
	 */
	public int[] connectNewClient() {
		if(clientCount < CLIENT_MAX){
			int[] chip = codeChips.get(clientCount);
			bitsDecoded.add( new ArrayList<Integer>() );
			clientCount++;
			return chip;
		}else {
			return null;
		}
	}

}

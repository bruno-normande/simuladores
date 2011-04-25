package cdma;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

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
	// diz de um determinado cliente está enviando characteres ou não
	private ArrayList<Boolean> isChar;
	
	/**
	 * inicia um server que escuta na porta passada como parametro
	 * @param port
	 * @throws IOException
	 */
	public Server(int port) throws IOException {
		welcomeSocket = new ServerSocket(port);
		codeChips = new ArrayList<int[]>();
		bitsDecoded = new ArrayList<ArrayList<Integer>>();
		isChar = new ArrayList<Boolean>();
		
		for(int i = 0; i < CLIENT_MAX; i++){
			isChar.add(false);
		}
		
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
				// para cada cliente
				for(int i = 0; i < clientCount; i++){
					//decodifica o bit de acordo com o protocolo CDMA
					bit = 0; 
					for(int j = 0; j < CHIP_TAM; j++){
						bit += bitCoded[j]*codeChips.get(i)[j];
					}
					bit /= CHIP_TAM;
					bit = bit <= 0 ? 0 : 1;
					
					// adiciona ao vetor de bits decodificados desse cliente
					bitsDecoded.get(i).add(bit);
					if(bitsDecoded.get(i).size() == BYTE_SIZE){ // se já decodificou um byte desse cliente
						System.out.print("Cliete " + i + " : ");
						String msgBytes = "";
						for(int j = 0; j < BYTE_SIZE; j++){
							msgBytes += bitsDecoded.get(i).remove(0);
						}
						System.out.print( msgBytes );
						if(!isChar.get(i)){
							System.out.print( " : " + toCharToken(msgBytes) );
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
	 * Cria um token do caracter, contendo seu valor númerico e 
	 * o caracter em sí
	 * @param msgBytes
	 * @return
	 */
	private String toCharToken(String msgBytes) {
		String token = "";
		int charValue = 0;
		
		for(int i = 0; i < msgBytes.length(); i++){
			int bit = (msgBytes.charAt( msgBytes.length() -1 - i ) == '1') ? 1 : 0;
			charValue += bit * Math.pow(2, i);
		}
		
		token = charValue + " : " + Character.toChars(charValue)[0];
		
		return token;
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

	/**
	 * Conecta um novo cliente que enviará código binário
	 * @return code chip
	 */
	public int[] connectNewBinClient() {
		int[] chip = connectNewClient();
		isChar.set(clientCount - 1, false);
		return chip;
	}

	/**
	 * Conecta um novo cliente que enviará characteres
	 * @return code chip
	 */
	public int[] connectNewTextClient() {
		int[] chip = connectNewClient();
		isChar.set(clientCount - 1, true);
		return chip;
	}

}

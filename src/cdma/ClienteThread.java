package cdma;

import java.io.File;
import cdma.CdmaMedium;

/**
 * Essa classe representam as thread que poderão enviar mensagens
 * @author bruno
 *
 */

public class ClienteThread extends Thread {
	// usado para criar o id dos clientes
	private static int clientCounter = 0; 
	// padrão strategy para codificação da entrada
	private IInputStrategy entrada;
	// id desse cliente
	private int myId;
	// code chip CDMA desse cliente
	private int[] chip;

	private CdmaMedium myMedium;
	
	/** Recebe como parametro o meio no qual está inserido
	 * @param meio */
	public ClienteThread(CdmaMedium medium, String fileIn, int[] chip) {
		myId = clientCounter; 
		clientCounter++;
		myMedium = medium;
		this.chip = chip;
		
		File file = new File(fileIn);
		String[] fileName = fileIn.split("\\.");
		String extencao = fileName[fileName.length - 1];
		
		if(extencao.equals("bin")){ //binário
			entrada = new BinInputStragy(file);
		}else if(extencao.equals("txt")){
			entrada = new TextInputStragy(file);
		}
		
		this.run();
	}
	
	/**
	 * Lê do arquivo, codifica e envia ao servidor 
	 */
	@Override
	public void run() {
		String string;
		int bit;

		
		while(entrada.hasNext()){
			string = entrada.getByte(); // usa a estrategia desse cliente para pegar o proximo byte
			
			System.out.print("Cliente "+ myId + " Enviando: "+ entrada.getToken() );
			System.out.println();
			for(int i = 0; i < string.length(); i++){
				bit = Integer.parseInt("" + string.charAt(i));
				sendBit(bit);
			}
		}
	}
	
	
	/** envia um bit para o servidor */
	private void sendBit(int bit){
		bit = (bit != 1) ? -1 : 1; // transforma 0 em -1
		int[] bitChiped = new int[chip.length];
		for(int i = 0; i < chip.length; i++){
			bitChiped[i] = chip[i] * bit;
		}
		
		myMedium.sendMessage(bitChiped, myId);
	}
	
}

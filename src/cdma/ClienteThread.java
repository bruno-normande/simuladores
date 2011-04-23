package cdma;

import java.awt.BufferCapabilities.FlipContents;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import cdma.CdmaMedium;

/**
 * Essa classe representam as thread que poderão enviar mensagens
 * @author bruno
 *
 */

public class ClienteThread extends Thread {
	private static int clientCounter = 0;
	
	private IInputStrategy entrada;
	
	private int myId;
	
	private int[] chip;

	private CdmaMedium myMedium;
	// so vou quebrar as letras em 1 byte, então, nada de unicode xP
	private static final int CHAR_BITS = 8; 
	
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
	
	@Override
	public void run() {
		String string;
		int bit;

		
		while(entrada.hasNext()){
			string = entrada.getByte();
			
			System.out.print("Cliente "+ myId + " Enviando: "+ entrada.getToken() );
			System.out.println();
			for(int i = 0; i < string.length(); i++){
				bit = Integer.parseInt("" + string.charAt(i));
				sendBit(bit);
			}
		}
			
//		System.out.println("Cliente "+ myId + " arquivo de entrada: " + fileBin.getAbsolutePath());
//				if(fileBin.canRead()){ // se o arquivo pode ser lido
//					try {
//						// isso só serve para entrada binária
//						scaner = new Scanner( new BufferedReader( new FileReader(fileBin)));
//						while(scaner.hasNext()){
//							string = scaner.nextLine();
//							
//							System.out.print("Cliente "+ myId + " Enviando: ");
//							for(int i = 0; i < string.length(); i++){
//								bit = Integer.parseInt("" + string.charAt(i));
//								System.out.print( bit );// envia cada um
//								sendBit(bit);
//							}
//							System.out.println();
//						}
//						
//					} catch (FileNotFoundException e) {
//						e.printStackTrace();
//					}
//				}
//			
//			msg = JOptionPane.showInputDialog(null, "Digite sua mensagem:", "Digite sua mensagem", JOptionPane.QUESTION_MESSAGE);
//			sendCDMAMessage(msg);
//			System.out.println("Mensagem enviada -- " + msg);
			
//		}
		
	}
	
	/**
	 * send a cdma codficated message 
	 * @param msg
	 */
	private void sendCDMAMessage(String msg) {
		
		for (int i = 0; i < msg.length(); i++) { // para todos os bytes
			int[][] charChiped = chipChar( msg.charAt(i) );
			
			for(int j = 0; j < CHAR_BITS; j++){ // there is a code to each bit
				myMedium.sendMessage(charChiped[j], myId);
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

	private int[][] chipChar(char charAt) {
		int[] bitChar = new int[CHAR_BITS]; // codificação binária do caracter (com -1 no lugar de 0)
		int charValue = Character.getNumericValue(charAt);
		int[][] charCoeded = new int[CHAR_BITS][chip.length];
		
		for(int i = 0; i < CHAR_BITS; i++){
			bitChar[CHAR_BITS -1 -i] = charValue & (int)Math.pow(2, i);
			if(bitChar[CHAR_BITS -1 -i] != 0){
				bitChar[CHAR_BITS -1 -i] = 1;
			}else{
				bitChar[CHAR_BITS -1 -i] = -1;
			}
		}
		
		for(int i = 0; i < CHAR_BITS; i++){
			for(int j = 0; j < chip.length; j++){
				charCoeded[i][j] = chip[j] * bitChar[i];
			}
		}
		
		return charCoeded;
	}
	
	
}

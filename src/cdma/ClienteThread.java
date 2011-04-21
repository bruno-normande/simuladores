package cdma;

import javax.swing.JOptionPane;

import cdma.CdmaMedium;

/**
 * Essa classe representam as thread que poderão enviar mensagens
 * @author bruno
 *
 */

public class ClienteThread extends Thread {
	private static int clientCounter = 0;
	
	private int myId;
	
	private int[] chip;

	private CdmaMedium myMedium;
	// so vou quebrar as letras em 1 byte, então, nada de unicode xP
	private static final int CHAR_BITS = 8; 
	
	/** Recebe como parametro o meio no qual está inserido
	 * @param meio */
	public ClienteThread(CdmaMedium medium, int[] chip) {
		myId = clientCounter; 
		clientCounter++;
		myMedium = medium;
		this.chip = chip;
		this.run();
	}
	
	@Override
	public void run() {
		String msg;
		
		while(true){
			
			msg = JOptionPane.showInputDialog(null, "Digite sua mensagem:", "Digite sua mensagem", JOptionPane.QUESTION_MESSAGE);
			sendCDMAMessage(msg);
			System.out.println("Mensagem enviada -- " + msg);
			
		}
		
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

package cdma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class TextInputStragy implements IInputStrategy {
	//entrada
	private Scanner myInput;
	//será false qnd não houver mais nada para passar
	private boolean hasNext;
	//ultimo token lido
	private String token;
	// linha
	private String line;
	
	public TextInputStragy(File file) {
		this.hasNext = file.canRead();
		if(hasNext){
			try {
				myInput = new Scanner( new BufferedReader( new FileReader(file)));
				hasNext = myInput.hasNextLine();
				if(hasNext){
					line = myInput.nextLine() + " ";
				}
				
			} catch (FileNotFoundException e) {
				
				hasNext = false;
				System.out.println("Arquivo "+ file.getAbsolutePath() +" não pôde ser lido");
			}
		}
	}

	@Override
	public String getByte() {
		String charInByte;
		char character = line.charAt(0);
		
		if(line.length() > 1){
			line = line.substring(1);
		}else if(myInput.hasNextLine()){
			line = myInput.nextLine() + " ";
		}else{
			hasNext = false;
		}
		
		charInByte = charToByte(character);
		
		token = character + " : " + (int)character + " : " + charInByte;
		
		return charInByte;
	}

	// retorna uma string com a codificação bit by bit do char
	private String charToByte(char c) {
		String bits = "";
		int charValue = (int)c;
		int bit;
		
		for(int i = 0; i < Server.BYTE_SIZE; i++){
			bit = (charValue & (int)Math.pow(2, i));
			bit = (bit != 0) ? 1 : 0;
			bits = bit + bits;
			
		}
		
		return bits;
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public String getToken() {
		return token;
	}

}

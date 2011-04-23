package cdma;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class BinInputStragy implements IInputStrategy {
	//entrada
	private Scanner myInput;
	//será false qnd não houver mais nada para passar
	private boolean hasNext;
	//ultimo token lido
	private String token;

	public BinInputStragy(File file) {
		this.hasNext = file.canRead();
		if(hasNext){
			try {
				myInput = new Scanner( new BufferedReader( new FileReader(file)));
				hasNext = myInput.hasNextLine();
				
			} catch (FileNotFoundException e) {
				
				hasNext = false;
				System.out.println("Arquivo "+ file.getAbsolutePath() +" não pôde ser lido");
			}
		}
	}

	@Override
	public String getByte() {
		String line = myInput.nextLine();
		int tam = line.length();
		
		hasNext = myInput.hasNextLine();
		
		if(tam != Server.BYTE_SIZE){
			if(tam < Server.BYTE_SIZE){ // preenche o resto do byte com '0'
				for(int i = 0; i < Server.BYTE_SIZE - tam; i++){
					line += '0';
				}
			}else{
				// trunca para 1 byte
				line = line.substring(0, Server.BYTE_SIZE);
			}
		}
		token = line;
		return line;
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

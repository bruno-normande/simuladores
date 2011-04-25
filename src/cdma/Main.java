package cdma;

/**
 * Ler README
 * @author bruno
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CdmaMedium medium = CdmaMedium.getInstance();
		
		medium.startServer(); // inicia o servidor
		medium.start();
		
		// cria novos clientes
		medium.startNewClient("./entrada.bin");
		medium.startNewClient("./entrada.txt");
		// para adiconar novos clientes basta seguir o exemplo acima
		
		
	}

}

package cdma;

import javax.swing.JOptionPane;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		CdmaMedium medium = CdmaMedium.getInstance();
		
		medium.startServer(); // inicia o servidor
		medium.start();
		
		
		int opcao;
		do{
			
			opcao = Integer.parseInt(JOptionPane.showInputDialog(null, "Entre com sua opção:\n"
											+ "1 - Adicionar cliente\n"
											+ "0 - Sair", "Digite sua opção", JOptionPane.QUESTION_MESSAGE));
			
			switch (opcao) {
			case 1:
				medium.startNewClient();
				break;

			default:
			}
			
		}while(opcao != 0);
		
	}

}

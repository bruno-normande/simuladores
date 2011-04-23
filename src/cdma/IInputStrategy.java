package cdma;

/** estrategia de leitura do arquivo de entrada */
public interface IInputStrategy {
	/**
	 * @return true if there is still stuff to read, false otherwise
	 */
	boolean hasNext();

	/**
	 * @return next byte in string format
	 */
	String getByte();

	/**
	 * @return last token readen
	 */
	String getToken();

}

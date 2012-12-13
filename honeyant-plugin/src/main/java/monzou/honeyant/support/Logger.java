package monzou.honeyant.support;

/**
 * Logger
 * 
 * @author monzou
 */
public interface Logger {

    /**
     * log information message.
     * 
     * @param format the message format
     * @param args arguments
     */
	void info(String format, Object... args);

	/**
	 * log error message.
	 * 
	 * @param t throwable
	 */
	void error(Throwable t);

}

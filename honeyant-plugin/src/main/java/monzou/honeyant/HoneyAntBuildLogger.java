package monzou.honeyant;

import java.io.IOException;
import java.io.PrintStream;

import monzou.honeyant.support.Constants;
import monzou.honeyant.support.Logger;
import monzou.honeyant.support.LoggerFactory;

import org.apache.tools.ant.DefaultLogger;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

/**
 * HoneyAntBuildLogger
 * 
 * @author monzou
 */
public class HoneyAntBuildLogger extends DefaultLogger {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoneyAntBuildLogger.class);

    private static final String SEPARATOR = System.getProperty("line.separator");

    public HoneyAntBuildLogger() {
        super();
    }

    @Override
    protected void printMessage(String message, PrintStream stream, int priority) {
        super.printMessage(message, stream, priority);
        write(message);
    }

    private void write(String message) {
        IOConsoleOutputStream os = getConsole().newOutputStream();
        try {
            os.write(message);
            os.write(SEPARATOR);
            os.flush();
            getConsole().activate();
        } catch (IOException e) {
            LOGGER.error(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException ignore) {
            }
        }
    }

    private IOConsole getConsole() {
        IConsoleManager consoleManager = getConsoleManager();
        IConsole[] consoles = consoleManager.getConsoles();
        if (consoles != null) {
            for (int i = 0; i < consoles.length; i++) {
                IConsole console = consoles[i];
                if (Constants.PLUGIN_NAME.equals(console.getName())) {
                    return (IOConsole) console;
                }
            }
        }
        IOConsole console = new IOConsole(Constants.PLUGIN_NAME, null);
        consoleManager.addConsoles(new IConsole[] { console, });
        return console;
    }

    private IConsoleManager getConsoleManager() {
        return ConsolePlugin.getDefault().getConsoleManager();
    }
}

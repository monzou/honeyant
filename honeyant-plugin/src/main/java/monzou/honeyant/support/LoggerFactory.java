package monzou.honeyant.support;

import monzou.honeyant.HoneyAnt;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * LoggerFactory
 * 
 * @author monzou
 */
public final class LoggerFactory {

    public static final Logger getLogger(Class<?> clazz) {
        return new LoggerImpl(clazz);
    }

    private static class LoggerImpl implements Logger {

        private final Class<?> clazz;

        LoggerImpl(Class<?> clazz) {
            this.clazz = clazz;
        }

        /** {@inheritDoc} */
        @Override
        public void info(String format, Object... args) {
            HoneyAnt plugin = HoneyAnt.getInstance();
            assert plugin != null;
            String message = String.format("[%s] %s", clazz.getSimpleName(), String.format(format, args));
            plugin.getLog().log(new Status(IStatus.INFO, Constants.PLUGIN_ID, message));
        }

        /** {@inheritDoc} */
        @Override
        public void error(Throwable t) {
            HoneyAnt plugin = HoneyAnt.getInstance();
            assert plugin != null;
            String message = String.format("[%s] %s", clazz.getSimpleName(), t.getMessage());
            plugin.getLog().log(new Status(IStatus.ERROR, Constants.PLUGIN_ID, message, t));
        }

    }

    private LoggerFactory() {
    }

}

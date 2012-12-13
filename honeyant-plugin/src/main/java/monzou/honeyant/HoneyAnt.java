package monzou.honeyant;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * HoneyAnt activator class which controls the plug-in life cycle.
 * 
 * @author monzou
 */
public class HoneyAnt extends AbstractUIPlugin {

    private static HoneyAnt plugin;

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static HoneyAnt getInstance() {
        return plugin;
    }

    /**
     * The constructor
     */
    public HoneyAnt() {
    }

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

}

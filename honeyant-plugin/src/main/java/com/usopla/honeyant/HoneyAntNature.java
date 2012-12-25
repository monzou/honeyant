package com.usopla.honeyant;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.usopla.honeyant.support.Constants;
import com.usopla.honeyant.support.Logger;
import com.usopla.honeyant.support.LoggerFactory;
import com.usopla.honeyant.support.PluginUtils;

/**
 * HoneyAntNature
 * 
 * @author monzou
 */
public class HoneyAntNature implements IProjectNature {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoneyAntNature.class);

    private IProject project;

    /** {@inheritDoc} */
    @Override
    public void configure() throws CoreException {
        LOGGER.info("configure ant nature for: %s", project.getName());
        PluginUtils.addBuilders(getProject(), Constants.BUILDER_ID);
    }

    /** {@inheritDoc} */
    @Override
    public void deconfigure() throws CoreException {
        PluginUtils.removeBuilders(getProject(), Constants.BUILDER_ID);
        LOGGER.info("deconfigure ant nature for: %s", project.getName());
    }

    /** {@inheritDoc} */
    @Override
    public IProject getProject() {
        return project;
    }

    /** {@inheritDoc} */
    @Override
    public void setProject(IProject project) {
        this.project = project;
    }

}

package com.usopla.honeyant.preferences.view;


import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.usopla.honeyant.preferences.model.HoneyAntPreferences;
import com.usopla.honeyant.support.Constants;
import com.usopla.honeyant.support.Logger;
import com.usopla.honeyant.support.LoggerFactory;
import com.usopla.honeyant.support.PluginUtils;
import com.usopla.honeyant.support.PreferenceModelFactory;

/**
 * HoneyAntPropertyPage
 * 
 * @author monzou
 */
public class HoneyAntPropertyPage extends PropertyPage {

    private static final Logger LOGGER = LoggerFactory.getLogger(HoneyAntPropertyPage.class);

    private Button pluginEnabled;

    private Text buildFile;

    private Text buildTarget;

    private Text buildTriggerAnnotation;

    private Text cacheFileDir;

    private Button incrementalBuildEnabled;

    private Button cacheEnabled;

    @Override
    protected Control createContents(Composite parent) {

        parent.setLayout(createLayout());

        pluginEnabled = new Button(parent, SWT.CHECK | SWT.LEFT);
        pluginEnabled.setText(String.format("Enable %s plugin", Constants.PLUGIN_NAME));
        pluginEnabled.addSelectionListener(new SelectionAdapter() {

            /** {@inheritDoc} */
            @Override
            public void widgetSelected(SelectionEvent e) {
                availableChanged();
            }

        });

        Composite config = new Composite(parent, SWT.NONE);
        config.setLayout(createContentsLayout());
        config.setLayoutData(createLayoutData());

        buildFile = addText(config, "Build file (XML)");
        buildTarget = addText(config, "Build target");
        buildTriggerAnnotation = addText(config, "Build trigger annotation");
        cacheFileDir = addText(config, "Cache directory");
        cacheEnabled = new Button(config, SWT.CHECK | SWT.LEFT);
        cacheEnabled.setText("Enable cache");
        incrementalBuildEnabled = new Button(config, SWT.CHECK | SWT.LEFT);
        incrementalBuildEnabled.setText("Enable incremental build");

        restore();
        updateState();
        noDefaultAndApplyButton();
        return parent;

    }

    private Layout createLayout() {
        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        return layout;
    }

    private Layout createContentsLayout() {
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        return layout;
    }

    private GridData createLayoutData() {
        GridData layoutData = new GridData();
        layoutData.verticalAlignment = GridData.FILL;
        layoutData.grabExcessVerticalSpace = true;
        layoutData.grabExcessHorizontalSpace = true;
        return layoutData;
    }

    private void availableChanged() {
        boolean available = pluginEnabled.getSelection();
        if (available) {
            initialize();
        } else {
            clear();
        }
        updateState();
    }

    private void updateState() {
        boolean available = pluginEnabled.getSelection();
        buildFile.setEnabled(available);
        buildTarget.setEnabled(available);
        buildTriggerAnnotation.setEnabled(available);
        cacheFileDir.setEnabled(available);
        cacheEnabled.setEnabled(available);
        incrementalBuildEnabled.setEnabled(available);
    }

    private void initialize() {
        buildFile.setText(String.format("%s.xml", Constants.PLUGIN_CODE));
        buildTarget.setText(Constants.PLUGIN_CODE);
        buildTriggerAnnotation.setText(Constants.PLUGIN_NAME);
        cacheFileDir.setText(String.format(".%s", Constants.PLUGIN_CODE));
        cacheEnabled.setSelection(true);
        incrementalBuildEnabled.setSelection(true);
    }

    private void clear() {
        buildFile.setText("");
        buildTarget.setText("");
        buildTriggerAnnotation.setText("");
        cacheFileDir.setText("");
        cacheEnabled.setSelection(false);
        incrementalBuildEnabled.setSelection(false);
    }

    private void restore() {
        HoneyAntPreferences preferences = getPreferences();
        boolean b = preferences.isPluginEnabled();
        pluginEnabled.setSelection(b);
        buildFile.setText(b ? preferences.getBuildFileName() : "");
        buildTarget.setText(b ? preferences.getBuildTarget() : "");
        buildTriggerAnnotation.setText(b ? preferences.getBuildTriggerAnnotation() : "");
        cacheFileDir.setText(b ? preferences.getCacheFileDir() : "");
        cacheEnabled.setSelection(b ? preferences.isCacheEnabled() : false);
        incrementalBuildEnabled.setSelection(b ? preferences.isIncrementalBuildEnabled() : false);
    }

    private void store() {
        HoneyAntPreferences preferences = getPreferences();
        boolean b = pluginEnabled.getSelection();
        preferences.setPluginEnabled(b);
        preferences.setBuildFileName(b ? buildFile.getText() : null);
        preferences.setBuildTarget(b ? buildTarget.getText() : null);
        preferences.setBuildTriggerAnnotation(b ? buildTriggerAnnotation.getText() : null);
        preferences.setCacheFileDir(b ? cacheFileDir.getText() : null);
        preferences.setCacheEnabled(b ? cacheEnabled.getSelection() : false);
        preferences.setIncrementalBuildEnabled(b ? incrementalBuildEnabled.getSelection() : false);
    }

    private Text addText(Composite parent, String propertyName) {
        Label label = new Label(parent, SWT.RIGHT);
        label.setText(propertyName);
        GridData ld = new GridData();
        ld.grabExcessHorizontalSpace = true;
        ld.minimumWidth = 150;
        label.setLayoutData(ld);
        Text text = new Text(parent, SWT.FILL | SWT.SINGLE | SWT.BORDER);
        GridData td = new GridData();
        td.horizontalAlignment = GridData.FILL;
        td.grabExcessHorizontalSpace = true;
        td.minimumWidth = 400;
        text.setLayoutData(td);
        return text;
    }

    /** {@inheritDoc} */
    @Override
    public boolean performOk() {
        IProject project = getProject();
        String id = Constants.NATURE_ID;
        try {
            if (pluginEnabled.getSelection()) {
                PluginUtils.addNature(project, id);
            } else {
                PluginUtils.removeNature(project, id);
            }
        } catch (CoreException e) {
            LOGGER.error(e);
        }
        store();
        return super.performOk();
    }

    private IProject getProject() {
        return ((IJavaProject) getElement()).getProject();
    }

    private HoneyAntPreferences getPreferences() {
        return PreferenceModelFactory.create(HoneyAntPreferences.class, getProject());
    }

}

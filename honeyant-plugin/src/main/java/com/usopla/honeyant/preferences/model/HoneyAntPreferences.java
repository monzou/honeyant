package com.usopla.honeyant.preferences.model;

/**
 * HoneyAntPreferences
 * 
 * @author monzou
 */
public interface HoneyAntPreferences {

    boolean isPluginEnabled();

    void setPluginEnabled(boolean enabled);

    boolean isCacheEnabled();

    void setCacheEnabled(boolean enabled);

    boolean isIncrementalBuildEnabled();

    void setIncrementalBuildEnabled(boolean enabled);

    String getCacheFileDir();

    void setCacheFileDir(String dir);

    String getBuildFileName();

    void setBuildFileName(String fileName);

    String getBuildTarget();

    void setBuildTarget(String target);

    String getBuildTriggerAnnotation();

    void setBuildTriggerAnnotation(String annotation);

}

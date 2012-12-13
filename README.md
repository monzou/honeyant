# HoneyAnt

HoneyAnt is an incremental [Ant](http://ant.apache.org/) build plugin for [Eclipse](www.eclipse.org/). 


## Features

When you save a Java file, HoneyAnt runs its build script automatically.

This plugin provides similar functions to APT (Annotation Processing Tool), but HoneyAnt is more powerful and freely !
You can run **any** Ant build script when you change the Java file. File generation, testing, etc, etc ...

If it is an incremental build (after file editing) and cache enabled, HoneyAnt runs its build script only when the source file is changed.
However, if it is full build (project cleaning), HoneyAnt always runs its build script.  

## Install

Currently HoneyAnt does not provide an update-site. Please install plugin from cloned git repository.

1. git clone https://github.com/monzou/honeyant.git
2. [ Help ] - [ Install New Software ] - [ Add ] - [ Local ] - [ {CLONE_REPO}/honeyant-feature-updatesite ]
3. Restart Eclipse.
3. Select your Java Project and configure HoneyAnt page.

## Example

See [Example](https://github.com/monzou/honeyant/tree/master/honeyant-example).

When you change the [Person.java](https://github.com/monzou/honeyant/blob/master/honeyant-example/src/main/java/monzou/honeyant/example/source/Person.java), HoneyAnt runs [DumpTask](https://github.com/monzou/honeyant/blob/master/honeyant-example/src/main/java/monzou/honeyant/example/task/DumpTask.java) automatically.
Dump file will be created at [dest](https://github.com/monzou/honeyant/tree/master/honeyant-example/dest).
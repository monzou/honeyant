# HoneyAnt

HoneyAnt is an incremental [Ant](http://ant.apache.org/) build plugin for [Eclipse](www.eclipse.org/). 


## Features

When you save a Java file or build Java project, HoneyAnt runs its build script automatically.
If cache is enabled, HoneyAnt runs its build script only when the source file is changed.

This plugin provides similar functions to APT (Annotation Processing Tool), but HoneyAnt is more powerful and freely !
You can run **any** Ant build script when you change the annotated Java file. File generation, testing, etc, etc ...


## Install

Currently HoneyAnt does not provide an update-site. Please install plugin from cloned git repository.

1. git clone https://github.com/monzou/honeyant.git
2. Run Eclipse
3. Help - Install New Software - Add - Local - CLONED_REPO/honeyant-feature-updatesite
4. Restart Eclipse
5. Select your Java Project and configure HoneyAnt page


## Configuration

* Build file : the Ant build file.
* Build target : the Ant build target.
* Build trigger annotation : the HoneyAnt build trigger annotation (simple name). 
* Cache directory : the directory which HoneyAnt puts its build result caches (file digest).
* Enable cache : set true if you want to avoid unnecessary build.
* Enable incremental build : set true if you want to build incrementally.


## Ant Task Environments

HoneyAnt runs its build script with following properties.

* clazz : The full qualified class name of triggered Java file.
* path : The absolute file path of triggered Java file.


## Example

See [Example](https://github.com/monzou/honeyant/tree/master/honeyant-example).

When you change the [Person.java](https://github.com/monzou/honeyant/blob/master/honeyant-example/src/main/java/monzou/honeyant/example/source/Person.java), HoneyAnt runs [DumpTask](https://github.com/monzou/honeyant/blob/master/honeyant-example/src/main/java/monzou/honeyant/example/task/DumpTask.java) automatically.
Dump file will be created at [dest](https://github.com/monzou/honeyant/tree/master/honeyant-example/dest).
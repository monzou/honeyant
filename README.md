# HoneyAnt

HoneyAnt is an incremental [Ant](http://ant.apache.org/) build plugin for [Eclipse](www.eclipse.org/). 


## Features

When you save a Java file, HoneyAnt runs its build script automatically.

This plugin provides similar functions to APT (Annotation Processing Tool), but HoneyAnt is more powerful and freely !
You can run **any** Ant build script when you change the Java file. File generation, testing, etc, etc ...

If it is an incremental build (after file editing) and cache enabled, HoneyAnt runs its build script only when the source file is changed.
However, if it is full build (project cleaning), HoneyAnt always runs its build script.  

## Configuration

Select your Java Project and configure HoneyAnt page.

## Example

See [Example](https://github.com/monzou/honeyant/tree/master/honeyant-example).
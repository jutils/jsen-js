JSEN - JavaScript Engine
======

Implementation of JavaScript engine for Java which makes easier to execute scripts in Java code. Interpreting of JavaScript is assured by [Rhino](https://developer.mozilla.org/en-US/docs/Rhino). Library provides especially simple and customizable way of exporting Java objects to JavaScript. Exporting is assured automatically or using special Java annotations. Interface that is used for executing scripts is familiar to JSR 223.

This library is fork of [ScriptBox](https://github.com/ITman1/ScriptBox).

## Building library

### Requirements

1. Have installed JDK 1.6 or newer - JDK 1.8 is recommended
2. Have installed [Maven build manager](http://maven.apache.org/download.cgi#Installation_Instructions)
3. Have set system variable `JAVA_HOME` to directory with installed JDK and have its binary directory
  in the system variable `PATH` - e.g. on Windows add to `PATH` variable `%JAVA_HOME%\bin` (more [here](http://maven.apache.org/download.cgi))
4. Have in the system variable `PATH` the directory with Maven installation

### Building

Simply run command: `mvn package`

## Using the library

### Using Maven

(will be completed soon)

### Custom way

Have built library (see previous section) and have it specified on classpath

### Example of using library

````
public class GlobalObject {

	@ScriptGetter(field="state")
	public ITransformationState getState() {}
		
	@ScriptFunction(name="write")
	public void print(Object obj) throws TransformationException {}
}

Generator generator = new Generator(transformationState);
		
AbstractScriptEngine engine = ScriptEngineManager.getInstance().getBrowserScriptEngine("text/javascript", 
	new GlobalObjectScriptSettings<Generator>(generator)
);

engine.eval(script);
````

For more examples see:
- Test of Java script annotations [here](https://github.com/ITman1/ScriptBox/blob/master/src/test/java/tests/script/engine/JavaScriptAnnotationsTests.java)
- Package `com.jsen.script.annotation`
- Another classes like `Window`, `Location`, etc. defined in [ScriptBox](https://github.com/ITman1/ScriptBox) project

# Known issues

Project is still in phase of development and targets the experimental frame
of new HTML 5.1 specification which has not been released yet, so bugs may 
occur in the current implementation or also in specification itself.

If you run into any bug, please report on:  
   https://github.com/ITman1/jsen/issues

## Issue list:

1. Thrown error during `javac` build: `error: annotation XYZ is missing value for the attribute <clinit>`  
      - This error may occur if you are running Sun JDK compiler  
      - It is known bug: 
          http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6857918
      - **Solution:** use JDK 8 or use different compiler than `javac` eg. Edifact Java Compiler (EJC)

## Contact and credits
                             
**Author:**    Radim Loskot  
**gmail.com:** radim.loskot (e-mail)

Please feel free to contact me if you have any questions.

README
======
Project instructions for developers below.

Maven (2.2.1):
--------------
- 'mvn clean install' to build
- 'mvn install -DskipTests=true' to skip tests
- 'mvn javadoc:aggregate' to just run javadoc (see target/site/apidocs/index.html)
- 'mvn site' to generate site docs (see target/site/index.html)

Eclipse:
--------
- Window -> Preferences -> Java -> Build Path -> Classpath Variables -> [New...]
	Name: M2_REPO
	Path: /home/<you>/.m2/repository (<you> == jdoe, for example)
- File -> Import... -> General -> Existing Projects into Workspace -> [Next >] ->
	[Browse...] ->/path/to/jboss-esb -> [OK] -> [Finish]


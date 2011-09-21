rem ### -*- batch file -*- ######################################################
rem #                                                                          ##
rem #  JBoss Bootstrap Script Configuration                                    ##
rem #                                                                          ##
rem #############################################################################

rem # $Id: run.conf.bat 88820 2009-05-13 15:25:44Z dimitris@jboss.org $

rem #
rem # This batch file is executed by run.bat to initialize the environment 
rem # variables that run.bat uses. It is recommended to use this file to
rem # configure these variables, rather than modifying run.bat itself. 
rem #

if not "x%JAVA_OPTS%" == "x" goto JAVA_OPTS_SET

rem # JVM memory allocation pool parameters - modify as appropriate.
set "JAVA_OPTS=-Xms64M -Xmx123m -XX:MaxPermSize=85m"

rem # Reduce the RMI GCs to once per hour for Sun JVMs.
set "JAVA_OPTS=%JAVA_OPTS% -Dsun.rmi.dgc.client.gcInterval=3600000 -Dsun.rmi.dgc.server.gcInterval=3600000"

rem # Warn when resolving remote XML DTDs or schemas.
set "JAVA_OPTS=%JAVA_OPTS% -Dorg.jboss.resolver.warning=true"

rem # Make Byteman classes visible in all module loaders
rem # This is necessary to inject Byteman rules into AS7 deployments
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.modules.system.pkgs=org.jboss.byteman"
:JAVA_OPTS_SET

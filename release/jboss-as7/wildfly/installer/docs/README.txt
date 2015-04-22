SwitchYard WildFly Standalone Installer
=========================================

Installing Runtime
------------------
This package contains resources for installing SwitchYard into a 
JBoss Enterprise Application Platform installation.

Prerequisites:
    1.  Java Runtime
    2.  Fully installed Ant available on the command line execution PATH.
    3.  A WildFly installation.

Instructions:
    1.  Open a terminal command prompt.
    2.  Change directory into the root of the bundle.
    3.  Execute command "ant".

This script will ask you for the path to the WildFly distribution and will 
install all the necessary files required to run SwitchYard applications on 
WildFly including the set of quickstart example applications.

Installing BPEL Console - Coming soon
-----------------------
Instructions:
    1. Execute command "ant install-bpel-console"

The script will ask for the location of your WildFly install where the console server and webapp will be deployed.


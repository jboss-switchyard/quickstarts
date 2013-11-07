SwitchYard JBoss EAP Standalone Installer
=========================================

Installing Runtime
------------------
This package contains resources for installing SwitchYard into a 
JBoss Enterprise Application Platform installation.

Prerequisites:
    1.  Java Runtime
    2.  Fully installed Ant available on the command line execution PATH.
    3.  A JBoss EAP installation.

Instructions:
    1.  Open a terminal command prompt.
    2.  Change directory into the root of the bundle.
    3.  Execute command "ant".

This script will ask you for the path to the JBoss EAP distribution and will 
install all the necessary files required to run SwitchYard applications on 
JBoss EAP including the set of quickstart example applications.

Installing BPEL Console
-----------------------
Instructions:
    1. Execute command "ant install-bpel-console"

The script will ask for the location of your EAP install where the console server and webapp will be deployed.

Installing Forge Tooling
------------------------
Prerequisites:
    1. Fully installed Forge 1.3.2 Final or above
    2. $FORGE_HOME environment variable set to above path.

Instructions:
    1. Execute command "ant install-forge"

The installer will copy the necessary SwitchYard forge plugins directly into your ~/.forge directory.

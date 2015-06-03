# SwitchYard Release Distributions

This repository houses the build scripts for the SwitchYard Release Distributions.  To build, just
clone this repository and run "mvn clean package" in the clone root directory, or in the root
folder of the base JBoss Application Server version on which you would like to run SwitchYard.

These scripts create JBoss Application server zip files with SwitchYard pre-installed and ready-to-go.
The zip files are also available (pre-built) in the
[JBoss Nexus Maven Repository](https://repository.jboss.org/nexus/content/groups/public/)
(groupId [org.switchyard](https://repository.jboss.org/nexus/content/groups/public/org/switchyard/),
artifactIds [switchyard-release-as6](https://repository.jboss.org/nexus/content/groups/public/org/switchyard/switchyard-release-as6)
and [switchyard-release-as7](https://repository.jboss.org/nexus/content/groups/public/org/switchyard/switchyard-release-as7)),
which means you can skip the build step and go straight to running SwitchYard.

The assembly definition for both release distributions assumes that the quickstarts repository has been checked out and exists in the same directory as your release checkout (i.e. quickstarts and release share the same parent directory).  The build will still work if this directory is not present, but the quickstarts will not be included in the built distributions.


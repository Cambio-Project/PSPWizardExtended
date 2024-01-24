# FSE-2011-PSPWizard

This repository contains information related to the tool PSPWizard: Machine-assisted Definition of Temporal Logical Properties with Specification Patterns

The tool was originally presented in this [Paper](http://dl.acm.org/citation.cfm?doid=2025113.2025193) at Foundations of Software Engineering, 2011.

This repository <b><i>is NOT</i></b> the original repository for this tool.
* Here is the link to the [Original Project Page](http://www.ict.swin.edu.au/personal/mlumpe/PSPWizard/index.html)<br>
* The Binary & Source Code can be Downloaded from the [Download Page](http://www.ict.swin.edu.au/personal/mlumpe/PSPWizard/download.html)

In this repository, for PSPWizard you will find:
* :white_check_mark: [Source code](https://github.com/SoftwareEngineeringToolDemos/FSE-2011-PSPWizard/tree/master/PSPWizard%20Src)
PSPWizard has been developed in netbeans 8 using Java 7. The source archive contains a complete netbeans project. To build it, just import it into your current netbeans workspace.
* :white_check_mark: [Binary Distribution](https://github.com/SoftwareEngineeringToolDemos/FSE-2011-PSPWizard/tree/master/PSPWizard%20Bin/bin) The binary distribution comprises of PSPWizard's Java jar-archives and system-specific application scripts to run PSPWizard as a stand-alone application. To start PSPWizard, invoke PSPWizard (Mac) or PSPWizard.bat (Windows) in the bin directory without any arguments. PSPWizard requires the Java 7 execution environment.
* :white_check_mark: [Virtual machine containing tool](http://go.ncsu.edu/SE-tool-VMs)
The Virtual Machine Disk image can be downloaded from the above link and directly imported into Virtual Box to start the Virtual Machine which contains the tool PSPWizard.
* :white_check_mark: [Virtual Machine containing Tool using Vagrant] (https://github.com/SoftwareEngineeringToolDemos/FSE-2011-PSPWizard/tree/master/build_vm)
This allows you to create a Virtual Machine using Vagrant & Virtual box which contains the tool PSPWizard.

This repository was constructed by [Manav Verma](https://github.com/mverma4) under the supervision of [Dr. Emerson Murphy-Hill](https://github.com/CaptainEmerson). Thanks to Dr.Markus Lumpe, Dr.Indika Meedeniya and Dr.Lars Grunske for their help in establishing this repository.

___
## Usage of the Rest-API

### Additional Requirements: 
- min. Java 17

### Usage:

#### Manual Install
1. Install the main PSPWizard Maven Project
2. Install the restAPI Module Maven Project (needs to be second because of dependencies)
3. Start the application with the .jar in ``/restAPI/target/restAPI-0.0.1-SNAPSHOT.jar``

#### Install via Docker

You can either use the provided `Dockerfile` to create build the image locally or use the included ``docker-compose.yml`` which uses the lastest image from the GitHub Container Registry

#### Docker compose
You can use the provided docker compose to download an image directly from the GitHub Container Registry.

Information to the parameters used in the ``docker-compose.yml``:
- ``image: ghcr.io/cambio-project/pspwizardextended:1.0.0``
  - The image parameter is used to determin the image you want to download. The preset is ``ghcr.io/cambio-project/pspwizardextended:1.0.0 `` but you can change it to the `ghcr.io/cambio-project/pspwizardextended:latest` tag to get the newest version.
- ``container_name: pspwizard``
  - This parameter is used to rename the container used by Docker.
- ``ports: 8081:8080``
  - The Ports parameter determins the host and container port in the following scheme: "host:container". You should not change the container port, but feel free to change the host port.
- ``pull_policy: always``
  - This parameter describes that docker tries to always pull the newest image available. Can be removed when you use a dedicated version.
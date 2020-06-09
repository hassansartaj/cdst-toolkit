# CDST Toolkit
The CDST toolkit automates the testing of Cockpit Display Systems (CDS). The toolkit consists of seven modules, (i) *CDS Model Generator*, (ii) *Comparator*, (iii)
*Reporting Module*, (iv) *CDS Constraint Specifier*, (v) *Test Case (Path) & Script Generator*, (vi) *Test Execution Module*, and (vii) *Cockpit Display Recorder*. The toolkit is aimed to assist the avionics engineers in the process of testing CDS i.e., test generation, execution, and evaluation.

## Architecture
The seven modules of CDST toolkit are distributed among four projects. 

**(1) CDST Core:-** 
The main purpose of the Core module is to prepare the test evaluation environment, perform test evaluation, and report results. This module contains three sub-modules 
(i) CDS Model Generator, (ii) Comparator, and (iii) Reporting Module.

* *CDS Model Generator* generates the CDS model using the XML file of the CDS under test, CDS profile, and the mapping file. 
* *Comparator* is used to prepare the test evaluation environment and perform test evaluation. 
* *Reporting Module* generates a test report at the end of the test evaluation. 


**(2) CDS Constraint Specifier:-** 
The tool presents interactive UI to write OCL constraints used for test evaluation. The tool is developed specifically to assist the CDS test engineers.

**(3) Test Case (Path) & Script Generator:-** 
The tool takes a UML state machine (aircraft behavioral model), flattens it, and applies N+ test strategy, and generates transition tree. The transition tree containing flight test paths is used to generate JSBSim test scripts. 
This module contains sub-module '*Test Execution Module*'. 
* The function of '*Test Execution Module*' is to take all the generated test scripts as input and execute each script individually to run the simulation.

**(4) Cockpit Display Recorder:-** 
The tool supports two methods to record cockpit display, one is to use an external camera to record the cockpit display during hardware-in-the-loop simulation, and the second way is to record the screenshots of the computer screen on which the (software-in-the-loop) simulation is running. Moreover, the tool allows to record images after a specified time interval and according to aircraft flight states.

# Research Papers for Citation
References:
```
1. CDS Testing Approach Paper
_____________________________
Iqbal, Muhammad Zohaib, Hassan Sartaj, Muhammad Uzair Khan, Fitash Ul Haq, and Ifrah Qaisar. "A Model-
Based Testing Approach for Cockpit Display Systems of Avionics." In 2019 ACM/IEEE 22nd International 
Conference on Model Driven Engineering Languages and Systems (MODELS), pp. 67-77. IEEE, 2019.
```
Download [PDF](https://www.researchgate.net/publication/337510742_A_Model-Based_Testing_Approach_for_Cockpit_Display_Systems_of_Avionics)
```
2. CDST Toolkit Paper
_____________________
Sartaj, Hassan, Muhammad Zohaib Iqbal, and Muhammad Uzair Khan. "CDST: A Toolkit for Testing 
Cockpit Display Systems of Avionics." arXiv preprint arXiv:2001.07869 (2020) (Accepted at ICST2020). 
```

Download [PDF](https://www.researchgate.net/publication/338762667_CDST_A_Toolkit_for_Testing_Cockpit_Display_Systems_of_Avionics)

Bibtex:
```
1. CDS Testing Approach Paper
_____________________________
@inproceedings{iqbal2019model,
  title={A Model-Based Testing Approach for Cockpit Display Systems of Avionics},
  author={Iqbal, Muhammad Zohaib and Sartaj, Hassan and Khan, Muhammad Uzair and Haq, Fitash Ul and Qaisar, Ifrah},
  booktitle={2019 ACM/IEEE 22nd International Conference on Model Driven Engineering Languages and Systems (MODELS)},
  pages={67--77},
  year={2019},
  organization={IEEE}
}

2. CDST Toolkit Paper
_____________________
@article{sartaj2020cdst,
  title={CDST: A Toolkit for Testing Cockpit Display Systems of Avionics},
  author={Sartaj, Hassan and Iqbal, Muhammad Zohaib and Khan, Muhammad Uzair},
  journal={arXiv preprint arXiv:2001.07869},
  year={2020}
  note = {(Accepted at ICST2020)}
}
```

# Getting CDST

The source code of the complete toolkit can be obtained from the github repository:
```
git clone https://github.com/hassansartaj/cdst-toolkit.git
```

# Getting Started

## Basic Requirements

* Machine: minimum 4GB RAM and 4-core processor

* OS: Windows 8/10 and MacOS

* Java: JDK 1.8 or higher

* IDE: Eclipse/Netbeans/IntelliJ with Maven addons

## Using CDST Toolkit

* Import all modules or a single module in Eclipse IDE
* To use the 'Core' module 
   * `cdst.core` requires the dependent project `cdst.textocr`
   * First update the `cdst.textocr` Gradle project
   * For `cdst.core`, first update the project and then run maven clean from Eclipse or using the command: `mvn clean`
   * For the example test evaluation, run 'CDSDriver' as Java application
* To use the 'CDS Constraints Specifier' module
   * First update `cdst.oclgenerator` project and then run maven clean from Eclipse or using the command: `mvn clean`
   * Run 'OCLGenMain' as Java application
* To use the 'Test Case (Path) & Script Generator' module
   * First update `cdst.testgenerator` project and then run maven clean from Eclipse or using the command: `mvn clean`
   * For the example test generation, run 'MainDriver' as Java application
   * For the example test script generation, run 'JSBSimScriptGenerator' as Java application
   * For the example test script  generation, run 'JSBSimScriptExecutor' as Java application
* To use the 'Cockpit Display Recorder' module
   * First update `cdst.cdrecorder` project and then run maven clean from Eclipse or using the command: `mvn clean`
   * Run 'RecorderMain' as Java application

## Building CDST Toolkit
* Building the 'Core' module 
   * `cdst.core` requires the dependent project `cdst.textocr`
   * First update the `cdst.textocr` Gradle project and then run gradle tasks using Eclipse or commands: `gradle test`, `gradle build`, `gradle clean`, `gradle tasks`
   * For `cdst.core`, run maven clean and install from Eclipse or using the command: `mvn clean install`
* Building the 'CDS Constraints Specifier' module
   * For `cdst.oclgenerator`, project run maven clean and install from Eclipse or using the command: `mvn clean install`
* Building the 'Test Case (Path) & Script Generator' module
   * For `cdst.testgenerator`, project run maven clean and install from Eclipse or using the command: `mvn clean install`
* Building the 'Cockpit Display Recorder' module
   * For `cdst.cdrecorder` project, run maven clean and install from Eclipse or using the command: `mvn clean install`

## Dataset
* `pfd-ds-icst`: This dataset contains simulation data (images) recorded for 275 minutes (used for ICST paper)
* `pfd-ds`: This dataset contains simulation data (images) for the PFD case study (used for MODELS paper)
* `pfd-sim-ds`: This dataset contains simulator raw data for the PFD experimental simulation
* `gcs-cds-ds`: This dataset contains simulation data (images) for the GCS-CDS case study
* `gcs-cds-sim-ds`: This dataset contains simulator raw data for the GCS-CDS experimental simulation

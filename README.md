# CDST Toolkit
The CDST toolkit automates the testing of Cockpit Display Systems (CDS). The toolkit comprises seven modules, (i) *CDS Model Generator*, (ii) *CDS Comparator*, (iii)
*Reporting Module*, (iv) *CDS Constraint Specifier*, (v) *Test Path & Script Generator*, (vi) *Test Execution Module*, and (vii) *Cockpit Display Recorder*. The toolkit is aimed to assist the avionics engineers in the process of testing CDS i.e., test generation, execution, and evaluation.
Further details can be found in the relevant research papers. 

# Architecture Diagram of CDST Toolkit
![Tookit Architecture](imgs/cdst-arch.png?raw=true "Tookit Architecture")

# Related Research Papers
References:
```
1. CDS Testing Approach Paper - Journal
_______________________________________
Sartaj, Hassan, Muhammad Zohaib Iqbal, and Muhammad Uzair Khan (2021). "Testing cockpit display systems of 
aircraft using a model-based approach." Software and Systems Modeling 20(6):1977–200.
DOI: https://doi.org/10.1007/s10270-020-00844-z

```
Download [PDF](https://www.researchgate.net/publication/348208118_Testing_cockpit_display_systems_of_aircraft_using_a_model-based_approach)
```

2. CDS Testing Approach Paper - Conference
__________________________________________
Iqbal, Muhammad Zohaib, Hassan Sartaj, Muhammad Uzair Khan, Fitash Ul Haq, and Ifrah Qaisar. "A Model-
Based Testing Approach for Cockpit Display Systems of Avionics." In 2019 ACM/IEEE 22nd International 
Conference on Model Driven Engineering Languages and Systems (MODELS), pp. 67-77. IEEE, 2019.
```
Download [PDF](https://www.researchgate.net/publication/337510742_A_Model-Based_Testing_Approach_for_Cockpit_Display_Systems_of_Avionics)
```
3. CDST Toolkit Paper
_____________________
Sartaj, Hassan, Muhammad Zohaib Iqbal, and Muhammad Uzair Khan. "CDST: A Toolkit for Testing Cockpit 
Display Systems." In 2020 IEEE 13th International Conference on Software Testing, Validation and 
Verification (ICST), pp. 436-441. IEEE, 2020.
```

Download [PDF](https://www.researchgate.net/publication/343627970_CDST_A_Toolkit_for_Testing_Cockpit_Display_Systems)

Bibtex:
```
1. CDS Testing Approach Paper - Journal
_______________________________________
@article{sartajtesting,
  author={Sartaj, Hassan and Iqbal, Muhammad Zohaib and Khan, Muhammad Uzair},
  title={Testing cockpit display systems of aircraft using a model-based approach},
  journal={Software and Systems Modeling},
  volume={20},
  number={6},
  pages={1977--2002},
  year={2021},
  publisher={Springer Berlin Heidelberg Berlin/Heidelberg},
  doi={10.1007/s10270-020-00844-z}
}

2. CDS Testing Approach Paper - Conference
__________________________________________

@inproceedings{iqbal2019model,
  title={A Model-Based Testing Approach for Cockpit Display Systems of Avionics},
  author={Iqbal, Muhammad Zohaib and Sartaj, Hassan and Khan, Muhammad Uzair and Haq, Fitash Ul and Qaisar, Ifrah},
  booktitle={2019 ACM/IEEE 22nd International Conference on Model Driven Engineering Languages and Systems (MODELS)},
  pages={67--77},
  year={2019},
  organization={IEEE},
  doi={10.1109/MODELS.2019.00-14}
}

3. CDST Toolkit Paper
_____________________
@inproceedings{sartaj2020cdst,
  title={CDST: A Toolkit for Testing Cockpit Display Systems},
  author={Sartaj, Hassan and Iqbal, Muhammad Zohaib and Khan, Muhammad Uzair},
  booktitle={2020 IEEE 13th International Conference on Software Testing, Validation and Verification (ICST)},
  pages={436--441},
  year={2020},
  organization={IEEE},
  doi={10.1109/ICST46399.2020.00058}
}

```

# Getting CDST

The source code of the complete toolkit can be obtained from the GitHub repository:
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
* To use the 'Test Path & Script Generator' module
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
* Building the 'Test Path & Script Generator' module
   * For `cdst.testgenerator`, project run maven clean and install from Eclipse or using the command: `mvn clean install`
* Building the 'Cockpit Display Recorder' module
   * For `cdst.cdrecorder` project, run maven clean and install from Eclipse or using the command: `mvn clean install`

## Dataset
* `pfd-ds-icst`: This dataset contains simulation data (images) recorded for 275 minutes (used for ICST paper)
* `pfd-ds`: This dataset contains simulation data (images) for the PFD case study (used for MODELS paper)
* `pfd-sim-ds`: This dataset contains simulator raw data for the PFD experimental simulation
* `gcs-cds-ds`: This dataset contains simulation data (images) for the GCS-CDS case study (used for SoSyM paper)
* `gcs-cds-sim-ds`: This dataset contains simulator raw data for the GCS-CDS experimental simulation

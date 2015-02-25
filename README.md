# RoCA - Root Cause Analysis in IT Landscapes using Markov Logic Networks

- - -

RoCA is a graphical tool to model IT infrastructures. It uses [ontologies](www.w3.org/TR/owl2-primer/) and [Markov Logic Networks (MLN)](http://link.springer.com/article/10.1007/s10994-006-5833-1#page-1) as internal representation. Based on the modeled infrastructure and provided evidence about available and unavailable components it conducts a root cause analysis, calculating the most probable underlying source of the observed unavailabilities. RoCA is the result of the project [Development of a risk management tool for complex technical systems](http://dws.informatik.uni-mannheim.de/en/projects/current-projects/#c13643).

RoCA translates the modeled infrastructure into a special MLN for abductive reasoning (inference to the best explanation). To find the root cause RoCA calculates the MAP state of the MLN. We use [RockIt](https://code.google.com/p/rockit/) as MLN solver. 


## Installation

### Prerequisites
RoCA requires the following software to be installed to run:

* [Java Runtime Environment (JRE) 1.8](http://java.com/inc/BrowserRedirect1.jsp?locale=en) 
* [Gurobi](http://www.gurobi.com/) (free academic license available)
* [MySQL](http://www.mysql.com/downloads/)
  Needed by RockIt. By default, it uses the following user account:
  * `sql_username = root`
  * `sql_password = mannheim1234` 

### Verifying the installation
You can verify from the console that all environment variables are set correctly: 
* Run the command `java -version`. The first line should show something like `java version "1.8.0_XX"`.
* Run `echo %PATH%` (Windows) or `echo $PATH` (Linux). The output must contain the `bin` folder of your Gurobi installation (and the path to the JRE 1.8).
* Run `echo %GUROBI_HOME%` or `echo $GUROBI_HOME`. It should point to root of your Gurobi installation.

### Starting RoCA
A binary to run RoCA can be [downloaded here](http://web.informatik.uni-mannheim.de/risk/).
To start the GUI either double-click on the JAR-file or execute the command `java -jar roca-XXX.jar` from a console in the folder where you put the binary.
<img src="http://web.informatik.uni-mannheim.de/risk/new.png" width="400px">

## Usage
RoCA comes with a default MLN and a default background ontology to get you started. After starting the application an empty model is presented. You can either load information about your infrastructure from an MLN evidence file or an ontology, or start from scratch. When your model is finished and contains all necessary or available information, you can start RockIt to run the root cause analysis.

### Loading a Model
loading the default MLN

<img src="http://web.informatik.uni-mannheim.de/risk/menu.png">

<img src="http://web.informatik.uni-mannheim.de/risk/dialog.png">


### Editing a Model
<img src="http://web.informatik.uni-mannheim.de/risk/model.png" width="400px">

<img src="http://web.informatik.uni-mannheim.de/risk/properties.png">

providing evidence
edges typed automatically

### Running the Root Cause Analysis
run, wait, popup showing results

<img src="http://web.informatik.uni-mannheim.de/risk/inference.png">

<img src="http://web.informatik.uni-mannheim.de/risk/cause.png">


- - -

## Publications
* Schoenfisch, J., von Stülpnagel, J., Ortmann, J., Meilicke, C., Stuckenschmidt, H.: 
  *Root Cause Analysis through Abduction in Markov Logic Networks*, 2015, to appear 
  ([download](http://web.informatik.uni-mannheim.de/risk/Schoenfisch2015.pdf))

- - -

## Acknowledgements
This work has been partially supported by the German Federal Ministry of 
Economics and Technology (BMWI) in the framework of the Central Innovation 
Program SME (Zentrales Innovationsprogramm Mittelstand - ZIM) within the project 
"Risk management tool for complex IT infrastructures".


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
  

### Starting RoCA
A binary to run RoCA can be downloaded from the [project homepage](http://dwslab.github.io/RoCA).
To start the GUI either double-click on the JAR-file or
execute `java -jar roca-XXX.jar` from the console. Your PATH variable has to include the path
to the JRE 1.8 `bin` folder.


## Usage

### Loading a Model
loading the default MLN

### Editing a Model
providing evidence
edges typed automatically

### Running the Root Cause Analysis
run, wait, popup showing results

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


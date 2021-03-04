# Semantic Web Analysis Graphs

Semantic Web Analysis Graphs (SWAG) allow to model and execute analytical processes over the the semantic web. For further details, refer to the project [website](https://swag-bi.github.io/swag/).

The project ships as a Java Maven web project and can be directly imported into Eclipse or other IDEs.

Once you import the project, you can build it with maven.

You can also use the ready war file in bin folder.

Afterwards, the project is ready to be deployed in e.g., Apache Tomcat.

The project comes with a example [SWAG for analysis of asylum applications](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl) which you can use to try the project out.

You will need a local SPARQL database e.g., GraphDB, running on port 7200 and a repository called "AMCIS_2021". Alternatively, you can change your connection string of the underlying SPARQL endpoint the [SWAG](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl) itself.

You will need to load the data into your SPARQL database in order to be able to obtain actual results and instntiate the SWAG:

* Download [dimension instances](https://github.com/lorenae/qb4olap/blob/master/examples/eurostat_instances_QB4OLAP_v1.3.ttl) and load them into your SPARQL database.

* Download [observations](observatoins/observations.ttl) which is a small, random set of observations that you can also load into your SPARQL database.

Once you have completed the previous two steps, you can proceed to run the project with [SWAG for analysis of asylum applications](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl).

To start: type in your browser localhost:8080/swag/ManipulateAnalysisGraphs (or whatever port you use).

 



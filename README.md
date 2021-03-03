# Semantic Web Analysis Graphs

Semantic Web Analysis Graphs (SWAG) allow to model and execute analytical processes over the the semantic web.

The project ships as a Java Maven web project and can be directly imported into Eclipse or other IDEs.

Once you import the project, you can build with maven.

You can also use the ready war file in bin folder.

Afterwards, the project is ready to be deployed in e.g., Apache Tomcat.

The project comes with a example [SWAG for analysis of asylum applications](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl) which you can use to try the project out.

You will need a local SPARQL database e.g., GraphDB, running on port 7200 and a repository called "AMCIS_2021". Alternatively, you can change it and set up your connection string at [SWAG for analysis of asylum applications](https://github.com/swag-bi/swag/blob/master/src/main/webapp/WEB-INF/resources/Uploaded/AGs/eurostat_AG_AMCIS2021.ttl).

In order to load the respective qb4o data, you can install the [eurostat schema](https://github.com/lorenae/qb4olap/blob/master/rdf/qb4olap.ttl) and the [dimension instances](https://github.com/lorenae/qb4olap/blob/master/examples/eurostat_schema_QB4OLAP_v1.3.ttl).
 



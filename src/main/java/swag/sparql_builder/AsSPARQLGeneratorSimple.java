package swag.sparql_builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.sparql.core.Var;
import org.apache.log4j.Logger;

import swag.analysis_graphs.execution_engine.AnalysisGraph;
import swag.analysis_graphs.execution_engine.analysis_situations.AnalysisSituation;
import swag.analysis_graphs.execution_engine.analysis_situations.IDimensionQualification;
import swag.analysis_graphs.execution_engine.analysis_situations.IMeasureInAS;
import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.md_elements.MDSchema;
import swag.sparql_builder.ASElements.DimensionParserCreator;
import swag.sparql_builder.ASElements.DimensionToSPARQLQueryGenerator;
import swag.sparql_builder.ASElements.DimensionsSubQueryGenerator;
import swag.sparql_builder.ASElements.IDimensionToSPARQLQueryGenerator;

/**
 * 
 * 
 * 
 * @author swag
 *
 */
public class AsSPARQLGeneratorSimple extends AsSPARQLGeneratorExtended {

	public AsSPARQLGeneratorSimple(MDSchema mdSchema, AnalysisGraph ag) {
		super(mdSchema, ag);
	}

	private void overrideConfiguration(AnalysisSituation as) {

		for (IDimensionQualification d : as.getDimensionsToAnalysisSituation()) {
			d.getConfiguration().resetConfiguration();
		}
		for (IMeasureInAS m : as.getResultMeasures()) {
			m.getConfiguration().resetConfiguration();
		}
	}

	private static final Logger logger = Logger.getLogger(AsSPARQLGeneratorSimple.class);

	/**
	 * Given an analysis situation, this function generates a corresponding
	 * SPARQL query The whole query body is treated as one single pattern, i.e.,
	 * any duplicated triple pattern anywhere in the whole query is removed
	 * 
	 * @param as
	 *            the bound analysis situation to generate its SPARQL
	 * @return a rooted SPARQL query
	 * @throws SPARQLQueryGenerationException
	 */
	@Override
	public CustomSPARQLQuery doQueryGenerationMainProcessing(AnalysisSituation as)
			throws SPARQLQueryGenerationException {

		logger.info("generating SPARQL query for Analysis Situation " + as.getName());
		try {

			overrideConfiguration(as);
			CustomSPARQLQuery rq = new CustomSPARQLQuery();

			Map<Var, Var> varsMappings = new HashMap<>();

			List<CustomSPARQLQuery> dimsList = new ArrayList<CustomSPARQLQuery>();
			List<Var> groupByVars = new ArrayList<Var>();

			logger.info("Starting generating SPARQL of dimensions/hierarchies.");
			// generating a query for each dimension
			for (IDimensionQualification dimToAS : as.getDimensionsToAnalysisSituation()) {

				IDimensionToSPARQLQueryGenerator dimGenerator = new DimensionToSPARQLQueryGenerator(as, ag, rq,
						mdElemToVarMap, dimToAS);

				dimsList.add(dimGenerator.generateSPARQLFromDimToAS(mdSchema, dimToAS, varsMappings));
				// adding a group by variable for the granularity if exists

				if (dimToAS.getGranularities().size() > 0) {
					LevelInAnalysisSituation granLvl = dimToAS.getGranularities().get(0).getPosition();
					if (granLvl != null && !granLvl.getIdentifyingName().equals("")
							&& !granLvl.getSignature().isVariable())
						groupByVars.add(granLvl.getMapping().getQuery().getUnaryProjectionVar());
				}
			}

			mdSchema.stringifyGraph();
			List<CustomSPARQLQuery> clonedList = new ArrayList<CustomSPARQLQuery>();

			boolean hasGroupByVars = false;
			boolean hasOtherVars = false;

			// edited on 04.05.2018
			// shifting dimensional queries without headers to the end of the
			// query to avoid exceptions
			for (CustomSPARQLQuery rootedQuery : dimsList) {
				if (rootedQuery.getSparqlQuery().getQueryPattern() != null) {
					if (rootedQuery.getSparqlQuery().getProjectVars().isEmpty()) {
						clonedList.add(clonedList.size(), rootedQuery);
						hasOtherVars = true;
					} else {
						clonedList.add(0, rootedQuery);
						hasGroupByVars = true;
					}
				}
			}

			List<Var> granVars = new ArrayList<>();
			logger.info("Generating SPARQL of dimensions.");

			if (hasGroupByVars || hasOtherVars) {

				DimensionsSubQueryGenerator dimsSubGenerator = DimensionParserCreator
						.createDimensionsSubQueryGenerator(as, ag, rq, mdElemToVarMap);

				granVars = dimsSubGenerator.mainGenerateSubQuery(clonedList, as.getFact().getHeadVar(), varsMappings);
			}

			CustomSPARQLQuery finalQuery = new CustomSPARQLQuery();
			finalQuery.getSparqlQuery().setQueryPattern(SPARQLUtilities.extractSubQuery(rq));

			// adding measure queries first to have a header (project) for the
			// query, otherwise if there
			// are no granularities and exception might occur.
			// adding measures queries

			List<Var> renamedGranVarsUsedForCount = new ArrayList<>();

			logger.info("Generating SPARQL of measures.");
			finalQuery = addMeasureSubQueries(finalQuery, mdSchema, as, renamedGranVarsUsedForCount);

			logger.info("Generating SPARQL of base measures filters.");
			addBaseMeasureFilters(rq, mdSchema, as);
			// adding the fact query
			// rq.joinWith(as.getFact().getMapping().getQuery());

			logger.info("Appending granularities to query.");
			// adding group by variables
			for (Var v : granVars) {
				finalQuery.getSparqlQuery().addGroupBy(v.getName());
				finalQuery.getSparqlQuery().getProjectVars().add(v);
			}
			// here removing any duplicated triple pattern
			// rq.removeQueryPatternDuplications();
			logger.info("Generating SPARQL of result filters.");
			addResultFilters(finalQuery, mdSchema, as);
			SPARQLUtilities.addHavingToQuery(finalQuery, " COUNT (*) > 0 ");

			logger.info("Dealing with labels.");
			finalQuery.setSparqlQuery(
					SPARQLUtilities.encapsulateBasicQuery(finalQuery, mdSchema.getPreferredLabelProperty()));

			return finalQuery;

		} catch (Exception ex) {
			SPARQLQueryGenerationException exSPARQL = new SPARQLQueryGenerationException(
					"Error generating SPARQL for analysis situation " + as.getName() + "\n Root cause: "
							+ ex.getMessage(),
					ex);
			logger.error("Error generating SPARQL for analysis situation " + as.getName(), ex);
			throw exSPARQL;
		}
	}

	@Override
	protected void appendSubQueryToMainQuery(CustomSPARQLQuery rq, CustomSPARQLQuery subQuery) {
		rq.joinWithOnlyPatternsAsGroupsByReference(subQuery);
	}

}

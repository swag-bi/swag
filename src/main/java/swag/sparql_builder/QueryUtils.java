package swag.sparql_builder;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;

import com.ibm.icu.util.Measure;

import swag.analysis_graphs.execution_engine.analysis_situations.LevelInAnalysisSituation;
import swag.md_elements.Descriptor;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDSchemaGraphQB4OLAP;

public class QueryUtils {
	
	MDSchemaGraphQB4OLAP schema;

	public Var getVarOfLevel(Dimension d, Level l) {
		String name = d.getName() + "_" + l.getName();
		return Var.alloc(name);
	}
	
	public Var getVarOfLevelAttribute(Dimension d, Level l, Descriptor a) {
		String name = d.getName() + "_" + l.getName() + "_" + a.getName();
		return Var.alloc(name);
	}
	
	public Var getVarOfMeasure(swag.md_elements.Measure m) {
		String name = m.getName();
		return Var.alloc(name);
	}
	
	public Var getVarOfFact(Fact f) {
		String name = f.getName();
		return Var.alloc(name);
	}
	
	public TripleCollectorBGP getTriplesOfLevel(Dimension d, Level l) {
		
		TripleCollectorBGP bgp = new TripleCollectorBGP();			
		Triple triple = new Triple(getVarOfLevel(d,l),
				NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"),
				NodeFactory.createURI(l.getURI()));
		bgp.addTriple(triple);
		return bgp;
	}
	
	public TripleCollectorBGP getTriplesOfRollUp(Dimension d, Level l) {
		
		TripleCollectorBGP bgp = new TripleCollectorBGP();			
		Triple triple = new Triple(getVarOfLevel(d,l),
				NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"),
				NodeFactory.createURI(l.getURI()));
		bgp.addTriple(triple);
		return bgp;
	}
}

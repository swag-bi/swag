package swag.sparql_builder;

import java.util.LinkedList;
import java.util.List;

import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;

import swag.md_elements.Descriptor;
import swag.md_elements.Dimension;
import swag.md_elements.Fact;
import swag.md_elements.Level;
import swag.md_elements.MDRelation;
import swag.md_elements.MDSchema;

public class QueryUtils {

	public QueryUtils(MDSchema schema) {
		super();
		this.schema = schema;
	}

	MDSchema schema;

	public Var getVarOfLevel(Dimension d, Level l) {
		return getVarOfLevel(d.getName(), l.getName());
	}

	public Var getVarOfLevel(String d, String l) {
		String name = d + "_" + l;
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

	public TripleCollectorBGP getTriplesOfLevel(String d, String l) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();
		Triple triple = new Triple(getVarOfLevel(d, l), NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"),
				NodeFactory.createURI(l));
		bgp.addTriple(triple);
		return bgp;
	}

	public TripleCollectorBGP getTriplesOfRollUp(String d, String l, String d2, String l2) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();
		Triple triple1 = new Triple(getVarOfLevel(d, l),
				NodeFactory.createURI("http://purl.org/qb4olap/cubes#memberOf"), NodeFactory.createURI(l));
		bgp.addTriple(triple1);
		MDRelation rel = schema.getRollUpProperty(d, l, d2, l2);
		Triple triple2 = new Triple(getVarOfLevel(d, l), NodeFactory.createURI(rel.getURI()),
				NodeFactory.createURI(l2));
		bgp.addTriple(triple2);
		return bgp;
	}

	public TripleCollectorBGP getTriplesOfPath(String d, String l, String l2) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();

		List<Level> path = new LinkedList<>();

		path = schema.getPath(path, l2, l, d);

		for (int i = 0; i < path.size(); i++) {
			for (Triple t : getTriplesOfLevel(d, path.get(i).getURI()).getBGP().getList()) {
				bgp.addTriple(t);
			}
			if (i < path.size() - 1) {
				for (Triple t : getTriplesOfRollUp(d, path.get(i).getURI(), d, path.get(i + 1).getURI()).getBGP()
						.getList()) {
					bgp.addTriple(t);
				}
			}
		}
		return bgp;
	}
}

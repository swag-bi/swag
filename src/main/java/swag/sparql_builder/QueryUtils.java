package swag.sparql_builder;

import java.util.Collections;
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
		String name = getLocalName(d).substring(0,2) + "_" + getLocalName(l);
		return Var.alloc(name);
	}

	private String getLocalName(String str){
		String newStr = str;

		int index = newStr.lastIndexOf("#");

		if (index == -1){
			return str;
		}

		newStr = newStr.substring(index + 1, newStr.length());

		return newStr;
	}

	public Var getVarOfLevelAttribute(String d, String l, String  a) {
		String name = getLocalName(d).substring(0,2) +
				"_" + getLocalName(l).substring(0,2) +
				"_" + getLocalName(a);
		return Var.alloc(name);
	}

	public Var getVarOfMeasure(String m) {
		return Var.alloc(getLocalName(m));
	}

	public Var getVarOfFact(String f) {
		return Var.alloc(getLocalName(f));
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
		SetBasedBGP.addTripleToBgp(bgp, triple1);

		MDRelation rel = schema.getRollUpProperty(l, d, l2, d2);
		Triple triple2 = new Triple(getVarOfLevel(d, l), NodeFactory.createURI(rel.getURI()),
				NodeFactory.createURI(l2));
		SetBasedBGP.addTripleToBgp(bgp, triple2);
		return bgp;
	}

	public TripleCollectorBGP getTriplesOfPath(String d, String l, String l2) {

		TripleCollectorBGP bgp = new TripleCollectorBGP();

		List<Level> path = new LinkedList<>();

		path = schema.getPath(path, l2, l, d);
		Collections.reverse(path);

		for (int i = 0; i < path.size(); i++) {
			for (Triple t : getTriplesOfLevel(d, path.get(i).getURI()).getBGP().getList()) {
				SetBasedBGP.addTripleToBgp(bgp, t);
			}
			if (i < path.size() - 1) {
				for (Triple t : getTriplesOfRollUp(d, path.get(i).getURI(), d, path.get(i + 1).getURI()).getBGP()
						.getList()) {
					SetBasedBGP.addTripleToBgp(bgp, t);
				}
			}
		}
		return bgp;
	}
}

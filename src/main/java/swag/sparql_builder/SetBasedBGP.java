package swag.sparql_builder;

import org.apache.jena.graph.Triple;
import org.apache.jena.sparql.syntax.TripleCollectorBGP;

public class SetBasedBGP {
    static void addTripleToBgp(TripleCollectorBGP bgp, Triple triple){
        if(!bgp.getBGP().getList().contains(triple)){
            bgp.addTriple(triple);
        }
    }
}

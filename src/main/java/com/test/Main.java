/*
 * Neo4JDemo
 *
 * Purpose is to test some functionalities of the NOSQL Neo4j database
 *
 * Tests done with 'neo4j-3.1.1' coming from "http://www.neo4j.org"
 * (using Java 8)
 *
 * this generates a demo.dot file containing a textual version of the graph
 * to convert it in png do "dot -tpng -O demo.dot". This "dot" program comes with graphviz software ( graphviz-2.38.zip )
 *
 */
package com.test;

import org.neo4j.graphdb.Node;

import com.test.Neo4jDemo.RelTypes;
import java.io.IOException;
import java.util.Scanner;

public class Main {



    /**
     * @param args
     */
    public static void main(String[] args)
    {
        Neo4jDemo nd = new Neo4jDemo ();
        nd.startDB();

        System.out.println("create some nodes...");
        Node nf = nd.createNode("Person,Boy",1,"Frank");
        Node np = nd.createNode("Person,Boy",2,"Philippe");
        Node nm = nd.createNode("Person,Boy",3,"Michel");
        Node nc = nd.createNode("Person,Boy",4,"Corentin");
        Node nr = nd.createNode("Person,Boy",5,"Romain");
        Node nv = nd.createNode("Person,Girl",6,"Véronique");
        Node ne = nd.createNode("Person,Girl",7,"Elisabeth");

        System.out.println("create some relations...");
        nd.createRelation(nm, nf, Neo4jDemo.RelTypes.IS_FATHER_OF,"1970");
        nd.createRelation(nf, nm, Neo4jDemo.RelTypes.IS_SON_OF,"1970");
        nd.createRelation(nm, np, Neo4jDemo.RelTypes.IS_FATHER_OF,"1969");
        nd.createRelation(np, nm, Neo4jDemo.RelTypes.IS_SON_OF,"1969");
        nd.createRelation(nf, nc, Neo4jDemo.RelTypes.IS_FATHER_OF,"1996");
        nd.createRelation(nc, nf, Neo4jDemo.RelTypes.IS_SON_OF,"1996");
        nd.createRelation(nf, nr, Neo4jDemo.RelTypes.IS_FATHER_OF,"2003");
        nd.createRelation(nr, nf, Neo4jDemo.RelTypes.IS_SON_OF,"2003");
        nd.createRelation(nf, nv, Neo4jDemo.RelTypes.IS_HUSBAND_OF,"1994");
        nd.createRelation(nv, nf, Neo4jDemo.RelTypes.IS_WIFE_OF,"1994");
        nd.createRelation(nm, ne, Neo4jDemo.RelTypes.IS_HUSBAND_OF,"1966");
        nd.createRelation(ne, nm, Neo4jDemo.RelTypes.IS_WIFE_OF,"1966");
        System.out.println("list all nodes...");
        nd.listNode();

        // search for some node
		/* to uncomment if node and relation creation is commented out
		Node nf = nd.testSearchNode ("Frank");
		Node nm = nd.testSearchNode ("Michel");
		*/

        System.out.println("list all sons of 'Michel'...");
        nd.traverserAtDepth(nm,1,RelTypes.IS_SON_OF);

        System.out.println("list all grandchild of 'Michel'...");
        nd.traverserAtDepth(nm,2,RelTypes.IS_SON_OF);

        System.out.println("list all sons of 'Frank'...");
        nd.traverserAtDepth(nf,1,RelTypes.IS_SON_OF);

        System.out.println("list wife of 'Frank'...");
        nd.traverserAtDepth(nf,1,RelTypes.IS_WIFE_OF);

        System.out.println("list husband of 'Elisabeth'...");
        nd.traverserAtDepth(ne,1,RelTypes.IS_HUSBAND_OF);

        try
        {
            nd.drawGraph();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        System.out.print("Enter a string to end ...");
        Scanner scanner = new Scanner(System. in);
        String inputString = scanner. nextLine();
        System.out.println("Ending");

        nd.stopDB();
    }

}

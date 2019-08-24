package com.test;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.visualization.graphviz.GraphvizWriter;
import org.neo4j.walk.Walker;


@SuppressWarnings("deprecation")
public class Neo4jDemo
{
    private GraphDatabaseService graphDb = null;			// database
    private  Index<Node>  nodeIndex = null;                 // index
    private File storeDir = new File("c:/temp/neo4j");               // database path
    public enum RelTypes implements RelationshipType		// relation types
    {
        IS_FATHER_OF,
        IS_SON_OF,
        IS_WIFE_OF,
        IS_HUSBAND_OF
    }

    //------------------------------------------------------
    // start the DB engine

    public void startDB ()
    {
        this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( storeDir );
        Transaction tx = this.graphDb.beginTx();
        try
        {
            this.nodeIndex = graphDb.index().forNodes("PersonNameIdx");
            tx.success();
            System.out.println ("startDB done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            tx.close();
        }
    }

    //------------------------------------------------------
    // create a node with some label(s) and with 'id' and 'name' properties

    public Node createNode(String label,int id,String name)
    {
        Transaction tx = this.graphDb.beginTx();
        try
        {
            System.out.println ("create node ");
            // create node
            Node userNode = this.graphDb.createNode();
            // set label
            if ( (label != null) && ( label.length() > 0) )
            {
                String [] labels = label.split(",");
                for (int i = 0; i < labels.length; i++)
                {
                    userNode.addLabel(DynamicLabel.label(labels[i]));
                }
            }
            // set properties
            userNode.setProperty("id", id);
            userNode.setProperty("name", name);
            // index node
            nodeIndex.add( userNode, "name", name);

            tx.success();
            System.out.println ("testCreateNode done");
            return userNode;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            tx.close();
        }
    }

    //------------------------------------------------------
    // search for a given node

    public Node searchNode (String name)
    {
        Transaction tx = this.graphDb.beginTx();
        try
        {
            System.out.println ("search node ");
            Node foundUser = nodeIndex.get( "name", name ).getSingle();
            if (foundUser != null) System.out.println ("id = "+foundUser.getProperty("id"));

            return foundUser;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            tx.close();
        }
    }

    //------------------------------------------------------
    // list all the nodes in the system

    public void listNode()
    {
        Transaction tx = this.graphDb.beginTx();
        try
        {
            Iterable<Node> nodes = this.graphDb.getAllNodes();
            Iterator<Node> litr = (Iterator<Node>) nodes.iterator();
            while (litr.hasNext())
            {
                Node node = litr.next();
                System.out.println ("id = "+ node.getProperty("id")+", name = "+node.getProperty("name"));
            }
            System.out.println ("testListNode done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            tx.close();
        }
    }

    //------------------------------------------------------
    // add a relation between two nodes, with a 'since' property
    public void createRelation (Node n1, Node n2, RelTypes relation,String since)
    {
        Transaction tx = this.graphDb.beginTx();
        try
        {
            Relationship rel = n1.createRelationshipTo(n2, relation);
            rel.setProperty("since", since);
            tx.success();
            System.out.println ("testCreateRelation done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            tx.close();
        }

    }

    //------------------------------------------------------
    // traverse the nodes and display some information about node of given depth and relation type

    public void traverserAtDepth (Node startNode, int depth, RelTypes relation)
    {
        Transaction tx = this.graphDb.beginTx();
        try
        {
            TraversalDescription tv = this.graphDb.traversalDescription().depthFirst().relationships( relation, Direction.INCOMING  ).evaluator( Evaluators.atDepth( depth ) );
            for ( Path path : tv.traverse( startNode ) )
            {
                System.out.println ("From "+startNode.getProperty("name")+" ,at depth " + path.length() + " => "+ path.endNode().getProperty( "name" ) );
            }
            tx.success();
            System.out.println ("testTraverser done");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            tx.close();
        }
    }

    //------------------------------------------------------
    // stop the database

    public void stopDB ()
    {
        this.graphDb.shutdown();
		/*
		// delete all files (if we want to crash them for testing purposes
		try
		{
			FileUtils.deleteRecursively( new File( storeDir ) );
		}
		catch ( IOException e )
		{
			e.printStackTrace();
		}
		*/
    }

    void drawGraph() throws IOException {
        Transaction tx = this.graphDb.beginTx();
        GraphvizWriter writer = new GraphvizWriter();
        Walker walker = Walker.fullGraph(this.graphDb);
        new File("target/result").mkdirs();
        writer.emit(new File("target/result/demo.dot"), walker);
        tx.success();
        tx.close();
    }

}

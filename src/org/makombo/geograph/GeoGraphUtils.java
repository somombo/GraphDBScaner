package org.makombo.geograph;

import java.io.File;
import java.io.IOException;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.FileUtils;

import com.thinkaurelius.titan.core.TitanFactory;
import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;

public class GeoGraphUtils {
	public static final String ROOT = "D:/Users/Makombo/Development/aurelius/titan-server/data";
	public static final String PATH = ROOT + "/geo";
	public static final String GRAPH_PATH = PATH + "/graph";
	public static final String INDEX_PATH = PATH + "/index";
	
	public static final String CONFIG_PATH = "titan-config.properties";

	public static final String INDEX_NAME = "search";
	public static final String POINT_KEY = "point";
	public static final String CENTROID_KEY = "centroid";
	
	
	public static final int LATITUDE_IDX = 0;
	public static final int LONGITUDE_IDX = 1;
	private GeoGraphUtils() {}
	
	public static TitanGraph open(final String path) {
		Configuration conf = new BaseConfiguration();
		
			conf.setProperty("storage.backend", "berkeleyje");
//			conf.setProperty("storage.backend", "persistit");		
			
			conf.setProperty("storage.directory", GRAPH_PATH);
			conf.setProperty("storage.index.search.directory", INDEX_PATH);
			
			conf.setProperty("storage.index.search.backend", "elasticsearch"); 
			conf.setProperty("storage.index.search.client-only", "false"); 
			conf.setProperty("storage.index.search.local-mode", "true");
			
		return TitanFactory.open(conf);
	}
	
	public static TitanGraph open() {

		return open(GRAPH_PATH);
	}
	
	public static double yToLatitude(double y) {
		final double GEO_WITHIN_OFFSET = 0.0001d;
		final double UNIT_LATITUDINAL_DISTANCE = 111.19507973436875d; // Geoshape.point(0, 0).getPoint().distance(Geoshape.point(1, 0).getPoint());	 System.out.println(UNIT_LATITUDINAL_DISTANCE);
		
		return (y * UNIT_LATITUDINAL_DISTANCE) + GEO_WITHIN_OFFSET ;
	}
	

	private static void delete() {

		try {
			FileUtils.deleteDirectory(new File(GeoGraphUtils.PATH));
		} catch (IOException e) {
			//e.printStackTrace();
		}
		
	}	
	private static void clear(TitanGraph g) {
		new GremlinPipeline<Vertex,PointWrapper>(g.getVertices()).remove();

		
	}

	public static TitanGraph openNew() {
		//delete();
		TitanGraph g = open();
			if(g != null) 
				clear(g);
			

		return g;
	}
	
	public static void show(TitanGraph g) {

			System.out.println("\n THE GRAPH");
	
			
			System.out.println( new GremlinPipeline<Vertex,Object>(g.getVertices()) 
										.map()
										.toList()
										.toString().replace("}, ", "},\n"));
			


		
	}
}

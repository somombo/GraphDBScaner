

package org.makombo.geograph;


import java.util.List;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterizing_RDBSCANClusterer;

import com.thinkaurelius.titan.core.TitanGraph;
import com.thinkaurelius.titan.core.attribute.Geo;
import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.gremlin.java.GremlinPipeline;
import com.tinkerpop.pipes.PipeFunction;


public class Clusterizer {
	



	public final static double EPS = GeoGraphUtils.yToLatitude(3); 
	public final static int MINPTS = 4;



	public static void execute(final TitanGraph g) {

			
			new GremlinPipeline<Vertex,PointWrapper>(g.getVertices())
					.has(GeoGraphUtils.CENTROID_KEY)
					.remove();
			
			List<PointWrapper> points = new GremlinPipeline<Vertex,PointWrapper>(g.getVertices()) 
					.has(GeoGraphUtils.POINT_KEY)
					.id()
					.transform(new PipeFunction<Object, PointWrapper>() {
						@Override
						public PointWrapper compute(Object id) {
							return new PointWrapper( (Geoshape) g.getVertex(id).getProperty(GeoGraphUtils.POINT_KEY) ).setID((Long)id);
						}
					})
					.toList();			
			
			//System.out.println(points);


			
	
			List<? extends Cluster<PointWrapper>> clusters = (new Clusterizing_RDBSCANClusterer<PointWrapper>(EPS,MINPTS)
			{

				@Override
				protected List<PointWrapper> getNeighbors(PointWrapper point, double eps) {

					List<PointWrapper> neighbors =  new GremlinPipeline<Vertex,PointWrapper>(g.query()
																		.has(GeoGraphUtils.POINT_KEY, 
																			Geo.WITHIN, 
																			Geoshape.circle(point.getPoint()[PointWrapper.LATITUDE_IDX], 
																							point.getPoint()[PointWrapper.LONGITUDE_IDX], 
																							eps))
																		.vertices())
										.has(GeoGraphUtils.POINT_KEY)
										.id()
										.transform(new PipeFunction<Object, PointWrapper>() {
											@Override
											public PointWrapper compute(Object id) {
												return new PointWrapper( (Geoshape) g.getVertex(id).getProperty(GeoGraphUtils.POINT_KEY) ).setID((Long)id);
											}
										})
										.toList();
					//System.out.println("Neighbourhood <<" + point  + " >> " + neighbors.hashCode() + " : "+ neighbors);
					return neighbors;
				}

				@Override 
				protected MyCluster newCluster() { 
					
					return new MyCluster(); 
					
				}
				
				class MyCluster extends Clusterizing_RDBSCANClusterer<PointWrapper>.MyCluster { private static final long serialVersionUID = 2468814556014117322L;
					private Vertex centroid;
					//private long _id;
					

					
					@Override 
					public void addPoint(PointWrapper point) { 
						
						super.addPoint(point); 
					}
					
					@Override
					public void calculateCentroid() {
						
								centroid = g.addVertex(null);
								centroid.setProperty(GeoGraphUtils.CENTROID_KEY, 
														Geoshape.circle(this.getCenter()[GeoGraphUtils.LATITUDE_IDX], 
																		this.getCenter()[GeoGraphUtils.LONGITUDE_IDX], 
																		this.getRadius()));     // g.commit();
						
					}

					@Override
					public double getRadius() {
						final SummaryStatistics deviations = new SummaryStatistics();
						final List<PointWrapper> points = this.getPoints();
						
						final Geoshape.Point mu = Geoshape.point(this.getCenter()[GeoGraphUtils.LATITUDE_IDX], 
																 this.getCenter()[GeoGraphUtils.LONGITUDE_IDX])
														  .getPoint();
						
						for (final PointWrapper x : points) {
							double distance = mu.distance( x.point() ); 
							g.addEdge(null, g.getVertex(x.getID()), centroid, "In_Cluster").setProperty("distance", distance); //g.commit();
							
							deviations.addValue( distance  );
						}
						return deviations.getMean();
					}	

					
				}
				
			}
			).cluster(points);  
			
			g.commit();
			//System.out.println(clusters);

		

		
		
	} // method

} //Class



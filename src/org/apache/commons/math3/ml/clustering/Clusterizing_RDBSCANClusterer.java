
package org.apache.commons.math3.ml.clustering;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.apache.commons.math3.ml.distance.EuclideanDistance;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.commons.math3.util.MathUtils;





public class Clusterizing_RDBSCANClusterer<T extends Clusterable> extends Clusterer<T> {

    private final double eps;

    private final int minPts;

    private enum PointStatus {NOISE, PART_OF_CLUSTER}

	private final Map<T, PointStatus> visited;

	private Iterable<T> points; 

    public Clusterizing_RDBSCANClusterer(final double eps, final int minPts)
        throws NotPositiveException {
        this(eps, minPts, new EuclideanDistance());
        
    }


    public Clusterizing_RDBSCANClusterer(final double eps, final int minPts, final DistanceMeasure measure)
        throws NotPositiveException {
        super(measure);

        if (eps < 0.0d) {
            throw new NotPositiveException(eps);
        }
        if (minPts < 0) {
            throw new NotPositiveException(minPts);
        }
        this.eps = eps;
        this.minPts = minPts;

        this.visited = new HashMap<T, PointStatus>();    

 
    }


    public double getEps() { return eps; }
    public int getMinPts() { return minPts; }
    

    @Override
    public List<? extends Cluster<T>> cluster(final Collection<T> points) throws NullArgumentException { return cluster( ((Iterable<T>) points) ); }


    public List<? extends Cluster<T>> cluster(final Iterable<T> points) throws NullArgumentException {
    	
        // sanity checks
        MathUtils.checkNotNull(points);
        this.points = points;
   
        
        final List<MyCluster> clusters = new ArrayList<MyCluster>();


        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
			
			final MyCluster expandedCluster = expand(point);
			if( !expandedCluster.getPoints().isEmpty()  ) {
				expandedCluster.calculateCentroid();
				clusters.add(expandedCluster);
			}
        }

        return clusters;
    }    
    
    private MyCluster expand(final T point) {

    	MyCluster cluster = newCluster();
			
		final List<T> neighbors = getNeighbors(point, eps);
		if (neighbors.size() >= minPts) {
			

			cluster.add(point);
			visited.put(point, PointStatus.PART_OF_CLUSTER);


			for (final T current : neighbors) {

				if (visited.get(current) == null) {
					cluster = merge(cluster, expand(current));
				}

				if (visited.get(current) != PointStatus.PART_OF_CLUSTER) {
					cluster.add(current);
					visited.put(point, PointStatus.PART_OF_CLUSTER);
				}

			} 

		} else {
		
			visited.put(point, PointStatus.NOISE);
		}
		return cluster;
    }


    protected List<T> getNeighbors(final T point, final double eps) {
        final List<T> neighbors = new ArrayList<T>();
        for (final T neighbor : points) {
            if (distance(neighbor, point) <= eps) {
                neighbors.add(neighbor);
            }
        }
        return neighbors;
    }

 
    private MyCluster merge(final MyCluster one, final MyCluster two) {

	
		final List<T> oneList = one.getPoints();
		final List<T> twoList = two.getPoints();

		if (oneList.isEmpty()) return two;
		if (twoList.isEmpty()) return one;
	
        final Set<T> oneSet = new HashSet<T>(oneList);
        for (T item : twoList) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }
    
    protected MyCluster newCluster() { return new MyCluster(); }
    
    protected class MyCluster extends Cluster<T> { private static final long serialVersionUID = -581336744286638389L;

		
		private SummaryStatistics[] coordinate;
		
		public MyCluster() { this(2); }		
		
		public MyCluster(int dimensions) { super();

    		coordinate = new SummaryStatistics[dimensions];
    		for(int i = 0; i < coordinate.length; i++) {
    			coordinate[i] = new SummaryStatistics();
    		}
    		
    	}
	
		@Deprecated
		@Override
		public void addPoint(T point) {
    		for(int i = 0; i < coordinate.length; i++) {		
    			coordinate[i].addValue(point.getPoint()[i]);
    		}
			
			super.addPoint(point);
		}
		
		public void add(T point) { this.addPoint(point); }
		
		public void add(Iterable<? extends T> points) { 
			for (T point : points) {
				this.add(point); 
			}
		}
		
		protected double[] getCenter() {
    		double[] _ = new double[coordinate.length];
    		for(int i = 0; i < coordinate.length; i++) {
    			_[i] = coordinate[i].getMean();
    		}
			return _;
		}
		
		protected double getRadius() {
    		double variance = 0d;
    		for(int i = 0; i < coordinate.length; i++) {
    			variance += coordinate[i].getVariance();
    		}
			return Math.sqrt(variance);
		}
		public void calculateCentroid() { }		
		
	    @Override 
	    public String toString() {
	    	
		    String 	_  = "\n\n";
		    		_ += "Cluster " + this.hashCode();
		    		_ += "\n   " + "Center:\t" + Arrays.toString(this.getCenter());
		    		_ += "\n   " + "Radius:\t" + this.getRadius();
		    		_ += "\n     " + this.getPoints();
			    
			return _;

		}

		
    }
}



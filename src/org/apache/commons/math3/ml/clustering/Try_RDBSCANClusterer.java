

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.math3.ml.clustering;
//package org.apache.commons.math3.ml.clustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathUtils;

import com.google.common.collect.Iterables;

/**
 * DBSCAN (density-based spatial clustering of applications with noise) algorithm.
 * <p>
 * The DBSCAN algorithm forms clusters based on the idea of density connectivity, i.e.
 * a point p is density connected to another point q, if there exists a chain of
 * points p<sub>i</sub>, with i = 1 .. n and p<sub>1</sub> = p and p<sub>n</sub> = q,
 * such that each pair &lt;p<sub>i</sub>, p<sub>i+1</sub>&gt; is directly density-reachable.
 * A point q is directly density-reachable from point p if it is in the &epsilon;-neighborhood
 * of this point.
 * <p>
 * Any point that is not density-reachable from a formed cluster is treated as noise, and
 * will thus not be present in the result.
 * <p>
 * The algorithm requires two parameters:
 * <ul>
 *   <li>eps: the distance that defines the &epsilon;-neighborhood of a point
 *   <li>minPoints: the minimum number of density-connected points required to form a cluster
 * </ul>
 *
 * @param <T> type of the points to cluster
 * @see <a href="http://en.wikipedia.org/wiki/DBSCAN">DBSCAN (wikipedia)</a>
 * @see <a href="http://www.dbs.ifi.lmu.de/Publikationen/Papers/KDD-96.final.frame.pdf">
 * A Density-Based Algorithm for Discovering Clusters in Large Spatial Databases with Noise</a>
 * @version $Id$
 * @since 3.2
 */
public abstract class Try_RDBSCANClusterer<T>{
	private Iterable<T> points; 
	private final Map<T, PointStatus> visited;

    /** Maximum radius of the neighborhood to be considered. */
    private final double              eps;

    /** Minimum number of points needed for a cluster. */
    private final int                 minPts;

    /** Status of a point during the clustering process. */
    private enum PointStatus {
        /** The point has is considered to be noise. */
        NOISE,
        /** The point is already part of a cluster. */
        PART_OF_CLUSTER
    }

 


    /**
     * Creates a new instance of a DBSCANClusterer.
     *
     * @param eps maximum radius of the neighborhood to be considered
     * @param minPts minimum number of points needed for a cluster
     * @param measure the distance measure to use
     * @throws NotPositiveException if {@code eps < 0.0} or {@code minPts < 0}
     */
    public Try_RDBSCANClusterer(final double eps, final int minPts)
        throws NotPositiveException {


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

    /**
     * Returns the maximum radius of the neighborhood to be considered.
     * @return maximum radius of the neighborhood
     */
    public double getEps() {
        return eps;
    }

    /**
     * Returns the minimum number of points needed for a cluster.
     * @return minimum number of points needed for a cluster
     */
    public int getMinPts() {
        return minPts;
    }

    /**
     * Performs DBSCAN cluster analysis.
     *
     * @param points the points to cluster
     * @return the list of clusters
     * @throws NullArgumentException if the data points are null
     */
//    public List<List<T>> cluster(final Collection<T> points) throws NullArgumentException {
//
//
//        return cluster(points);
//    }

    
    public Iterable<T> getPoints() {
		return points;
	}



	/**
     * Performs DBSCAN cluster analysis.
     *
     * @param points the points to cluster
     * @return the list of clusters
     * @throws NullArgumentException if the data points are null
     */
    public List<List<T>> cluster(final Iterable<T> points) throws NullArgumentException {
    	
        // sanity checks
        MathUtils.checkNotNull(points);
        this.points = points;
   
        
        final List<List<T>> clusters = new ArrayList<List<T>>();


        for (final T point : points) {
            if (visited.get(point) != null) {
                continue;
            }
			
			final List<T> expandedCluster = expand(point);
			if( !expandedCluster.isEmpty()  ) {
				clusters.add(expandedCluster);
			}
        }

        return clusters;
    }    
    
    /**
     * Recursively Expands the cluster to include density-reachable items.
     *
     * @param cluster Cluster to expand
     * @param point Point to add to cluster
     * @param neighbors List of neighbors
     * @param points the data set
     * @param visited the set of already visited points
     * @return the expanded cluster
     */
    private List<T> expand(final T point) {
    	
		List<T> cluster = new ArrayList<T>();
			
		final Iterable<T> neighbors = getNeighbors(point, eps);
		if (Iterables.size(neighbors) >= minPts) {
			
			// BEGIN expansion 
			cluster.add(point);
			visited.put(point, PointStatus.PART_OF_CLUSTER);

			Iterable<T> seeds = neighbors;
			//int index = 0; while (index < seeds.size()) { final T current = seeds.get(index);
			for (final T current : seeds) { // FOR EACH current IN seeds DO
				


				if (visited.get(current) == null) { 	// only check non-visited points
					cluster = merge(cluster, expand(current));
					/*
					final List<T> currentNeighbors = getNeighbors(current, points);
					if (currentNeighbors.size() >= minPts) {
						seeds = merge(seeds, currentNeighbors);
					}
					*/
				}

				if (visited.get(current) != PointStatus.PART_OF_CLUSTER) {
					cluster.add(current);
					visited.put(current, PointStatus.PART_OF_CLUSTER);
				}

			} // END FOR
			// index++;}
			

			// END expandsion
		} else {
		
			visited.put(point, PointStatus.NOISE);
		}
		return cluster;
    }

    /**
     * Returns a list of density-reachable neighbors of a {@code point}.
     * Can be overriden to allow for use of (spatial indices)
	 *
     * @param point the point to look for
     * @param points possible neighbors
     * @return the List of neighbors
     */
   protected abstract Iterable<T> getNeighbors(final T point, final double eps) ;

    /**
     * Merges two clusters together.
     *
     * @param one first cluster
     * @param two second cluster
     * @return merged clusters
     */	
    private List<T> merge(final List<T> one, final List<T> two) {
 	
	


		if (one.isEmpty()) return two;
		if (two.isEmpty()) return one;
	
        final Set<T> oneSet = new HashSet<T>(one);
        for (T item : two) {
            if (!oneSet.contains(item)) {
                one.add(item);
            }
        }
        return one;
    }
}
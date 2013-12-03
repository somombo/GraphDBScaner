package org.makombo.geograph;

//Now we will create a list of these wrapper objects (one for each location), which serves as input to our clustering algorithm.
import org.apache.commons.math3.ml.clustering.Clusterable;

import com.thinkaurelius.titan.core.attribute.Geoshape;

// wrapper class
public class PointWrapper implements Clusterable {

	public static final int LATITUDE_IDX = 0;
	public static final int LONGITUDE_IDX = 1;
	
	private double[] coordinates = new double[2];
	
    private long _id;

	private long _time;	
	private Geoshape.Point point;
	
    public PointWrapper(Geoshape point) { this(point.getPoint()); }
    
    public PointWrapper(Geoshape.Point point) {

       
    	this.coordinates[LATITUDE_IDX] =  point.getLatitude(); 
    	this.coordinates[LONGITUDE_IDX] = point.getLongitude(); 
        

		this._time = System.currentTimeMillis();


		this.point = point;
    	
    }


	@Override
	public int hashCode() {

		return (int) this.getID();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PointWrapper))
			return false;	
		if (obj == this)
			return true;
		return (this.getID() == ((PointWrapper) obj).getID());
	}
	
	public long getTime(){
    	return this._time;
    }

    
    public long getID() {
		return this._id;
	}
 
	public PointWrapper setID(long id) {
		this._id = id;
		return this;
	}

	public String toString(){
    	return "("+ this._id + ")" + this.point;
    }

	public Geoshape.Point point() {
		return point;
	}





	@Override
    public double[] getPoint() {
        return coordinates;
    }


}
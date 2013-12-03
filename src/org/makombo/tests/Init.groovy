package org.makombo.tests;


def fill(double... data) {
	
	com.thinkaurelius.titan.core.TitanGraph g = org.makombo.geograph.GeoGraphUtils.openNew();
		if(!g.getIndexedKeys(com.tinkerpop.blueprints.Vertex.class).contains(org.makombo.geograph.GeoGraphUtils.POINT_KEY)) {
			g.makeKey(org.makombo.geograph.GeoGraphUtils.POINT_KEY).dataType(com.thinkaurelius.titan.core.attribute.Geoshape.class).indexed(org.makombo.geograph.GeoGraphUtils.INDEX_NAME, com.tinkerpop.blueprints.Vertex.class).make();
			g.commit();
		}
	
		for(double datum : data){

			g.addVertex(null).setProperty(org.makombo.geograph.GeoGraphUtils.POINT_KEY, com.thinkaurelius.titan.core.attribute.Geoshape.point(datum, 0));

		}
	g.shutdown();
}

def fill() {
	

	fill(1, 2, 3, 4,7,10,20.1,20.2,20.3,20.4); 		System.out.println("\n Done Filling! \n");
//		com.thinkaurelius.titan.core.TitanGraph g = org.makombo.geograph.GeoGraphUtils.open();
//		g.makeKey("point").dataType(com.thinkaurelius.titan.core.attribute.Geoshape.class).indexed("search", com.tinkerpop.blueprints.Vertex.class).make()
//		org.makombo.geograph.Clusterizer.execute(g);
//		org.makombo.geograph.GeoGraphUtils.show(g);
//		g.shutdown();

}

fill()





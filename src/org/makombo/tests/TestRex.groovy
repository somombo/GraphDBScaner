package org.makombo.tests






//g = com.thinkaurelius.titan.core.TitanFactory.open('C:/prexster/data/geo/graph')
//g = com.thinkaurelius.titan.core.TitanFactory.open('D:/Users/Makombo/Documents/eclipse/workspace/TestTitan/src/org/makombo/tests/titan-config.properties')

conf = new org.apache.commons.configuration.BaseConfiguration();
conf.setProperty("storage.backend","berkeleyje")
conf.setProperty("storage.directory","C:/prexster/data/geo/graph")
conf.setProperty("storage.index.search.directory", "C:/prexster/data/geo/index")
conf.setProperty("storage.index.search.backend", "elasticsearch")
conf.setProperty("storage.index.search.client-only", "false")
conf.setProperty("storage.index.search.local-mode", "true")
g = com.thinkaurelius.titan.core.TitanFactory.open(conf)

if(!g.getIndexedKeys(com.tinkerpop.blueprints.Vertex.class).contains("coords")) 
	g.makeKey("coords").dataType(com.thinkaurelius.titan.core.attribute.Geoshape.class).indexed("search", com.tinkerpop.blueprints.Vertex.class).make();
g.addVertex(null).setProperty("coords", com.thinkaurelius.titan.core.attribute.Geoshape.point(1.234, 3.142))
g.commit()
g.V().has("coords", com.thinkaurelius.titan.core.attribute.Geo.WITHIN,	com.thinkaurelius.titan.core.attribute.Geoshape.circle(0, 0, 1000)).map()
g.shutdown()
//System.out.println("Done")

//  bin\rexster.bat -s -c config\rexster.xml
//g = com.thinkaurelius.titan.core.TitanFactory.open('C:/rexster/data/geo/graph')
//	g.makeKey("point").dataType(com.thinkaurelius.titan.core.attribute.Geoshape.class).indexed("search", com.tinkerpop.blueprints.Vertex.class).make()
		
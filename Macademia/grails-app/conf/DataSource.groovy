dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
    mongoDbUrl = "poliwiki.macalester.edu"
//    mongoDbUrl = '127.0.0.1'
    wpMongoDbName = "wikipediaReadOnly"
}

hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}


// environment specific settings
environments {
    /**
     * Test database environments (one per person)
     */
	populateTest {
		dataSource {
			dbCreate = "create" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:db/test_backup/db;shutdown=true"
            mongoDbName = "macademia_test_${System.getProperty('user.name')}"
            wpMongoDbName = "wikipediaReadOnlyTest"
        }
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:db/test/db;shutdown=true"
            mongoDbName = "macademia_test_${System.getProperty('user.name')}"
            wpMongoDbName = "wikipediaReadOnlyTest"
        }
	}
    /**
     * Development database environments (one per person)
     */
	populateDev {
		dataSource {
			dbCreate = "create" // one of 'create', 'create-drop','update'
            mongoDbName = "macademia_dev_${System.getProperty('user.name')}"
			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"
		}
	}
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"  //dev
            mongoDbName = "macademia_dev_${System.getProperty('user.name')}"
		}
	}
    /**
     * Production database environments (shared)
     */
	populateProd {
		dataSource {
			dbCreate = "create"
            pooled = false
            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_prod"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
            mongoDbName = "macademia_prod"
		}
	}
	production {
		dataSource {
			dbCreate = "update"
            pooled = false
            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_prod"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
            mongoDbName = "macademia_prod"
		}
	}
    /**
     * Benchmark database environments
     */
    benchmark {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'              
            mongoDbName = "benchmark"

            pooled = false
            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_bench"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
		}
	}
    populateBenchmark{
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
            mongoDbName = "benchmark"
            
            pooled = false

            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_bench"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect

		}
	}
}

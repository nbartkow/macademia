dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
//    driverClassName = "com.p6spy.engine.spy.P6SpyDriver" // use this driver to enable p6spy logging
	username = "sa"
	password = ""
//    mongoDbUrl = "poliwiki.macalester.edu"
    mongoDbUrl = '127.0.0.1'
//    wpMongoDbName = "wikipediaReadOnly"
    wpMongoDbName = "fromWikipedia"
}

hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}


// environment specific settings
environments {
	development {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
//			url = "jdbc:hsqldb:file:db/test/db;shutdown=true"   //test
//			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"  //dev
//            url = "jdbc:hsqldb:file:db/benchmark/db;shutdown=true"//benchmark
            mongoDbName = "macademia_prod"
            
            // For postgresql:
            pooled = false
            url = "jdbc:postgresql://localhost:5432/macademia_prod?charSet=UNICODE"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect // the difference is.
         /*
            pooled = false
            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_bench"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect   */

		}
	}
	populate {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
            mongoDbName = "dev_${System.getProperty('user.name')}"

            // For hsql:
			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"

            // For postgresql:
//            pooled = false
//            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_dev?charSet=UNICODE"
//            driverClassName = "org.postgresql.Driver"
//            username = "grails"
//            password = "grails"
//            dialect = net.sf.hibernate.dialect.PostgreSQLDialect // the difference is.

		}
	}
	populateTest {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:db/test_backup/db;shutdown=true"
            mongoDbName = "test_${System.getProperty('user.name')}"
        }
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:db/test/db;shutdown=true"
            mongoDbName = "test_${System.getProperty('user.name')}"
        }
	}
	production {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:prodDb;shutdown=true"
            mongoDbName = "production"
		}
	}
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

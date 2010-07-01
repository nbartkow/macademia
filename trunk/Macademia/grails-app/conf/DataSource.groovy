dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
	username = "sa"
	password = ""
    mongoDbUrl = "poliwiki.macalester.edu"
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
            mongoDbName = "dev"

            // for hsql
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
	populate {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
            monogDbName = "dev"

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
            mongoDbName = "test"
		}
	}
	test {
		dataSource {
			dbCreate = "update"
			url = "jdbc:hsqldb:file:db/test/db;shutdown=true"
            mongoDbName = "test"
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

            pooled = false
            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_bench"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
            
            mongoDbName = "benchmark"
		}
	}
    populateBenchmark{
		dataSource {
            pooled = false

            url = "jdbc:postgresql://poliwiki.macalester.edu:5432/macademia_bench"
            driverClassName = "org.postgresql.Driver"
            username = "grails"
            password = "grails"
            dialect = net.sf.hibernate.dialect.PostgreSQLDialect
            
			dbCreate = "update" // one of 'create', 'create-drop','update'
            monogDbName = "benchmark"
		}
	}
}

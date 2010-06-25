dataSource {
	pooled = true
	driverClassName = "org.hsqldb.jdbcDriver"
//    driverClassName = "com.p6spy.engine.spy.P6SpyDriver" // use this driver to enable p6spy logging
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
//			url = "jdbc:hsqldb:file:db/test/db;shutdown=true"
			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"
            mongoDbName = "dev"
		}
	}
	populate {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:db/dev/full/devDb;shutdown=true"
            mongoDbName = "dev"
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
			url = "jdbc:hsqldb:file:db/benchmark/db;shutdown=true"
            mongoDbName = "benchmark"
		}
	}
    populateBenchmark{
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:hsqldb:file:db/benchmark_backup/db;shutdown=true"
            monogDbName = "benchmark"
		}
	}
}

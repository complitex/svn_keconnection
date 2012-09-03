GLASSFISH_ASADMIN=/usr/glassfishv3/glassfish/bin/asadmin

echo ---------------------------------------------------
echo Local database and Realm
echo ---------------------------------------------------
echo
echo Register the JDBC connection pool
exec %GLASSFISH_ASADMIN% create-jdbc-connection-pool --datasourceclassname com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource --restype javax.sql.ConnectionPoolDataSource --property url=jdbc\:mysql\://localhost\:3306/keconnection:user=keconnection:password=keconnection:characterResultSets=utf8:characterEncoding=utf8:useUnicode=true keconnectionPool

echo
echo Create a JDBC resource with the specified JNDI name
exec %GLASSFISH_ASADMIN% create-jdbc-resource --connectionpoolid keconnectionPool jdbc/keconnectionResource

echo
echo Add the named authentication realm
exec %GLASSFISH_ASADMIN% create-auth-realm --classname com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property jaas-context=jdbcRealm:datasource-jndi=jdbc/keconnectionResource:user-table=user:user-name-column=login:password-column=password:group-table=usergroup:group-name-column=group_name:charset=UTF-8:digest-algorithm=MD5 keconnectionRealm
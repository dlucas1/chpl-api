package gov.healthit.chpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.DefaultKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.acls.AclPermissionCacheOptimizer;
import org.springframework.security.acls.AclPermissionEvaluator;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.ConsoleAuditLogger;
import org.springframework.security.acls.domain.DefaultPermissionGrantingStrategy;
import org.springframework.security.acls.domain.EhCacheBasedAclCache;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import com.google.common.collect.Lists;

import gov.healthit.chpl.caching.CacheInitializor;

@SuppressWarnings("deprecation")
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled=true)
@PropertySource("classpath:/environment.test.properties")
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass=true)
@EnableTransactionManagement
@ComponentScan(basePackages = {"gov.healthit.chpl.**"}, excludeFilters = {
		@ComponentScan.Filter(type = FilterType.ANNOTATION, value = Configuration.class),
		@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = CacheInitializor.class)
		})
public class CHPLTestConfig implements EnvironmentAware, CachingConfigurer {
	private Environment env;
	
	@Override
	public void setEnvironment(final Environment e) {
		this.env = e;
	}
	
	@Bean
    public GlobalAuthenticationConfigurerAdapter uniqueEnableGlobalAuthenticationAutowiredConfigurer(ApplicationContext context) {
        return new EnableGlobalAuthenticationAutowiredConfigurer(context);
    }

    private static class EnableGlobalAuthenticationAutowiredConfigurer extends GlobalAuthenticationConfigurerAdapter {
        private final ApplicationContext context;
        private static final Log logger = LogFactory.getLog(EnableGlobalAuthenticationAutowiredConfigurer.class);

        public EnableGlobalAuthenticationAutowiredConfigurer(ApplicationContext context) {
            this.context = context;
        }

        @Override
        public void init(AuthenticationManagerBuilder auth) {
            Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(EnableGlobalAuthentication.class);
            if(logger.isDebugEnabled()) {
                logger.debug("Eagerly initializing " + beansWithAnnotation);
            }
        }
    }
	
	@Bean
    @Override
    public CacheManager cacheManager() {

        List<CacheManager> cacheManagers = Lists.newArrayList();

        cacheManagers.add(ehCacheCacheManager());

        CompositeCacheManager cacheManager = new CompositeCacheManager();

        cacheManager.setCacheManagers(cacheManagers);
        cacheManager.setFallbackToNoOpCache(false);

        return cacheManager;
    }
	
 	@Bean
    @Override
    public KeyGenerator keyGenerator() {
        return new DefaultKeyGenerator();
    }

	@Override
	public CacheResolver cacheResolver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CacheErrorHandler errorHandler() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Bean
    public EhCacheCacheManager ehCacheCacheManager() {
        return new EhCacheCacheManager(ehCacheManagerFactoryBean().getObject());
    }

	@Bean
	public EhCacheManagerFactoryBean ehCacheManagerFactoryBean() {
		EhCacheManagerFactoryBean cmfb = new EhCacheManagerFactoryBean();
		cmfb.setConfigLocation(new ClassPathResource("ehCache-test.xml"));
		cmfb.setShared(true);
		return cmfb;
	}
	
	@Bean
	public EhCacheFactoryBean ehCacheFactoryBean(){
		EhCacheFactoryBean bean = new EhCacheFactoryBean();
		bean.setCacheManager(ehCacheManagerFactoryBean().getObject());
		return bean;
	}
	
	@Bean
	public EhCacheBasedAclCache aclCache(){
		EhCacheBasedAclCache bean = new EhCacheBasedAclCache(
				ehCacheFactoryBean().getObject(),
				defaultPermissionGrantingStrategy(), 
				aclAuthorizationStrategyImpl());
		return bean;
	}
	
	@Bean
	public DataSource dataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
    	ds.setServerName(env.getRequiredProperty("testDbServer"));
        ds.setUser(env.getRequiredProperty("testDbUser"));
        ds.setPassword(env.getRequiredProperty("testDbPassword"));
        ds.setDatabaseName(env.getRequiredProperty("testDbName"));
		return ds;
	}
	
	@Bean
	public DatabaseConfigBean databaseConfig() {
		DatabaseConfigBean bean = new DatabaseConfigBean();
		//we need this because dbunit deletes everything from the db to start with
		//and the table "user" is declared as "user" and not user (since user is a reserved word
		//and perhaps not the best choice of table name). The syntax "delete from user" is invalid
		//but "delete from "user"" is valid. we need the table names escaped.
		bean.setEscapePattern("\"?\"");
		
		//dbunit has limited support for postgres enum types so we have to tell
		//it about any enum type names here
		PostgresqlDataTypeFactory factory = new PostgresqlDataTypeFactory(){
			  public boolean isEnumType(String sqlTypeName) {
			    if(sqlTypeName.equalsIgnoreCase("attestation")){
			      return true;
			    }
			    return false;
			  }
			};
		bean.setDatatypeFactory(factory);
		return bean;
	}
	
	@Bean
	public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
		DatabaseDataSourceConnectionFactoryBean bean = new DatabaseDataSourceConnectionFactoryBean();
		bean.setDataSource(dataSource());
		bean.setDatabaseConfig(databaseConfig());
		return bean;
	}
	
	@Bean
	public org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean entityManagerFactory(){
		org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean bean = new org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean();
		bean.setDataSource(dataSource());
		bean.setPersistenceUnitName(env.getProperty("persistenceUnitName"));
		return bean;
	}
	
	@Bean
	public org.springframework.orm.jpa.JpaTransactionManager transactionManager(){
		org.springframework.orm.jpa.JpaTransactionManager bean = new org.springframework.orm.jpa.JpaTransactionManager();
		bean.setEntityManagerFactory(entityManagerFactory().getObject());
		return bean;
	}
	
	@Bean
	public org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor(){
		return new org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor();
	}
	
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public UserDetailsChecker userDetailsChecker(){
		return new AccountStatusUserDetailsChecker();
	}
	
	@Bean
	public MappingJackson2HttpMessageConverter jsonConverter(){
		
		MappingJackson2HttpMessageConverter bean = new MappingJackson2HttpMessageConverter();
		
		bean.setPrefixJson(false);
		
		List<MediaType> mediaTypes = new ArrayList<MediaType>();
		mediaTypes.add(MediaType.APPLICATION_JSON);
		
		bean.setSupportedMediaTypes(mediaTypes);
		
		return bean;
	}
	
	@Bean
	public ConsoleAuditLogger consoleAuditLogger(){
		ConsoleAuditLogger bean = new ConsoleAuditLogger();
		return bean;
	}
	
	@Bean
	public DefaultPermissionGrantingStrategy defaultPermissionGrantingStrategy(){
		DefaultPermissionGrantingStrategy bean = new DefaultPermissionGrantingStrategy(consoleAuditLogger());
		return bean;
	}
	
	@Bean
	public SimpleGrantedAuthority aclAdminGrantedAuthority(){
		SimpleGrantedAuthority bean = new SimpleGrantedAuthority("ROLE_ACL_ADMIN");
		return bean;
	}
	
	@Bean 
	public AclAuthorizationStrategyImpl aclAuthorizationStrategyImpl(){
		AclAuthorizationStrategyImpl bean = new AclAuthorizationStrategyImpl(aclAdminGrantedAuthority());
		return bean;
	}
	
	@Bean
	public SimpleGrantedAuthority roleAdminGrantedAuthority(){
		SimpleGrantedAuthority bean = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
		return bean;
	}
	
	@Bean 
	public AclAuthorizationStrategyImpl aclAuthorizationStrategyImplAdmin(){
		AclAuthorizationStrategyImpl bean = new AclAuthorizationStrategyImpl(roleAdminGrantedAuthority());
		return bean;
	}
	
	@Bean
	public BasicLookupStrategy lookupStrategy() throws Exception {
		
		DataSource datasource = (DataSource) dataSource();//.getObject();
		
		BasicLookupStrategy bean = new BasicLookupStrategy(
				datasource,
				aclCache(),
				aclAuthorizationStrategyImplAdmin(),
				consoleAuditLogger());
		return bean;
	}
	
	@Bean
	public JdbcMutableAclService mutableAclService() throws Exception{
		
		DataSource datasource = (DataSource) dataSource();
		
		JdbcMutableAclService bean = new JdbcMutableAclService(datasource, 
				lookupStrategy(), 
				aclCache());
		
		return bean;
	}
	
	@Bean
	public AclPermissionEvaluator permissionEvaluator() throws Exception{
		AclPermissionEvaluator bean = new AclPermissionEvaluator(mutableAclService());
		return bean;
	}
	
	@Bean
	public AclPermissionCacheOptimizer aclPermissionCacheOptimizer() throws Exception{
		AclPermissionCacheOptimizer bean = new AclPermissionCacheOptimizer(mutableAclService());
		return bean;
	}
	
	@Bean
	public DefaultMethodSecurityExpressionHandler expressionHandler() throws Exception {
		
		DefaultMethodSecurityExpressionHandler bean = new DefaultMethodSecurityExpressionHandler();
		bean.setPermissionEvaluator(permissionEvaluator());
		bean.setPermissionCacheOptimizer(aclPermissionCacheOptimizer());
		return bean;
	}
	
}

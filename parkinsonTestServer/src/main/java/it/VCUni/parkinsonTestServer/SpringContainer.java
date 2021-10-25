package it.VCUni.parkinsonTestServer;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.VCUni.parkinsonTestServer.service.AuthFilter;
import it.VCUni.parkinsonTestServer.settings.DbConnectionFile;
import it.VCUni.parkinsonTestServer.settings.IDbConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@ComponentScan("it.VCUni.parkinsonTestServer.persistence")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.users")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.tests")
@ComponentScan("it.VCUni.parkinsonTestServer.persistence.samples")
@ComponentScan("it.VCUni.parkinsonTestServer.handler")
@ComponentScan("it.VCUni.parkinsonTestServer.services")

/**
 * Container class that inizialize spring's component
*/
public class SpringContainer extends WebSecurityConfigurerAdapter{

	@Autowired
	ApplicationContext context;
	
	protected void configure(HttpSecurity http) throws Exception {
		http
		.addFilterBefore(context.getBean(AuthFilter.class), BasicAuthenticationFilter.class)
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().cors()
		.and().authorizeRequests()
			.antMatchers(HttpMethod.POST, "/services/userauth/**").permitAll()
			.antMatchers(HttpMethod.GET, "/*").permitAll()
			.antMatchers(HttpMethod.GET, "/assets/**").permitAll()
			.antMatchers(HttpMethod.GET, "/icons/**").permitAll()
			.anyRequest().authenticated()
		.and().csrf().disable();
		
	}
	
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
	   	return new BCryptPasswordEncoder();
	};
	
	
	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
	
	
	@Bean
	@Scope("prototype")
	public Logger getLogger(InjectionPoint target)
	{
		Class<?> targetClass = target.getMember().getDeclaringClass();
        Logger logger = LoggerFactory.getLogger(targetClass);
        return logger;
	}
	
	
	@Bean
	public IDbConnection dbSettings() throws JsonParseException, JsonMappingException, IOException{
		String log_dir = System.getenv("server_settings");
		String path = "connection.json";
		if(log_dir!=null) path = log_dir;
		IDbConnection conn = new ObjectMapper().readValue(new File(path), DbConnectionFile.class);
		
		try {
			File directory = new File(conn.getFilePath()); 
			if(!directory.exists()) throw new Exception();
			File directoryTest = new File(conn.getFilePath() + "raw-test-data");
		    boolean resTest = directoryTest.mkdir();
		    if(resTest) System.out.println("Directory raw-test-data has been created in " + conn.getFilePath() + ".");
		    else System.out.println("Directory raw-test-data already exists.");
		    File directoryTrain = new File(conn.getFilePath() + "UserAudioTrain");
		    boolean resTrain = directoryTrain.mkdir();
		    if(resTrain) System.out.println("Directory UserAudioTrain has been created in " + conn.getFilePath() + ".");
		    else System.out.println("Directory UserAudioTrain already exists.");
	    } catch(Exception e) {
	    	System.err.println("unable to create directory" + conn.getFilePath() + ", specify the new path in connection.json");
	        e.printStackTrace();
	        return null;
	    }
		return conn;
	}
	
	
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        configuration.setAllowedHeaders(Arrays.asList("*"));
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}

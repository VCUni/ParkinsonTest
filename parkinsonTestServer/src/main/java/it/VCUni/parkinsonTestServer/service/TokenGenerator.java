package it.VCUni.parkinsonTestServer.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import it.VCUni.parkinsonTestServer.exception.UserNotFoundException;
import it.VCUni.parkinsonTestServer.interfaces.UserAccess;

/**
 *  Service to generate and verify JWT
*/
@Component
public class TokenGenerator {
	public final String HEADER = "auth-tok";
	protected final String SECRET = "lS+3Nqke8AoC1/QElta8/Q"; 

	@Autowired
	UserAccess users;

	@Autowired
	UserDetailsService details;

	
    /**
     * @param userlogin
     * @return
     * @throws UserNotFoundException
     * @throws Exception
     */
    public String generateToken(String userlogin) throws UserNotFoundException, Exception {
		return Jwts.builder()
                .setIssuer("parkinsonTestServer")
                .setSubject(userlogin)
				.setId(String.valueOf(users.getHashedPassword(userlogin)))
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(7, ChronoUnit.DAYS)))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    
    /**
     * @param request
     * @return
     */
    public UserDetails validateToken(HttpServletRequest request) {
    	String token = request.getHeader(HEADER);
		if (token == null || token.isBlank())
			return null;

		try {
			final Claims body = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
			final String email = body.getSubject();

			if (Integer.parseInt(body.getId()) != users.getHashedPassword(email))
				return null;

			return details.loadUserByUsername(email);
		} catch (Exception ex) {
				return null;
		}
    }
    
}
package auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import auth.domain.KeyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JwtIntegrityTest {
	
	@Test
	public void checkJwt() {
		PrivateKey privateKey = KeyReader.getPrivateKey("keys/private-key.der");
		PublicKey publicKey = KeyReader.getPublicKey("keys/public-key.der");
		
		String id = "ejofwjfoawjfweorer";
		Instant expirationInstant = LocalDateTime.now(ZoneOffset.UTC).plusHours(3).atZone(ZoneOffset.UTC).toInstant();

		// use private key to sign
		String token = Jwts.builder().setSubject(id).
				setExpiration(Date.from(expirationInstant)).
				setIssuer("PlanOut").
				signWith(SignatureAlgorithm.RS512, privateKey).compact();
		
		// use public key to parse
		org.junit.Assert.assertEquals(Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().getSubject(), id);
		org.junit.Assert.assertEquals(Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().getIssuer(), "PlanOut");
		org.junit.Assert.assertEquals(Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().getExpiration().toString(), Date.from(expirationInstant).toString());
		org.junit.Assert.assertTrue(Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token).getBody().getExpiration().after(new Date()));
	}
}

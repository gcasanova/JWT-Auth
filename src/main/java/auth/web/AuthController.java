package auth.web;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.tuple.Pair;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import auth.domain.SecretGenerator;
import auth.domain.entities.SecretChallenge;
import auth.service.FacebookService;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Value("${keystore.password}")
	private String keystorePassword;

	@Value("${keystore.jwt.alias.name}")
	private String keystoreAliasName;

	@Value("${keystore.jwt.alias.password}")
	private String keystoreAliasPassword;

	@Autowired
	private RedisTemplate<String, String> redis;
	@Autowired
	private FacebookService facebookService;

	@RequestMapping(value = "/challenge", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> challenge(@RequestHeader(value = "FacebookId", required = false) Long facebookId, @RequestHeader(value = "FacebookToken", required = false) String facebookToken) {
		try {
			boolean isAuthenticated = false;
			if (facebookId != null && facebookToken != null) {
				if (this.facebookService.isTokenValid(facebookId, facebookToken)) {
					isAuthenticated = true;
				} else {
					return new ResponseEntity<Map<String, String>>(HttpStatus.FORBIDDEN);
				}
			}

			String userId = isAuthenticated ? String.valueOf(facebookId) : UUID.randomUUID().toString();
			Pair<String, String> pair = SecretGenerator.challenge();

			SecretChallenge secretChallenge = new SecretChallenge();
			secretChallenge.setChallenge(pair.getRight());
			secretChallenge.setAuthenticated(isAuthenticated);

			this.redis.opsForValue().set(userId, new JSONObject(secretChallenge).toString());
			this.redis.expire(userId, 30, TimeUnit.SECONDS);

			Map<String, String> result = new HashMap<String, String>();
			result.put("challenge", pair.getLeft());
			result.put("id", userId);
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.ACCEPTED);
		} catch (Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("error", e.getMessage());
			return new ResponseEntity<Map<String, String>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ResponseEntity<Map<String, String>> create(@RequestHeader("Secret") String secret, @RequestHeader("id") String id) {
		try {
			SecretChallenge secretChallenge = new ObjectMapper().readValue(this.redis.opsForValue().get(id), SecretChallenge.class);
			if (secretChallenge.getChallenge().equals(secret)) {
				KeyStore ks = KeyStore.getInstance("JKS");
				ks.load(new ClassPathResource("keystore.jks").getInputStream(), keystorePassword.toCharArray());
				PrivateKeyEntry entry = (KeyStore.PrivateKeyEntry) ks.getEntry(keystoreAliasName, new KeyStore.PasswordProtection(keystoreAliasPassword.toCharArray()));
				long expiration = secretChallenge.isAuthenticated() ? 6 : 3;

				String token = Jwts.builder().setSubject("id").
						setExpiration(Date.from(LocalDateTime.now(ZoneOffset.UTC).plusHours(expiration).atZone(ZoneOffset.UTC).toInstant())).
						setIssuer("PlanOut").
						signWith(SignatureAlgorithm.RS512, entry.getPrivateKey()).compact();

				Map<String, String> result = new HashMap<String, String>();
				result.put("token", token);
				return new ResponseEntity<Map<String, String>>(result, HttpStatus.ACCEPTED);
			}
			return new ResponseEntity<Map<String, String>>(HttpStatus.FORBIDDEN);
		} catch (Exception e) {
			Map<String, String> result = new HashMap<String, String>();
			result.put("error", e.getMessage());

			return new ResponseEntity<Map<String, String>>(result, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

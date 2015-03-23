package auth;

import java.security.PrivateKey;
import java.security.PublicKey;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import auth.Application;
import auth.domain.KeyReader;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class KeyReaderTest {
	
	@Test
	public void getPrivateKeyTest() {
		PrivateKey key = KeyReader.getPrivateKey("keys/private-key.der");
		org.junit.Assert.assertNotNull(key);
	}
	
	@Test
	public void getPublicKeyTest() {
		PublicKey key = KeyReader.getPublicKey("keys/public-key.der");
		org.junit.Assert.assertNotNull(key);
	}
}

package auth.domain;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;

public class SecretGenerator {

	@Value("secret.salt")
	private static String salt;

	public static String secret() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update((LocalDate.now(ZoneOffset.UTC).toString() + salt).getBytes());
		return new String(md.digest(), "UTF-8");
	}

	public static Pair<String, String> challenge() throws NoSuchAlgorithmException, UnsupportedEncodingException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		String challenge = UUID.randomUUID().toString();
		Cipher c = Cipher.getInstance("DES");
		c.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(Arrays.copyOfRange(secret().getBytes(), 0, 8), "DES"));
		String result = new String(Base64.encodeBase64(new String(c.doFinal(challenge.getBytes()), "UTF-8").getBytes()), "UTF-8").substring(0, 36);
		return new ImmutablePair<String, String>(challenge, result);
	}
}

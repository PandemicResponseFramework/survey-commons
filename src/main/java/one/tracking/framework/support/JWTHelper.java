/**
 *
 */
package one.tracking.framework.support;

import java.text.ParseException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * @author Marko Voß
 *
 */
@Component
public class JWTHelper {

  public static final String CLAIM_ROLES = "roles";

  @Value("${app.token.secret}")
  private String tokenSecret;

  @Value("${app.token.issuer}")
  private String issuer;

  public String createJWT(final String subject, final long expiration, final String... roles) {

    final Claims claims = Jwts.claims().setSubject(subject);
    claims.setExpiration(Date.from(Instant.now().plusSeconds(expiration)));
    claims.setIssuedAt(Date.from(Instant.now()));
    claims.setIssuer(this.issuer);

    if (roles != null && roles.length > 0)
      claims.put(CLAIM_ROLES, Arrays.asList(roles));


    return Jwts.builder().setSubject(subject)
        .signWith(SignatureAlgorithm.HS512, this.tokenSecret)
        .setClaims(claims)
        .compact();
  }

  public Claims decodeJWT(final String token) {

    return Jwts.parser()
        .setSigningKey(this.tokenSecret)
        .requireIssuer(this.issuer)
        .parseClaimsJws(token)
        .getBody();
  }

  @SuppressWarnings("unchecked")
  public List<String> getRoles(final Claims claims) {

    Assert.notNull(claims, "Claims must not be null.");
    return claims.get(CLAIM_ROLES, List.class);
  }

  public String createJWE(final String encodedSecret, final String payload) throws KeyLengthException, JOSEException {

    final byte[] decodedKey = Base64.getDecoder().decode(encodedSecret);
    final SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

    final JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, EncryptionMethod.A128GCM);
    final Payload jwePayload = new Payload(payload);
    final JWEObject jweObject = new JWEObject(header, jwePayload);
    jweObject.encrypt(new DirectEncrypter(key));

    return jweObject.serialize();
  }

  public String decodeJWE(final String encodedSecret, final String jweString)
      throws ParseException, KeyLengthException, JOSEException {

    final byte[] decodedKey = Base64.getDecoder().decode(encodedSecret);
    final SecretKey key = new SecretKeySpec(decodedKey, 0, decodedKey.length, "AES");

    final JWEObject jweObject = JWEObject.parse(jweString);
    jweObject.decrypt(new DirectDecrypter(key));
    return jweObject.getPayload().toString();
  }
}

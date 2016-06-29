package com.ppi.api.security;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicReference;
import com.hazelcast.core.IMap;
import com.ppi.api.model.NestupUser;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Component
public class TokenStore {
    @Autowired
    private HazelcastInstance hazelcastInstance;
    private RsaJsonWebKey rsaJsonWebKey;
    private IMap<String, String> jwtMap;
    private JwtConsumer jwtConsumer;

    @PostConstruct
    public void init() throws Exception {
        IAtomicReference<RsaJsonWebKey> atomicReference = hazelcastInstance.getAtomicReference("token-key");
        jwtMap = hazelcastInstance.getMap("jwt");
        while (atomicReference.get() == null) {
            // Generate an RSA key pair, which will be used for signing and verification of the JWT, wrapped in a JWK
            RsaJsonWebKey jsonWebKey = RsaJwkGenerator.generateJwk(2048);

            // Give the JWK a Key ID (kid), which is just the polite thing to do
            jsonWebKey.setKeyId("nestup-key");
            if (atomicReference.compareAndSet(null, jsonWebKey)) {
                rsaJsonWebKey = jsonWebKey;
                break;
            }
        }
        if (rsaJsonWebKey == null) rsaJsonWebKey = atomicReference.get();
        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        jwtConsumer =  new JwtConsumerBuilder()
            .setRequireExpirationTime() // the JWT must have an expiration time
            .setMaxFutureValidityInMinutes(300) // but the  expiration time can't be too crazy
            .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
            .setRequireSubject() // the JWT must have a subject claim
            .setExpectedIssuer("Nestup") // whom the JWT needs to have been issued by
            .setExpectedAudience("nestup.com") // to whom the JWT is intended for
            .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
            .build(); // create the JwtConsumer instance
    }

    public String generateToken(NestupUser user) throws Exception {
        String token = buildToken(user);
        jwtMap.put(token, user.getEmail());
        return token;
    }

    public String findUserEmailForToken(String jwt) throws Exception {
        JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
        Assert.notNull(jwtClaims);
        String email = jwtMap.get(jwt);
        Assert.notNull(email);
        return email;
    }

    private String buildToken(NestupUser user) throws JoseException {
        // Create the Claims, which will be the content of the JWT
        JwtClaims claims = new JwtClaims();
        claims.setIssuer("Nestup");  // who creates the token and signs it
        claims.setAudience("nestup.com"); // to whom the token is intended to be sent
        claims.setExpirationTimeMinutesInTheFuture(20); // time when the token will expire (10 minutes from now)
        claims.setGeneratedJwtId(); // a unique identifier for the token
        claims.setIssuedAtToNow();  // when the token was issued/created (now)
        claims.setNotBeforeMinutesInThePast(2); // time before which the token is not yet valid (2 minutes ago)
        claims.setSubject(user.getEmail()); // the subject/principal is whom the token is about
//        List<String> groups = Arrays.asList("group-one", "other-group", "group-three");
//        claims.setStringListClaim("groups", groups); // multi-valued claims work too and will end up as a JSON array

        // A JWT is a JWS and/or a JWE with JSON claims as the payload.
        // In this example it is a JWS so we create a JsonWebSignature object.
        JsonWebSignature jws = new JsonWebSignature();

        // The payload of the JWS is JSON content of the JWT Claims
        jws.setPayload(claims.toJson());

        // The JWT is signed using the private key
        jws.setKey(rsaJsonWebKey.getPrivateKey());

        // Set the Key ID (kid) header because it's just the polite thing to do.
        // We only have one key in this example but a using a Key ID helps
        // facilitate a smooth key rollover process
        jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());

        // Set the signature algorithm on the JWT/JWS that will integrity protect the claims
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        // Sign the JWS and produce the compact serialization or the complete JWT/JWS
        // representation, which is a string consisting of three dot ('.') separated
        // base64url-encoded parts in the form Header.Payload.Signature
        // If you wanted to encrypt it, you can simply set this jwt as the payload
        // of a JsonWebEncryption object and set the cty (Content Type) header to "jwt".
        return jws.getCompactSerialization();
    }
}

#!/bin/bash

HOST="vpn.ba-leipzig.de"
PORT="443"

CERTIFICATE_FILE="ba.cert"
PROVIDER_FILE="./bcprov-jdk15on-157.jar"
KEYSTORE_FILE="../app/src/main/res/raw/keystore.bks"
# password must be equal to mystore_password in res/values/keys.xml
KEYSTORE_PASSWORD="(FGW4z9dSf3[hE"


rm "$CERTIFICATE_FILE"
echo Downloading ssl certificate from "$HOST":"$PORT" to "$CERTIFICATE_FILE"
echo -n | openssl s_client -connect "$HOST":"$PORT" | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > "$CERTIFICATE_FILE"

echo Generating keystore in "$KEYSTORE_FILE"
rm "$KEYSTORE_FILE"
keytool -importcert -v -trustcacerts -file "$CERTIFICATE_FILE" -alias IntermediateCA -keystore "$KEYSTORE_FILE" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "$PROVIDER_FILE" -storetype BKS -storepass "$KEYSTORE_PASSWORD"

echo Verifying keystore
keytool -list -keystore "$KEYSTORE_FILE" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "$PROVIDER_FILE" -storetype BKS -storepass "$KEYSTORE_PASSWORD"


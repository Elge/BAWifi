#!/bin/bash

HOST="vpn.ba-leipzig.de"
PORTNUMBER="443"

CERTFILE="ba.cert"
KEYSTORE="../app/src/main/res/raw/keystore.bks"
# passwd must be equal to mystore_password in res/values/keys.xml
KEYSTORE_PASSWD="(FGW4z9dSf3[hE"
PROVIDER="./bcprov-jdk15on-157.jar"


rm $CERTFILE
echo Downloading ssl certificate from $HOST:$PORTNUMBER to $CERTFILE
echo -n | openssl s_client -connect $HOST:$PORTNUMBER | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > $CERTFILE

echo Generating keystore in $KEYSTORE
rm $KEYSTORE
keytool -importcert -v -trustcacerts -file "$CERTFILE" -alias IntermediateCA -keystore "$KEYSTORE" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "$PROVIDER" -storetype BKS -storepass "$KEYSTORE_PASSWD"

echo Verifying keystore
keytool -list -keystore "$KEYSTORE" -provider org.bouncycastle.jce.provider.BouncyCastleProvider -providerpath "$PROVIDER" -storetype BKS -storepass "$KEYSTORE_PASSWD"


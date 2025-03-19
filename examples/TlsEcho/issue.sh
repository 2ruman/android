#!/bin/bash
#
# [v] Download BC Provider(bcprov-jdk18on-1.80.jar) at https://www.bouncycastle.org/download/bouncy-castle-java/#latest
#

# Parameters into keytool
SERVER_KS_NAME="server_ks"
CLIENT_KS_NAME="client_ks"
SERVER_ALIAS="server_key"
CLIENT_ALIAS="server_crt"
SERVER_KS_PWD="spassword"
CLIENT_KS_PWD="cpassword"
DNAME="CN=Server, OU=Truman, O=Truman, L=Seoul, ST=Seoul, C=KR"
BC_PROV="org.bouncycastle.jce.provider.BouncyCastleProvider"
BC_PROV_PATH="dist/bcprov-jdk18on-1.80.jar"
CDIR="certs"

# Clear all
rm ${CDIR}${SERVER_KS_NAME}.jks ${CDIR}/${SERVER_KS_NAME}.crt ${CDIR}/${SERVER_KS_NAME}.bks ${CDIR}/${CLIENT_KS_NAME}.jks ${CDIR}/${CLIENT_KS_NAME}.bks

# Generate server keys in JKS
keytool -genkeypair -keystore ${CDIR}/${SERVER_KS_NAME}.jks -storepass ${SERVER_KS_PWD} -alias ${SERVER_ALIAS} -keyalg RSA -dname "${DNAME}"

# Export server certificate in raw
keytool -exportcert -keystore ${CDIR}/${SERVER_KS_NAME}.jks -storepass ${SERVER_KS_PWD} -alias ${SERVER_ALIAS} -file ${CDIR}/${SERVER_KS_NAME}.crt

# Import server certificate into JKS
keytool -import -keystore ${CDIR}/${CLIENT_KS_NAME}.jks -storepass ${CLIENT_KS_PWD} -alias ${CLIENT_ALIAS} -file ${CDIR}/${SERVER_KS_NAME}.crt

# Convert server's JKS to BKS
keytool -importkeystore -srckeystore ${CDIR}/${SERVER_KS_NAME}.jks -srcstoretype PKCS12 -srcstorepass ${SERVER_KS_PWD} -destkeystore ${CDIR}/${SERVER_KS_NAME}.bks -deststoretype BKS -deststorepass ${SERVER_KS_PWD} -provider ${BC_PROV} -providerpath ${BC_PROV_PATH}

# Convert client's JKS to BKS
keytool -importkeystore -srckeystore ${CDIR}/${CLIENT_KS_NAME}.jks -srcstoretype PKCS12 -srcstorepass ${CLIENT_KS_PWD} -destkeystore ${CDIR}/${CLIENT_KS_NAME}.bks -deststoretype BKS -deststorepass ${CLIENT_KS_PWD} -provider ${BC_PROV} -providerpath ${BC_PROV_PATH}

# Copy client's BKS to resource path
cp ${CDIR}/${CLIENT_KS_NAME}.bks client/src/main/res/raw/

# Verify server's BKS
echo "--------------------------------------------------------------------------------"
echo -e "\n\n\tVerifying Server's Keystore: ${CDIR}/${SERVER_KS_NAME}.bks\n\n"
echo "--------------------------------------------------------------------------------"
keytool -list -v -keystore ${CDIR}/${SERVER_KS_NAME}.bks -storetype BKS -storepass ${SERVER_KS_PWD} -provider ${BC_PROV} -providerpath ${BC_PROV_PATH}

# Verify client's BKS
echo "--------------------------------------------------------------------------------"
echo -e "\n\n\tVerifying Client's Keystore: ${CDIR}/${CLIENT_KS_NAME}.bks\n\n"
echo "--------------------------------------------------------------------------------"
keytool -list -v -keystore ${CDIR}/${CLIENT_KS_NAME}.bks -storetype BKS -storepass ${CLIENT_KS_PWD} -provider ${BC_PROV} -providerpath ${BC_PROV_PATH}

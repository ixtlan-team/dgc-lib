/*-
 * ---license-start
 * EU Digital Green Certificate Gateway Service / dgc-lib
 * ---
 * Copyright (C) 2021 T-Systems International GmbH and all other contributors
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ---license-end
 */

package eu.europa.ec.dgc.gateway.connector.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty("dgc.gateway.connector.enabled")
@Slf4j
public class DgcGatewayConnectorKeystore {

    private final DgcGatewayConnectorConfigProperties dgcConfigProperties;

    /**
     * Creates a KeyStore instance with keys for DGC TrustAnchor.
     *
     * @return KeyStore Instance
     * @throws KeyStoreException        if no implementation for the specified type found
     * @throws IOException              if there is an I/O or format problem with the keystore data
     * @throws CertificateException     if any of the certificates in the keystore could not be loaded
     * @throws NoSuchAlgorithmException if the algorithm used to check the integrity of the keystore cannot be found
     */
    @Bean
    @Qualifier("trustAnchor")
    public KeyStore trustAnchorKeyStore() throws KeyStoreException, IOException,
        CertificateException, NoSuchAlgorithmException {
        KeyStore keyStore = KeyStore.getInstance("JKS");

        loadKeyStore(
            keyStore,
            dgcConfigProperties.getTrustAnchor().getPath(),
            dgcConfigProperties.getTrustAnchor().getPassword());

        return keyStore;
    }

    private void loadKeyStore(KeyStore keyStore, String path, char[] password)
        throws CertificateException, NoSuchAlgorithmException, IOException {

        try (InputStream fileStream = new FileInputStream(ResourceUtils.getFile(path))) {

            if (fileStream.available() > 0) {
                keyStore.load(fileStream, password);
                fileStream.close();
            } else {
                keyStore.load(null);
                log.info("Could not find Keystore {}", path);
            }
        }
    }
}

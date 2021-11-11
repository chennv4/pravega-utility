package com.dell.sdp.pravega.service;

import com.dell.sdp.pravega.common.DataGenerator;
import com.dell.sdp.pravega.common.PravegaProperties;
import com.dell.sdp.pravega.common.serialization.JsonNodeSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import io.pravega.client.ClientConfig;
import io.pravega.client.EventStreamClientFactory;
import io.pravega.client.admin.ReaderGroupManager;
import io.pravega.client.admin.StreamManager;
import io.pravega.client.stream.*;
import io.pravega.shared.security.auth.Credentials;
import io.pravega.shared.security.auth.DefaultCredentials;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.File;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Configuration
public class RGPravegaService implements PravegaService {

	 // Logger initialization
    private static final Logger LOG = LoggerFactory.getLogger(RGPravegaService.class);

    @Autowired
    PravegaProperties pravegaProperties;

    @Value("${pravega.controller_uri}")
    private String uri;

    @Value("${pravega.scope}")
    private String scope;

    @Value("${pravega.stream}")
    private String stream;

    @Value("${pravega.client_auth_method}")
    private String client_auth_method;

    @Value("${pravega.client_auth_loadDynamic}")
    private String client_auth_loadDynamic;

    @Value("${pravega.keycloak_service_account}")
    private String keycloak_service_account;

    @Value("${pravega.target_events_per_sec}")
    private int target_events_per_sec;

    @Value("${pravega.scale_factor}")
    private int scale_factor;

    @Autowired
    private ResourceLoader resourceLoader;


    @Override
    public void write() {

    }

    @Override
    public void listRG() {
        LOG.info("RGPravegaService START");

        try {
            LOG.info("@@@@@@@@@@@@@ URI  @@@@@@@@@@@@@  "+uri);
            URI controllerURI = URI.create(uri);
            //Credentials cred = new DefaultCredentials("QE58PVjASEQQhSC9bW4Nkcz3s8SvrH6aUfi850jgVSI=","admin");
            Credentials cred = new DefaultCredentials("1111_aaaa","admin");

            ClientConfig clientConfig = ClientConfig.builder()
                    .credentials(cred)
                    .controllerURI(controllerURI)
                    //.trustStore(certAsFile.getPath())
                    //.validateHostName(false)
                    .build();

            String readerGroupName = "scaleGroup";
            ReaderGroupManager rgManager = ReaderGroupManager.withScope(scope, clientConfig);
            ReaderGroup rg = rgManager.getReaderGroup(readerGroupName);
            Set<String> readers = rg.getOnlineReaders();

            LOG.info("@@@@@@@@@@@@@ READERS  @@@@@@@@@@@@@  " + readers);

            Iterator iterate_value = readers.iterator();

            while (iterate_value.hasNext()) {
                String readerId = (String)iterate_value.next();
                LOG.info("@@@@@@@@@@@@@ READER ID @@@@@@@@@@@@@  " + readerId);
                if(!(readerId.equalsIgnoreCase("a5f69d48-3fb5-4aa3-96df-020fbf2f7bb2")))
                {
                    rg.readerOffline(readerId, null);
                }

            }

            /*String readerId = getStringFromUser("Input reader id: ");
            rg.readerOffline(readerId, null);*/


        } catch (Exception e) {
            LOG.error("@@@@@@@@@@@@@ ERROR  @@@@@@@@@@@@@  " + e.getMessage());
        }
    }
}

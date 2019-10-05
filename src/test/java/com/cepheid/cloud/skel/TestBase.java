package com.cepheid.cloud.skel;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = { SkelApplication.class })
public class TestBase {
	private String mServerUri;

	protected Client mClient;

	@Value("${server.port}")
	protected int mPort;

	@PostConstruct
	public void postConstruct() {
		mServerUri = "http://localhost:" + mPort;
		mClient = createClient();
	}

	// Create the normal get builder.
	public Builder getBuilder(String path, Object... values) {
		URI uri = UriBuilder.fromUri(mServerUri + path).build(values);

		WebTarget webTarget = mClient.target(uri);
		webTarget = webTarget.register(MultiPartFeature.class);

		Builder builder = webTarget.request(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		return builder;
	}

	// Create the query builder which supports to add the query parameters.
	public Builder getQueryBuilder(String path, Properties query_params, Object... values) {
		UriBuilder uriBuilder = UriBuilder.fromUri(mServerUri + path);
		// Add all the query parameters into the uri.
		query_params.forEach((name, value) -> {
			uriBuilder.queryParam(name.toString(), value.toString());
		});
		// Build the final uri and then send HTTP request to target server.
		URI uri = uriBuilder.build(values);
		// System.out.println(uri.toString());
		WebTarget webTarget = mClient.target(uri);
		webTarget = webTarget.register(MultiPartFeature.class);

		Builder builder = webTarget.request(MediaType.APPLICATION_JSON_TYPE, MediaType.APPLICATION_OCTET_STREAM_TYPE);

		return builder;
	}

	protected Client createClient() {
		ClientBuilder clientBuilder = ClientBuilder.newBuilder();
		return clientBuilder.build();
	}

}

package org.springframework.samples.petclinic.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;
import org.bson.Document;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MongoDB {

	private static MongoDB mongoDBInstance;

	private static final String URI = "mongodb+srv://admin:admin@ecosystemaccountcluster.3wsyrgk.mongodb.net/?retryWrites=true&w=majority&appName=EcosystemAccountClusterTesis";

	private static final String DATABASE = "ecosystem_accounts";

	private static final String COLLECTION = "ecosystem_accounts_data";

	private MongoClient mongoClient;

	private ArrayList<Document> documents = new ArrayList<>();

	private ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();

	private MongoClientSettings settings = MongoClientSettings.builder()
		.applyConnectionString(new ConnectionString(URI))
		.timeout(60, TimeUnit.SECONDS)
		.serverApi(serverApi)
		.build();

	public static MongoDB getMongoDB() {
		if (mongoDBInstance == null) {
			mongoDBInstance = new MongoDB();
		}

		return mongoDBInstance;
	}

	public void report(Map<String, String> data, String metric, Double metricValue) {

		Document document = new Document();
		document.append(metric, metricValue);
		document.append("application", System.getenv("APPLICATION"));
		document.append("platform", System.getenv("PLATFORM"));
		data.forEach((key, value) -> document.append(key, value));

		this.documents.add(document);

		System.out.println("Documents size: " + documents.size());

	}

	public void storeDocuments() {
		System.out.printf("Total elements in document array: %d", documents.size());
		this.getClient().getDatabase(DATABASE).getCollection(COLLECTION).insertMany(documents);
	}

	private MongoClient getClient() {

		if (this.mongoClient == null) {
			this.mongoClient = MongoClients.create(settings);
			this.mongoClient.getDatabase(DATABASE).getCollection(COLLECTION);
		}
		return this.mongoClient;
	}

	private String sha256hex(String originalString) {
		return Hashing.sha256().hashString(originalString, StandardCharsets.UTF_8).toString();
	}

}

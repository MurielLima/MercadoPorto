package config;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import repository.ClienteRepository;
import utility.Dados;

@Configuration
@EnableMongoRepositories(basePackageClasses = ClienteRepository.class)

public class DBConfig extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "mercado";
    }

    @Override
    public Mongo mongo() throws Exception {
        Dados dados = new Dados("config.txt");

        MongoClient client;
        client = new MongoClient(dados.ler(), 27017);

        return client;

    }
}

package repository;

import model.Historico;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Muriel
 */
public interface HistoricoRepository extends MongoRepository<Historico, String> {

}

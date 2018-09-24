package repository;

import java.util.List;
import model.Cliente;
import model.Historico;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Muriel
 */
public interface HistoricoRepository extends MongoRepository<Historico, String> {

   
}

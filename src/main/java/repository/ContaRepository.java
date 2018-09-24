package repository;

import java.util.List;
import model.Cliente;
import model.Conta;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Muriel
 */
public interface ContaRepository extends MongoRepository<Conta, String> {
public int countByIdCliente(Cliente idCliente);
public List<Conta> findByIdCliente(Cliente idCliente);
   
}

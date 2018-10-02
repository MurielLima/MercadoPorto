package repository;

import java.util.List;
import model.Cliente;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author Muriel
 */
public interface ClienteRepository extends MongoRepository<Cliente, String> {

    public List<Cliente> findByNomeLikeIgnoreCase(String nome);

    public Cliente findByNome(String nome);

    public List findByNomeLikeIgnoreCaseOrSobrenomeLikeIgnoreCaseOrDocumentoLikeIgnoreCase(String nome, String sobrenome, String documento);

    public Cliente findById(String id);

    public Cliente findById(Cliente id);
}

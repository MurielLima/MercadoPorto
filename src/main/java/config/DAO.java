package config;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import repository.*;


public class DAO {

    private static final AnnotationConfigApplicationContext ctx
            = new AnnotationConfigApplicationContext(DBConfig.class);
      public static ClienteRepository clienteRepository = ctx.getBean(ClienteRepository.class);
 public static ContaRepository contaRepository = ctx.getBean(ContaRepository.class);
  public static HistoricoRepository historicoRepository = ctx.getBean(HistoricoRepository.class);
}

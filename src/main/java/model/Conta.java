/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static config.Config.df;
import static config.DAO.clienteRepository;
import static config.DAO.historicoRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Muriel Classe do tipo entidade do banco MONGODB Responsavél por
 * armazenar os dados de cada conta vinculando os clientes
 *
 */
@Document
public class Conta {

    /**
     * @var id: responsavel pela identificação única da classe
     * @var idCliente: responsável por identificar o cliente proprietario desta
     * conta, chave estrangeira
     * @var observacao: responsavel por armazenar informações sobre a conta
     * @var data: responsavel por armazenar a data de cada conta adicionada
     * @var valor: responsavel por armazenar o valor da conta,(Tipo BigDecimal
     * para fazer arredondamento de valores)
     */
    @Id
    private String id;
    @DBRef
    private Cliente idCliente;
    private LocalDate data;
    private String observacao;
    private BigDecimal valor;

    public Conta() {
    }

    public Conta(Cliente idCliente, double valor, String observacao, LocalDate date) {
        this.setIdCliente(idCliente);
        this.data = date;
        this.setValor(valor);
        this.setObservacao(observacao);
    }

    public Conta(Cliente idCliente, double valor) {
        this.setIdCliente(idCliente);
        this.setValor(valor);
    }

    public String getNomeCliente() {
        return idCliente.getNome();
    }

    public Cliente getIdCliente() {
        return idCliente;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    /**
     *
     * @return Valor da conta formatado com ',' do tipo String
     */
    public String getValorFormat() {
        return (String.valueOf(valor)).replace(".", ",");
    }

    public BigDecimal getValor() {
        return valor;
    }

    /**
     * Transforma o double valor em um valor BigDecimal que será armazenado na
     * classe. Com arredondamento de 2 casas decimais
     *
     * @param valor
     */
    public void setValor(double valor) {
        if (valor > 0.0) {
            this.valor = (new BigDecimal(valor).setScale(2, RoundingMode.HALF_EVEN));
        } else {
            this.valor = BigDecimal.ZERO;
        }
        setHistorico();
    }

    /**
     *
     * @return String data formatada DD/MM/YYYY
     */
    public String getDataFormat() {
        if (data != null) {
            return data.format(df);
        } else {
            return "";
        }
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    /**
     * A cada alteração de valor da conta, é armazenado um histórico no banco de
     * dados para tornar a transação mais segura
     */
    private void setHistorico() {
        System.out.println(idCliente.getDataCadastro());
        String mensagem = "[" + LocalDateTime.now() + "]:" + "COMPRA DE:" + this.observacao + ", FOI ALTERADO O VALOR PARA " + getValor();
        Historico log = new Historico(clienteRepository.findByNome(idCliente.getNome()), mensagem);
        historicoRepository.insert(log);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Conta other = (Conta) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id;
    }

}

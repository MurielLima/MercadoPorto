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
import java.util.logging.Logger;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Muriel
 */
@Document
public class Conta {

    @Id
    private String id;

    @DBRef
    private Cliente idCliente;

    private LocalDate data;
    private String Observacao;
    private double valor;

    public Conta() {
    }

    public Conta(Cliente idCliente, double valor,String observacao, LocalDate date) {
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
        return Observacao;
    }

    public void setObservacao(String Observacao) {
        this.Observacao = Observacao;
    }

    public void setIdCliente(Cliente idCliente) {
        this.idCliente = idCliente;
    }

    public String getValorFormat() {
        return (String.valueOf(valor)).replace(".",",");
    }
    public double getValor() {
        return valor;
    }
    public void setValor(double valor) {
        if (valor > 0.0) {
            this.valor = (new BigDecimal(valor).setScale(2, RoundingMode.HALF_EVEN)).doubleValue();
        } else {
            this.valor = 0.0;
        }
        setHistorico();
    }

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

    private void setHistorico() {
        System.out.println(idCliente.getDataCadastro());
        String mensagem = "[" + LocalDateTime.now() + "]:" + " FOI ALTERADO O VALOR PARA " + getValor();
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import static config.DAO.contaRepository;
import java.time.LocalDate;
import java.util.Objects;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author Muriel Classe do tipo entidade do banco MONGODB Responsavél por
 * armazenar os dados de cada cliente
 */
@Document
public class Cliente {

    /**
     * @var id : reponsavel pela identificação da classe, do tipo chave primária
     * @var nome: reponsavel por armazenar nome do cliente
     * @var sobrenome: responsavel por armazenar sobrenome.\r\n Existem dois
     * campos para nome e sobrenome por questões de ser obrigatorio o sobrenome
     * @var documento: um numero de documento que não foi previamente
     * especificado
     * @var dataCadastro: responsável por armazenar a data de cadastro de cada
     * cliente
     */
    @Id
    private String id;
    private String nome;
    private String sobrenome;
    @Indexed(unique = true)
    private String documento;
    private LocalDate dataCadastro;

    public Cliente() {
    }

    public Cliente(String nome, String sobrenome, LocalDate dataCadastro) {
        setNome(nome);
        setSobrenome(sobrenome);
        setDocumento(documento);
        setDataCadastro(dataCadastro);
    }

    public Cliente(String nome, String sobrenome) {
        setNome(nome);
        setSobrenome(sobrenome);
        setDocumento(documento);
    }

    public Cliente(String nome, String sobrenome, String documento) {
        setNome(nome);
        setSobrenome(sobrenome);
        setDocumento(documento);
    }

    public Cliente(String nome, String sobrenome, String documento, LocalDate dataCadastro) {
        setNome(nome);
        setSobrenome(sobrenome);
        setDocumento(documento);
        setDataCadastro(dataCadastro);
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome.toUpperCase();
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome.toUpperCase();
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    /**
     *
     * @return Quantidade de contas cadastradas para o this.nome cliente
     */
    public int getQtdeContas() {
        return contaRepository.countByIdCliente(this);
    }

    /*
    * @return : Gera um id unico para a classe
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.id);
        return hash;
    }

    /**
     *
     * @param obj
     * @return compara classe this == other
     */
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
        final Cliente other = (Cliente) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return nome + " " + sobrenome;
    }

}

package br.edu.ifpb.laboratorio.modelo.entidade;

import java.util.Objects;

/**
 * Entidade que representa uma pessoa (estudante ou servidor) cadastrada no sistema.
 * Atende aos requisitos 4.1 (encapsulamento, atributos privados, validacao).
 */
public class Pessoa {

    private final String matricula;
    private String nome;

    public Pessoa(String matricula, String nome) {
        if (matricula == null || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("A matricula da pessoa nao pode ser nula ou vazia.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da pessoa nao pode ser nulo ou vazio.");
        }
        this.matricula = matricula.trim();
        this.nome = nome.trim();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome da pessoa nao pode ser nulo ou vazio.");
        }
        this.nome = nome.trim();
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", this.nome, this.matricula);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pessoa pessoa)) return false;
        return Objects.equals(matricula, pessoa.matricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matricula);
    }
}

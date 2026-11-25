package br.edu.ifpb.laboratorio.persistencia;

import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementacao de persistencia do cadastro de pessoas utilizando arquivo de texto CSV com BufferedReader.
 * Atende aos requisitos 3.3 e 4.6.
 */
public class RepositorioPessoasArquivo implements IRepositorioPessoas {

    private final File arquivo;

    public RepositorioPessoasArquivo(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.trim().isEmpty()) {
            throw new IllegalArgumentException("O caminho do arquivo de pessoas nao pode ser nulo ou vazio.");
        }
        this.arquivo = new File(caminhoArquivo);
    }

    public RepositorioPessoasArquivo(File arquivo) {
        if (arquivo == null) {
            throw new IllegalArgumentException("O arquivo de pessoas nao pode ser nulo.");
        }
        this.arquivo = arquivo;
    }

    @Override
    public List<Pessoa> carregar() throws IOException, FormatoArquivoInvalidoException {
        if (!arquivo.exists()) {
            throw new FileNotFoundException(String.format("Arquivo de pessoas nao encontrado em: '%s'. " +
                    "Certifique-se de que a pasta 'dados' com o arquivo 'pessoas.csv' esta presente.", arquivo.getPath()));
        }

        List<Pessoa> pessoas = new ArrayList<>();
        int numeroLinha = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo, StandardCharsets.UTF_8))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                String linhaLimpa = linha.trim();

                // Ignora linhas em branco e comentarios com #
                if (linhaLimpa.isEmpty() || linhaLimpa.startsWith("#")) {
                    continue;
                }

                String[] partes = linhaLimpa.split(";");
                if (partes.length < 2) {
                    throw new FormatoArquivoInvalidoException(
                            arquivo.getName(),
                            numeroLinha,
                            linha,
                            "Esperava 2 campos separados por ponto e virgula: matricula;nome");
                }

                String matricula = partes[0].trim();
                String nome = partes[1].trim();

                pessoas.add(new Pessoa(matricula, nome));
            }
        }

        return pessoas;
    }

    public File getArquivo() {
        return arquivo;
    }
}

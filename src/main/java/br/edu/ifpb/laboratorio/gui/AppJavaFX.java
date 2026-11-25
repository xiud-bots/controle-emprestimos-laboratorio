package br.edu.ifpb.laboratorio.gui;

import br.edu.ifpb.laboratorio.gui.controlador.JanelaPrincipalControlador;
import br.edu.ifpb.laboratorio.gui.util.AlertaUtil;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioAcervo;
import br.edu.ifpb.laboratorio.persistencia.IRepositorioPessoas;
import br.edu.ifpb.laboratorio.persistencia.RepositorioAcervoArquivo;
import br.edu.ifpb.laboratorio.persistencia.RepositorioPessoasArquivo;
import br.edu.ifpb.laboratorio.servico.IServicoEmprestimo;
import br.edu.ifpb.laboratorio.servico.ServicoEmprestimo;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Ponto de entrada da Entrega 2: Aplicacao JavaFX (Interface Grafica Desacoplada).
 * Carrega a interface FXML, inicializa a camada de servico e exibe a janela principal.
 */
public class AppJavaFX extends Application {

    private IServicoEmprestimo servico;

    @Override
    public void init() throws Exception {
        super.init();
        // Inicializacao do modelo e repositorios com caminhos confiaveis
        String caminhoAcervo = resolverCaminhoDados("dados/acervo.csv");
        String caminhoPessoas = resolverCaminhoDados("dados/pessoas.csv");

        IRepositorioAcervo repoAcervo = new RepositorioAcervoArquivo(caminhoAcervo);
        IRepositorioPessoas repoPessoas = new RepositorioPessoasArquivo(caminhoPessoas);

        this.servico = new ServicoEmprestimo(repoAcervo, repoPessoas);
        try {
            this.servico.carregarDados();
        } catch (FormatoArquivoInvalidoException | IOException e) {
            System.err.println("[AVISO] Falha ao carregar dados iniciais no init: " + e.getMessage());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JanelaPrincipal.fxml"));
            Parent root = loader.load();

            JanelaPrincipalControlador controlador = loader.getController();
            controlador.setServico(this.servico);

            Scene scene = new Scene(root, 1080, 720);

            primaryStage.setTitle("Controle de Empréstimos do Laboratório — IFPB Campus Monteiro");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(920);
            primaryStage.setMinHeight(620);
            primaryStage.show();

        } catch (Exception e) {
            AlertaUtil.exibirErro("Erro ao Iniciar Interface",
                    "Falha crítica ao inicializar a interface JavaFX", e.getMessage());
            e.printStackTrace();
        }
    }

    private static String resolverCaminhoDados(String caminhoRelativo) {
        File f = new File(caminhoRelativo);
        if (f.exists()) {
            return f.getAbsolutePath();
        }
        File fProjeto = new File("dados", new File(caminhoRelativo).getName());
        if (fProjeto.exists()) {
            return fProjeto.getAbsolutePath();
        }
        return caminhoRelativo;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

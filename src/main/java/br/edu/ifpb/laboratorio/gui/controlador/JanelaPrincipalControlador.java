package br.edu.ifpb.laboratorio.gui.controlador;

import br.edu.ifpb.laboratorio.gui.util.AlertaUtil;
import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.servico.IServicoEmprestimo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador da Janela Principal da aplicacao JavaFX (Entrega 2).
 * Orquestra as tabelas de acervo e emprestimos, acoes de usuario e painel de totais.
 */
public class JanelaPrincipalControlador implements Initializable {

    @FXML private TableView<Item> tabelaAcervo;
    @FXML private TableColumn<Item, String> colPatrimonio;
    @FXML private TableColumn<Item, String> colTipo;
    @FXML private TableColumn<Item, String> colDescricao;
    @FXML private TableColumn<Item, String> colPrazo;
    @FXML private TableColumn<Item, String> colSituacao;
    @FXML private TableColumn<Item, String> colRegraMulta;

    @FXML private TableView<Emprestimo> tabelaEmprestimos;
    @FXML private TableColumn<Emprestimo, String> colEmpPatrimonio;
    @FXML private TableColumn<Emprestimo, String> colEmpDescricao;
    @FXML private TableColumn<Emprestimo, String> colEmpPessoa;
    @FXML private TableColumn<Emprestimo, String> colEmpMatricula;
    @FXML private TableColumn<Emprestimo, String> colEmpDataEmprestimo;
    @FXML private TableColumn<Emprestimo, String> colEmpDataPrevista;
    @FXML private TableColumn<Emprestimo, String> colEmpAtraso;
    @FXML private TableColumn<Emprestimo, String> colEmpMulta;

    @FXML private Label lblTotalAcervo;
    @FXML private Label lblTotalDisponiveis;
    @FXML private Label lblTotalEmprestados;
    @FXML private Label lblTotalMultas;

    @FXML private ComboBox<String> cbFiltroAcervo;
    @FXML private Button btnNovoEmprestimo;
    @FXML private Button btnDevolucao;
    @FXML private Button btnCadastrarItem;
    @FXML private Button btnRecarregar;

    private IServicoEmprestimo servico;
    private final ObservableList<Item> itensObservaveis = FXCollections.observableArrayList();
    private final ObservableList<Emprestimo> emprestimosObservaveis = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarColunasTabelaAcervo();
        configurarColunasTabelaEmprestimos();
        configurarFiltroAcervo();
    }

    public void setServico(IServicoEmprestimo servico) {
        this.servico = servico;
        atualizarTelas();
    }

    private void configurarColunasTabelaAcervo() {
        colPatrimonio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatrimonio()));
        colTipo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTipo()));
        colDescricao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescricao()));
        colPrazo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrazoEmprestimoDias() + " dias"));
        colSituacao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSituacao()));
        colRegraMulta.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegraMulta().getDescricao()));

        // Coloracao customizada para a coluna de situacao
        colSituacao.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String situacao, boolean empty) {
                super.updateItem(situacao, empty);
                if (empty || situacao == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(situacao);
                    if ("Disponível".equalsIgnoreCase(situacao)) {
                        setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                    }
                }
            }
        });

        tabelaAcervo.setItems(itensObservaveis);
    }

    private void configurarColunasTabelaEmprestimos() {
        colEmpPatrimonio.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getPatrimonio()));
        colEmpDescricao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getItem().getDescricao()));
        colEmpPessoa.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPessoa().getNome()));
        colEmpMatricula.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPessoa().getMatricula()));
        colEmpDataEmprestimo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataEmprestimoFormatada()));
        colEmpDataPrevista.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataPrevistaDevolucaoFormatada()));
        colEmpAtraso.setCellValueFactory(cellData -> {
            long dias = cellData.getValue().calcularDiasAtraso();
            return new SimpleStringProperty(dias > 0 ? dias + " dias de atraso" : "No prazo");
        });
        colEmpMulta.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().calcularMulta())));

        tabelaEmprestimos.setItems(emprestimosObservaveis);
    }

    private void configurarFiltroAcervo() {
        cbFiltroAcervo.setItems(FXCollections.observableArrayList("Todos os Itens", "Apenas Disponíveis", "Apenas Emprestados"));
        cbFiltroAcervo.getSelectionModel().selectFirst();
    }

    public void atualizarTelas() {
        if (servico == null) return;

        // Atualizar lista de itens do acervo conforme filtro selecionado
        aplicarFiltroAcervo();

        // Atualizar lista de emprestimos ativos
        emprestimosObservaveis.setAll(servico.listarEmprestimosAtivos());

        // Atualizar painel de totais (Secao 6.1 do enunciado)
        lblTotalAcervo.setText(String.valueOf(servico.getQuantidadeTotalItens()));
        lblTotalDisponiveis.setText(String.valueOf(servico.listarItensDisponiveis().size()));
        lblTotalEmprestados.setText(String.valueOf(servico.getQuantidadeItensEmprestados()));
        lblTotalMultas.setText(String.format("R$ %.2f", servico.calcularTotalMultas()));
    }

    @FXML
    private void handleFiltrarAcervo(ActionEvent event) {
        aplicarFiltroAcervo();
    }

    private void aplicarFiltroAcervo() {
        if (servico == null) return;
        String opcao = cbFiltroAcervo.getValue();
        if ("Apenas Disponíveis".equals(opcao)) {
            itensObservaveis.setAll(servico.listarItensDisponiveis());
        } else if ("Apenas Emprestados".equals(opcao)) {
            itensObservaveis.setAll(servico.listarItensEmprestados());
        } else {
            itensObservaveis.setAll(servico.getAcervo());
        }
    }

    @FXML
    private void abrirModalEmprestimo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModalEmprestimo.fxml"));
            Parent root = loader.load();

            ModalEmprestimoControlador modalCtrl = loader.getController();
            modalCtrl.setServico(servico, this);

            Stage modalStage = new Stage();
            modalStage.setTitle("Novo Empréstimo — Laboratório IFPB");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tabelaAcervo.getScene().getWindow());
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            AlertaUtil.exibirErro("Erro de Interface", "Não foi possível carregar o formulário", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegistrarDevolucao(ActionEvent event) {
        // Verifica se há item selecionado na tabela de emprestimos ou na tabela de acervo
        Emprestimo empSelecionado = tabelaEmprestimos.getSelectionModel().getSelectedItem();
        Item itemSelecionado = tabelaAcervo.getSelectionModel().getSelectedItem();

        String patrimonio = null;
        if (empSelecionado != null) {
            patrimonio = empSelecionado.getItem().getPatrimonio();
        } else if (itemSelecionado != null) {
            if (!itemSelecionado.isEmprestado()) {
                AlertaUtil.exibirAviso("Aviso", "Item já disponível",
                        String.format("O item '%s - %s' não está emprestado no momento.",
                                itemSelecionado.getPatrimonio(), itemSelecionado.getDescricao()));
                return;
            }
            patrimonio = itemSelecionado.getPatrimonio();
        }

        if (patrimonio == null) {
            AlertaUtil.exibirAviso("Seleção Necessária", "Nenhum item selecionado",
                    "Por favor, selecione um item emprestado na tabela de Acervo ou na tabela de Empréstimos para registrar a devolução.");
            return;
        }

        boolean confirmar = AlertaUtil.exibirConfirmacao("Confirmar Devolução",
                "Deseja registrar a baixa do item " + patrimonio + "?",
                "O item retornará ao estado DISPONÍVEL e seu empréstimo será finalizado.");

        if (!confirmar) return;

        try {
            Emprestimo devolvido = servico.registrarDevolucao(patrimonio);
            double multa = devolvido.calcularMulta();

            String mensagem = String.format("Item '%s' devolvido com sucesso!\nData da Devolução: %s",
                    devolvido.getItem().getPatrimonio(), devolvido.getDataDevolucaoFormatada());

            if (multa > 0.0) {
                mensagem += String.format("\n\nATENÇÃO: Multa por atraso gerada: R$ %.2f", multa);
            }

            AlertaUtil.exibirInformacao("Devolução Concluída", "Baixa de Empréstimo Registrada", mensagem);
            atualizarTelas();

        } catch (ItemNaoEncontradoException | IllegalArgumentException e) {
            AlertaUtil.exibirErro("Erro na Devolução", "Não foi possível concluir a baixa", e.getMessage());
        }
    }

    @FXML
    private void abrirModalCadastroItem(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ModalCadastroItem.fxml"));
            Parent root = loader.load();

            ModalCadastroItemControlador modalCtrl = loader.getController();
            modalCtrl.setServico(servico, this);

            Stage modalStage = new Stage();
            modalStage.setTitle("Cadastrar Novo Item — Acervo do Laboratório");
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initOwner(tabelaAcervo.getScene().getWindow());
            modalStage.setScene(new Scene(root));
            modalStage.setResizable(false);
            modalStage.showAndWait();

        } catch (IOException e) {
            AlertaUtil.exibirErro("Erro de Interface", "Não foi possível abrir o cadastro", e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRecarregarDados(ActionEvent event) {
        try {
            servico.carregarDados();
            atualizarTelas();
            AlertaUtil.exibirInformacao("Dados Atualizados", "Arquivos CSV Recarregados",
                    "O acervo e as pessoas foram recarregados com sucesso dos arquivos da pasta dados.");
        } catch (FormatoArquivoInvalidoException | IOException e) {
            AlertaUtil.exibirErro("Erro de Carregamento", "Falha ao ler arquivos", e.getMessage());
        }
    }
}

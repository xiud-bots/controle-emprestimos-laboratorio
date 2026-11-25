package br.edu.ifpb.laboratorio.gui.controlador;

import br.edu.ifpb.laboratorio.gui.util.AlertaUtil;
import br.edu.ifpb.laboratorio.modelo.entidade.Emprestimo;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.entidade.Pessoa;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemJaEmprestadoException;
import br.edu.ifpb.laboratorio.modelo.excecao.ItemNaoEncontradoException;
import br.edu.ifpb.laboratorio.modelo.excecao.PessoaNaoEncontradaException;
import br.edu.ifpb.laboratorio.servico.IServicoEmprestimo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador da Janela Modal de Novo Emprestimo (Entrega 2).
 * Captura a ItemJaEmprestadoException e a exibe em caixa de dialogo Alert,
 * cumprindo cirurgicamente os requisitos 4.6, 6.1 e o passo 3 do roteiro da Secao 7.
 */
public class ModalEmprestimoControlador {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML private ComboBox<Item> cbItem;
    @FXML private ComboBox<Pessoa> cbPessoa;
    @FXML private VBox boxDetalhesItem;
    @FXML private Label lblDetalheTipo;
    @FXML private Label lblDetalhePrazo;
    @FXML private Label lblDetalhePrevisao;
    @FXML private Label lblDetalheMulta;
    @FXML private Button btnConfirmar;

    private IServicoEmprestimo servico;
    private JanelaPrincipalControlador controladorPai;

    public void setServico(IServicoEmprestimo servico, JanelaPrincipalControlador controladorPai) {
        this.servico = servico;
        this.controladorPai = controladorPai;

        configurarConversoresComboBox();
        carregarOpcoes();
    }

    private void configurarConversoresComboBox() {
        cbItem.setConverter(new StringConverter<>() {
            @Override
            public String toString(Item item) {
                if (item == null) return "";
                String status = item.isEmprestado() ? "[EMPRESTADO]" : "[Disponivel]";
                return String.format("%s %s - %s (%s)", status, item.getPatrimonio(), item.getDescricao(), item.getTipo());
            }

            @Override
            public Item fromString(String string) {
                return null;
            }
        });

        cbPessoa.setConverter(new StringConverter<>() {
            @Override
            public String toString(Pessoa pessoa) {
                if (pessoa == null) return "";
                return String.format("%s (Matrícula: %s)", pessoa.getNome(), pessoa.getMatricula());
            }

            @Override
            public Pessoa fromString(String string) {
                return null;
            }
        });
    }

    private void carregarOpcoes() {
        if (servico == null) return;
        cbItem.setItems(FXCollections.observableArrayList(servico.getAcervo()));
        cbPessoa.setItems(FXCollections.observableArrayList(servico.getPessoas()));

        if (!cbItem.getItems().isEmpty()) {
            cbItem.getSelectionModel().selectFirst();
            atualizarDetalhesItem(cbItem.getValue());
        }
        if (!cbPessoa.getItems().isEmpty()) {
            cbPessoa.getSelectionModel().selectFirst();
        }
    }

    @FXML
    private void handleItemSelecionado(ActionEvent event) {
        Item item = cbItem.getValue();
        atualizarDetalhesItem(item);
    }

    private void atualizarDetalhesItem(Item item) {
        if (item == null) {
            lblDetalheTipo.setText("Tipo: -");
            lblDetalhePrazo.setText("Prazo regulamentar: -");
            lblDetalhePrevisao.setText("Data prevista para devolução: -");
            lblDetalheMulta.setText("Política de multa: -");
            return;
        }

        LocalDate hoje = LocalDate.now();
        LocalDate prevista = hoje.plusDays(item.getPrazoEmprestimoDias());

        lblDetalheTipo.setText("Tipo: " + item.getTipo());
        lblDetalhePrazo.setText(String.format("Prazo regulamentar: %d dias", item.getPrazoEmprestimoDias()));
        lblDetalhePrevisao.setText(String.format("Data prevista para devolução: %s", prevista.format(FORMATO_DATA)));
        lblDetalheMulta.setText(String.format("Política de multa: %s", item.getRegraMulta().getDescricao()));
    }

    @FXML
    private void handleConfirmar(ActionEvent event) {
        Item item = cbItem.getValue();
        Pessoa pessoa = cbPessoa.getValue();

        if (item == null || pessoa == null) {
            AlertaUtil.exibirAviso("Campos Incompletos", "Seleção obrigatória",
                    "Por favor, selecione um item e a pessoa responsável pelo empréstimo.");
            return;
        }

        try {
            // Chamada ao servico de dominio (mesma logica da Entrega 1)
            Emprestimo emprestimo = servico.registrarEmprestimo(item.getPatrimonio(), pessoa.getMatricula());

            // Alerta de sucesso
            AlertaUtil.exibirInformacao("Empréstimo Realizado", "Registro concluído com sucesso!",
                    String.format("Item: %s (%s)\nResponsável: %s\nData Prevista de Devolução: %s",
                            emprestimo.getItem().getPatrimonio(),
                            emprestimo.getItem().getDescricao(),
                            emprestimo.getPessoa().getNome(),
                            emprestimo.getDataPrevistaDevolucaoFormatada()));

            if (controladorPai != null) {
                controladorPai.atualizarTelas();
            }

            fecharModal();

        } catch (ItemJaEmprestadoException e) {
            // Requisito 6.1 e Passo 3 da Secao 7:
            // "Quando a excecao de item ja emprestado acontecer, ela deve aparecer
            // para o usuario numa janela de dialogo, nao no terminal."
            AlertaUtil.exibirErro("Empréstimo Recusado",
                    "Item Já Emprestado (" + e.getClass().getSimpleName() + ")",
                    e.getMessage());

        } catch (ItemNaoEncontradoException | PessoaNaoEncontradaException e) {
            AlertaUtil.exibirErro("Erro de Registro", "Entidade não encontrada", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharModal();
    }

    private void fecharModal() {
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }
}

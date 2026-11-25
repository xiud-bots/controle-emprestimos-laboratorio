package br.edu.ifpb.laboratorio.gui.controlador;

import br.edu.ifpb.laboratorio.gui.util.AlertaUtil;
import br.edu.ifpb.laboratorio.modelo.entidade.Item;
import br.edu.ifpb.laboratorio.modelo.excecao.FormatoArquivoInvalidoException;
import br.edu.ifpb.laboratorio.servico.IServicoEmprestimo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador da Janela Modal de Cadastro de Itens no Acervo (Entrega 2).
 * Atende ao requisito 6.1 ("operacao de manutencao que o grupo escolheu").
 */
public class ModalCadastroItemControlador {

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtPatrimonio;
    @FXML private TextField txtDescricao;
    @FXML private Button btnSalvar;

    private IServicoEmprestimo servico;
    private JanelaPrincipalControlador controladorPai;

    public void setServico(IServicoEmprestimo servico, JanelaPrincipalControlador controladorPai) {
        this.servico = servico;
        this.controladorPai = controladorPai;

        cbTipo.setItems(FXCollections.observableArrayList(
                "NOTEBOOK",
                "PROJETOR",
                "KIT_COMPONENTES"
        ));
        cbTipo.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleSalvar(ActionEvent event) {
        String tipo = cbTipo.getValue();
        String patrimonio = txtPatrimonio.getText() != null ? txtPatrimonio.getText().trim() : "";
        String descricao = txtDescricao.getText() != null ? txtDescricao.getText().trim() : "";

        if (tipo == null || patrimonio.isEmpty() || descricao.isEmpty()) {
            AlertaUtil.exibirAviso("Campos Vazios", "Preenchimento Obrigatório",
                    "Por favor, preencha o tipo, o patrimônio e a descrição do equipamento.");
            return;
        }

        try {
            Item novoItem = servico.cadastrarItem(tipo, patrimonio, descricao);

            AlertaUtil.exibirInformacao("Item Cadastrado", "Sucesso na Inclusão",
                    String.format("O equipamento '%s - %s' (%s) foi adicionado ao acervo e salvo em 'dados/acervo.csv'.",
                            novoItem.getPatrimonio(), novoItem.getDescricao(), novoItem.getTipo()));

            if (controladorPai != null) {
                controladorPai.atualizarTelas();
            }

            fecharModal();

        } catch (IllegalArgumentException | FormatoArquivoInvalidoException e) {
            AlertaUtil.exibirErro("Dados Inválidos", "Validação do Domínio", e.getMessage());
        } catch (IOException e) {
            AlertaUtil.exibirErro("Erro de Persistência", "Falha ao gravar arquivo CSV", e.getMessage());
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        fecharModal();
    }

    private void fecharModal() {
        Stage stage = (Stage) btnSalvar.getScene().getWindow();
        stage.close();
    }
}

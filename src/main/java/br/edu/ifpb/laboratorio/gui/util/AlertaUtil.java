package br.edu.ifpb.laboratorio.gui.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utilitario didatico para criacao de caixas de dialogo no JavaFX.
 * Centraliza a exibicao de alertas de sucesso, informacao, erro e confirmacao.
 */
public final class AlertaUtil {

    private AlertaUtil() {
        // Construtor privado para classe utilitaria
    }

    public static void exibirInformacao(String titulo, String cabecalho, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public static void exibirErro(String titulo, String cabecalho, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public static void exibirAviso(String titulo, String cabecalho, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    public static boolean exibirConfirmacao(String titulo, String cabecalho, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(cabecalho);
        alerta.setContentText(mensagem);
        Optional<ButtonType> resultado = alerta.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }
}

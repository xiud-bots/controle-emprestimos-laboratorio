package br.edu.ifpb.laboratorio.gui;

import javafx.application.Application;

/**
 * Ponto de entrada independente para execucao da aplicacao JavaFX sem necessidade de modulos JVM.
 * Permite que a aplicacao seja disparada facilmente por scripts de terminal, IDEs ou empacotamentos JAR.
 */
public class AppLauncher {
    public static void main(String[] args) {
        Application.launch(AppJavaFX.class, args);
    }
}

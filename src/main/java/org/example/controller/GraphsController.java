package org.example.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import org.example.model.Complaint;
import org.example.util.ScreenManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.HashMap;
import java.util.Map;

public class GraphsController {

    @FXML
    private BorderPane rootPane;

    private ObservableList<Complaint> complaintsList;

    public void setData(ObservableList<Complaint> lista) {
        this.complaintsList = lista;
        Platform.runLater(this::criarGrafico);
    }

    private void criarGrafico() {
        if (complaintsList == null || complaintsList.isEmpty()) return;

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<String, Integer> contagem = new HashMap<>();
        for (Complaint c : complaintsList) {
            String cat = c.getCategory().toString();
            contagem.put(cat, contagem.getOrDefault(cat, 0) + 1);
        }

        contagem.forEach((categoria, qtd) -> {
            dataset.addValue(qtd, "Reclamações", categoria);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Reclamações por Categoria", // Título
                "Categoria",                 // Label do eixo X
                "Quantidade",                // Label do eixo Y
                dataset
        );

        ChartViewer viewer = new ChartViewer(chart);
        rootPane.setCenter(viewer);
    }

    public void seeComplaints(ActionEvent event) {

        ScreenManager.loadScreen("ComplaintList.fxml");
    }
}
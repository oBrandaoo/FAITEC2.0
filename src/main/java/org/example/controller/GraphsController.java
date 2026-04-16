package org.example.controller;

import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.example.model.Complaint;
import org.example.util.ScreenManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.fx.ChartViewer;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GraphsController {

    @FXML
    public void initialize() {
        Platform.runLater(() -> aplicarEmTodos(rootPane));
    }

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

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();

        // cores das barras
        renderer.setSeriesPaint(0, java.awt.Color.BLUE);

        // bordas
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, java.awt.Color.BLACK);

        // fontes
        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 20));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 14));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 14));

        // tooltips
        renderer.setDefaultToolTipGenerator((dataSet, row, column) ->
                dataSet.getRowKey(row) + " : " + dataSet.getValue(row, column)
        );

        // legenda
        chart.getLegend().setItemFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        chart.getLegend().setPosition(org.jfree.chart.ui.RectangleEdge.BOTTOM);

        ChartViewer viewer = new ChartViewer(chart);
        rootPane.setCenter(viewer);
    }

    private void aplicarEmTodos(Node node) {
        if (node instanceof Button button) {
            aplicarEfeito(button);
        }

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                aplicarEmTodos(child);
            }
        }
    }

    public void aplicarEfeito(Button botao) {

        ScaleTransition aumentar = new ScaleTransition(Duration.millis(150), botao);
        aumentar.setToX(1.1);
        aumentar.setToY(1.1);

        ScaleTransition diminuir = new ScaleTransition(Duration.millis(150), botao);
        diminuir.setToX(1.0);
        diminuir.setToY(1.0);

        botao.setOnMouseEntered(e -> aumentar.playFromStart());
        botao.setOnMouseExited(e -> diminuir.playFromStart());
    }

    public void seeComplaints(ActionEvent event) {

        ScreenManager.loadScreen("ComplaintList.fxml");
    }
}
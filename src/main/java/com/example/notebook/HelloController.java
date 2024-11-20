package com.example.notebook;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;

public class HelloController {
    @FXML
    private TextField inputField;

    @FXML
    private TextArea notebookArea;

    // История строк для реализации "отмены последнего действия"
    private LinkedList<String> history = new LinkedList<>();

    @FXML
    public void initialize() {
        // Добавление строки в текстовое поле при нажатии Enter
        inputField.setOnAction(event -> {
            String text = inputField.getText();
            if (!text.isEmpty()) {
                history.addLast(notebookArea.getText()); // сохраняем текущее состояние перед изменением
                notebookArea.appendText(text + "\n");
                inputField.clear();
            }
        });

        // Обработка комбинаций клавиш
        notebookArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.isControlDown()) {
                if (event.getCode() == KeyCode.S) {
                    saveToFile();
                    event.consume();
                } else if (event.getCode() == KeyCode.D) {
                    clearTextArea();
                    event.consume();
                } else if (event.getCode() == KeyCode.Z) {
                    undoLastAction();
                    event.consume();
                } else if (event.getCode() == KeyCode.A) {
                    loadFromFile();
                    event.consume();
                }
            }
        });
    }

    private void saveToFile() {
        try (FileWriter writer = new FileWriter("notebook.txt", false
        )) {
            writer.write(notebookArea.getText());
            showAlert("Сохранение успешно", "Данные записаны в файл notebook.txt");
            writer.close();
        } catch (IOException e) {
            showAlert("Ошибка", "Не удалось сохранить данные: " + e.getMessage());
        }
    }

    private void clearTextArea() {
        history.addLast(notebookArea.getText()); // сохраняем текущее состояние перед очисткой
        notebookArea.clear();
        showAlert("Операция выполнена", "Поле успешно очищено.");
    }

    private void undoLastAction() {
        if (!history.isEmpty()) {
            String previousState = history.removeLast();
            notebookArea.setText(previousState);
        } else {
            showAlert("Отмена невозможна", "История пуста.");
        }
    }

    private void loadFromFile() {
        File file = new File("notebook.txt");
        if (file.exists() && file.isFile()) {
            try {
                String content = Files.readString(file.toPath());
                history.addLast(notebookArea.getText());
                notebookArea.setText(content);
                showAlert("Загрузка успешна", "Данные успешно загружены из notebook.txt");
            } catch (IOException e) {
                showAlert("Ошибка", "Не удалось загрузить данные: " + e.getMessage());
            }
        } else {
            showAlert("Ошибка", "Файл notebook.txt не найден.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

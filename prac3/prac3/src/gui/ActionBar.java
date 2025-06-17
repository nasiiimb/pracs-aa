package gui;

import main.Notify;
import model.Model;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ActionBar extends JPanel {
    public ActionBar(Notify notify, Model model) {
        JComboBox<String> selector = new JComboBox<>(new String[]{"Uniforme", "Normal", "Exponencial"});
        JTextField nField = new JTextField("", 5);
        JButton generateBtn = new JButton("Generar");
        JLabel estimationLabel = new JLabel("Temps estimat: ? ms");

        generateBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int n = Integer.parseInt(nField.getText());
                    String dist = (String) selector.getSelectedItem();
                    model.setConfig(n, dist);
                    notify.notify(Notify.START);

                    var closest = model.getClosest();
                    var farthest = model.getFarthest();
                    if (closest != null && farthest != null) {
                        System.out.println("Parell més proper: " + closest[0] + " <-> " + closest[1]);
                        System.out.println("Parell més llunyà: " + farthest[0] + " <-> " + farthest[1]);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Introdueix un valor vàlid per N.");
                }
            }
        });

        // Actualitza la predicció en temps real
        nField.getDocument().addDocumentListener(new DocumentListener() {
            private void updateEstimation() {
                try {
                    int n = Integer.parseInt(nField.getText());
                    long t1 = model.estimateBruteForceTime(n);
                    long t2 = model.estimateDivideAndConquerTime(n);
                    estimationLabel.setText("Temps estimat → Brute: ~" + t1 + " ms | Divide: ~" + t2 + " ms");
                } catch (NumberFormatException e) {
                    estimationLabel.setText("Temps estimat: ? ms");
                }
            }

            public void insertUpdate(DocumentEvent e) { updateEstimation(); }
            public void removeUpdate(DocumentEvent e) { updateEstimation(); }
            public void changedUpdate(DocumentEvent e) { updateEstimation(); }
        });

        add(new JLabel("N:"));
        add(nField);
        add(selector);
        add(generateBtn);
        add(estimationLabel);
    }
}

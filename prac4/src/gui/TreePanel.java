package gui;

import model.Model;

import javax.swing.*;
import java.awt.*;
import java.util.Map;


/**
 * Simulates the graphic output of the model.
 */
public class TreePanel extends JPanel
{
    private final Model model;
    private final JTextArea textArea;

    public TreePanel(int width, int height, Model model)
    {
        this.model = model;
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 18));

        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        updateEncoderDisplay();
    }

    /**
     * Updates the text area with the current encoder map from the model.
     * This method should be called whenever the model's data changes.
     */
    public void updateEncoderDisplay()
    {
        Map<Character, String> encoder = model.getEncoder();
        if (encoder == null) return;

        StringBuilder sb = new StringBuilder("\n  Huffman Encoding Map:\n");
        sb.append("  ---------------------\n");
        for (Map.Entry<Character, String> entry : encoder.entrySet()) {
            String keyStr;
            char key = entry.getKey();

            if (key == '\n')
                keyStr = "\\n";
            else if (key == '\t')
                keyStr = "\\t";
            else if (key == '\r')
                keyStr = "\\r";
            else if (Character.isWhitespace(key))
                keyStr = String.format("\\u%04x", (int) key);
            else if (!Character.isISOControl(key))
                keyStr = String.valueOf(key);
            else
                keyStr = String.format("\\u%04x", (int) key);

            sb.append(String.format("  '%s' -> %s%n", keyStr, entry.getValue()));
        }

        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);
    }

}

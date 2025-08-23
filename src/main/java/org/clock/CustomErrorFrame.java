package org.clock;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomErrorFrame extends JFrame {

    public CustomErrorFrame(String message, JFrame parentFrame) {
        super("Error");
        setUndecorated(true); // Remove standard decorations
        setAlwaysOnTop(true); // Keep it on top

        // Create components
        JLabel iconLabel = new JLabel(UIManager.getIcon("OptionPane.errorIcon"));
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setBackground(getBackground()); // Match frame background

        JButton closeButton = new JButton("OK");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the frame
                if (parentFrame != null) {
                    parentFrame.setEnabled(true); // Re-enable parent frame
                }
            }
        });

        // Layout components
        setLayout(new BorderLayout(10, 10));
        add(iconLabel, BorderLayout.WEST);
        add(messageArea, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        //pack(); // Size the frame to fit components
        setLocationRelativeTo(parentFrame); // Center relative to parent

        if (parentFrame != null) {
            parentFrame.setEnabled(false); // Disable parent frame
        }
    }

    public static void main(String[] args) {
        JFrame mainFrame = new JFrame("Main Application");
        mainFrame.setSize(600, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);

        JButton showErrorButton = new JButton("Show Error");
        showErrorButton.addActionListener(e -> {
            new CustomErrorFrame("An unexpected error occurred. Please try again.", mainFrame).setVisible(true);
        });
        mainFrame.add(showErrorButton, BorderLayout.CENTER);
    }
}
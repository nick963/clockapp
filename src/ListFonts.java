/*
 * Copyright 2025 Nick Lerissa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import javax.swing.*;
import java.awt.*;

public class ListFonts {
    public static void main(String[] args) {
        // 1. Get available font family names
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();

        // 2. Create a JList with the font names
        JList<String> fontList = new JList<>(fontNames);
        fontList.setCellRenderer(new MyListCellRenderer());

        // 3. Embed the JList in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(fontList);

        // Create the JFrame to display the UI
        JFrame frame = new JFrame("Available Fonts");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(scrollPane);
        frame.setSize(300, 400);
        frame.setLocationRelativeTo(null); // Center the window
        frame.setVisible(true);
    }

    static class MyListCellRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component component = super.getListCellRendererComponent(list, "0123456789" + value, index, isSelected, cellHasFocus);
            component.setFont(new Font(value.toString(), Font.BOLD, 60));
            return component;
        }
    }
}

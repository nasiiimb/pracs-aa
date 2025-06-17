package gui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


/**
 * Simulates a combo box with checkboxes for selecting multiple items.
 */
public class CheckComboBox extends JComboBox<CheckComboBox.CheckableItem>
{
    private final String displayText;
    private boolean keepPopupOpen = false;
    private boolean selectAllState = false;


    public CheckComboBox(String[] items, String displayText)
    {
        super(toCheckableItems(items));
        this.displayText = displayText;
        this.setActionCommand(displayText);
        this.setRenderer(new CheckBoxRenderer());
        this.setFont(new Font("Arial", Font.PLAIN, 16));

        // Ensure the combo never changes its selected index
        setSelectedIndex(-1);

        // Attach a listener when the popup becomes visible to intercept clicks
        addPopupMenuListener(new PopupMenuListener()
        {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e)
            {
                ComboPopup popup = (ComboPopup) getUI().getAccessibleChild(CheckComboBox.this, 0);
                JList<?> list = popup.getList();
                // Remove any prior listener to avoid duplicates
                for (MouseListener ml : list.getMouseListeners())
                {
                    if (ml.getClass() == CheckComboBoxItemMouseListener.class)
                        list.removeMouseListener(ml);
                }
                // Add our custom listener
                list.addMouseListener(new CheckComboBoxItemMouseListener());
            }
            @Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
        });

    }

    @Override
    public void setPopupVisible(boolean visible)
    {
        // If an internal click sets visible=false but we want to keep open, skip
        if (!visible && keepPopupOpen)
        {
            keepPopupOpen = false;
            return;
        }
        super.setPopupVisible(visible);
    }

    private static CheckableItem[] toCheckableItems(String[] items)
    {
        CheckableItem[] arr = new CheckableItem[items.length];
        for (int i = 0; i < items.length; i++)
            arr[i] = new CheckableItem(items[i]);

        return arr;
    }

    /**
     * Retrieve selected items.
     */
    public ArrayList<String> getSelectedItems()
    {
        ArrayList<String> selected = new ArrayList<>();
        for (int i = 1; i < getItemCount(); i++)
        {
            CheckableItem ci = getItemAt(i);
            if (ci.isSelected())
                selected.add(ci.getItem());
        }
        return selected;
    }


    public void setSelectedItems(ArrayList<String> selected)
    {
        for (int i = 1; i < getItemCount(); i++)
        {
            CheckableItem ci = getItemAt(i);
            if (selected.contains(ci.getItem()))
            {
                ci.setSelected(true);
                ci.setEnabled(false);
            }
            else
            {
                ci.setSelected(false);
                ci.setEnabled(true);
            }
        }
        setSelectedIndex(-1);
    }


    private void toggleAll(boolean select)
    {
        for (int i = 0; i < getItemCount(); i++)
        {
            CheckableItem item = getItemAt(i);
            if (item.isEnabled())
                item.setSelected(select);
        }
        selectAllState = select;
    }



    /**
     * Mouse listener for handling item toggles in the popup.
     */
    private class CheckComboBoxItemMouseListener extends MouseAdapter
    {
        @Override
        public void mousePressed(MouseEvent me)
        {
            ComboPopup popup = (ComboPopup) getUI().getAccessibleChild(CheckComboBox.this, 0);
            JList<?> list = popup.getList();
            int index = list.locationToIndex(me.getPoint());

            if (index == 0)
            {
                selectAllState = !selectAllState;
                toggleAll(selectAllState);
                list.repaint();
                me.consume();
                keepPopupOpen = true;
            }
            else if (index > 0)
            {
                CheckableItem item = getItemAt(index); // Adjust for Select All button
                if (item.isEnabled())
                {
                    item.setSelected(!item.isSelected());
                    list.repaint();
                }
                me.consume();
                keepPopupOpen = true;
            }
            setSelectedIndex(-1);
        }

    }

    /**
     * Wrapper class to hold an item and its selected state.
     */
    static class CheckableItem
    {
        private final String item;
        private boolean selected;
        private boolean enabled;

        public CheckableItem(String item)
        {
            this.item = item;
            this.selected = false;
            this.enabled = true;
        }

        public String getItem() { return item; }
        public boolean isSelected() { return selected; }
        public void setSelected(boolean sel) { this.selected = sel; }
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean en) { this.enabled = en; }

        @Override
        public String toString() { return item; }
    }

    /**
     * Renderer to show each item as a JCheckBox, and a constant text when closed.
     */
    class CheckBoxRenderer implements ListCellRenderer<CheckableItem>
    {
        private final JCheckBox checkBox = new JCheckBox();
        private final JLabel label = new JLabel();
        private final JCheckBox selectAllBox = new JCheckBox();


        @Override
        public Component getListCellRendererComponent(JList<? extends CheckableItem> list,
                                                      CheckableItem value,
                                                      int index,
                                                      boolean isSelected,
                                                      boolean cellHasFocus) {
            if (index == -1)
            {
                // closed state: simple text
                label.setText(displayText);
                label.setOpaque(true);
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
                return label;
            }
            else if (index == 0)
            {
                // Select All button
                if (getItemCount() > getSelectedItems().size())
                    selectAllBox.setText("Select All");
                else
                    selectAllBox.setText("Deselect All");

                selectAllBox.setSelected(selectAllState);
                selectAllBox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                selectAllBox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                return selectAllBox;
            }
            else
            {
                // Regular items (shifted by 1 due to Select All button)
                CheckableItem item = getItemAt(index);
                checkBox.setSelected(item.isSelected());
                checkBox.setText(item.getItem());
                checkBox.setEnabled(item.isEnabled());
                checkBox.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
                checkBox.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                return checkBox;
            }

        }
    }
}

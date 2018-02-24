package nl.weeaboo.vn.buildgui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.TreeSet;

import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.buildtools.project.NvlistProjectConnection;
import nl.weeaboo.vn.buildtools.project.ProjectFolderConfig;

@SuppressWarnings("serial")
final class PropertiesPanel extends JPanel implements IProjectModelListener {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesPanel.class);

    private final JTable table;
    private final JButton editButton;
    private final JButton refreshButton;

    private @Nullable ProjectFolderConfig folderConfig;

    public PropertiesPanel() {
        table = new JTable();

        editButton = new JButton("Edit");
        editButton.addActionListener(e -> edit());

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(editButton);
        buttonPanel.add(refreshButton);

        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setPreferredSize(new Dimension(400, 200));
        tablePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.EAST);

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(tablePanel, BorderLayout.WEST);
    }

    @Override
    public void onProjectChanged(NvlistProjectConnection projectModel) {
        folderConfig = projectModel.getFolderConfig();

        refresh();
    }

    private void edit() {
        if (folderConfig == null) {
            return;
        }

        File buildProperties = folderConfig.getBuildPropertiesFile();
        try {
            Desktop.getDesktop().open(buildProperties);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error opening file: " + e, "Unable to open file",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refresh() {
        editButton.setEnabled(folderConfig != null);
        refreshButton.setEnabled(folderConfig != null);

        Properties props = new Properties();

        if (folderConfig != null) {
            File buildProperties = folderConfig.getBuildPropertiesFile();
            try (FileInputStream in = new FileInputStream(buildProperties)) {
                props.load(in);
            } catch (IOException e) {
                LOG.warn("Unable to read build properties: {}", buildProperties);
            }
        }

        updateTableModel(props);
    }

    private void updateTableModel(Properties props) {
        String[] cols = { "Property", "Value" };
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        for (String key : new TreeSet<>(props.stringPropertyNames())) {
            model.addRow(new Object[] {key, props.getProperty(key)});
        }
        table.setModel(model);
    }

}

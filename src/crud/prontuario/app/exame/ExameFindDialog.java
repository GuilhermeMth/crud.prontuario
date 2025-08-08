package crud.prontuario.app.exame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

// Janela para localizar exames
public class ExameFindDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Campos de busca e botões
    private final JTextField txtBusca = new JTextField();
    private final JButton btnPesquisar = new JButton("Pesquisar");
    private final JButton btnSair = new JButton("Sair");

    // Tabela e modelo para exibir os resultados da busca
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Objetos para interagir com o banco de dados
    private final ExameDAO exameDAO;
    private final PacienteDAO pacienteDAO;

    // Construtor: monta a janela de busca
    public ExameFindDialog(Frame owner) {
        super(owner, "Localizar Exame", true);

        // Inicializa os DAOs para o banco de dados
        exameDAO = new ExameDAO(new DatabaseConnectionMySQL());
        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Define as colunas e o modelo da tabela
        String[] colunas = {"ID", "Paciente", "Data do Exame", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            // Impede a edição das células da tabela
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Painel principal com layout BorderLayout
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(painelPrincipal);

        // Painel para o campo de busca e o botão
        JPanel painelBusca = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Adiciona o rótulo
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 5); gbc.anchor = GridBagConstraints.WEST;
        painelBusca.add(new JLabel("Buscar por nome do paciente:"), gbc);

        // Adiciona o campo de texto
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 0, 5);
        painelBusca.add(txtBusca, gbc);

        // Adiciona o botão de pesquisa
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.insets = new Insets(0, 0, 0, 0);
        painelBusca.add(btnPesquisar, gbc);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        // Adiciona a tabela em um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Painel com o botão de sair
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelInferior.add(btnSair);
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        // Adiciona as ações aos botões
        btnPesquisar.addActionListener(this::acaoPesquisar);
        btnSair.addActionListener(e -> dispose());

        // Define as dimensões e o comportamento da janela
        setPreferredSize(new Dimension(700, 400));
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    // Carrega a tabela com os exames, filtrando pelo termo de busca
    private void carregarTabela(String termo) {
        List<Exame> exames = exameDAO.findAll();
        tableModel.setRowCount(0); // Limpa a tabela

        termo = termo.toLowerCase();

        for (Exame ex : exames) {
            Paciente p = pacienteDAO.findById(ex.getPacienteId());
            String nomePaciente = p != null ? p.getNome() : "Desconhecido";

            // Se o nome do paciente contém o termo de busca, adiciona à tabela
            if (termo.isEmpty() || nomePaciente.toLowerCase().contains(termo)) {
                String dataStr = ex.getDataExame() != null ? ex.getDataExame().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
                tableModel.addRow(new Object[] {
                        ex.getId(),
                        nomePaciente,
                        dataStr,
                        ex.getDescricao()
                });
            }
        }
    }

    // Ação do botão 'Pesquisar'
    private void acaoPesquisar(ActionEvent e) {
        String termo = txtBusca.getText().trim();

        if (termo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Digite um termo para pesquisar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        carregarTabela(termo);

        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Nenhum exame encontrado com o termo informado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
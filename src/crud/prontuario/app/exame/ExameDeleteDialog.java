package crud.prontuario.app.exame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

// Esta é a janela para deletar um exame
public class ExameDeleteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Tabela e seu modelo para exibir a lista de exames
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Objetos para interagir com o banco de dados
    private final ExameDAO exameDAO;
    private final PacienteDAO pacienteDAO;

    // Construtor: monta a janela e inicializa os componentes
    public ExameDeleteDialog(Frame owner) {
        super(owner, "Excluir Exame", true);

        // Instancia os DAOs para acesso ao banco de dados
        exameDAO = new ExameDAO(new DatabaseConnectionMySQL());
        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Define as colunas da tabela
        String[] colunas = {"ID", "Paciente", "Data do Exame", "Descrição"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
			// Impede que as células da tabela sejam editáveis
			@Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Carrega a lista de exames do banco e preenche a tabela
        carregarExames();

        // Configura o layout da janela
        setLayout(new BorderLayout(10, 10));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões 'Deletar' e 'Sair'
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnDeletar = new JButton("Deletar");
        JButton btnSair = new JButton("Sair");
        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnSair);
        add(painelBotoes, BorderLayout.SOUTH);

        // Associa as ações aos botões
        btnDeletar.addActionListener(this::acaoDeletar);
        btnSair.addActionListener(e -> dispose());

        // Define o tamanho e o comportamento da janela
        setPreferredSize(new Dimension(700, 400));
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    // Carrega todos os exames do banco e exibe na tabela
    private void carregarExames() {
        List<Exame> exames = exameDAO.findAll();
        tableModel.setRowCount(0); // Limpa a tabela antes de preencher

        for (Exame ex : exames) {
            // Busca o nome do paciente pelo ID do exame
            Paciente p = pacienteDAO.findById(ex.getPacienteId());
            String nomePaciente = p != null ? p.getNome() : "Desconhecido";
            String dataStr = ex.getDataExame() != null ? ex.getDataExame().toString() : "";
            // Adiciona uma nova linha com os dados do exame
            tableModel.addRow(new Object[] {ex.getId(), nomePaciente, dataStr, ex.getDescricao()});
        }
    }

    // Ação do botão 'Deletar': confirma a exclusão e remove o exame do banco
    private void acaoDeletar(ActionEvent e) {
        int linhaSelecionada = table.getSelectedRow();
        if (linhaSelecionada < 0) {
            JOptionPane.showMessageDialog(this, "Selecione um exame para deletar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega o ID e a descrição do exame da linha selecionada
        Long exameId = (Long) tableModel.getValueAt(linhaSelecionada, 0);
        String descricao = (String) tableModel.getValueAt(linhaSelecionada, 3);

        // Pede confirmação antes de excluir
        int confirmar = JOptionPane.showConfirmDialog(
                this,
                "Deseja realmente excluir o exame: \"" + descricao + "\"?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION
        );

        if (confirmar == JOptionPane.YES_OPTION) {
            try {
                Exame ex = exameDAO.findById(exameId);
                if (ex != null) {
                    // Chama o DAO para deletar o exame
                    exameDAO.delete(ex);
                    JOptionPane.showMessageDialog(this, "Exame excluído com sucesso!");
                    // Atualiza a tabela após a exclusão
                    carregarExames();
                } else {
                    JOptionPane.showMessageDialog(this, "Exame não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
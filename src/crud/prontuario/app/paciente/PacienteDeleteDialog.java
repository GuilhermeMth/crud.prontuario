package crud.prontuario.app.paciente;

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

import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;

// Janela para excluir um paciente
public class PacienteDeleteDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Objeto para interagir com o banco de dados
    private final PacienteDAO pacienteDAO;
    // Tabela e seu modelo para exibir a lista de pacientes
    private final JTable table;
    private final DefaultTableModel tableModel;
    // Botões de ação
    private final JButton btnExcluir = new JButton("Excluir");
    private final JButton btnSair = new JButton("Sair");

    // Construtor: monta a janela de exclusão
    public PacienteDeleteDialog(Frame owner) {
        super(owner, "Excluir Paciente", true);
        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Configurações básicas da janela
        setLayout(new BorderLayout(10, 10));
        setPreferredSize(new Dimension(600, 400));
        setResizable(false);
        setLocationRelativeTo(owner);

        // Define as colunas e o modelo da tabela
        String[] colunas = {"ID", "Nome", "CPF", "Data de Nascimento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            // Impede a edição das células
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Permite selecionar apenas uma linha

        // Adiciona a tabela em um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os botões 'Excluir' e 'Sair'
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        painelBotoes.add(btnExcluir);
        painelBotoes.add(btnSair);
        add(painelBotoes, BorderLayout.SOUTH);

        // Adiciona as ações aos botões
        btnExcluir.addActionListener(this::excluirPacienteSelecionado);
        btnSair.addActionListener(e -> dispose());

        // Carrega os dados dos pacientes na tabela
        carregarPacientes();

        pack();
    }

    // Carrega todos os pacientes do banco e exibe na tabela
    private void carregarPacientes() {
        tableModel.setRowCount(0); // Limpa a tabela antes de preencher
        try {
            List<Paciente> pacientes = pacienteDAO.findAll();
            pacientes.forEach(p -> {
                // Formata a data de nascimento para o formato DD/MM/AAAA
                String dataStr = p.getDataNascimento() != null
                        ? p.getDataNascimento().toLocalDate().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        : "";
                // Adiciona uma nova linha com os dados do paciente
                tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getCpf(), dataStr});
            });
        } catch (Exception e) {
            // Em caso de erro, exibe uma mensagem
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar pacientes: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // Ação do botão 'Excluir': verifica, confirma e deleta o paciente selecionado
    private void excluirPacienteSelecionado(ActionEvent e) {
        int linha = table.getSelectedRow();
        if (linha == -1) { // Nenhuma linha selecionada
            JOptionPane.showMessageDialog(this,
                    "Selecione um paciente na tabela para excluir.",
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega o ID do paciente da linha selecionada
        Long id = (Long) tableModel.getValueAt(linha, 0);

        try {
            Paciente paciente = pacienteDAO.findById(id);
            if (paciente == null) {
                JOptionPane.showMessageDialog(this,
                        "Paciente não encontrado para exclusão.",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                carregarPacientes(); // Atualiza a tabela
                return;
            }

            // Janela de confirmação antes de excluir
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja excluir o paciente \"" + paciente.getNome() + "\"?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                // Chama o DAO para deletar o paciente
                pacienteDAO.delete(paciente);
                JOptionPane.showMessageDialog(this, "Paciente excluído com sucesso!");
                carregarPacientes(); // Atualiza a tabela
            }
        } catch (Exception ex) {
            // Em caso de erro na exclusão, exibe uma mensagem
            JOptionPane.showMessageDialog(this,
                    "Erro ao excluir paciente: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
package crud.prontuario.app.paciente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;
import crud.prontuario.util.ValidacaoUtil;

// Janela para editar pacientes existentes
public class PacienteEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Tabela e seu modelo para exibir a lista de pacientes
    private final DefaultTableModel tableModel;
    private final JTable table;

    // Campos de entrada para edição de dados
    private final JTextField campoNome = new JTextField();
    private final JFormattedTextField campoCpf;
    private final JFormattedTextField campoDataNascimento;

    // Formatador para a data (DD/MM/AAAA)
    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Objeto para interagir com o banco de dados
    private final PacienteDAO pacienteDAO;

    // Construtor: monta a janela de edição
    public PacienteEditDialog(Frame owner) throws Exception {
        super(owner, "Editar Paciente", true);

        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Inicializa os campos de entrada com máscaras
        campoNome.setColumns(20);
        campoCpf = criarCampoComMascara("###.###.###-##");
        campoCpf.setColumns(20);
        campoDataNascimento = criarCampoComMascara("##/##/####");
        campoDataNascimento.setColumns(10);

        // Configura o modelo e a tabela
        String[] colunas = {"ID", "Nome", "CPF", "Data de Nascimento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Configura o layout da janela
        setLayout(new BorderLayout());

        // Adiciona a tabela em um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));
        scrollPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os campos de edição
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        // Adiciona os rótulos e campos na grade
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("CPF:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Data Nascimento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(campoDataNascimento, gbc);

        add(painelCampos, BorderLayout.NORTH);

        // Painel para os botões 'Atualizar' e 'Sair'
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(this::acaoAtualizar);
        painelBotoes.add(btnAtualizar);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> dispose());
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);

        // Carrega os dados dos pacientes na tabela
        carregarPacientes();

        // Adiciona um ouvinte para a seleção da tabela para preencher os campos
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                preencherCamposDaSelecao();
            }
        });

        // Define o tamanho e o comportamento da janela
        setPreferredSize(new Dimension(600, 450));
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    // Cria um campo de texto formatado com uma máscara
    private JFormattedTextField criarCampoComMascara(String mascara) {
        try {
            MaskFormatter mf = new MaskFormatter(mascara);
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar máscara: " + mascara, e);
        }
    }

    // Carrega todos os pacientes do banco de dados na tabela
    private void carregarPacientes() {
        tableModel.setRowCount(0); // Limpa a tabela
        List<Paciente> pacientes = pacienteDAO.findAll();
        for (Paciente p : pacientes) {
            String dataStr = p.getDataNascimento() != null ? p.getDataNascimento().toLocalDate().format(formatterData) : "";
            tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getCpf(), dataStr});
        }
    }

    // Preenche os campos de edição com os dados do paciente selecionado na tabela
    private void preencherCamposDaSelecao() {
        int linha = table.getSelectedRow();
        if (linha == -1) {
            limparCampos();
            return;
        }

        campoNome.setText((String) tableModel.getValueAt(linha, 1));
        campoCpf.setText((String) tableModel.getValueAt(linha, 2));
        campoDataNascimento.setText((String) tableModel.getValueAt(linha, 3));
    }

    // Limpa todos os campos do formulário
    private void limparCampos() {
        campoNome.setText("");
        campoCpf.setText("");
        campoDataNascimento.setText("");
    }

    // Ação do botão 'Atualizar': valida os dados e atualiza o paciente no banco
    private void acaoAtualizar(ActionEvent e) {
        int linha = table.getSelectedRow();
        if (linha == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um paciente na tabela para atualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Long id = (Long) tableModel.getValueAt(linha, 0);
        String nome = campoNome.getText().trim();
        String cpf = campoCpf.getText().trim();
        String data = campoDataNascimento.getText().trim();

        // Validação dos campos
        if (ValidacaoUtil.isVazio(nome)) {
            mostrarErro("Nome é obrigatório.");
            return;
        }
        if (!ValidacaoUtil.isNomeValido(nome)) {
            mostrarErro("Nome inválido. Use entre 3 e 100 letras e espaços.");
            return;
        }
        if (ValidacaoUtil.isVazio(cpf)) {
            mostrarErro("CPF é obrigatório.");
            return;
        }
        if (!ValidacaoUtil.isFormatoCPF(cpf)) {
            mostrarErro("CPF deve estar no formato XXX.XXX.XXX-XX.");
            return;
        }
        if (!ValidacaoUtil.isCPFValido(cpf)) {
            mostrarErro("CPF inválido.");
            return;
        }
        if (ValidacaoUtil.isVazio(data)) {
            mostrarErro("Data de nascimento é obrigatória.");
            return;
        }
        if (!ValidacaoUtil.isDataValida(data)) {
            mostrarErro("Data inválida. Use o formato DD/MM/AAAA.");
            return;
        }

        try {
            // Verifica se o novo CPF já existe para outro paciente
            Paciente pacienteExistente = pacienteDAO.findByCpf(cpf);
            if (pacienteExistente != null && !pacienteExistente.getId().equals(id)) {
                mostrarErro("CPF já cadastrado para outro paciente.");
                return;
            }

            // Converte a data e cria o objeto Paciente
            LocalDateTime dataNascimento = LocalDateTime.of(
                java.time.LocalDate.parse(data, formatterData),
                java.time.LocalTime.MIDNIGHT
            );
            Paciente paciente = new Paciente(id, nome, cpf, dataNascimento);

            // Chama o DAO para atualizar o paciente no banco
            pacienteDAO.update(paciente);

            // Atualiza a linha da tabela com os novos dados
            tableModel.setValueAt(nome, linha, 1);
            tableModel.setValueAt(cpf, linha, 2);
            tableModel.setValueAt(data, linha, 3);

            JOptionPane.showMessageDialog(this, "Paciente atualizado com sucesso!");
        } catch (Exception ex) {
            mostrarErro("Erro ao atualizar paciente: " + ex.getMessage());
        }
    }

    // Exibe uma caixa de diálogo com uma mensagem de erro
    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro de validação", JOptionPane.ERROR_MESSAGE);
    }
}
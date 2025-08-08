package crud.prontuario.app.exame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;
import crud.prontuario.util.ValidacaoUtil;

// Esta é a janela para editar exames existentes
public class ExameEditDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Tabela e seu modelo para exibir a lista de exames
    private final DefaultTableModel tableModel;
    private final JTable table;

    // Campos de entrada para a edição dos dados
    private final JComboBox<Paciente> comboPaciente;
    private final JTextField txtDataExame;
    private final JTextArea txtDescricao;

    // Objetos para interagir com o banco de dados
    private final ExameDAO exameDAO;
    private final PacienteDAO pacienteDAO;

    // Armazena o exame selecionado na tabela para ser editado
    private Exame exameSelecionado;

    // Formatador para a data
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor: monta a janela e inicializa os componentes
    public ExameEditDialog(Frame owner) {
        super(owner, "Editar Exame", true);

        // Instancia os DAOs para acesso ao banco de dados
        exameDAO = new ExameDAO(new DatabaseConnectionMySQL());
        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Inicializa o JComboBox de pacientes e personaliza a exibição dos itens
        comboPaciente = new JComboBox<>();
        carregarPacientes();
        comboPaciente.setRenderer(new ListCellRenderer<Paciente>() {
            private final JLabel label = new JLabel();

            @Override
            public Component getListCellRendererComponent(JList<? extends Paciente> list, Paciente value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                if (value != null) {
                    label.setText(value.getNome() + " (" + value.getCpf() + ")");
                } else {
                    label.setText("");
                }
                if (isSelected) {
                    label.setBackground(list.getSelectionBackground());
                    label.setForeground(list.getSelectionForeground());
                } else {
                    label.setBackground(list.getBackground());
                    label.setForeground(list.getForeground());
                }
                label.setOpaque(true);
                return label;
            }
        });

        // Inicializa os campos de texto e área de texto
        txtDataExame = new JTextField(10);
        txtDescricao = new JTextArea(4, 30);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        // Configura a tabela
        String[] colunas = {"ID", "Descrição", "Data do Exame", "Paciente"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        table = new JTable(tableModel) {
            private static final long serialVersionUID = 1L;

            // Personaliza a pintura da tabela para preencher o espaço vazio
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() > 0) {
                    Rectangle rect = g.getClipBounds();
                    int heightTabela = getRowHeight() * getRowCount();
                    if (rect.height > heightTabela) {
                        g.setColor(getParent().getBackground());
                        g.fillRect(0, heightTabela, getWidth(), rect.height - heightTabela);
                    }
                }
            }
        };

        // Configura o comportamento da tabela
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowHeight(24);

        // Carrega os exames do banco de dados na tabela
        carregarExames();

        // Configura o layout da janela
        setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(650, 200));
        scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        add(scrollPane, BorderLayout.CENTER);

        // Painel para os campos de edição
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Adiciona os rótulos e os campos na grade
        gbc.gridx = 0; gbc.gridy = 0; painelCampos.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(comboPaciente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Data (dd/MM/yyyy):"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(txtDataExame, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painelCampos.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollDesc = new JScrollPane(txtDescricao);
        painelCampos.add(scrollDesc, gbc);

        add(painelCampos, BorderLayout.NORTH);

        // Painel para os botões
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAtualizar = new JButton("Atualizar");
        JButton btnSair = new JButton("Sair");
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnSair);
        add(painelBotoes, BorderLayout.SOUTH);

        // Associa as ações aos botões
        btnAtualizar.addActionListener(this::acaoAtualizar);
        btnSair.addActionListener(e -> dispose());

        // Adiciona um ouvinte para o evento de seleção da tabela
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                int linha = table.getSelectedRow();
                if (linha >= 0) {
                    Long idExame = (Long) tableModel.getValueAt(linha, 0);
                    carregarExameSelecionado(idExame);
                }
            }
        });

        // Define o tamanho e a posição final da janela
        setPreferredSize(new Dimension(700, 450));
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    // Carrega todos os exames do banco e exibe na tabela
    private void carregarExames() {
        List<Exame> exames = exameDAO.findAll();
        tableModel.setRowCount(0); // Limpa a tabela
        for (Exame ex : exames) {
            Paciente p = pacienteDAO.findById(ex.getPacienteId());
            String nomePaciente = p != null ? p.getNome() + " (" + p.getCpf() + ")" : "Desconhecido";
            String dataStr = ex.getDataExame() != null ? ex.getDataExame().format(formatter) : "";
            tableModel.addRow(new Object[] {
                ex.getId(),
                ex.getDescricao(),
                dataStr,
                nomePaciente
            });
        }
    }

    // Carrega a lista de pacientes do banco e exibe na combo box
    private void carregarPacientes() {
        List<Paciente> pacientes = pacienteDAO.findAll();
        DefaultComboBoxModel<Paciente> model = new DefaultComboBoxModel<>();
        for (Paciente p : pacientes) {
            model.addElement(p);
        }
        comboPaciente.setModel(model);
    }

    // Carrega os dados do exame selecionado nos campos de edição
    private void carregarExameSelecionado(Long idExame) {
        exameSelecionado = exameDAO.findById(idExame);
        if (exameSelecionado != null) {
            // Preenche os campos com os dados do exame
            Paciente paciente = pacienteDAO.findById(exameSelecionado.getPacienteId());
            if (paciente != null) {
                comboPaciente.setSelectedItem(paciente);
            } else {
                comboPaciente.setSelectedIndex(-1);
            }
            txtDataExame.setText(exameSelecionado.getDataExame() != null ? exameSelecionado.getDataExame().format(formatter) : "");
            txtDescricao.setText(exameSelecionado.getDescricao());
        }
    }

    // Ação do botão 'Atualizar': valida e salva as alterações
    private void acaoAtualizar(ActionEvent e) {
        if (exameSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um exame na tabela para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Valida se um paciente foi selecionado
        Paciente pacienteSelecionado = (Paciente) comboPaciente.getSelectedItem();
        if (pacienteSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um paciente válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String dataStr = txtDataExame.getText().trim();
        // Valida se a data é válida
        if (!ValidacaoUtil.isDataValida(dataStr)) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use o formato dd/MM/yyyy.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String descricao = txtDescricao.getText().trim();
        // Valida se a descrição não está vazia
        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Descrição não pode ser vazia.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Atualiza o objeto exame com os novos dados
            exameSelecionado.setPacienteId(pacienteSelecionado.getId());
            exameSelecionado.setDataExame(LocalDate.parse(dataStr, formatter));
            exameSelecionado.setDescricao(descricao);

            // Chama o DAO para atualizar o exame no banco de dados
            exameDAO.update(exameSelecionado);
            JOptionPane.showMessageDialog(this, "Exame atualizado com sucesso!");
            
            // Recarrega a tabela para exibir as alterações
            carregarExames();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar exame: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
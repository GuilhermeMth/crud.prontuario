package crud.prontuario.app.paciente;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;
import crud.prontuario.util.ValidacaoUtil;

// Janela para localizar pacientes por nome ou CPF
public class PacienteFindDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Campos de busca
    private final JTextField txtBuscaNome = new JTextField();
    private final JFormattedTextField txtBuscaCpf;

    // Botões de rádio para escolher o tipo de busca
    private final JRadioButton rbNome = new JRadioButton("Nome");
    private final JRadioButton rbCpf = new JRadioButton("CPF");

    // Botões de ação
    private final JButton btnPesquisar = new JButton("Pesquisar");
    private final JButton btnSair = new JButton("Sair");

    // Tabela e seu modelo para exibir os resultados
    private final JTable table;
    private final DefaultTableModel tableModel;

    // Objeto para interagir com o banco de dados
    private final PacienteDAO pacienteDAO;

    // Formatador para a data (DD/MM/AAAA)
    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor: monta a janela de busca
    public PacienteFindDialog(Frame owner) {
        super(owner, "Localizar Paciente", true);

        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Tenta criar o campo de CPF com máscara
        JFormattedTextField tempCpf = null;
        try {
            MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
            cpfMask.setPlaceholderCharacter('_');
            tempCpf = new JFormattedTextField(cpfMask);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        txtBuscaCpf = tempCpf;

        // Define um tamanho fixo para os campos de busca
        Dimension tamanhoFixo = new Dimension(200, 24);
        txtBuscaNome.setPreferredSize(tamanhoFixo);
        txtBuscaNome.setMinimumSize(tamanhoFixo);
        txtBuscaCpf.setPreferredSize(tamanhoFixo);
        txtBuscaCpf.setMinimumSize(tamanhoFixo);

        // Configurações básicas da janela
        setPreferredSize(new Dimension(700, 400));
        setResizable(false);
        setLocationRelativeTo(owner);

        // Painel principal com layout BorderLayout
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setContentPane(painelPrincipal);

        // Painel para os controles de busca
        JPanel painelBusca = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Adiciona os rótulos e botões de rádio
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 5); gbc.anchor = GridBagConstraints.WEST;
        painelBusca.add(new JLabel("Buscar por:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 5);
        painelBusca.add(rbNome, gbc);
        gbc.gridx = 2; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 0, 15);
        painelBusca.add(rbCpf, gbc);

        // Adiciona os campos de busca. Inicialmente apenas o de nome está visível
        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 0, 5);
        painelBusca.add(txtBuscaNome, gbc);
        painelBusca.add(txtBuscaCpf, gbc);
        txtBuscaNome.setVisible(true);
        txtBuscaCpf.setVisible(false);

        // Adiciona o botão 'Pesquisar'
        gbc.gridx = 4; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.insets = new Insets(0, 0, 0, 0);
        btnPesquisar.setPreferredSize(new Dimension(100, 24));
        painelBusca.add(btnPesquisar, gbc);

        painelPrincipal.add(painelBusca, BorderLayout.NORTH);

        // Define as colunas e o modelo da tabela
        String[] colunas = {"ID", "Nome", "CPF", "Data de Nascimento"};
        tableModel = new DefaultTableModel(colunas, 0) {
            private static final long serialVersionUID = 1L;
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Adiciona a tabela em um painel com rolagem
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Painel para o botão 'Sair'
        JPanel painelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        painelInferior.add(btnSair);
        painelPrincipal.add(painelInferior, BorderLayout.SOUTH);

        // Agrupa os botões de rádio para que apenas um possa ser selecionado
        ButtonGroup grupoBusca = new ButtonGroup();
        grupoBusca.add(rbNome);
        grupoBusca.add(rbCpf);
        rbNome.setSelected(true);

        // Adiciona ouvintes para alternar os campos de busca
        rbNome.addActionListener(e -> {
            txtBuscaNome.setVisible(true);
            txtBuscaCpf.setVisible(false);
            txtBuscaCpf.setValue(null);
            txtBuscaNome.requestFocusInWindow();
            painelBusca.revalidate();
            painelBusca.repaint();
        });

        rbCpf.addActionListener(e -> {
            txtBuscaNome.setVisible(false);
            txtBuscaCpf.setVisible(true);
            txtBuscaNome.setText("");
            txtBuscaCpf.requestFocusInWindow();
            painelBusca.revalidate();
            painelBusca.repaint();
        });

        // Adiciona as ações aos botões
        btnPesquisar.addActionListener(e -> pesquisarPacientes());
        btnSair.addActionListener(e -> dispose());

        pack();
    }

    // Método principal de pesquisa
    private void pesquisarPacientes() {
        String buscaValor;

        // Determina qual campo usar para a busca
        if (rbCpf.isSelected()) {
            buscaValor = txtBuscaCpf.getText().trim();
        } else {
            buscaValor = txtBuscaNome.getText().trim();
        }

        // Lógica para busca por CPF
        if (rbCpf.isSelected()) {
            if (buscaValor.isEmpty()) {
                limparTabela();
                return;
            }
            // Validações de formato e validade do CPF
            if (!ValidacaoUtil.isFormatoCPF(buscaValor)) {
                JOptionPane.showMessageDialog(this, "Formato de CPF inválido. Use XXX.XXX.XXX-XX.", "Erro", JOptionPane.ERROR_MESSAGE);
                limparTabela();
                return;
            }
            if (!ValidacaoUtil.isCPFValido(buscaValor)) {
                JOptionPane.showMessageDialog(this, "CPF inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                limparTabela();
                return;
            }

            try {
                // Busca o paciente no banco de dados e exibe o resultado
                Paciente p = pacienteDAO.findByCpf(buscaValor);
                limparTabela();
                if (p != null) {
                    adicionarPacienteNaTabela(p);
                } else {
                    JOptionPane.showMessageDialog(this, "Paciente não encontrado.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao buscar paciente por CPF: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }

        } else { // Lógica para busca por nome
            if (!buscaValor.isEmpty() && buscaValor.length() < 3) {
                JOptionPane.showMessageDialog(this, "Digite pelo menos 3 caracteres para busca por nome.", "Aviso", JOptionPane.WARNING_MESSAGE);
                limparTabela();
                return;
            }

            try {
                // Busca pacientes por nome e exibe os resultados na tabela
                List<Paciente> resultados = pacienteDAO.findByNome(buscaValor);
                limparTabela();
                if (resultados.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nenhum paciente encontrado com esse nome.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    for (Paciente p : resultados) {
                        adicionarPacienteNaTabela(p);
                    }
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao buscar pacientes: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Limpa todas as linhas da tabela
    private void limparTabela() {
        tableModel.setRowCount(0);
    }

    // Adiciona um paciente à tabela
    private void adicionarPacienteNaTabela(Paciente p) {
        String dataStr = p.getDataNascimento() != null ? p.getDataNascimento().toLocalDate().format(formatterData) : "";
        tableModel.addRow(new Object[]{p.getId(), p.getNome(), p.getCpf(), dataStr});
    }
}
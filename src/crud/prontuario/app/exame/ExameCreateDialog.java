package crud.prontuario.app.exame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.ExameDAO;
import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;
import crud.prontuario.util.ValidacaoUtil;

// Esta é a janela para criar um novo exame
public class ExameCreateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Campos da interface gráfica (JComboBox para paciente, campo de data e área de texto)
    private final JComboBox<Paciente> comboPaciente;
    private final JFormattedTextField campoDataExame;
    private final JTextArea campoDescricao;

    // Objetos para interagir com o banco de dados
    private final ExameDAO exameDAO;
    private final PacienteDAO pacienteDAO;

    // Formatador para padronizar a data (DD/MM/AAAA)
    private final DateTimeFormatter formatterData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor: monta a janela e inicializa os componentes
    public ExameCreateDialog(Frame owner) throws Exception {
        super(owner, "Criar Exame", true);

        // Instancia os DAOs para acesso ao banco de dados
        exameDAO = new ExameDAO(new DatabaseConnectionMySQL());
        pacienteDAO = new PacienteDAO(new DatabaseConnectionMySQL());

        // Inicializa os componentes visuais
        comboPaciente = new JComboBox<>();
        campoDataExame = criarCampoData();
        campoDescricao = new JTextArea(5, 20);
        campoDescricao.setLineWrap(true);
        campoDescricao.setWrapStyleWord(true);

        // Carrega a lista de pacientes do banco de dados para a combo box
        carregarPacientes();

        // Configura o layout e os botões da janela
        initLayout();

        // Define o tamanho e o comportamento da janela
        setPreferredSize(new Dimension(500, 300));
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    // Método para criar um campo de texto com máscara para o formato de data
    private JFormattedTextField criarCampoData() {
        try {
            MaskFormatter mf = new MaskFormatter("##/##/####");
            mf.setPlaceholderCharacter('_');
            return new JFormattedTextField(mf);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar máscara de data", e);
        }
    }

    // Carrega a lista de pacientes do banco e exibe na combo box
    private void carregarPacientes() throws Exception {
        List<Paciente> pacientes = pacienteDAO.findAll();
        comboPaciente.removeAllItems();
        for (Paciente p : pacientes) {
            comboPaciente.addItem(p);
        }
        // Personaliza como o nome do paciente é exibido na lista
        comboPaciente.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value == null) return new JLabel("");
            return new JLabel(value.getNome() + " (CPF: " + value.getCpf() + ")");
        });
    }

    // Monta o layout da janela, organizando os campos e botões
    private void initLayout() {
        setLayout(new BorderLayout(10, 10));
        JPanel painelCampos = new JPanel(new GridBagLayout());
        painelCampos.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Adiciona os rótulos e os campos na grade
        gbc.gridx = 0; gbc.gridy = 0; painelCampos.add(new JLabel("Paciente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(comboPaciente, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Data do Exame:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        painelCampos.add(campoDataExame, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        painelCampos.add(new JLabel("Descrição:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane scrollDesc = new JScrollPane(campoDescricao);
        painelCampos.add(scrollDesc, gbc);

        add(painelCampos, BorderLayout.CENTER);

        // Painel para os botões 'Salvar', 'Limpar' e 'Sair'
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnLimpar = new JButton("Limpar");
        JButton btnSair = new JButton("Sair");

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnLimpar);
        painelBotoes.add(btnSair);
        add(painelBotoes, BorderLayout.SOUTH);

        // Associa as ações aos botões
        btnSalvar.addActionListener(this::acaoSalvar);
        btnLimpar.addActionListener(e -> limparCampos());
        btnSair.addActionListener(e -> dispose());
    }

    // Ação do botão 'Salvar': coleta, valida os dados e salva no banco
    private void acaoSalvar(ActionEvent e) {
        Paciente pacienteSelecionado = (Paciente) comboPaciente.getSelectedItem();
        String dataStr = campoDataExame.getText().trim();
        String descricao = campoDescricao.getText().trim();

        // Valida se os campos obrigatórios foram preenchidos
        if (pacienteSelecionado == null) {
            mostrarErro("Selecione um paciente.");
            return;
        }
        if (ValidacaoUtil.isVazio(dataStr)) {
            mostrarErro("Data do exame é obrigatória.");
            return;
        }
        if (!ValidacaoUtil.isDataValida(dataStr)) {
            mostrarErro("Data inválida. Use o formato DD/MM/AAAA.");
            return;
        }
        if (ValidacaoUtil.isVazio(descricao)) {
            mostrarErro("Descrição do exame é obrigatória.");
            return;
        }

        try {
            // Converte a data de String para LocalDate e cria o objeto Exame
            LocalDate dataExame = LocalDate.parse(dataStr, formatterData);
            Exame exame = new Exame();
            exame.setPacienteId(pacienteSelecionado.getId());
            exame.setDataExame(dataExame);
            exame.setDescricao(descricao);

            // Chama o DAO para salvar o exame no banco
            exameDAO.create(exame);

            // Exibe mensagem de sucesso e fecha a janela
            JOptionPane.showMessageDialog(this, "Exame criado com sucesso!");
            dispose();

        } catch (Exception ex) {
            // Em caso de erro, exibe uma mensagem de falha
            mostrarErro("Erro ao salvar exame: " + ex.getMessage());
        }
    }

    // Limpa todos os campos do formulário
    private void limparCampos() {
        comboPaciente.setSelectedIndex(-1);
        campoDataExame.setValue(null);
        campoDescricao.setText("");
    }

    // Exibe uma caixa de diálogo com uma mensagem de erro
    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro de validação", JOptionPane.ERROR_MESSAGE);
    }
}
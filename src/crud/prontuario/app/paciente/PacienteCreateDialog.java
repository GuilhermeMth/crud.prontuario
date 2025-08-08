package crud.prontuario.app.paciente;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.MaskFormatter;

import crud.prontuario.dao.PacienteDAO;
import crud.prontuario.database.DatabaseConnectionMySQL;
import crud.prontuario.model.Paciente;
import crud.prontuario.util.ValidacaoUtil;

// Janela para criar um novo paciente
public class PacienteCreateDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // Campos de entrada para o nome, CPF e data de nascimento
    private final JTextField campoNome = new JTextField();
    private final JFormattedTextField campoCpf;
    private final JFormattedTextField campoDataNascimento;

    // Formatador para a data (DD/MM/AAAA)
    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Construtor: monta a janela de cadastro
    public PacienteCreateDialog(Frame owner) throws Exception {
        super(owner, "Criar Paciente", true);
        setLayout(new BorderLayout());

        // Máscara para o campo CPF
        MaskFormatter cpfFormatter = new MaskFormatter("###.###.###-##");
        cpfFormatter.setPlaceholderCharacter('_');
        cpfFormatter.setValueContainsLiteralCharacters(true);
        campoCpf = new JFormattedTextField(cpfFormatter);

        // Máscara para o campo de data de nascimento
        MaskFormatter dataFormatter = new MaskFormatter("##/##/####");
        dataFormatter.setPlaceholderCharacter('_');
        dataFormatter.setValueContainsLiteralCharacters(true);
        campoDataNascimento = new JFormattedTextField(dataFormatter);

        // Painel principal para os campos de entrada
        JPanel painelCampos = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Adiciona os rótulos e campos na grade
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Nome:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoNome.setColumns(20);
        painelCampos.add(campoNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("CPF:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoCpf.setColumns(20);
        painelCampos.add(campoCpf, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        painelCampos.add(new JLabel("Data de Nascimento:"), gbc);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        campoDataNascimento.setColumns(20);
        painelCampos.add(campoDataNascimento, gbc);

        add(painelCampos, BorderLayout.CENTER);

        // Painel para os botões 'Salvar', 'Limpar' e 'Sair'
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(this::acaoSalvar);
        painelBotoes.add(btnSalvar);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.addActionListener(e -> limparCampos());
        painelBotoes.add(btnLimpar);

        JButton btnSair = new JButton("Sair");
        btnSair.addActionListener(e -> dispose());
        painelBotoes.add(btnSair);

        add(painelBotoes, BorderLayout.SOUTH);

        // Define o tamanho e o comportamento da janela
        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    // Limpa todos os campos do formulário
    private void limparCampos() {
        campoNome.setText("");
        campoCpf.setValue(null);
        campoDataNascimento.setValue(null);
    }

    // Ação do botão 'Salvar': valida os dados e cadastra o paciente
    private void acaoSalvar(ActionEvent e) {
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
            DatabaseConnectionMySQL dbConn = new DatabaseConnectionMySQL();
            PacienteDAO dao = new PacienteDAO(dbConn);

            // Verifica se o CPF já está cadastrado
            if (dao.findByCpf(cpf) != null) {
                mostrarErro("CPF já cadastrado.");
                return;
            }

            // Converte a data de String para LocalDateTime
            LocalDate localDate = LocalDate.parse(data, FORMATTER_DATA);
            LocalDateTime dataNascimento = localDate.atStartOfDay();

            // Cria o objeto Paciente e salva no banco de dados
            Paciente paciente = new Paciente(nome, cpf, dataNascimento);
            dao.create(paciente);

            // Exibe mensagem de sucesso e fecha a janela
            JOptionPane.showMessageDialog(this, "Paciente cadastrado com sucesso!");
            dispose();

        } catch (Exception ex) {
            mostrarErro("Erro ao salvar paciente: " + ex.getMessage());
        }
    }

    // Exibe uma caixa de diálogo com uma mensagem de erro
    private void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro de validação", JOptionPane.ERROR_MESSAGE);
    }
}
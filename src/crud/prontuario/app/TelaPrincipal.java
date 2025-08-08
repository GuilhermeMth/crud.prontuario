package crud.prontuario.app;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import crud.prontuario.app.exame.ExameCreateDialog;
import crud.prontuario.app.exame.ExameDeleteDialog;
import crud.prontuario.app.exame.ExameEditDialog;
import crud.prontuario.app.exame.ExameFindDialog;
import crud.prontuario.app.paciente.PacienteCreateDialog;
import crud.prontuario.app.paciente.PacienteDeleteDialog;
import crud.prontuario.app.paciente.PacienteEditDialog;
import crud.prontuario.app.paciente.PacienteFindDialog;
import crud.prontuario.database.DatabaseInitializer;

// A janela principal da aplicação
public class TelaPrincipal extends JFrame {

    private static final long serialVersionUID = 1L;

    // Construtor: configura a janela
    public TelaPrincipal() {
        // Título e tamanho da janela
        setTitle("CRUD Prontuário");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Define o layout principal
        setLayout(new BorderLayout());

        // Cria a barra de menu
        JMenuBar menuBar = new JMenuBar();

        // Menu para 'Pacientes'
        JMenu menuPacientes = new JMenu("Pacientes");
        
        // --- Itens do menu Pacientes ---
        
        // Item 'Novo': abre a tela de cadastro
        JMenuItem itemNovoPaciente = new JMenuItem("Novo");
        itemNovoPaciente.addActionListener(e -> {
            try {
                PacienteCreateDialog dialog = new PacienteCreateDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de criação: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuPacientes.add(itemNovoPaciente);
        
        // Item 'Editar': abre a tela de edição
        JMenuItem itemEditarPaciente = new JMenuItem("Editar");
        itemEditarPaciente.addActionListener(e -> {
            try {
                PacienteEditDialog dialog = new PacienteEditDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de edição: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuPacientes.add(itemEditarPaciente);

        // Item 'Localizar': abre a tela de busca
        JMenuItem itemLocalizarPaciente = new JMenuItem("Localizar");
        itemLocalizarPaciente.addActionListener(e -> {
            try {
                PacienteFindDialog dialog = new PacienteFindDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de localização: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuPacientes.add(itemLocalizarPaciente);

        // Item 'Excluir': abre a tela de exclusão
        JMenuItem itemExcluirPaciente = new JMenuItem("Excluir");
        itemExcluirPaciente.addActionListener(e -> {
            try {
                PacienteDeleteDialog dialog = new PacienteDeleteDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de exclusão: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuPacientes.add(itemExcluirPaciente);

        // Menu para 'Exames'
        JMenu menuExames = new JMenu("Exames");

        // --- Itens do menu Exames ---
        
        // Item 'Novo Exame'
        JMenuItem itemNovoExame = new JMenuItem("Novo");
        itemNovoExame.addActionListener(e -> {
            try {
                ExameCreateDialog dialog = new ExameCreateDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de criação de exame: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuExames.add(itemNovoExame);

        // Item 'Editar Exame'
        JMenuItem itemEditarExame = new JMenuItem("Editar");
        itemEditarExame.addActionListener(e -> {
            try {
                ExameEditDialog dialog = new ExameEditDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de edição de exame: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuExames.add(itemEditarExame);

        // Item 'Localizar Exame'
        JMenuItem itemLocalizarExame = new JMenuItem("Localizar");
        itemLocalizarExame.addActionListener(e -> {
            try {
                ExameFindDialog dialog = new ExameFindDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de localização de exame: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuExames.add(itemLocalizarExame);

        // Item 'Excluir Exame'
        JMenuItem itemExcluirExame = new JMenuItem("Excluir");
        itemExcluirExame.addActionListener(e -> {
            try {
                ExameDeleteDialog dialog = new ExameDeleteDialog(this);
                dialog.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Erro ao abrir janela de exclusão de exame: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
        menuExames.add(itemExcluirExame);

        // Menu para 'Sair'
        JMenu menuSair = new JMenu("Sair");
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.addActionListener((ActionEvent e) -> {
            // Pede confirmação para sair
            int resposta = JOptionPane.showConfirmDialog(
                    this,
                    "Deseja realmente sair?",
                    "Confirmação de Saída",
                    JOptionPane.YES_NO_OPTION);
            if (resposta == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        menuSair.add(itemSair);

        // Adiciona todos os menus à barra
        menuBar.add(menuPacientes);
        menuBar.add(menuExames);
        menuBar.add(menuSair);

        // Define a barra de menu da janela
        setJMenuBar(menuBar);

        // Conteúdo da parte central da janela
        JPanel painelCentral = new JPanel(new BorderLayout());
        JLabel mensagem = new JLabel("Bem-vindo ao CRUD Prontuário!", JLabel.CENTER);
        painelCentral.add(mensagem, BorderLayout.CENTER);

        add(painelCentral, BorderLayout.CENTER);
    }

    // Método principal: ponto de entrada do programa
    public static void main(String[] args) {
        // Inicializa o banco de dados
        DatabaseInitializer.inicializarBanco();

        // Inicia a interface gráfica de forma segura
        SwingUtilities.invokeLater(() -> {
            new TelaPrincipal().setVisible(true);
        });
    }
}
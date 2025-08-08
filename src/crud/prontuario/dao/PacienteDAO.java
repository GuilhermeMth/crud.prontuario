package crud.prontuario.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;
import crud.prontuario.model.Paciente;

// Classe de acesso a dados (DAO) para a entidade Paciente
public class PacienteDAO implements IEntityDAO<Paciente> {

    // Conexão com o banco de dados
    private IConnection conn;
    // DAO para a entidade Exame (para operações em cascata)
    private IEntityDAO<Exame> exameDAO;

    // Construtor que inicializa a conexão e o exameDAO
    public PacienteDAO(IConnection conn) {
        this.conn = conn;
        this.exameDAO = new ExameDAO(conn);
    }

    // Cria um novo paciente no banco de dados
    @Override
    public void create(Paciente t) {
        // Validação básica dos campos obrigatórios
        if (t.getCpf() == null || t.getNome() == null || t.getDataNascimento() == null) {
            System.out.println("[ERRO] CPF, Nome e Data de Nascimento são obrigatórios.");
            return;
        }

        try (Connection connection = conn.getConnection()) {
            // Prepara a query SQL de inserção
            PreparedStatement pstm = connection.prepareStatement(
                "INSERT INTO PACIENTES (nome, cpf, data_nascimento) VALUES (?, ?, ?);",
                PreparedStatement.RETURN_GENERATED_KEYS);
            // Define os valores dos parâmetros
            pstm.setString(1, t.getNome());
            pstm.setString(2, t.getCpf());
            pstm.setTimestamp(3, Timestamp.valueOf(t.getDataNascimento()));

            // Executa a atualização
            int affected = pstm.executeUpdate();
            if (affected == 0) {
                System.out.println("[ERRO] Nenhum paciente foi inserido.");
                return;
            }

            // Obtém e define o ID gerado para o objeto Paciente
            ResultSet keys = pstm.getGeneratedKeys();
            if (keys.next()) {
                t.setId(keys.getLong(1));
            }

            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao cadastrar paciente: " + e.getMessage());
        }

        // Se houver exames associados, cria-os também
        if (t.getExames() != null) {
            for (Exame ex : t.getExames()) {
                exameDAO.create(ex);
            }
        }
    }

    // Busca um paciente por ID
    @Override
    public Paciente findById(Long id) {
        Paciente p = null;
        try (Connection connection = conn.getConnection()) {
            // Prepara a query SQL de busca
            PreparedStatement pstm = connection.prepareStatement(
                "SELECT * FROM PACIENTES WHERE id = ?;");
            pstm.setLong(1, id);

            // Executa a query
            ResultSet rs = pstm.executeQuery();
            // Se encontrar, cria o objeto Paciente
            if (rs.next()) {
                p = new Paciente();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                Timestamp ts = rs.getTimestamp("data_nascimento");
                if (ts != null) {
                    p.setDataNascimento(ts.toLocalDateTime());
                }
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar paciente: " + e.getMessage());
        }
        return p;
    }
    
    // Busca um paciente por CPF
    public Paciente findByCpf(String cpf) {
        Paciente p = null;
        try (Connection connection = conn.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement(
                "SELECT * FROM PACIENTES WHERE cpf = ?;");
            pstm.setString(1, cpf);

            ResultSet rs = pstm.executeQuery();
            if (rs.next()) {
                p = new Paciente();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                Timestamp ts = rs.getTimestamp("data_nascimento");
                if (ts != null) {
                    p.setDataNascimento(ts.toLocalDateTime());
                }
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar paciente por CPF: " + e.getMessage());
        }
        return p;
    }
    
    // Busca pacientes por nome, usando LIKE
    public List<Paciente> findByNome(String nome) {
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection connection = conn.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement(
                "SELECT * FROM PACIENTES WHERE nome LIKE ?;");
            pstm.setString(1, "%" + nome + "%"); // Adiciona os curingas para a busca
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Paciente p = new Paciente();
                p.setId(rs.getLong("id"));
                p.setNome(rs.getString("nome"));
                p.setCpf(rs.getString("cpf"));
                Timestamp ts = rs.getTimestamp("data_nascimento");
                if (ts != null) {
                    p.setDataNascimento(ts.toLocalDateTime());
                }
                pacientes.add(p);
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar paciente por nome: " + e.getMessage());
        }
        return pacientes;
    }

    // Deleta um paciente do banco de dados
    @Override
    public void delete(Paciente t) {
        try (Connection connection = conn.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement(
                "DELETE FROM PACIENTES WHERE id = ?;");
            pstm.setLong(1, t.getId());

            int deleted = pstm.executeUpdate();
            if (deleted == 0) {
                System.out.println("[INFO] Nenhum paciente foi removido.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao remover paciente: " + e.getMessage());
        }
    }

    // Busca todos os pacientes
    @Override
    public List<Paciente> findAll() {
        List<Paciente> pacientes = new ArrayList<>();
        try (Connection connection = conn.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement(
                "SELECT * FROM PACIENTES;");
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Paciente p = new Paciente(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getTimestamp("data_nascimento") != null ? rs.getTimestamp("data_nascimento").toLocalDateTime() : null
                );
                pacientes.add(p);
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar pacientes: " + e.getMessage());
        }
        return pacientes;
    }

    // Atualiza um paciente existente no banco de dados
    @Override
    public void update(Paciente t) {
        try (Connection connection = conn.getConnection()) {
            PreparedStatement pstm = connection.prepareStatement(
                "UPDATE PACIENTES SET nome = ?, cpf = ?, data_nascimento = ? WHERE id = ?;");
            pstm.setString(1, t.getNome());
            pstm.setString(2, t.getCpf());
            pstm.setTimestamp(3, Timestamp.valueOf(t.getDataNascimento()));
            pstm.setLong(4, t.getId());

            int updated = pstm.executeUpdate();
            if (updated == 0) {
                System.out.println("[INFO] Nenhum paciente foi atualizado.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao atualizar paciente: " + e.getMessage());
        }
    }
}
package crud.prontuario.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import crud.prontuario.database.IConnection;
import crud.prontuario.model.Exame;

// Classe que implementa a interface de acesso a dados para a entidade Exame
public class ExameDAO implements IEntityDAO<Exame> {

    // Conexão com o banco de dados
    private IConnection conn;

    // Construtor que recebe a conexão
    public ExameDAO(IConnection connection) {
        this.conn = connection;
    }

    // Método para criar um novo exame no banco de dados
    @Override
    public void create(Exame t) {
        try {
            // Prepara a query SQL de inserção
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("INSERT INTO EXAMES (descricao, data_exame, paciente_id) VALUES (?, ?, ?);",
                            PreparedStatement.RETURN_GENERATED_KEYS);
            // Define os valores dos parâmetros
            pstm.setString(1, t.getDescricao());
            pstm.setDate(2, Date.valueOf(t.getDataExame()));
            pstm.setLong(3, t.getPacienteId());

            // Executa a atualização
            int affectedRows = pstm.executeUpdate();

            if (affectedRows == 0) {
                System.out.println("[ERRO] Nenhuma linha foi inserida para o exame.");
                return;
            }

            // Obtém o ID gerado e define no objeto
            ResultSet generatedKeys = pstm.getGeneratedKeys();
            if (generatedKeys.next()) {
                long generatedId = generatedKeys.getLong(1);
                t.setId(generatedId);
            } else {
                System.out.println("[ERRO] Falha ao obter o ID gerado para o exame.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao inserir exame: " + e.getMessage());
        }
    }

    // Método para buscar um exame por ID
    @Override
    public Exame findById(Long id) {
        Exame ex = null;
        try {
            // Prepara a query SQL de busca
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("SELECT * FROM EXAMES WHERE id = ?;");
            pstm.setLong(1, id);
            // Executa a query
            ResultSet rs = pstm.executeQuery();
            // Se encontrar o registro, cria o objeto Exame
            if (rs.next()) {
                ex = new Exame();
                ex.setId(rs.getLong("id"));
                ex.setDescricao(rs.getString("descricao"));
                ex.setDataExame(rs.getDate("data_exame").toLocalDate());
                ex.setPacienteId(rs.getLong("paciente_id"));
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao buscar exame por ID: " + e.getMessage());
        }
        return ex;
    }

    // Método para deletar um exame do banco de dados
    @Override
    public void delete(Exame t) {
        try {
            // Prepara a query SQL de deleção
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("DELETE FROM EXAMES WHERE id = ?;");
            pstm.setLong(1, t.getId());
            // Executa a atualização
            int deleted = pstm.executeUpdate();
            if (deleted == 0) {
                System.out.println("[ERRO] Nenhum exame foi deletado. Verifique o ID.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao deletar exame: " + e.getMessage());
        }
    }

    // Método para buscar todos os exames
    @Override
    public List<Exame> findAll() {
        List<Exame> exames = new ArrayList<>();
        try {
            // Prepara a query SQL para buscar todos os registros
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("SELECT * FROM EXAMES;");
            // Executa a query
            ResultSet rs = pstm.executeQuery();
            // Itera sobre os resultados e adiciona na lista
            while (rs.next()) {
                exames.add(new Exame(
                        rs.getLong("id"),
                        rs.getString("descricao"),
                        rs.getDate("data_exame").toLocalDate(),
                        rs.getLong("paciente_id")
                ));
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao listar exames: " + e.getMessage());
        }
        return exames;
    }

    // Método para atualizar um exame existente
    @Override
    public void update(Exame t) {
        try {
            // Prepara a query SQL de atualização
            PreparedStatement pstm = conn.getConnection()
                    .prepareStatement("UPDATE EXAMES SET descricao = ?, data_exame = ?, paciente_id = ? WHERE id = ?;");
            // Define os novos valores
            pstm.setString(1, t.getDescricao());
            pstm.setDate(2, Date.valueOf(t.getDataExame()));
            pstm.setLong(3, t.getPacienteId());
            pstm.setLong(4, t.getId());
            // Executa a atualização
            int updated = pstm.executeUpdate();
            if (updated == 0) {
                System.out.println("[ERRO] Nenhum exame foi atualizado. Verifique o ID.");
            }
            pstm.close();
        } catch (SQLException e) {
            System.out.println("[ERRO] Falha ao atualizar exame: " + e.getMessage());
        }
    }
}
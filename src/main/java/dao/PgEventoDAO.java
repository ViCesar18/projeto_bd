package dao;

import model.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PgEventoDAO implements EventoDAO {
    private final Connection connection;

    private static final String CREATE_QUERY =
            "INSERT INTO rede_musical.evento" +
            "(nome, descricao, data_inicio, data_termino, numero_participantes, categoria, usuario_id, nome_local, rua, numero, bairro, cep)" +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private static final String READ_QUERY =
            "SELECT nome, descricao, data_inicio, data_termino, numero_participantes, categoria, usuario_id, nome_local, rua, numero, bairro, cep" +
            " FROM rede_musical.evento" +
            " WHERE id = ?;";

    private static final String UPDATE_EVENTO_DATA_QUERY =
            "UPDATE rede_musical.evento" +
            " SET nome = ?, descricao = ?, data_inicio = ?, data_termino = ?, numero_participantes = ?, categoria = ?, nome_local = ?, rua = ?, numero = ?, bairro = ?, cep = ?" +
            " WHERE id = ?;";

    private static final String DELETE_QUERY =
            "DELETE FROM rede_musical.evento" +
            " WHERE id = ?;";

    private static final String ALL_QUERY =
            "SELECT id, nome, descricao, categoria, data_inicio, data_termino, numero_participantes, nome_local, rua, numero, bairro, cep, usuario_id" +
            " FROM rede_musical.evento" +
            " ORDER BY id;";

    private static String INSERT_USUARIO_COMPARECE_EVENTO =
            "INSERT INTO rede_musical.usuario_comparece_em_evento " +
            "(usuario_id, evento_id)" +
            "VALUES(?,?);";

    private static String DELETE_USUARIO_COMPARECE_EVENTO =
            "DELETE FROM rede_musical.usuario_comparece_em_evento " +
            "WHERE usuario_id = ? AND evento_id = ?;";

    private static String READ_USUARIO_COMPARECE_EVENTO =
            "SELECT COUNT(*) > 0 AS comparece " +
            "FROM rede_musical.usuario_comparece_em_evento " +
            "WHERE usuario_id = ? AND evento_id = ?;";

    private static final String NUMERO_DE_PARTICIPANTES =
            "SELECT COUNT(*) AS participantes " +
            "FROM rede_musical.usuario_comparece_em_evento " +
            "WHERE evento_id = ?;";

    @Override
    public void create(Evento evento) throws SQLException {
        try (PreparedStatement statement = this.connection.prepareStatement(CREATE_QUERY)) {
            statement.setString(1, evento.getNome());
            statement.setString(2, evento.getDescricao());
            statement.setTimestamp(3, evento.getData_inicio());
            statement.setTimestamp(4, evento.getData_termino());
            statement.setInt(5, evento.getnParticipantes());
            statement.setString(6, evento.getCategoria());
            statement.setInt(7, evento.getUsername_id());
            statement.setString(8, evento.getNome_local());
            statement.setString(9, evento.getRua());
            statement.setString(10, evento.getNumero());
            statement.setString(11, evento.getBairro());
            statement.setString(12, evento.getCep());

            statement.executeUpdate();
        } catch (SQLException e){
            Logger.getLogger(PgEventoDAO.class.getName()).log(Level.SEVERE,"DAO", e);

            if (e.getMessage().contains("uq_evento_id")) {
                throw new SQLException("Erro ao inserir evento: id já existente");
            }
            else if(e.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao inserir evento: pelo menos um campo está em branco.");
            }
            else {
                throw new SQLException("Erro ao inserir evento.");
            }
        }
    }

    @Override
    public Evento read(Integer id) throws SQLException {
        Evento evento = new Evento();

        try (PreparedStatement statement = connection.prepareStatement(READ_QUERY)){
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()){
                if (result.next()){
                    evento.setId(id);
                    evento.setNome(result.getString("nome"));
                    evento.setDescricao(result.getString("descricao"));
                    evento.setData_inicio(result.getTimestamp("data_inicio"));
                    evento.setData_termino(result.getTimestamp("data_termino"));
                    evento.setnParticipantes(result.getInt("numero_participantes"));
                    evento.setCategoria(result.getString("categoria"));
                    evento.setUsername_id(result.getInt("usuario_id"));
                    evento.setNome_local(result.getString("nome_local"));
                    evento.setRua(result.getString("rua"));
                    evento.setNumero(result.getString("numero"));
                    evento.setBairro(result.getString("bairro"));
                    evento.setCep(result.getString("cep"));
                }
                else {
                    throw new SQLException("Erro ao vizualizar: evento não encontrado");
                }
            }
        } catch(SQLException e){
            Logger.getLogger(PgEventoDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            if (e.getMessage().equals("Erro ao vizualizar: evento não encontrado")) {
                throw e;
            }
            else{
                throw new SQLException("Erro ao vizualizar.");
            }
        }

        return evento;
    }

    @Override
    public void update(Evento evento) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_EVENTO_DATA_QUERY)) {
            statement.setString(1, evento.getNome());
            statement.setString(2, evento.getDescricao());
            statement.setTimestamp(3, evento.getData_inicio());
            statement.setTimestamp(4, evento.getData_termino());
            statement.setInt(5, evento.getnParticipantes());
            statement.setString(6, evento.getCategoria());
            statement.setString(7, evento.getNome_local());
            statement.setString(8, evento.getRua());
            statement.setString(9, evento.getNumero());
            statement.setString(10, evento.getBairro());
            statement.setString(11, evento.getCep());
            statement.setInt(12, evento.getId());

            if (statement.executeUpdate() < 1){
                throw new SQLException("Erro ao editar: evento não encontrado.");
            }
        } catch (SQLException e){
            Logger.getLogger(PgEventoDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            if (e.getMessage().equals("Erro ao editar: evento não encontrado.")){
                throw e;
            }
            else if (e.getMessage().contains("uq_evento_id")){
                throw new SQLException("Erro ao editar: evento já existe.");
            }
            else if(e.getMessage().contains("not-null")) {
                throw new SQLException("Erro ao editar: pelo menos um campo está em branco.");
            }
            else {
                throw new SQLException("Erro ao editar.");
            }
        }
    }

    @Override
    public void delete(Integer id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_QUERY)){
            statement.setInt(1, id);

            if (statement.executeUpdate() < 1){
                throw new SQLException("Erro ao deletar: evento não encontrado.");
            }
        } catch (SQLException e){
            Logger.getLogger(PgEventoDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            if (e.getMessage().equals("Erro ao deletar: evento não encontrado")){
                throw e;
            }
            else{
                throw new SQLException("Erro ao deletar");
            }
        }
    }

    @Override
    public List<Evento> all() throws SQLException {
        List<Evento> eventos = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement(ALL_QUERY);
             ResultSet result = statement.executeQuery()){
            while (result.next()){
                Evento evento = new Evento();

                evento.setId(result.getInt("id"));
                evento.setNome(result.getString("nome"));
                evento.setDescricao(result.getString("descricao"));
                evento.setCategoria(result.getString("categoria"));
                evento.setData_inicio(result.getTimestamp("data_inicio"));
                evento.setData_termino(result.getTimestamp("data_termino"));
                evento.setNome_local(result.getString("nome_local"));
                evento.setRua(result.getString("rua"));
                evento.setNumero(result.getString("numero"));
                evento.setBairro(result.getString("bairro"));
                evento.setCep(result.getString("cep"));
                evento.setnParticipantes(result.getInt("numero_participantes"));
                evento.setUsername_id(result.getInt("usuario_id"));



                eventos.add (evento);
            }
        } catch (SQLException e){
            Logger.getLogger(PgEventoDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            throw new SQLException("Erro ao listar eventos");
        }

        return eventos;

    }

    public void insertUsuarioCompareceEvento(Integer idUsuario, Integer idEvento) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USUARIO_COMPARECE_EVENTO)){
            statement.setInt(1, idUsuario);
            statement.setInt(2, idEvento);

            statement.executeUpdate();
        } catch (SQLException e){
            Logger.getLogger(PgUsuarioDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            throw new SQLException("Erro ao comparecer em evento");
        }
    }

    public void deleteUsuarioCompareceEvento(Integer idUsuario, Integer idEvento) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USUARIO_COMPARECE_EVENTO)){
            statement.setInt(1, idUsuario);
            statement.setInt(2, idEvento);

            statement.executeUpdate();
        } catch (SQLException e){
            Logger.getLogger(PgUsuarioDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            throw new SQLException("Erro ao deixa de comparecer em evento");
        }
    }

    public Boolean readUsuarioCompareceEvento(Integer idUsuario, Integer idEvento) throws SQLException{
        Boolean comparece = false;

        try (PreparedStatement statement = connection.prepareStatement(READ_USUARIO_COMPARECE_EVENTO)){
            statement.setInt(1, idUsuario);
            statement.setInt(2, idEvento);

            try (ResultSet result = statement.executeQuery()){
                if (result.next()) {
                    String compareceStr = result.getString("comparece");

                    comparece = compareceStr.equals("t") ? true : false;
                }
            } catch (SQLException e){
                Logger.getLogger(PgUsuarioDAO.class.getName()).log(Level.SEVERE, "DAO", e);

                throw new SQLException("Erro ao verificar o comparecimento do usuario em um evento.");
            }

            return comparece;
        }
    }

    public Integer readNumeroParticipantes(Integer id) throws SQLException {
        Integer participantes = null;

        try(PreparedStatement statement = connection.prepareStatement(NUMERO_DE_PARTICIPANTES)) {
            statement.setInt(1, id);

            try(ResultSet result = statement.executeQuery()) {
                if(result.next()) {
                    participantes = Integer.parseInt(result.getString("participantes"));
                }
            }
        } catch(SQLException e) {
            Logger.getLogger(PgUsuarioDAO.class.getName()).log(Level.SEVERE, "DAO", e);

            throw new SQLException("Erro verificar numero de participantes do evento.");
        }

        return participantes;
    }

    public PgEventoDAO(Connection connection) {
        this.connection = connection;
    }
}

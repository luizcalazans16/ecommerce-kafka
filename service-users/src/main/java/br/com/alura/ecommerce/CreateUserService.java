package br.com.alura.ecommerce;

import br.com.alura.ecommerce.model.Message;
import br.com.alura.ecommerce.model.Order;
import br.com.alura.ecommerce.service.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class CreateUserService {

    private final Connection connection;

    public CreateUserService() throws SQLException {
        String databaseURL = "jdbc:sqlite:build/users_database.db";
        connection = DriverManager.getConnection(databaseURL);
        try {
            connection.createStatement().execute("create table Users (" +
                    "uuid varchar(200) primary key," +
                    "email varchar(200))");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws SQLException, ExecutionException, InterruptedException {
        var createUserService = new CreateUserService();
        try (var service = new KafkaService<>(CreateUserService.class.getSimpleName(),
                "ECOMMERCE_NEW_ORDER",
                createUserService::parse,
                Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<Order>> record) throws SQLException {
        System.out.println(record);
        System.out.println("------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.key());

        var order = record.value().getPayload();
        if (isNewUser(order.getEmail())) {
            insertNewUser(order.getEmail());

        }
    }

    private void insertNewUser(String email) throws SQLException {
        var insertStatement = connection.prepareStatement("insert into Users (uuid, email) values (?,?)");
        insertStatement.setString(1, UUID.randomUUID().toString());
        insertStatement.setString(2, email);
        insertStatement.execute();

        System.out.println("Usuário uuid e " + email + " adicionado");
    }

    private boolean isNewUser(String email) throws SQLException {
        var existsStatement = connection.prepareStatement("select uuid from Users " +
                "where email = ? limit 1");
        existsStatement.setString(1, email);
        var results = existsStatement.executeQuery();
        return !results.next();
    }
}

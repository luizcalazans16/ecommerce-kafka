package br.com.alura.ecommerce.consumer;

import br.com.alura.ecommerce.model.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {

    void consume(ConsumerRecord<String, Message<T>> record) throws Exception;
}

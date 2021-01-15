package com.nnrh.sso.mq;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Objects;

public class ActiveMqTopicConsumer {

    private static final String ACTIVE_MQ_URL = "tcp://192.168.1.56:61616";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    private static final String TOPIC_NAME_USER = "topic_iam_account_APP005";
    private static final String TOPIC_NAME_ORG = "topic_iam_org";

    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(USERNAME, PASSWORD, ACTIVE_MQ_URL);
        Connection connection = activeMQConnectionFactory.createConnection();
        connection.setClientID("client_id");
        connection.start();

        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Topic topic = session.createTopic(TOPIC_NAME_USER);
        TopicSubscriber topicSubscriber = session.createDurableSubscriber(topic, "remark");
        connection.start();

        Message message = topicSubscriber.receive();

        while (!Objects.isNull(message)) {
            TextMessage textMessage = (TextMessage) message;
            System.out.println("topic持久化的消息  " + textMessage.getText());

            // 1秒钟以后收不到消息，自动断开，相当于取关
            message = topicSubscriber.receive(1000L);
        }

        System.in.read();
        session.close();
        connection.close();
    }
}

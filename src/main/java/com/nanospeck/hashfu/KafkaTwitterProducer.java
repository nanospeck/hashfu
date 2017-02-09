package com.nanospeck.hashfu;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.*;
import twitter4j.conf.*;

import twitter4j.StallWarning;
import twitter4j.FilterQuery;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.json.DataObjectFactory;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import kafka.producer.KeyedMessage;

/**
 * A Kafka Producer that gets tweets on certain keywords
 * from twitter datasource and publishes to a kafka topic
 * 
 * Arguments: <comsumerKey> <consumerSecret> <accessToken> <accessTokenSecret> <topic-name> <keyword_1> ... <keyword_n>
 * <comsumerKey>		- Twitter consumer key 
 * <consumerSecret>  	- Twitter consumer secret
 * <accessToken>		- Twitter access token
 * <accessTokenSecret>	- Twitter access token secret
 * <topic-name>			- The kafka topic to subscribe to
 * <keyword_1>			- The keyword to filter tweets
 * <keyword_n>			- Any number of keywords to filter tweets
 * 
 * More discussion at stdatalabs.blogspot.com
 * 
 * @author Sachin Thirumala
 * ref: https://github.com/stdatalabs/SparkTwitterStreamAnalysis/blob/master/src/main/scala/com/stdatalabs/Kafka/KafkaTwitterProducer.java	
 */

public class KafkaTwitterProducer {
	 
	
	public static void main(String[] args) throws Exception {
		final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);

//		if (args.length < 4) {
//			System.out.println(
//					"Usage: KafkaTwitterProducer <twitter-consumer-key> <twitter-consumer-secret> <twitter-access-token> <twitter-access-token-secret> <topic-name> <twitter-search-keywords>");
//			return;
//		}
//
//		String topicName = args[4].toString();
//		String[] arguments = args.clone();
//		String[] keyWords = Arrays.copyOfRange(arguments, 5, arguments.length);

		String topicName = "twitter-topic";
		String[] keyWords = {"startup"};

		
		// Set twitter oAuth tokens in the configuration
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(Constants.consumerKey).setOAuthConsumerSecret(Constants.consumerSecret)
				.setOAuthAccessToken(Constants.accessToken).setOAuthAccessTokenSecret(Constants.accessTokenSecret);

		// Create twitterstream using the configuration
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		twitterStream.addListener(listener);

		// Filter keywords
		FilterQuery query = new FilterQuery().track(keyWords);
		twitterStream.filter(query);

		// Thread.sleep(5000);

		// Add Kafka producer config settings
		Properties props = new Properties();
		props.put("metadata.broker.list", "localhost:9092");
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);

		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		int i = 0;
		int j = 0;

		// poll for new tweets in the queue. If new tweets are added, send them
		// to the topic
		while (true) {
			Status ret = queue.poll();

			if (ret == null) {
				Thread.sleep(100);
				// i++;
			} else {
				//for (HashtagEntity hashtage : ret.getHashtagEntities()) {
					System.out.println("Tweet:" + ret.getText());
//					System.out.println("Hashtag: " + ret.getHashtagEntities());
					// producer.send(new ProducerRecord<String, String>(
					// topicName, Integer.toString(j++), hashtage.getText()));
					producer.send(new ProducerRecord<String, String>(topicName, Integer.toString(j++), ret.getText()));
				//}
			}
		}
		// producer.close();
		// Thread.sleep(500);
		// twitterStream.shutdown();
	}

}
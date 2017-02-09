package com.nanospeck.hashfu;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TActions {

	static String replyMessage = "Agree! #isupport";
	static String tweetId = "829335852400664576";
	static Long inReplyToStatusId = Long.valueOf(tweetId);

	public static void main(String[] args){
//		favouriteTweet(tweetId);
		reply( inReplyToStatusId, replyMessage);
	}
	
	public static void favouriteTweet(String tweetId) {
		try {
			TwitterFactory factory = new TwitterFactory();
			Twitter twitter = factory.getInstance();
			twitter.setOAuthConsumer(Constants.consumerKey, Constants.consumerSecret);
			AccessToken accessToken = new AccessToken(Constants.accessToken, Constants.accessTokenSecret);
			twitter.setOAuthAccessToken(accessToken);
			Status status = twitter.createFavorite(Long.valueOf(tweetId));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// See more at:
		// http://www.tothenew.com/blog/mark-tweet-as-favorite-using-twitter4j/#sthash.AD1Sc78q.dpuf
	}

	public static void reply(Long inReplyToStatusId, String replyMessage) {
		try {
		TwitterFactory factory = new TwitterFactory();
		Twitter twitter = factory.getInstance();
		twitter.setOAuthConsumer(Constants.consumerKey, Constants.consumerSecret);
		AccessToken accessToken = new AccessToken(Constants.accessToken, Constants.accessTokenSecret);
		twitter.setOAuthAccessToken(accessToken);
		StatusUpdate statusUpdate = new StatusUpdate(replyMessage);
		statusUpdate.setInReplyToStatusId(inReplyToStatusId);
			Status status = twitter.updateStatus(statusUpdate);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

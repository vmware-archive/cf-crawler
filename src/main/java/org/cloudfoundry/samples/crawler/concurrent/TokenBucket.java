package org.cloudfoundry.samples.crawler.concurrent;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * A very simple TokenBucket implementation, its not thread safe, but the only side effect would be the bucket not being refilled 
 * completely on every refil.
 * @author vcarvalho
 *
 */
@Component
public class TokenBucket {
	private final int rate;
	private Timer timer;
	private BlockingQueue<Integer> tokens;
	
	@Autowired
	public TokenBucket(@Value("${bucket.tokens}") int rate) {
		this.timer = new Timer();
		this.rate = rate;
		this.tokens = new ArrayBlockingQueue<Integer>(rate);
		timer.schedule(new BucketFill(),1000L,1000L );
	}

	public void acquire() throws InterruptedException {
		tokens.take();
	}

	public void fill() throws InterruptedException {
		int fill = rate - tokens.size();
		for(int i=0;i<fill;i++){
			tokens.put(i);
		}
	}

	private class BucketFill extends TimerTask {

		@Override
		public void run() {
			try {
				TokenBucket.this.fill();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}

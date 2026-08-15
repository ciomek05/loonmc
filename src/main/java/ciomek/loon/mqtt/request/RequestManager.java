package ciomek.loon.mqtt.request;

import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestManager {
	static ConcurrentLinkedQueue<IRequest> queue = new ConcurrentLinkedQueue<>();

	public static void addRequest(IRequest request)
	{
		queue.add(request);
	}

	public static IRequest pollRequest()
	{
		return queue.poll();
	}
}

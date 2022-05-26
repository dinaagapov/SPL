package bgu.spl.mics;

import bgu.spl.mics.Events.PublishResultsEvent;
import bgu.spl.mics.Events.TestModelEvent;
import bgu.spl.mics.Events.TrainModelEvent;

import java.util.Hashtable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private Hashtable<Integer, LinkedBlockingQueue<Message>> MicroServicesQ = new Hashtable<>();
	private Hashtable<Class, LinkedBlockingQueue<Integer>> RegistrationQ = new Hashtable<>();
	private Hashtable<Event, Future> Futures = new Hashtable<>();
	private static AtomicInteger counterID = new AtomicInteger(0);
	//public static final Object lockGetFuture = new Object();


	private static class MessageBusHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}
	private MessageBusImpl() {
	}

	public static MessageBusImpl getInstance() {
		return MessageBusHolder.instance;

	}


	@Override
	// m is a micro service that can handle event of Type type;
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		subscribe(type,m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		subscribe(type,m);
	}

	public void subscribe(Class<? extends Message> type, MicroService m) {
		synchronized (RegistrationQ) {
			if(!RegistrationQ.containsKey(type)){
				RegistrationQ.putIfAbsent(type, new LinkedBlockingQueue<>());
			}
			RegistrationQ.get(type).add(m.getId());
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(Futures.containsKey(e)) {
			Futures.get(e).resolve(result);
		}

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		synchronized (RegistrationQ) {
			// check if there is Q that is register to get broadcast of b.get class
			if (RegistrationQ.containsKey(b.getClass())) {
				if (RegistrationQ.get(b.getClass()).size() > 0) {
					// and for every micro Service in the Q we add the broadCast b to their Q
					for(int id : RegistrationQ.get(b.getClass())) {
						try {
							MicroServicesQ.get(id).put(b);
						} catch (InterruptedException ignored) {
						}
					}
				}
			}
		}
	}


	@Override

	public <T> Future<T> sendEvent(Event<T> e) {
		Future future = null;
		synchronized (RegistrationQ) {
			synchronized (MicroServicesQ) {
				// check if there is microservice that can handel this type of event
				if (RegistrationQ.containsKey(e.getClass())) {
					if (RegistrationQ.get(e.getClass()).size() > 0) {
						//get the first id of the micro service that can handel this type of events
						int id = RegistrationQ.get(e.getClass()).remove();
						// we remove it from the queue becuse the robin manner
						RegistrationQ.get(e.getClass()).add(id);
						future = new Future();
						// we add the event to the micro service that we removed from the top of the Q
						if (e instanceof TrainModelEvent || e instanceof TestModelEvent || e instanceof PublishResultsEvent)
						{
							LinkedBlockingQueue<Message> temp = new LinkedBlockingQueue<>(MicroServicesQ.get(id));
							MicroServicesQ.get(id).clear();
							MicroServicesQ.get(id).add(e);
							MicroServicesQ.get(id).addAll(temp);
						}
						else
							MicroServicesQ.get(id).add(e);
						// we want to notify all the Q because maybe they got new message
						Futures.put(e, future);
						MicroServicesQ.notifyAll();
						return future;
					}
				}
				return future;
			}
		}


	}

	@Override
	public void register(MicroService m) {
		synchronized (MicroServicesQ) {
			LinkedBlockingQueue<Message> Q = new LinkedBlockingQueue<Message>();
			MicroServicesQ.put(m.getId(), Q);
			counterID.incrementAndGet();
			MicroServicesQ.notifyAll();
		}

	}

	@Override
	public void unregister(MicroService m)
	{
		synchronized (RegistrationQ) {
			synchronized (MicroServicesQ) {
				if ((MicroServicesQ.get(m.getId())).size() > 0) {
					for (Message message : MicroServicesQ.get(m.getId())) {
						if (message instanceof Event) {
							complete((Event) message, null);
						}
					}
				}
				MicroServicesQ.remove(m.getId());
				for (LinkedBlockingQueue tmp : RegistrationQ.values()) {
					tmp.remove(m.getId());
				}
			}
		}


	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		Message message = null;
		if (MicroServicesQ.get(m.getId())!=null) {
			message = MicroServicesQ.get(m.getId()).take();
			if (!m.getTypesToRegister().contains(message.getClass())) {
				System.out.println("---");
			}
		}
		return message;
	}

	/**
	 * @return: the Micro services queues
	 */
	public Hashtable<Integer, LinkedBlockingQueue<Message>> getMicroServicesQ() {
		return MicroServicesQ;
	}

	/**
	 * @return: the hash table of registration ms
	 */
	public Hashtable<Class, LinkedBlockingQueue<Integer>> getRegistrationQ() {
		return RegistrationQ;
	}

	/**
	 * @return: the hash table og event-future
	 */
	public Hashtable<Event, Future> getFutures() {  return Futures;}

	/**
	 *
	 * @param i - id of micro service
	 * @pre:  none
	 * @post: NumberOfRegisteredMS()=NumberOfRegisteredMS+1
	 * @return: true if ms with id i registered to mMessageBus
	 */
	public boolean isMicroServiceRegistered(Integer i)
	{
		return MicroServicesQ.get(i) != null;
	}

	/**
	 * @pre:
	 * @post: none
	 * @return: number of registerd ms to the massgaeBus
	 */
	public int NumberOfRegisteredMS()
	{
		return MicroServicesQ.size();
	}

	/**
	 *
	 * @param MsId
	 * @pre: NumberOfRegisteredMS > 0 && isMicroServiceRegistered(msId) == true
	 * @post: none
	 * @return: number of massages in q of ms
	 */
	public int NumOfMessagesInMs (int MsId)
	{
		return MicroServicesQ.get(MsId).size();
	}

	/**
	 * @pre: NumberOfRegisteredMS>0
	 * @post:
	 * @param e
	 */
	public void EnterNewMessage(Event e)
	{
		Futures.put( e,new Future());
	}

	/**
	 * @pre: NumberOfRegisteredMS>0
	 * @post: none
	 * @param e
	 * @return:
	 */
	public Future getFutureOfEvent(Event e)
	{
		return Futures.get(e);
	}
	public void setFuturesOfEvent(Event e , Future f)
	{
		Futures.remove(e);
		Futures.put(e,f);
	}
	/**
	 * @pre: NumberOfRegisteredMS>0
	 * @post: none
	 * @param c - class type
	 * @return: number of registerd ms to specific class c
	 */
	public int NumOfClassRegistered(Class c)
	{
		return RegistrationQ.get(c).size();
	}

	/**
	 * @pre: NumberOfRegisteredMS>0
	 * @post: none
	 * @param: c
	 * @return: true if microservice m registerd to class c
	 */


	public AtomicInteger getCounterID() {
		return counterID;
	}

	public void setCounterID() {
		counterID.incrementAndGet();
	}
}
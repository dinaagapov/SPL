package bgu.spl.mics.application.objects;



import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the cluster.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * Add all the fields described in the assignment as private fields.
 * Add fields and methods to this class as you see fit (including public methods and constructors).
 */
public class Cluster {
	private static class ClusterHolder{
		private static Cluster instance = new Cluster();
	}
	private ConcurrentLinkedQueue<GPU> GPUS = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<GPU> AvailableGPUS = new ConcurrentLinkedQueue<>();
	private PriorityQueue<CPU> CPUS ;
	private ConcurrentLinkedQueue<ConferenceInformation> Conferences = new ConcurrentLinkedQueue<ConferenceInformation>();
	private Object gpuObj = new Object();
	private HashMap<String,String> studentsTrainedModels = new HashMap<String, String>();
	private AtomicInteger batchesProcessed = new AtomicInteger(0);
	private List<Model> allModels = new ArrayList<>();
	private Cluster(){
		CPUS = new PriorityQueue<>(new Comparator<CPU>() {
		@Override
		public int compare(CPU o1, CPU o2) {
			return o1.getTotalTime() - o2.getTotalTime();
		}
	});
	}
	public void setAllModels(List<Model> ALL)
	{
		allModels.addAll(ALL);
	}
	public List<Model> getAllModels()
	{
		return allModels;
	}
	public GPU getCurrentGPU() throws InterruptedException {
		GPU current = null;
		synchronized (gpuObj) {
			while (AvailableGPUS.isEmpty()) {
				gpuObj.wait();
			}
			current = AvailableGPUS.poll();
			return current;
		}
		}
	public static Cluster getInstance() {
		return ClusterHolder.instance;
	}

	public void  sendToGPU(DataBatch db) {
		synchronized (GPUS) {
			db.getGPU().receiveProcessed(db);

		}
		batchesProcessed.incrementAndGet();
	}
	public void addAvailableGPU(GPU gpu){
		synchronized (gpuObj) {
			AvailableGPUS.add(gpu);
			gpuObj.notifyAll();
		}

	}
	public  void  sendToCPU (DataBatch db){
		synchronized(CPUS) {
			CPU cpuCurrent = CPUS.poll();
			cpuCurrent.receiveDataBatch(db);
			CPUS.add(cpuCurrent);
		}
	}



	public void addTrainModel(Model m){
		studentsTrainedModels.putIfAbsent(m.getStudent().getName() ,m.getName());
	}
	public ConcurrentLinkedQueue<GPU> getGPUS() {
		return GPUS;
	}

	public PriorityQueue<CPU> getCPUS() {
		return CPUS;
	}

	public ConferenceInformation getCurrentConference(){
		ConferenceInformation conferenceInformation = Conferences.poll();
		return conferenceInformation;
	}
	public void setConferences(ConferenceInformation conferenceInformation)
	{
		Conferences.add(conferenceInformation);
	}
	public int getBatchesProcessed(){
		return batchesProcessed.get();
	}
	public int getCpuTimeUsed(){
		Integer total = 0;
		for(CPU cpu : CPUS)
			total = cpu.getTick() + total;
		return total;
	}
	public int getGpuTimeUsed(){
		Integer total = 0;
		for(GPU gpu : GPUS)
			total = gpu.getTick() + total;
		return total;

	}
	public void addGPUs(LinkedList<GPU> gpus){
		for(GPU gpu : gpus) {
			GPUS.add(gpu);
			AvailableGPUS.add(gpu);
		}
	}
	public void addCPUs(LinkedList<CPU> cpus){
		CPUS.addAll(cpus);
	}
	public void addConferences (LinkedList<ConferenceInformation> conList)
	{
		Conferences.addAll(conList);
	}

	public HashMap<String, String> getStudentsTrainedModels() {
		return studentsTrainedModels;
	}
	public ConcurrentLinkedQueue<ConferenceInformation> getConferences()
	{
		return Conferences;
	}
}

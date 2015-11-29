package yitgogo.smart.tools;

public abstract class Mission implements Runnable {

	private boolean isCanceled = false;

	@Override
	public void run() {
		missionStart();
		cacelMission();
	}

	public void cacelMission() {
		isCanceled = true;
	}

	public abstract void missionStart();

	public boolean isCanceled() {
		return isCanceled;
	}

}

package es.upm.tfo.lst.pdmanagertest.cognitive.LondonTowers;

import es.upm.tfo.lst.pdmanagertest.tools.RNG;

public class LondonTowersGame extends LondonTowersGraph
{
	private RNG rand;
	public int minMoves;
	public String sinit, starget;
	public TowerStacks init, target;
	
	public LondonTowersGame()
	{
		rand = new RNG();
		reset();
	}

	public void reset()
	{
		int
			range = graphPositions.length-1,
			i = rand.getIntInClosedRange(0, range),
			j = rand.getIntInClosedRangeAvoiding(0, range, i);
		sinit = graphPositions[i];
		starget = graphPositions[j];
		minMoves = getPathLenDijkstra(sinit, starget);
		init = new TowerStacks(gamePositions[i]);
		target = new TowerStacks(gamePositions[j]);
	}

	public boolean isResolved()
	{
		return init.equals(target);
	}
}
package com.rwtema.denseores;

public class DenseOre {
	String baseBlock;
	int metadata;
	double prob;
	String underlyingBlock;
	String texture;
	int id;

	public DenseOre(int id, String baseBlock, int metadata, double prob, String underlyingBlock, String texture) {
		this.id = id;
		this.baseBlock = baseBlock;
		this.metadata = metadata;
		this.prob = prob;
		this.underlyingBlock = underlyingBlock;
		this.texture = texture;
	}

}
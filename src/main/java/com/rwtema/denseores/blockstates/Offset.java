package com.rwtema.denseores.blockstates;

public class Offset {
	public int[] dx;
	public int[] dy;

	public static Offset getOffset(int renderType) {
		Offset offset = new Offset();
		switch (renderType) {
			default:
			case 0:
				offset.dx = new int[]{-1, 2, 3};
				offset.dy = new int[]{-1, 0, 1};
				break;
			case 1:
				offset.dx = new int[]{-1, 1, 0, 0, -1, -1, 1, 1, -2, 2, 0, 0};
				offset.dy = new int[]{0, 0, -1, 1, -1, 1, -1, 1, 0, 0, -2, 2};
				break;
			case 2:
				offset.dx = new int[]{-1, 0, 1};
				offset.dy = new int[]{-1, 0, 1};
				break;
			case 3:
				offset.dx = new int[]{-2, 2, 1, 1};
				offset.dy = new int[]{1, 1, -2, 2};
			case 4:
				offset.dx = new int[]{-6, -3, 3, 6};
				offset.dy = new int[]{0, 0, 0, 0};
				break;
			case 5:
				offset.dx = new int[]{-5, -5, 5, 5};
				offset.dy = new int[]{-5, 5, -5, 5};
				break;
			case 6:
				offset.dx = new int[]{0, 1, 2, 3};
				offset.dy = new int[]{0, -3, 2, -1};
				break;
			case 7:
				offset.dx = new int[]{-1, 1, 0, 0};
				offset.dy = new int[]{0, 0, -1, 1};
				break;
		}
		return offset;
	}
}

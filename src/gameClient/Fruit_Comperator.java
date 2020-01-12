package gameClient;

import java.util.Comparator;

public class Fruit_Comperator implements Comparator<Fruit> {

	public Fruit_Comperator() {
		;
	}

	@Override
	public int compare(Fruit f1, Fruit f2) {
		int biggest = (int) (f2.getValue() - f1.getValue());
		return biggest;
	}

}

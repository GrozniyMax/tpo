clean:
	rm ./graphs/*

graphs:
	python3 scripts/plot_throughput.py

redraw: clean
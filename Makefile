.PHONY: clean prepare graphs redraw

clean:
	rm ./graphs/*

clean-data:
	rm load/*.csv

prepare:
	mv allTable load/allTable.csv
	mv config1Table load/config1Table.csv
	mv config2Table load/config2Table.csv
	mv config3Table load/config3Table.csv


graphs-comparison: prepare
	python3 scripts/plot_throughput.py

graphs-load:
	python3 scripts/plot_stress_results.py


redraw: clean graphs
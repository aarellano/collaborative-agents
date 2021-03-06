#!/usr/bin/python
import os
import sys
import pickle
import numpy
import matplotlib.pyplot as plt


#algorithms to graph
maps = ["empty"]#,"reg","rep","rooms"]
algorithms = ["CFS"]#,"GS","DGS"]
collaborative = ["GAUSS","GAUSS2"]#,"GAUSS", "FOLLOWERS_BRAKER", "SHARED_PLAN"]
number_agents = [1,2,3]#,4,5,10]
sameStartingPosition = ["true"]#, "false"]


def load_hash(fname):
	return pickle.load(open(fname))


def initialize_hash():
	res = {}
	for coll in collaborative:
		res[coll] = {}
		for algorithm in algorithms:
			res[coll][algorithm] = {}
			for mapa in maps:
				res[coll][algorithm][mapa] = {}
				for pos in sameStartingPosition:
					res[coll][algorithm][mapa][pos] = {}
					for agents in number_agents:
						res[coll][algorithm][mapa][pos][str(agents)] = {}
						for data in ['avg revisited','std dev revisited','avg time','std dev time','obstacles','cells']:
							res[coll][algorithm][mapa][pos][str(agents)][data] = 0
	return res


def filter_hash(hashtbl):
	res = initialize_hash();
	for coll in collaborative:
		for algorithm in algorithms:
			for mapa in maps:
				for pos in sameStartingPosition:
					for agent in number_agents:
						agent = str(agent)
						res[coll][algorithm][mapa][pos][agent]['obstacles'] = hashtbl[coll][algorithm][mapa][pos][agent]['obstacles'][0]
						res[coll][algorithm][mapa][pos][agent]['cells'] = hashtbl[coll][algorithm][mapa][pos][agent]['cells'][0]
						arr = numpy.array(hashtbl[coll][algorithm][mapa][pos][agent]['revisited'])
						res[coll][algorithm][mapa][pos][agent]['avg revisited'] = numpy.average(arr)
						res[coll][algorithm][mapa][pos][agent]['std dev revisited'] = numpy.std(arr)
						arr = numpy.array(hashtbl[coll][algorithm][mapa][pos][agent]['time'])
						res[coll][algorithm][mapa][pos][agent]['avg time'] = numpy.average(arr)
						res[coll][algorithm][mapa][pos][agent]['std dev time'] = numpy.std(arr)
						# TODO
						# for pos in initial_pos:
						# 	if pos == res[coll][algorithm][mapa][pos][agent][pos]???
	return res


def add_scatter_series(hashtbl, collaborative_subset, algorithms_subset, maps_subset, initial_subset, label, marker, color):
	x = []
	y = []
	for coll in collaborative_subset:
		for algorithm in algorithms_subset:
			for mapa in maps_subset:
				num_cells_to_visit = get_perfect_solution(hashtbl, mapa)
				for pos in initial_subset:
					for agent in number_agents:
						x.append(agent)
						yy = hashtbl[coll][algorithm][mapa][pos][str(agent)]['avg time']
						yy = float(num_cells_to_visit)/float(yy)
						y.append(yy)
	plt.scatter(x,y, marker=marker, label=label, color=color)


def get_perfect_solution(hashtbl, mapa):
	res = hashtbl[collaborative[0]][algorithms[0]][mapa][sameStartingPosition[0]][str(number_agents[0])]['cells']
	res = res - hashtbl[collaborative[0]][algorithms[0]][mapa][sameStartingPosition[0]][str(number_agents[0])]['obstacles']
	return  res
			

def create_scatter_series_CFS(hashtbl, mapa, pos):
	# add_scatter_series(hashtbl, ["ORIG"], ["CFS"], mapa, pos, "CFS - Non Coll", "*", "r")
	add_scatter_series(hashtbl, ["GAUSS"], ["CFS"], mapa, pos, "CFS - Offline", "o", "g")
	add_scatter_series(hashtbl, ["GAUSS2"], ["CFS"], mapa, pos, "CFS - Offline", "^", "m")
	# add_scatter_series(hashtbl, ["FOLLOWERS_BRAKER"], ["CFS"], mapa, pos, "CFS - FB", "^", "m")
	# add_scatter_series(hashtbl, ["SHARED_PLAN"], ["CFS"], mapa, pos, "CFS - SP", "D", "k")


def create_scatter_series_GS(hashtbl, mapa, pos):
	# add_scatter_series(hashtbl, ["ORIG"], ["GS"], mapa, pos, "GS - Non Coll", "*", "r")
	add_scatter_series(hashtbl, ["GAUSS"], ["GS"], mapa, pos, "GS - Offline", "o", "g")
	# add_scatter_series(hashtbl, ["FOLLOWERS_BRAKER"], ["GS"], mapa, pos, "GS - FB", "^", "m")
	# add_scatter_series(hashtbl, ["SHARED_PLAN"], ["GS"], mapa, pos, "GS - SP", "D", "k")


def create_scatter_series_DGS(hashtbl, mapa, pos):
	# add_scatter_series(hashtbl, ["ORIG"], ["DGS"], mapa, pos, "DGS - Non Coll", "*", "r")
	add_scatter_series(hashtbl, ["GAUSS"], ["DGS"], mapa, pos, "DGS - Offline", "o", "g")
	# add_scatter_series(hashtbl, ["FOLLOWERS_BRAKER"], ["DGS"], mapa, pos, "DGS - FB", "^", "m")
	# add_scatter_series(hashtbl, ["SHARED_PLAN"], ["DGS"], mapa, pos, "DGS - SP", "D", "k")


def create_graph(hashtbl):
	plt.plot(range(1,max(number_agents)+1),range(1,max(number_agents)+1), label="Theoretical")
	a = plt.gca()
	a.set_xlim(0,max(number_agents)+1)
	a.set_ylim(0,max(number_agents)+1)
	plt.xlabel('Number of Agents')
	plt.ylabel('Performance')
	plt.title('CFS 0% obstacles map. Random starting point\n')
	create_scatter_series_CFS(hashtbl, ["empty"], ["true"])
	plt.legend(loc='upper center', bbox_to_anchor=(0.5, 1.01),
          ncol=3, fancybox=True, shadow=True)
	


if __name__ == "__main__":
	if len(sys.argv) != 3:
	    sys.stderr.write('Usage: sys.argv[0] serial_object_fname output_fname\n')
	    sys.exit(1)

	serial_object_fname = sys.argv[1]
	output_fname = sys.argv[2]

	hashtbl = load_hash(serial_object_fname)
	hashtbl = filter_hash(hashtbl)
	create_graph(hashtbl)
	plt.savefig(output_fname)

